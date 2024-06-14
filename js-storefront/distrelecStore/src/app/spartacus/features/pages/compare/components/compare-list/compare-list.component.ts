import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { ProductAvailability } from '@model/product-availability.model';
import {
  createFrom,
  EventService,
  GlobalMessageService,
  GlobalMessageType,
  Product,
  Translatable,
} from '@spartacus/core';
import { ViewItemListEvent } from '@features/tracking/events/view-item-list-event';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { filter, first, map, mergeMap, switchMap, take, tap, toArray } from 'rxjs/operators';
import { from, Observable } from 'rxjs';
import { Prices } from '@model/price.model';
import { PriceService } from '@services/price.service';
import { CompareService } from '../../core';
import { CompareList } from '@model/compare.model';
import { Channel } from '@model/site-settings.model';

export interface AttributeItem {
  code: string;
  name: string;
}

@Component({
  selector: 'app-compare-list',
  templateUrl: './compare-list.component.html',
  styleUrls: ['./compare-list.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class CompareListComponent implements OnInit {
  @Input() compareList$: Observable<CompareList>;
  @Input() channel: Channel;

  compareProducts$: Observable<Product[]>;

  isEnergyLabel: boolean;

  attributes: AttributeItem[] = [];

  products$: Observable<Product[]>;

  availability$: Observable<ProductAvailability[]>;

  private commonAttributes: Map<string, AttributeItem> = new Map();
  private possibleOtherAttributes: Map<string, AttributeItem> = new Map();
  private otherAttributes: Map<string, AttributeItem> = new Map();

  constructor(
    private eventService: EventService,
    private priceService: PriceService,
    private compareService: CompareService,
    private globalMessageService: GlobalMessageService,
  ) {}

  ngOnInit(): void {
    this.compareProducts$ = this.compareList$.pipe(
      map((data) => this.mapThroughCompareProduct(data)),
      filter((compareList) => compareList.products.length > 0),
      switchMap((compareList) => this.returnProductsWithPrices(compareList.products)),
      tap((products) => {
        this.assignTechAttributes(products);
        this.getIsEnergyLabelPresent(products);
        this.eventService.dispatch(createFrom(ViewItemListEvent, { products, listType: ItemListEntity.COMPARE }));
      }),
    );
  }

  assignTechAttributes(products: Product[]): void {
    // iterate over each product attribute to create the total attributes array
    products.forEach((product) => {
      product.commonAttrs?.forEach((attribute) => {
        this.commonAttributes.set(attribute.code, { code: attribute.code, name: attribute.name });
      });
      product.otherAttributes?.forEach((attribute) => {
        this.otherAttributes.set(attribute.value.code, { code: attribute.value.code, name: attribute.value.name });
      });
      product.possibleOtherAttributes.entry?.forEach((attribute) => {
        this.possibleOtherAttributes.set(attribute.key, { code: attribute.key, name: attribute.value });
      });
    });

    const merged = new Map([...this.commonAttributes, ...this.otherAttributes, ...this.possibleOtherAttributes]);
    this.attributes = Array.from(merged.values());
  }

  getIsEnergyLabelPresent(products: Product[]): void {
    this.isEnergyLabel = products.some((product) => product.energyEfficiency);
  }

  mapThroughCompareProduct(data: CompareList): CompareList {
    const availableProducts = [];
    const unavailableProducts = [];
    const unavailableStatus = ['60', '61', '62', '90', '91', '92', '99'];

    data.products.forEach((product) => {
      // If product matches unavailable status'
      if (unavailableStatus.includes(product.salesStatus)) {
        unavailableProducts.push(product.code);
        // If product is unavailable for B2B and customer is a B2B customer
      } else if (!product.availableToB2B && this.channel === 'B2B') {
        unavailableProducts.push(product.code);
        // If product is unavailable for B2C and customer is a B2C customer
      } else if (!product.availableToB2C && this.channel === 'B2C') {
        unavailableProducts.push(product.code);
      } else {
        availableProducts.push(product);
      }
    });

    if (unavailableProducts.length > 0) {
      this.handleUnavailbleProductsInCompareList(unavailableProducts);
    }
    return { ...data, products: availableProducts };
  }

  handleUnavailbleProductsInCompareList(unavailableProductCodes: string[]): void {
    this.compareService.removeCompareProduct(unavailableProductCodes).subscribe();

    const msg: Translatable = {
      key: 'product.compare.punchout.error',
      params: { 0: unavailableProductCodes.join(', ') },
    };
    // This global error will be added multiple times so checking if global message array is empty first
    this.globalMessageService
      .get()
      .pipe(first())
      .subscribe((error) => {
        if (!error['[GlobalMessage] Error'] || error['[GlobalMessage] Error'].length === 0) {
          this.globalMessageService.add(msg, GlobalMessageType.MSG_TYPE_ERROR);
        }
        unavailableProductCodes.length = 0;
      });
  }

  returnProductsWithPrices(products: Product[]): Observable<Product[]> {
    return from(products).pipe(
      mergeMap((product) =>
        this.loadPrices(product.code).pipe(
          take(1),
          map((prices) => ({ ...product, ...prices })),
        ),
      ),
      toArray(),
    );
  }

  private loadPrices(productCode: string): Observable<Prices> {
    return this.priceService.getPrices(productCode);
  }
}
