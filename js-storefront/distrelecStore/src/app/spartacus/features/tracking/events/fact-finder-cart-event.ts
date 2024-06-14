import { CxEvent } from '@spartacus/core';
import { SearchEventPayload } from '../model/event-search';

export class FactFinderCartEvent extends CxEvent implements SearchEventPayload {
  static type = 'fact-finder-cart';
  event = 'cart';
  track: 'true';
  sid: string;
  trackQuery: string;
  pos: string;
  origPos: string;
  page: string;
  pageSize: string;
  origPageSize: string;
  quantity: string;
  product: string;
  prodprice: string;
  cart: string;
  pageType?: string;
  userId: string;
  filterapplied?: string;
}
