import { Component, Input } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { faCheckCircle, faCircleNotch } from '@fortawesome/free-solid-svg-icons';
import { LocalStorageService } from '@services/local-storage.service';
import { UserIdService } from '@spartacus/core';
import { CartStoreService } from '@state/cartState.service';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { DistCartService } from 'src/app/spartacus/services/cart.service';
import { Cart } from '@spartacus/cart/base/root';

@Component({
  selector: 'app-cart-voucher',
  templateUrl: './cart-voucher.component.html',
  styleUrls: ['./cart-voucher.component.scss'],
})
export class CartVoucherComponent {
  @Input() cartForm: UntypedFormGroup;
  @Input() isVoucherError_: BehaviorSubject<boolean>;
  @Input() isVoucherSuccess_: BehaviorSubject<boolean>;
  @Input() erpVoucherInfoData: any;

  isVoucherCode: boolean;
  isVoucherLoading$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  faSync = faCircleNotch;
  faCheckCircle = faCheckCircle;

  userId$: Observable<string> = this.userIdService.getUserId();

  constructor(
    private cartService: DistCartService,
    private cartStoreService: CartStoreService,
    private router: Router,
    private localStorageService: LocalStorageService,
    private userIdService: UserIdService,
  ) {}

  validateVoucher(userId: string) {
    this.localStorageService.setItem('voucherCode', this.cartForm.get('voucherCode').value);

    if (userId !== 'current' && !this.cartStoreService.isCartUserGuest()) {
      this.router.navigate(['/login/checkout']);
    } else {
      this.isVoucherLoading$.next(true);
      this.cartService
        .validateVoucher(this.cartForm.get('voucherCode').value)
        .pipe(
          tap((data: Cart) => {
            this.cartStoreService.updateCartKeys(data);
            this.isVoucherError_.next(false);
            this.isVoucherSuccess_.next(true);
            this.isVoucherCode = false;
          }),
          catchError(() => {
            this.cartForm.patchValue({
              voucherCode: '',
            });
            return of(this.isVoucherError_.next(true));
          }),
          tap(() => this.localStorageService.removeItem('voucherCode')),
        )
        .subscribe(() => this.isVoucherLoading$.next(false));
    }
  }

  removeVoucher(): void {
    this.isVoucherLoading$.next(true);
    this.cartService
      .removeVoucher(this.erpVoucherInfoData.code)
      .pipe(
        tap((data: Cart) => {
          this.cartStoreService.updateCartKeys(data);
          this.cartStoreService.updateCartState('erpVoucherInfoData', null);
          this.isVoucherLoading$.next(false);
          this.cartForm.patchValue({
            voucherCode: '',
          });
          this.isVoucherCode = false;
          if (this.isVoucherSuccess_.getValue()) {
            this.isVoucherSuccess_.next(false);
          }
          this.localStorageService.removeItem('voucherCode');
        }),
      )
      .subscribe(() => this.isVoucherLoading$.next(false));
  }
}
