import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Product, UserIdService } from '@spartacus/core';
import { Observable, ReplaySubject, Subject } from 'rxjs';
import { map, takeUntil, tap } from 'rxjs/operators';

import { AppendComponentService } from '@services/append-component.service';
import { PageHelper } from '@helpers/page-helper';
import { FloatingToolbarEvents } from '@features/pages/product/plp/product-list/product-list-main/floating-toolbar/floating-toolbar.component';
import { ProductEnhancementsService } from '@features/pages/product/core/services/product-enhancements.service';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { ActivatedRoute } from '@angular/router';
import { CompareService, ShoppingListService } from '@services/feature-services';
import { ShoppingListPayloadItem } from '../../../../../model/shopping-list.model';
import { UntypedFormControl } from '@angular/forms';
import { SlideDrawerService } from '@design-system/slide-drawer/slide-drawer.service';

@Component({
  selector: 'app-add-to-list',
  templateUrl: './add-to-list.component.html',
  styleUrls: ['./add-to-list.component.scss'],
})
export class AddToListComponent implements OnInit, OnDestroy {
  @Input() product: Product;
  @Input() toolbarEvent?: Subject<FloatingToolbarEvents>; // Used on the PLP only

  @Input() compareListId?: string;
  @Input() shoppingListId?: string;
  @Input() control?: UntypedFormControl;

  userId: string;
  isInShoppingList$: Observable<boolean>;
  isInCompareList$: Observable<boolean>;
  private isInCompareList = false;
  private destroyed$ = new ReplaySubject<void>();
  private itemListEntity?: ItemListEntity;

  constructor(
    private compareService: CompareService,
    private productEnhancements: ProductEnhancementsService,
    private appendComponentService: AppendComponentService,
    private pageHelper: PageHelper,
    private activatedRoute: ActivatedRoute,
    private userIdService: UserIdService,
    private slideDrawerService: SlideDrawerService,
    private shoppingListService: ShoppingListService,
  ) {}

  ngOnInit(): void {
    this.userIdService
      .getUserId()
      .pipe(takeUntil(this.destroyed$))
      .subscribe((userId) => {
        this.userId = userId;
        this.assignIfInShoppingList();
      });

    this.isInCompareList$ = this.compareService.getCompareListState().pipe(
      map((res) => res.products?.some((product) => product.code === this.product.code)),
      tap((isInList) => (this.isInCompareList = isInList)),
    );

    if (this.pageHelper.isProductListPage()) {
      this.itemListEntity = this.pageHelper.getListPageEntityType();
    } else {
      this.itemListEntity = this.activatedRoute.snapshot.queryParams.itemList as ItemListEntity;
    }
  }

  ngOnDestroy(): void {
    this.destroyed$.next();
    this.destroyed$.complete();
  }

  onCompareClick(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    if (this.isInCompareList) {
      this.removeCompareProduct();
    } else {
      this.addCompareProduct();
    }
  }

  onShoppingListClick(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    const checkbox = event.target as HTMLInputElement;
    checkbox.checked = !checkbox.checked;

    this.shoppingListService.shoppingListProductPayload$.next({
      products: this.returnPayloadItems(),
      itemListEntity: this.itemListEntity,
    });

    this.slideDrawerService.openPanel(
      new Event('default'),
      this.userId === 'current' ? 'add-to-list-drawer' : 'add-list-drawer-login',
    );
  }

  private assignIfInShoppingList(): void {
    this.isInShoppingList$ = this.productEnhancements.isInShoppingList(this.product.code);
  }

  private returnPayloadItems(): ShoppingListPayloadItem[] {
    return [
      {
        product: this.product,
        desired: this.control?.value,
      },
    ];
  }

  private addCompareProduct(): void {
    this.isInCompareList = true;
    this.compareService.addCompareProduct(this.product).subscribe();
    this.toolbarEvent?.next(FloatingToolbarEvents.ITEM_ADDED);
  }

  private removeCompareProduct(): void {
    this.isInCompareList = false;
    this.compareService.removeCompareProduct([this.product.code]).subscribe();
    this.toolbarEvent?.next(FloatingToolbarEvents.ITEM_REMOVED);
  }
}
