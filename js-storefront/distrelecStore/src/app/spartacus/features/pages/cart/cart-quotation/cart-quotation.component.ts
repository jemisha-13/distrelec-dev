import { Component } from '@angular/core';
import { DistrelecBasesitesService } from '@services/basesites.service';
import { BehaviorSubject, combineLatest, Observable, of } from 'rxjs';
import { catchError, first, map, switchMap, take, tap } from 'rxjs/operators';
import { AppendComponentService } from 'src/app/spartacus/services/append-component.service';
import { DistCartService } from 'src/app/spartacus/services/cart.service';
import { DistrelecUserService } from '@services/user.service';
import { AuthService } from '@spartacus/core';
import { RequestQuote } from '@model/cart.model';
import { CustomerType } from '@model/site-settings.model';

@Component({
  selector: 'app-cart-quotation',
  templateUrl: './cart-quotation.component.html',
  styleUrls: ['./cart-quotation.component.scss'],
})
export class CartQuotationComponent {
  isLoading_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  calculateIsRequestQuotationVisible$: Observable<boolean> = combineLatest([
    this.authService.isUserLoggedIn(),
    this.baseSiteService.activeBaseStore$,
  ]).pipe(
    switchMap(([isLoggedIn, baseStore]) => {
      if (isLoggedIn) {
        return this.userService.getUserInformation().pipe(
          take(1),
          map((userDetails) => {
            const quotationsEnabled = baseStore.quotationsEnabled || userDetails?.erpSelectedCustomer;
            const isUserB2BType = userDetails?.customerType === CustomerType.B2B || userDetails?.customerType === CustomerType.B2B_KEY_ACCOUNT;
            return quotationsEnabled && userDetails?.requestQuotationPermission && isUserB2BType;
          }),
        );
      }
      return of(false);
    }),
  );

  constructor(
    private cartService: DistCartService,
    private appendComponentService: AppendComponentService,
    private baseSiteService: DistrelecBasesitesService,
    private userService: DistrelecUserService,
    private authService: AuthService,
  ) {}

  requestCartQuotation(): void {
    this.isLoading_.next(true);
    this.cartService
      .requestQuotation()
      .pipe(
        first(),
        tap((res: RequestQuote) => {
          if (res.code === 'SUCCESSFUL') {
            this.appendComponentService.appendSuccessPopup('cart.quotation_title', 'cart.quotation_subtitle');
          } else {
            if (res.status === 'LIMIT_EXCEEDED') {
              this.addWarningPopup(true);
            } else {
              this.addWarningPopup();
            }
          }
          this.isLoading_.next(false);
        }),
        catchError(() => of(this.addWarningPopup(), this.isLoading_.next(false))),
      )
      .subscribe();
  }

  addWarningPopup(limitMsg?: boolean): void {
    if (limitMsg) {
      this.appendComponentService.appendWarningPopup(
        null,
        'quote.limit.message.title',
        null,
        'quote.limit.message',
        'warning',
      );
    } else {
      this.appendComponentService.appendWarningPopup(
        null,
        'quote.resubmit.error.title',
        null,
        'quote.resubmit.error',
        'warning',
      );
    }
  }
}
