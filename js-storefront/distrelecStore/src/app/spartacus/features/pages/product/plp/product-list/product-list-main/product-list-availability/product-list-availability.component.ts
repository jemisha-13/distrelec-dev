import { Component, Input, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { ProductAvailability } from '@model/product-availability.model';
import { ProductAvailabilityService } from '@features/pages/product/core/services/product-availability.service';
import { Observable, Subscription, tap } from 'rxjs';
import { tickCancelCircle, xInStock } from '@assets/icons';
import { DistIcon } from '@model/icon.model';

interface MessageConfig {
  message?: string;
  class?: string;
  icon?: DistIcon;
  stockTotal?: number;
}

@Component({
  selector: 'app-product-list-availability',
  templateUrl: './product-list-availability.component.html',
  styleUrls: ['./product-list-availability.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ProductListAvailabilityComponent implements OnInit, OnDestroy {
  @Input() product;

  availability$: Observable<ProductAvailability>;

  config?: MessageConfig;
  tickCancelCircle = tickCancelCircle;
  xInStock = xInStock;

  private subscription = new Subscription();

  constructor(private availabilityService: ProductAvailabilityService) {}

  ngOnInit(): void {
    this.availability$ = this.availabilityService.getAvailability(this.product.code).pipe(
      tap((availability) => {
        this.config = this.availabilityService.getConfig(this.product.salesStatus, availability);
      }),
    );
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
}
