import { Injectable, OnDestroy } from '@angular/core';
import { EventService, LogoutEvent } from '@spartacus/core';
import { Subscription } from 'rxjs';
import { LocalStorageService } from '@services/local-storage.service';
import { FactFinderCartEvent } from '@features/tracking/events/fact-finder-cart-event';
import { FactFinderCheckoutEvent } from '@features/tracking/events/fact-finder-checkout-event';

const STORAGE_KEY = 'factFinder_productQueries';

@Injectable({
  providedIn: 'root',
})
export class FactFinderCartQueryService implements OnDestroy {
  private eventSubscriptions = new Subscription();

  constructor(
    private storage: LocalStorageService,
    private eventService: EventService,
  ) {
    this.bindClearProductQueries();
  }

  ngOnDestroy(): void {
    this.eventSubscriptions.unsubscribe();
  }

  clearProductQueries(): void {
    this.storage.removeItem(STORAGE_KEY);
  }

  trackProductQuery(event: FactFinderCartEvent): void {
    const { product, trackQuery } = event;

    if (trackQuery) {
      const productQueries = this.storage.getItem(STORAGE_KEY) ?? {};
      productQueries[product] = trackQuery;
      this.storage.setItem(STORAGE_KEY, productQueries);
    }
  }

  getProductQueries(): Record<string, string> {
    return this.storage.getItem(STORAGE_KEY) ?? {};
  }

  private bindClearProductQueries(): void {
    this.eventSubscriptions.add(this.eventService.get(LogoutEvent).subscribe(() => this.clearProductQueries()));
    this.eventSubscriptions.add(
      this.eventService.get(FactFinderCheckoutEvent).subscribe(() => this.clearProductQueries()),
    );
  }
}
