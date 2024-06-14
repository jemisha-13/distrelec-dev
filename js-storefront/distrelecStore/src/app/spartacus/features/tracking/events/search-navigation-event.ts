import { NavigationEvent } from '@spartacus/storefront';

export class SearchNavigationEvent extends NavigationEvent {
  force: boolean; // Force PageView event to be created. Triggered after search results are loaded.
}
