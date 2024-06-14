import { AnalyticsCustomerType } from '@features/tracking/model/event-user-details';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { PDFType } from '@features/tracking/model/pdf-types';

export const MOCK_DOWNLOAD_PDF_EVENT = {
  event: "download_PDF",
  user: {
    logged_in: false,
    language: "english",
    customer_type: AnalyticsCustomerType.B2C,
    mg: false
  },
  page: {
    document_title: "RND 135-00207 | RND THT LED White 3000K 1.3cd 3mm Cylindrical | Distrelec Switzerland",
    url: "https://distrelec-ch.local:4200/en/tht-led-white-3000k-3cd-3mm-cylindrical-rnd-rnd-135-00207/p/30158802?itemList=cart",
    category: "pdp page",
    market: "CH"
  },
  pageType: ItemListEntity.BACKORDER,
  PDF_type: PDFType.BACKORDER,
  ecommerce: {
    items: [
      {
        item_id: "30383883",
        item_name: "Industrial Ethernet Cable and Network Tester, LinkIQ, 10Gbps, RJ45 / USB-C",
        affiliation: "Distrelec Switzerland",
        index: 0,
        item_brand: "Fluke Networks",
        item_list_id: "backorder_list",
        item_list_name: "Backorder List",
        location_id: "30",
        quantity: 1,
        item_moq: 1,
        currency: undefined,
        item_category: "Test & Measurement",
        item_category2: "Network, Data & Communications",
        item_category3: "Network Analysers"
      }
    ]
  },
}
