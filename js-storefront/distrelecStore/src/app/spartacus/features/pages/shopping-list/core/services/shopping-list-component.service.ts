/* eslint-disable @typescript-eslint/naming-convention */
import { formatDate } from '@angular/common';
import { ElementRef, Injectable, QueryList, signal } from '@angular/core';
import { ShoppingListWarningPopupComponent } from '@features/shared-modules/popups/shopping-list-warning-popup/shopping-list-warning-popup';
import { ShoppingList, ShoppingListEntry } from '@model/shopping-list.model';
import { BulkProducts } from '@model/cart.model';
import { AppendComponentService } from '@services/append-component.service';
import { DistBreakpointService } from '@services/breakpoint.service';
import { DistCartService } from '@services/cart.service';
import { ExcelService } from '@services/excel.service';
import { PriceService } from '@services/price.service';
import { EventService, RoutingService, WindowRef, createFrom } from '@spartacus/core';
import { BREAKPOINT } from '@spartacus/storefront';
import { AngularCsv } from 'angular-csv-ext/dist/Angular-csv';
import { BehaviorSubject, Observable, from, of } from 'rxjs';
import { ShoppingListService } from './shopping.service';
import { first, map, mergeMap, switchMap, take, tap, toArray } from 'rxjs/operators';
import { ViewItemListEvent } from '@features/tracking/events/view-item-list-event';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { Location } from '@angular/common';

@Injectable({
  providedIn: 'root',
})
export class ShoppingListComponentService {
  isTriggerViewItemListEventOnLoad = true;
  isPopupOpen = false;

  selected = { flag: false, code: [], qty: 0 };

  csvData = [];
  cartData = [];

  phasedOutProducts_: BehaviorSubject<string> = this.cartService.phasedOutProducts_;
  shoppingList_ = new BehaviorSubject<ShoppingList>(null);
  addedToCartSignal = signal([]);

  constructor(
    private eventService: EventService,
    private routingService: RoutingService,
    private shoppingListService: ShoppingListService,
    private excelService: ExcelService,
    private cartService: DistCartService,
    private winRef: WindowRef,
    private appendComponentService: AppendComponentService,
    private priceService: PriceService,
    private breakpointService: DistBreakpointService,
    private location: Location,
  ) {}

  selectAll($event): void {
    if ($event.target.checked) {
      this.selectProduct(true);
    } else {
      this.selectProduct(false);
    }
  }

  selectProduct(isAllChecked: boolean): void {
    // eslint-disable-next-line @typescript-eslint/prefer-for-of
    for (let i = 0; i < this.winRef.document.querySelectorAll('.js-select-product').length; i++) {
      const checkbox = <HTMLInputElement>this.winRef.document.querySelectorAll('.js-select-product')[i];

      if (isAllChecked) {
        if (!checkbox.disabled) {
          this.selected.code.push(checkbox.getAttribute('data-product-id'));
          checkbox.checked = true;
          this.selected.flag = true;
        }
      } else {
        checkbox.checked = false;
        this.selected.flag = false;
        (<HTMLInputElement>this.winRef.document.getElementById('shoppingList_select-all')).checked = false;
        // When clicking on unselect all, cart data must be empty to prevent stacking the product in cart data
        this.selected = { flag: false, code: [], qty: 0 };
        this.cartData = [];
      }
    }

    this.addListToCartData();
  }

  addListToCartData(): void {
    this.shoppingList_.value.entries.forEach((entry) => {
      if (this.selected.code.find((code) => code === entry.product.code)) {
        this.cartData.push({
          itemNumber: '',
          productCode: entry?.product?.code,
          quantity: entry?.desired,
          reference: '',
          userId: 'current',
        });
      } else {
        this.cartData = this.cartData.filter((item) => item?.product?.code !== entry?.product?.code);
      }
    });
  }

