import { Component, ElementRef, OnDestroy, OnInit, QueryList, ViewChild, ViewChildren } from '@angular/core';
import { faPrint, faShoppingCart } from '@fortawesome/free-solid-svg-icons';
import { BehaviorSubject, Observable, Subscription, of } from 'rxjs';
import { filter, first, map, switchMap, take, tap } from 'rxjs/operators';
import { DistCartService } from 'src/app/spartacus/services/cart.service';
import { NavigationStart, Router } from '@angular/router';
import { ShoppingListItemComponent } from './shopping-list-item/shopping-list-item.component';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { BulkProducts } from '@model/cart.model';
import { PrintPageEvent } from '@features/tracking/events/print-page-event';
import { ShoppingListService } from './core';
import { ProductEnhancementsService } from '../product/core/services/product-enhancements.service';
import { ShoppingListComponentService } from './core/services/shopping-list-component.service';
import { ShoppingList } from '@model/shopping-list.model';
import { WindowRef } from '@spartacus/core';
import { createFrom, EventService } from '@spartacus/core';

@Component({
  selector: 'app-shopping-list',
  templateUrl: './shopping-list.component.html',
  styleUrls: ['./shopping-list.component.scss'],
})
export class ShoppingListComponent implements OnInit, OnDestroy {
  @ViewChild('selectAllEl') selectAllEl: ElementRef;
  @ViewChildren(ShoppingListItemComponent) selectedProducts: QueryList<any>;

  shoppingList_ = this.shoppingListComponentService.shoppingList_;

  phasedOutProducts_: BehaviorSubject<string> = this.cartService.phasedOutProducts_;

  faShoppingCart = faShoppingCart;
  faPrint = faPrint;

  itemListEntity = ItemListEntity;

  isAlertBannerVisible: boolean;
  punchedOutProductCodes: string[] = [];

  timeout: NodeJS.Timeout;

  private subscriptions: Subscription = new Subscription();

  constructor(
    private eventService: EventService,
    private shoppingListComponentService: ShoppingListComponentService,
    private shoppingListService: ShoppingListService,
    private cartService: DistCartService,
    private router: Router,
    private productEnhancementService: ProductEnhancementsService,
    private winRef: WindowRef,
  ) {
    this.subscriptions.add(
      this.router.events
        .pipe(filter((event: NavigationStart) => event instanceof NavigationStart))
        .subscribe((event: NavigationStart) => {
          if (event.url === '/shopping' && event.navigationTrigger !== 'imperative') {
            router.navigateByUrl('/cart');
          }
        }),
    );
  }

