import { Component, EventEmitter, Output } from '@angular/core';
import { EventService, OCC_USER_ID_ANONYMOUS, WindowRef, createFrom } from '@spartacus/core';
import { Observable, Subscription } from 'rxjs';
import { DistCartService } from 'src/app/spartacus/services/cart.service';
import { LoginService } from 'src/app/spartacus/services/login.service';
import { CheckoutService } from '@services/checkout.service';
import { LoginServiceHelper } from '@helpers/login-helpers';
import { CartComponentService } from '@features/pages/cart/cart-component.service';
import { DistLogoutService } from '@services/logout.service';
import { HeaderLoginService, UserType } from './header-login.service';
import { SlideDrawerService } from '@design-system/slide-drawer/slide-drawer.service';
import { DistBreakpointService } from '@services/breakpoint.service';
import { CompareService } from '@services/feature-services';
import { HeaderInteractionEvent } from '@features/tracking/events/header-interaction-event';
import { Ga4HeaderInteractionEventType } from '@features/tracking/model/event-ga-header-types';

@Component({
  selector: 'app-header-login',
  templateUrl: './header-login.component.html',
  styleUrls: ['./header-login.component.scss', './header-login-modal.scss'],
  providers: [HeaderLoginService],
})
export class HeaderLoginComponent {
  static count = 0;

  @Output() sendEventToParent = new EventEmitter<any>();

  isBrowser: boolean;
  userType$: Observable<UserType> = this.headerLoginService.userType$;
  isMobileBreakpoint$: Observable<boolean> = this.breakpointService.isMobileBreakpoint();
  subscription: Subscription = new Subscription();
  headerSignInHtmlId: string;
  headerCurrentHtmlId: string;
  headerGuestHtmlId: string;
  loginRegisterDrawerUid: string;

  constructor(
    private cartService: DistCartService,
    private cartComponentService: CartComponentService,
    private checkoutService: CheckoutService,
    private breakpointService: DistBreakpointService,
    private winRef: WindowRef,
    private loginServiceHelper: LoginServiceHelper,
    private loginService: LoginService,
    private logoutService: DistLogoutService,
    private headerLoginService: HeaderLoginService,
    private slideDrawerService: SlideDrawerService,
    private eventService: EventService,
  ) {
    this.loginServiceHelper.checkIfCaptchaDisabled(this.loginService.isCaptchaDisabled_);
    this.isBrowser = this.winRef.isBrowser();

    this.headerSignInHtmlId = `header-signin-label-${HeaderLoginComponent.count}`;
    this.headerCurrentHtmlId = `header-account-label-${HeaderLoginComponent.count}`;
    this.headerGuestHtmlId = `header-guest-label-${HeaderLoginComponent.count}`;
    this.loginRegisterDrawerUid = `login_register_${HeaderLoginComponent.count}`;
    HeaderLoginComponent.count++;
  }

  onGuestLogout(): void {
    this.cartService
      .deleteCart()
      .subscribe(() => {
        this.logoutService.logoutGuestUser();
        this.cartComponentService.resetErrorMessages();
        this.checkoutService.clearCheckoutData();
      })
      .unsubscribe();

    this.headerLoginService.setUserIdAnonymous();
    this.closePanel();
  }

  openPanel(event, id, userType): void {
    event.stopPropagation();
    if (userType !== 'guest') {
      this.slideDrawerService.openPanel(event, id);
      this.dispatchHeaderInteractionEvent(userType);
    } else {
      this.onGuestLogout();
    }
  }

  closePanel(): void {
    this.slideDrawerService.closePanel();
  }

  handleLogout() {
    this.closePanel();
    this.logoutService.postLogoutRequest();
  }

  dispatchHeaderInteractionEvent(userType: UserType): void {
    if (userType === OCC_USER_ID_ANONYMOUS) {
      this.eventService.dispatch(
        createFrom(HeaderInteractionEvent, {
          type: Ga4HeaderInteractionEventType.HEADER_SIGN_IN,
        }),
      );
    } else {
      this.eventService.dispatch(
        createFrom(HeaderInteractionEvent, {
          type: Ga4HeaderInteractionEventType.HEADER_MY_ACCOUNT_INTERACTION,
        }),
      );
    }
  }

  public sliderTitle(userType): string {
    return userType === 'anonymous' ? 'login.login_register' : 'form.my_account';
  }
}
