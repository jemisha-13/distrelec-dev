import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { DistCartService } from 'src/app/spartacus/services/cart.service';
import { FactFinderService } from '@features/pages/product/core/fact-finder/services/fact-finder.service';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { createFrom, EventService, Product } from '@spartacus/core';
import { ProductClickEvent } from '@features/tracking/events/ga4/product-click-event';
import { OrderEntry } from '@spartacus/cart/base/root';
import { UntypedFormGroup, UntypedFormControl } from '@angular/forms';

@Component({
  selector: 'app-cart-recommender',
  templateUrl: './cart-recommender.component.html',
  styleUrls: ['./cart-recommender.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class CartRecommenderComponent implements OnInit {
  @Input() currentChannel: any;
  @Input() cartEntries: OrderEntry[];

  isRenderRecommendations$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);
  recommendations$: any;
  itemListEntity = ItemListEntity;

  images = [];

  addToCartForm = new UntypedFormGroup({
    quantity: new UntypedFormControl(1),
  });

  constructor(
    private eventService: EventService,
    private factFinderService: FactFinderService,
    private cartService: DistCartService,
  ) {}

  ngOnInit() {
    if (this.cartEntries?.length > 0) {
      this.recommendations$ = this.factFinderService.getRecommendations(this.cartEntries, 8).pipe(
        tap((data) => {
          data.resultRecords.forEach((element: any) => {
            if (element.record?.AdditionalImageURLs) {
              this.images.push({ path: element.record.ImageURL });
              const prices = element.record?.Price.split('|');
              const pricesArray = [];
              prices.forEach((price) => {
                if (price) {
                  const priceDetailArray: any = price.split(';');
                  const priceDetail = priceDetailArray[2].split('=');
                  priceDetailArray.pop();
                  priceDetailArray.push({
                    minQuantity: priceDetail[0] === 'Min' ? element.record?.ItemsMin : priceDetail[0],
                    quantityPrice: priceDetail[1],
                  });
                  pricesArray.push({
                    currency: priceDetailArray[0],
                    priceType: priceDetailArray[1],
                    price: priceDetailArray[2],
                  });
                }
              });
              element.record.Price = pricesArray;
              this.addToCartForm.get('quantity').setValue(+element.record?.ItemsMin);
            }
          });
        }),
      );
    }

    // We initialise this observable to update this component when the cart is deleted asynchronously
    this.isRenderRecommendations$ = this.cartService.renderRecommendations$;
  }

  trackProductClick(product, index: number) {
    this.eventService.dispatch(
      createFrom(ProductClickEvent, {
        product: this.createProductObjectForEvent(product.record),
        listType: ItemListEntity.CART_RELATED,
        index,
      } as ProductClickEvent),
    );
  }

  createProductObjectForEvent(record): Product {
    return {
      categories: [
        record.Category1 ? { name: record.Category1, level: 1 } : {},
        record.Category2 ? { name: record.Category2, level: 2 } : {},
        record.Category3 ? { name: record.Category3, level: 3 } : {},
        record.Category4 ? { name: record.Category4, level: 4 } : {},
        record.Category5 ? { name: record.Category5, level: 5 } : {},
      ],
      distManufacturer: {
        name: record.Manufacturer,
      },
      salesStatus: record.salesStatus,
      orderQuantityMinimum: record.ItemsMin,
      price: {
        currencyIso: record.Price[0].currency,
        basePrice: this.getNetPriceFromFFStandardPrice(record.StandardPrice),
      },
      code: record.ProductNumber,
      name: record.Title,
    } as Product;
  }

  getNetPriceFromFFStandardPrice(standardPrice: string): number {
    return parseFloat(standardPrice.split('Net;')[1].split('=')[1].replace('|', ''));
  }
}