  onSelected($event, selectedProducts: QueryList<any>, selectAllEl: ElementRef): void {
    if ($event.event.target.checked) {
      this.selected.flag = true;
      this.cartData.push({
        itemNumber: '',
        productCode: $event.productData?.product?.code,
        quantity: $event.productData?.desired,
        reference: '',
        userId: 'current',
      });
    } else {
      this.selected.flag = false;
      this.cartData = this.cartData.filter((item) => item?.productCode !== $event.productData?.product?.code);
      (<HTMLInputElement>this.winRef.document.getElementById('shoppingList_select-all')).checked = false;
    }

    if (this.checkAllSelected(selectedProducts)) {
      this.setAllSelectedCheckbox(true, selectAllEl);
    }
  }

  checkAllSelected(selectedProducts: QueryList<any>): boolean {
    const length = selectedProducts.length;
    let productsChecked = 0;
    selectedProducts.forEach((prod) => {
      const checked = prod.selectedProduct.nativeElement.checked;
      if (checked) {
        productsChecked++;
      }
    });
    if (productsChecked === length) {
      return true;
    }
  }

  onQuantityChange({ quantity, productCode, listId }): Observable<ShoppingList> {
    return this.shoppingListService.updateEntry(listId, quantity, productCode).pipe(
      take(1),
      tap(() => {
        const entries = this.shoppingList_.value.entries;
        const updatedProduct = entries.find((entry) => entry.product.code === productCode);
        updatedProduct.desired = quantity;
        const indexOfProduct = entries.findIndex((entry) => entry.product.code === productCode);
        entries[indexOfProduct] = updatedProduct;
        this.shoppingList_.next({ ...this.shoppingList_.value, calculated: false, entries });
      }),
    );
  }

  remove(event, selectedProducts: QueryList<any>, selectAllEl: ElementRef): Observable<ShoppingList> {
    return this.shoppingListService.removeProductFromList(event.listId, event.productCode).pipe(
      tap(() => {
        const shoppingList = this.shoppingList_.value;
        shoppingList.entries.splice(event.index, 1);
        this.shoppingList_.next({ ...shoppingList, calculated: false });
        this.shoppingListService.updateShoppingListState(shoppingList);

        if (this.checkAllSelected(selectedProducts) && shoppingList.entries.length < 1) {
          this.setAllSelectedCheckbox(false, selectAllEl);
        }
      }),
    );
  }

  setAllSelectedCheckbox(checked: boolean, selectAllEl: ElementRef): void {
    selectAllEl.nativeElement.checked = checked;
  }

  downloadProducts($event, name: string, type: string): void {
    $event.preventDefault();

    this.csvData = [];

    const currentDate = formatDate(Date.now(), 'YYYYMMddhhmmss', 'en-US');

    const options = {
      fieldSeparator: ',',
      quoteStrings: '"',
      decimalseparator: '.',
      showLabels: true,
      showTitle: true,
      title: `shoppingList_${name}`,
      useBom: true,
      noDownload: false,
      headers: [
        'Quantity',
        'Distrelec Article Number',
        'Reference',
        'Manufacturer',
        'Article Number',
        'Name',
        'Availability',
        'Stock',
        'Expired On',
        'unit 1',
        'price 1',
        'unit 2',
        'price 2',
        'unit 3',
        'price 3',
        'unit 4',
        'price 4',
        'unit 5',
        'price 5',
        'unit 6',
        'price 6',
      ],
      useHeader: false,
      nullToEmptyString: true,
    };

    this.shoppingList_.value.entries.map((entry) => {
      this.csvData.push({
        Quantity: entry?.desired,
        'Distrelec Article Number': entry?.product?.code,
        Reference: '',
        Manufacturer: entry?.product?.distManufacturer?.name,
        'Article Number': entry?.product?.typeName,
        Name: entry?.product?.name,
      });
    });

    if (type === 'csv') {
      new AngularCsv(this.csvData, `ShoppingList_${name}_${currentDate}`, options);
    } else {
      this.excelService.exportAsExcelFile(this.csvData, `ShoppingList_${name}_${currentDate}`);
    }
  }

