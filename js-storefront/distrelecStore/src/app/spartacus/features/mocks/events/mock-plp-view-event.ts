import { BloomreachPlpViewEvent } from '@features/tracking/events/bloomreach/bloomreach-plp-view-event';

/* eslint-disable @typescript-eslint/naming-convention */
export const MOCK_PLP_VIEW_EVENT: BloomreachPlpViewEvent = {
  type: 'plp_view',
  body: {
    plp_category_name: 'Search',
    plp_url: undefined,
    plp_id: '',
    plp_category_path: '',
    search_text: 'tester',
  },
};
