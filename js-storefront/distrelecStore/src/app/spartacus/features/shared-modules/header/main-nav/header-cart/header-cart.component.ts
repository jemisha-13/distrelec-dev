import { Component, OnDestroy } from '@angular/core';
import { faAngleRight, faCartArrowDown } from '@fortawesome/free-solid-svg-icons';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { DistCartService, recentlyAddedLimit } from 'src/app/spartacus/services/cart.service';
import { UseWebpImage } from '@helpers/useWebpImage';
import { SiteConfigService } from '@services/siteConfig.service';
import { DefaultImageService } from '@services/default-image.service';
import { DistBreakpointService } from '@services/breakpoint.service';
import { SlideDrawerService } from '@design-system/slide-drawer/slide-drawer.service';
import { take } from 'rxjs/operators';
import { EventService, RoutingService, createFrom } from '@spartacus/core';
import { BREAKPOINT } from '@spartacus/storefront';
import { DistMiniCartComponentService } from './dist-mini-cart-component.service';
import { Cart, OrderEntry } from '@spartacus/cart/base/root';
import { HeaderInteractionEvent } from '@features/tracking/events/header-interaction-event';
import { Ga4HeaderInteractionEventType } from '@features/tracking/model/event-ga-header-types';

@Component({
  selector: 'app-header-cart',
  templateUrl: './header-cart.component.html',
  styleUrls: ['./header-cart.component.scss'],
})
export class HeaderCartComponent implements OnDestroy {
  static count = 0;
  productDisplaylimit = recentlyAddedLimit;
  cartData$: Observable<Partial<Cart>> = this.miniCartService.getMiniCartData();
  productsRecentlyAdded_: BehaviorSubject<OrderEntry[]> = this.cartService.productsRecentlyAdded_;

  faAngleRight = faAngleRight;
  faCartArrowDown = faCartArrowDown;
  useWebpImg = UseWebpImage;
  missingImgSrc = this.defaultImage.getDefaultImage();
  subscriptions: Subscription = new Subscription();

  headerCartHtmlId: string;

  constructor(
    private cartService: DistCartService,
    private miniCartService: DistMiniCartComponentService,
    private eventService: EventService,
    private defaultImage: DefaultImageService,
    private breakpointService: DistBreakpointService,
    private slideDrawerService: SlideDrawerService,
    private routingService: RoutingService,
  ) {
    this.headerCartHtmlId = `header_cart_link_${HeaderCartComponent.count}`;
    HeaderCartComponent.count++;
  }

  public cancelPanelCloseTimeout(event: Event): void {
    this.slideDrawerService.cancelPanelCloseTimeout(event);
  }

  public openPanel(event, id): void {
    this.breakpointService
      .isDown(BREAKPOINT.md)
      .pipe(take(1))
      .subscribe((val) => {
        if (val === true) {
          this.routingService.goByUrl('/cart');
          return;
        }

        event.preventDefault();
        this.slideDrawerService.openPanel(event, id);
      });
  }

  dispatchHeaderInteractionEvent(): void {
    this.eventService.dispatch(
      createFrom(HeaderInteractionEvent, {
        type: Ga4HeaderInteractionEventType.HEADER_CART_CLICK,
      }),
    );
  }

  public getMobileBreakpoint(): Observable<boolean> {
    return this.breakpointService.isMobileBreakpoint();
  }

  public ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
}
