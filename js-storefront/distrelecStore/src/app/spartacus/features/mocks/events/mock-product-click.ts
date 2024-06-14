import { ItemListEntity } from "@features/tracking/model/generic-event-types";

export const MOCK_PRODUCT_CLICK_DATA = {
  event: "productClick",
  product: {
    code: "30294964",
    name: "LED Driver, DALI Dimmable CV, 360W 30A 12V IP66",
    url: "/en/led-driver-dali-dimmable-cv-360w-30a-12v-ip66-rnd-rnd-500-00054/p/30294964",
    image: "https://pretest.media.distrelec.com/Web/WebShopImages/landscape_small/4-/01/30294964-01.jpg",
    articleNr: "30294964",
    typeName: "RND 500-00054",
    itemMin: "1",
    itemStep: "1",
    priceData: {
      price: "184.851",
      currency: "CHF"
    },
    energyEfficiency: "{}",
    energyEfficiencyLabelImageUrl: ""
  },
  listType: ItemListEntity.SUGGESTED_SEARCH,
  index: 2
}