/* eslint-disable @typescript-eslint/naming-convention */
import { CxEvent } from '@spartacus/core';

export class SearchEvent extends CxEvent {
  search_term: string;
  search_category?: string;
}
