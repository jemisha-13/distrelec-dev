import { Component, Input, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { faInfoCircle } from '@fortawesome/free-solid-svg-icons';
import { DistCartService } from '@services/cart.service';
import { Observable, Subscription } from 'rxjs';
import { CountryService } from 'src/app/spartacus/site-context/services/country.service';
import { DistBreakpointService } from '@services/breakpoint.service';
import { Cart } from '@spartacus/cart/base/root';

@Component({
  selector: 'app-cart-reevoo-checkbox',
  templateUrl: './cart-reevoo-checkbox.component.html',
  styleUrls: ['./cart-reevoo-checkbox.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class CartReevooCheckboxComponent implements OnInit, OnDestroy {
  @Input() cartData: Cart;
  faInfoCircle = faInfoCircle;
  isMobileBreakpoint$: Observable<boolean> = this.breakpointService.isMobileBreakpoint();
  activeCountryCode$: Observable<string> = this.countryService.getActive();
  private cartReevooSubscription = new Subscription();

  constructor(
    private countryService: CountryService,
    private cartService: DistCartService,
    private breakpointService: DistBreakpointService,
  ) {}

  ngOnInit() {
    this.cartReevooSubscription.add(this.isMobileBreakpoint$.subscribe());
  }

  ngOnDestroy() {
    this.cartReevooSubscription.unsubscribe();
  }

  handleChange(): void {
    this.cartService.setReevooEligible(!this.cartData.reevooEligible).subscribe();
  }
}
