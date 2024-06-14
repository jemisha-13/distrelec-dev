import { OrderDetailsService } from '@spartacus/order/components';
import { DistOrderDetailsService } from '@features/pages/order/core/dist-order-details.service';
import { OrderService } from '@spartacus/order/core';
import { DistOrderService } from '@features/pages/order/core/dist-order.service';

export const orderProviders = [
  {
    provide: OrderDetailsService,
    useClass: DistOrderDetailsService,
  },
  {
    provide: OrderService,
    useClass: DistOrderService,
  },
];
