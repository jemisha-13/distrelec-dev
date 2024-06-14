import { MiniCartComponentService } from '@spartacus/cart/base/components/mini-cart';
import { Injectable } from '@angular/core';
import { map, startWith, tap } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { CartStoreService } from '@state/cartState.service';
import { AuthService, EventService, SiteContextParamsService, StatePersistenceService } from '@spartacus/core';
import { ActiveCartFacade, Cart } from '@spartacus/cart/base/root';

@Injectable({
  providedIn: 'root',
})
export class DistMiniCartComponentService extends MiniCartComponentService {
  constructor(
    activeCartFacade: ActiveCartFacade,
    authService: AuthService,
    statePersistenceService: StatePersistenceService,
    siteContextParamsService: SiteContextParamsService,
    eventService: EventService,
    private cartStateService: CartStoreService,
  ) {
    super(activeCartFacade, authService, statePersistenceService, siteContextParamsService, eventService);
  }

  getMiniCartData(): Observable<Partial<Cart>> {
    return this.activeCartFacade.getActive().pipe(
      tap((cart) => {
        this.cartStateService.updateCartKeys(cart as Cart);
      }),
      startWith({ totalItems: 0, entries: [] }),
      map((cart) => cart as Partial<Cart>),
    );
  }
}
