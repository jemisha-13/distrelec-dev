import { Injectable } from '@angular/core';
import { RoutingService } from '@spartacus/core';
import { OrderDetailsService } from '@spartacus/order/components';
import { OrderHistoryFacade } from '@spartacus/order/root';

@Injectable({
  providedIn: 'root',
})
export class DistOrderDetailsService extends OrderDetailsService {
  constructor(
    // alias the name as it's private not protected in the superclass
    private orderHistoryService: OrderHistoryFacade,
    routingService: RoutingService,
  ) {
    super(orderHistoryService, routingService);
  }

  clearOrderDetails() {
    this.orderHistoryService.clearOrderDetails();
  }
}
