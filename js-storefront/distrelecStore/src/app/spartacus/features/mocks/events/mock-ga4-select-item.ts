import { AnalyticsCustomerType } from "@features/tracking/model/event-user-details";

export const MOCK_GA4_SELECT_ITEM = {
    event: 'select_item',
    ecommerce: {
      item_list_id: "suggested_search_list",
      item_list_name: "Suggested Search List",
      currency: "CHF",
      items: [
          {
            item_id: "30294964",
            item_name: "LED Driver, DALI Dimmable CV, 360W 30A 12V IP66",
            affiliation: "Distrelec Switzerland",
            index: 2,
            item_list_id: "suggested_search_list",
            item_list_name: "Suggested Search List",
            price: 184.851,
            quantity: 1,
            item_moq: 1,
        }
      ]
    },
    user: {
      logged_in: false,
      language: 'english',
      customer_type: AnalyticsCustomerType.B2C,
      mg: false
    },
    page: { 
      document_title: 'RND 135-00207 | RND THT LED White 3000K 1.3cd 3mm Cylindrical | Distrelec Switzerland', 
      url: 'https://distrelec-ch.local:4200/en/tht-led-white-3000k-3cd-3mm-cylindrical-rnd-rnd-135-00207/p/30158802?itemList=cart', 
      category: 'pdp page', 
      market: 'CH', 
    },
    filterPosition: 'sidebar',
    viewPreference: 'compact'
  }