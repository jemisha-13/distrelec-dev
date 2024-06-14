import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder } from '@angular/forms';
import { SlideDrawerService } from '@design-system/slide-drawer/slide-drawer.service';
import { ProductEnhancementsService } from '@features/pages/product/core/services/product-enhancements.service';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { ShoppingListPayloadItem, ShoppingList, ShoppingListEntry } from '@model/shopping-list.model';
import { first, map, switchMap, takeUntil, tap } from 'rxjs/operators';
import { EMPTY, Observable, ReplaySubject, Subscription, combineLatest } from 'rxjs';
import { ShoppingListService } from '@services/feature-services';
import { DistBreakpointService } from '@services/breakpoint.service';

@Component({
  selector: 'app-add-to-shopping-list',
  templateUrl: './add-to-shopping-list.component.html',
  styleUrls: ['./add-to-shopping-list.component.scss'],
})
export class ShoppingListAddToListComponent implements OnInit, OnDestroy {
  isInProgress: boolean;
  isMobileBreakpoint$: Observable<boolean> = this.breakpointService.isMobileBreakpoint();
  isMobile: boolean;
  uncheckedCheckboxes: string[] = [];

  shoppingListsAll$: Observable<ShoppingList[]> = this.shoppingListService
    .getShoppingListsState()
    .pipe(map((lists) => lists.list));

  addToListForm: FormGroup = this.fb.group({
    name: new FormControl(),
  });

  subscriptions = new Subscription();

  private shoppingListPayload: { products: ShoppingListPayloadItem[]; itemListEntity: ItemListEntity };
  private readonly destroyed$ = new ReplaySubject<void>();

  constructor(
    private slideDrawerService: SlideDrawerService,
    private shoppingListService: ShoppingListService,
    private fb: FormBuilder,
    private productEnhancementService: ProductEnhancementsService,
    private breakpointService: DistBreakpointService,
  ) {}

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
    this.destroyed$.next();
    this.destroyed$.complete();
  }

  ngOnInit(): void {
    combineLatest([this.slideDrawerService.drawer$().pipe(map((uid) => !!uid)), this.shoppingListsAll$])
      .pipe(
        takeUntil(this.destroyed$),
        switchMap(([drawer, lists]) => {
          if (drawer) {
            return this.checkIfProductIsInShoppingList(lists);
          } else {
            this.reinitializeForm();
            return EMPTY;
          }
        }),
      )
      .subscribe();

    this.subscriptions.add(
      this.isMobileBreakpoint$.subscribe((data) => {
        this.isMobile = data;
      }),
    );
  }

  checkIfProductIsInShoppingList(
    lists: ShoppingList[],
  ): Observable<{ products: ShoppingListPayloadItem[]; itemListEntity: ItemListEntity }> {
    return this.shoppingListService.shoppingListProductPayload$.pipe(
      takeUntil(this.destroyed$),
      tap((payload: { products: ShoppingListPayloadItem[]; itemListEntity: ItemListEntity }) => {
        if (payload?.products?.length) {
          this.shoppingListPayload = payload;
          lists.forEach((list: ShoppingList) => {
            list.entries.forEach((entry: ShoppingListEntry) => {
              if (entry.product.code === payload.products[0].product.code) {
                this.addToListForm.addControl(`checkbox-list-${list.uniqueId}`, this.fb.control(list.uniqueId));
                this.addToListForm.get(`checkbox-list-${list.uniqueId}`).setValue(list.uniqueId);
              }
            });
          });
        }
      }),
    );
  }

  closePanel(): void {
    this.slideDrawerService.closePanel();
    this.uncheckedCheckboxes = [];
  }

  onCheck(listId: string, event): void {
    if (event.target.checked) {
      const index = this.uncheckedCheckboxes.indexOf(listId);
      if (index !== -1) {
        this.uncheckedCheckboxes.splice(index, 1);
      }
      this.addToListForm.addControl(`checkbox-list-${listId}`, this.fb.control(listId));
    } else {
      if (!this.uncheckedCheckboxes.includes(listId)) {
        this.uncheckedCheckboxes.push(listId);
      }
      this.addToListForm.removeControl(`checkbox-list-${listId}`);
    }
  }

  saveProductToShoppingList(listId: string): void {
    this.subscriptions.add(
      this.shoppingListService
        .addToShoppingList(listId, this.shoppingListPayload.products, this.shoppingListPayload.itemListEntity)
        .pipe(
          tap(() => {
            this.productEnhancementService.load(this.shoppingListPayload.products.map((entry) => entry.product.code));
          }),
        )
        .subscribe(() => {
          this.closePanel();
        }),
    );
  }

  removeProductFromShoppingList(listId: string): void {
    this.subscriptions.add(
      this.shoppingListService
        .removeProductFromList(listId, this.shoppingListPayload.products[0].product.code)
        .subscribe(() => {
          this.isInProgress = false;
          this.productEnhancementService.load(this.shoppingListPayload.products.map((entry) => entry.product.code));
          this.closePanel();
        }),
    );
  }

  saveProductToNewList(): void {
    this.shoppingListService
      .createShoppingList(this.addToListForm.get('name').value)
      .pipe(
        first(),
        switchMap((res) =>
          this.shoppingListService.addToShoppingList(res?.uniqueId, this.shoppingListPayload.products, null),
        ),
        tap(() => {
          this.productEnhancementService.load(this.shoppingListPayload.products.map((entry) => entry.product.code));
          this.closePanel();
        }),
      )
      .subscribe();
  }

  handleSaveRequest(): void {
    if (this.addToListForm.get('name').value !== '' && this.addToListForm.get('name').value !== null) {
      this.saveProductToNewList();
    } else {
      this.productEnhancementService.load(this.shoppingListPayload.products.map((entry) => entry.product.code));
    }

    Object.keys(this.addToListForm.controls).forEach((key) => {
      if (key.includes('checkbox-list')) {
        const listId = this.addToListForm.get(key).value;
        this.saveProductToShoppingList(listId);
      }
    });

    if (this.uncheckedCheckboxes.length > 0) {
      this.shoppingListsAll$.pipe(first()).subscribe((lists) => {
        lists.forEach((list) => {
          const productExists = list.entries.some(
            (entry: ShoppingListEntry) => entry.product.code === this.shoppingListPayload.products[0].product.code,
          );
          if (productExists && this.uncheckedCheckboxes.includes(list.uniqueId)) {
            this.isInProgress = true;
            this.removeProductFromShoppingList(list.uniqueId);
          }
        });
      });
    }
  }

  private reinitializeForm(): void {
    this.addToListForm = this.fb.group({
      name: new FormControl(),
    });
  }
}
