import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

const TRANSLATIONS = {
  'productDetails.quantity': 'Quantity',
  /* eslint-disable @typescript-eslint/naming-convention */
  'product.product_info.add_to_cart_btn': 'Add to Cart',
  'productDetails.addToCart': 'Add to Cart',
  'base.decrease-quantity': 'Decrease quantity',
  'base.increase-quantity': 'Increase quantity',
  'searchResults.moqMsg': 'Minimum order quantity for article 30158807 is 1 pcs',
  'validation.error.max.order.quantity.reached': 'Maximum order quantity reached',
};

@Injectable()
export class StorybookTranslationService {
  translate(key: string): Observable<string> {
    return of(TRANSLATIONS[key] ?? key);
  }
}
