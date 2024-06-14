import { AnalyticsCustomerType } from "@features/tracking/model/event-user-details";

export const MOCK_WISHLIST_EVENT = {
  event: "add_to_wishlist",
  ecommerce: {
    currency: "CHF",
    value: 50000,
    items: [
      {
        item_id: "30378718",
        item_name: "Notebook, MacBook Pro 2023, 14.2\" (36.1 cm), Apple M2 Max, 2.4GHz, 8TB SSD, 32GB LPDDR5, Silver",
        affiliation: "Distrelec Switzerland",
        index: 0,
        item_brand: "Apple",
        location_id: "30",
        price: 50000,
        quantity: 1,
        item_moq: 1,
        item_category: "Office, Computing & Network Products",
        item_category2: "PCs, Notebooks, Tablets & Servers",
        item_category3: "Notebooks & Accessories",
        item_category4: "Notebooks"
      }
    ]
  },
  user: {
    logged_in: false,
    language: "english",
    customer_type: AnalyticsCustomerType.B2C,
    mg: false,
  },
  page: { 
    document_title: 'RND 135-00207 | RND THT LED White 3000K 1.3cd 3mm Cylindrical | Distrelec Switzerland', 
    url: 'https://distrelec-ch.local:4200/en/tht-led-white-3000k-3cd-3mm-cylindrical-rnd-rnd-135-00207/p/30158802?itemList=cart', 
    category: 'pdp page', 
    market: 'CH' 
  }
}