  onAddedToCart(): void {
    this.addedToCartSignal.set(this.cartData.map((entry) => entry.productCode));
    this.selectProduct(false);

    if (!this.isPopupOpen) {
      this.breakpointService
        .isDown(BREAKPOINT.md)
        .pipe(take(1))
        .subscribe((val) => {
          if (val === true && this.phasedOutProducts_.value !== '') {
            this.openErrorPopup();
            this.isPopupOpen = true;
          }
        });
    }
  }

  shoppingListCart(): BulkProducts | undefined {
    if (this.cartData && this.cartData.length === 0) {
      this.selectProduct(true);
    }

    return this.cartData;
  }

  openErrorPopup(): void {
    const warningPopupRef = this.appendComponentService.appendComponent(ShoppingListWarningPopupComponent, {
      title: 'lightboxstatus.error.title',
      subtitle: 'shoppingList.shoppinglist_product_error_phaseout',
      subtitleArgument: this.phasedOutProducts_.value,
      type: 'warning',
    });

    warningPopupRef.instance.popupClosed.pipe(take(1)).subscribe(() => {
      this.appendComponentService.destroyComponent(warningPopupRef);
      this.phasedOutProducts_.next('');
      this.isPopupOpen = false;
    });
  }

  print(): void {
    this.winRef.nativeWindow.print();
  }

  setToFirstList(lists: ShoppingList[]): void {
    this.routingService
      .getRouterState()
      .pipe(take(1))
      .subscribe(() => {
        this.routingService.go([`/shopping/${lists[0]?.uniqueId}`], { replaceUrl: true });
        this.shoppingList_.next(lists[0]);
      });
  }

  calculate(listId: string): Observable<ShoppingList> {
    return this.shoppingListService.calculateShoppingList(listId).pipe(
      tap((calculatedList: ShoppingList) =>
        this.shoppingList_.next({
          ...this.shoppingList_.value,
          calculated: true,
          subTotal: calculatedList.subTotal,
          totalPrice: calculatedList.totalPrice,
          totalTax: calculatedList.totalTax,
        }),
      ),
    );
  }

  replaceComaInPricing(price: string): string {
    return this.shoppingListService.replaceComaInPricing(price);
  }

  onChangeList(id: string): Observable<ShoppingList> {
    return this.shoppingListService.getShoppingLisById(id).pipe(
      tap((list: ShoppingList) => {
        this.setShoppingListById(list);
        this.location.replaceState('/shopping/' + id);
      }),
    );
  }

  onSortByClick($event): void {
    this.shoppingList_.next({ ...this.shoppingList_.value, entries: $event });
  }

  setShoppingListById(shoppingList: ShoppingList): ShoppingList {
    this.shoppingList_.next(shoppingList);
    this.setProductsWithPricing(shoppingList);

    if (!shoppingList.calculated) {
      this.calculate(shoppingList.uniqueId);
    }

    return shoppingList;
  }

  resetIsTriggerViewItemListEventOnLoad(): void {
    this.isTriggerViewItemListEventOnLoad = true;
  }

  private setProductsWithPricing(shoppingList: ShoppingList): void {
    of(shoppingList)
      .pipe(
        switchMap((list: ShoppingList) => this.loadPrices(list.entries, list)),
        tap((updatedList: ShoppingList) => {
          this.shoppingList_.next(updatedList);
          this.shoppingListService.updateShoppingListState(updatedList);
          if (this.isTriggerViewItemListEventOnLoad) {
            this.eventService.dispatch(
              createFrom(ViewItemListEvent, {
                products: this.shoppingList_.value.entries.map((product) => ({
                  ...product.product,
                  volumePricesMap: product.priceObject.volumePricesMap,
                })),
                listType: ItemListEntity.SHOPPING,
              }),
            );
          }
          this.isTriggerViewItemListEventOnLoad = false;
        }),
      )
      .subscribe();
  }

  private loadPrices(entries: ShoppingListEntry[], list: ShoppingList): Observable<ShoppingList> {
    return from(entries).pipe(
      mergeMap((entry: any) =>
        this.priceService.getPrices(entry.product.code).pipe(
          first(),
          map((price) => ({ ...entry, priceObject: price })),
        ),
      ),
      toArray(),
      map((array) => ({ ...list, entries: array })),
    );
  }
}