  ngOnInit(): void {
    const currentShoppingListId = this.shoppingListService.getCurrentShoppingListId();
    if (currentShoppingListId === '' || currentShoppingListId === '/' || currentShoppingListId === 'undefined') {
      this.subscriptions.add(this.getAndSetFirstShoppingListFromState().subscribe());
    } else {
      this.subscriptions.add(this.getAndSetFirstShoppingListById(currentShoppingListId).subscribe());
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
    this.shoppingListComponentService.resetIsTriggerViewItemListEventOnLoad();
  }

  getAndSetFirstShoppingListFromState(): Observable<ShoppingList[]> {
    return this.shoppingListService.getShoppingListsState().pipe(
      map((lists) => lists.list),
      filter((l) => l.length > 0),
      tap((lists) => {
        this.shoppingListComponentService.setToFirstList(lists);
        this.checkForPunchoutProducts(lists[0]);
        this.shoppingListComponentService.setShoppingListById(lists[0]);
        if (!lists[0].calculated) {
          this.calculate(lists[0].uniqueId);
        }
      }),
    );
  }

  getAndSetFirstShoppingListById(currentShoppingListId): Observable<ShoppingList> {
    return this.shoppingListService.getShoppingLisById(currentShoppingListId).pipe(
      take(1),
      tap((shoppingList: ShoppingList) => {
        this.checkForPunchoutProducts(shoppingList);
        this.shoppingListComponentService.setShoppingListById(shoppingList);
        if (!shoppingList.calculated) {
          this.calculate(shoppingList.uniqueId);
        }
      }),
    );
  }

  checkForPunchoutProducts(list: ShoppingList): void {
    this.isAlertBannerVisible = false;
    this.punchedOutProductCodes.splice(0);
    list.punchOutProducts?.forEach((punchedOutProduct) => {
      const { isBuyable, isPunchedOut, salesStatus, productCode } = punchedOutProduct;
      const unavailableStatus = ['60', '61', '62', '90', '91', '92', '99'];
      this.isAlertBannerVisible =
        isPunchedOut || (unavailableStatus.includes(salesStatus) && !isBuyable && !isPunchedOut);

      if (isPunchedOut) {
        this.punchedOutProductCodes.push(productCode);
        this.productEnhancementService.get(productCode, true).pipe(first()).subscribe();
      }

      if (unavailableStatus.includes(salesStatus) && !this.punchedOutProductCodes.includes(productCode)) {
        if (!this.punchedOutProductCodes.includes(productCode)) {
          this.punchedOutProductCodes.push(productCode);
        }
        this.productEnhancementService.get(productCode, true).pipe(first()).subscribe();
      }
    });
  }

  selectAll($event): void {
    this.shoppingListComponentService.selectAll($event);
  }

  addListToCartData() {
    this.shoppingListComponentService.addListToCartData();
  }

  onSelected($event): void {
    this.shoppingListComponentService.onSelected($event, this.selectedProducts, this.selectAllEl);
  }

  onQuantityChange({ quantity, productCode, listId }): void {
    if (this.winRef) {
      clearTimeout(this.timeout);

      this.subscriptions.add(
        this.shoppingListComponentService.onQuantityChange({ quantity, productCode, listId }).subscribe(),
      );
    }
  }

  remove(event): void {
    this.subscriptions.add(
      this.shoppingListComponentService.remove(event, this.selectedProducts, this.selectAllEl).subscribe(),
    );
  }

  setAllSelectedCheckbox(checked: boolean): void {
    this.shoppingListComponentService.setAllSelectedCheckbox(checked, this.selectAllEl);
  }

  downloadProducts($event, name: string, type: string): void {
    this.shoppingListComponentService.downloadProducts($event, name, type);
  }

  onAddedToCart(): void {
    this.shoppingListComponentService.onAddedToCart();
  }

  shoppingListCart(): BulkProducts | undefined {
    return this.shoppingListComponentService.shoppingListCart();
  }

  openErrorPopup(): void {
    this.shoppingListComponentService.openErrorPopup();
  }

  isDisabled(count: number): boolean {
    return count === 0;
  }

  isNoProductSelected(): boolean {
    return !this.shoppingListComponentService.cartData.length;
  }

  print(): void {
    this.eventService.dispatch(createFrom(PrintPageEvent, { context: { pageType: ItemListEntity.SHOPPING } }));
    this.shoppingListComponentService.print();
  }

  calculate(listId: string = this.shoppingListService.getCurrentShoppingListId()): void {
    this.subscriptions.add(this.shoppingListComponentService.calculate(listId).subscribe());
  }

  replaceComaInPricing(price: string): string {
    return this.shoppingListService.replaceComaInPricing(price);
  }

  onChangeList(id: string): void {
    if (id === this.shoppingList_.value.uniqueId) {
      return;
    }

    this.subscriptions.add(
      this.shoppingListComponentService
        .onChangeList(id)
        .pipe(
          switchMap((list: ShoppingList) => {
            this.checkForPunchoutProducts(list);
            this.setAllSelectedCheckbox(false);

            if (!list.calculated) {
              return this.shoppingListComponentService.calculate(list.uniqueId);
            }
            return of(list);
          }),
        )
        .subscribe(),
    );
  }

  onSortByClick($event): void {
    this.shoppingListComponentService.onSortByClick($event);
  }
}
