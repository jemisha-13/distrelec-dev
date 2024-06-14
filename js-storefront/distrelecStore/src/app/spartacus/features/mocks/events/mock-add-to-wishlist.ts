import { AddToShoppingListEvent } from "@features/tracking/events/add-to-shopping-list-event";

export const MOCK_WISHLIST_DATA: AddToShoppingListEvent = {
    event: "addToShoppingList",
    shoppingListEntries: [
      {
        addedDate: "2024-05-15T14:22:05+0000",
        desired: 1,
        product: {
          activePromotionLabels: [],
          alternativeAliasMPN: "",
          availableInSnapEda: false,
          availableToB2B: true,
          availableToB2C: true,
          baseOptions: [],
          batteryComplianceCode: "Y",
          breadcrumbs: [
            {
              code: "cat-L1D_379523",
              introText: "Distrelec offer a huge range of office and domestic computing supplies, with a large number of products in the following areas:",
              level: 1,
              name: "Office, Computing & Network Products",
              nameEN: "Office, Computing & Network Products",
              selected: false,
              seoMetaDescription: "Shop our range of Office, Computing & Network Products supplies & accessories at Distrelec. Free Next Day Delivery. Get the latest deals today.",
              seoMetaTitle: "Office, Computing & Network Products | $(siteName)",
              seoSections: [
                {
                  header: "Office, Computing & Network Products",
                  text: "• Printing: 2D & 3D printers including an array of ink supplies and paper <br/>• Visual entertainment: TVs, projectors, projection screens and supporting cables <br/>• Travel: GPS systems, both hand-held and car-mountable devices. We also feature travel packs, holders and charging cables <br/>• Software: CAD and graphics software, Microsoft applications and operating systems all make up the software section of our website <br/>• Photography: We have an assortment of compact and action cameras for capturing the best quality photos when outdoors on the go or projects that need that little extra precision <br/>• Computing: If you’re building or maintaining the hardware of your computer you need to check out our range of storage drives, power supplies and other computer components <br/>• Office furniture and supplies: Chairs, cleaning products, calculators and other office supplies can all be found here. When moving onto a new desk or renovating a new office you will always need those essential supplies <br/>• Organisation: Whether it's draw filing systems, transport storage or protecting casing, Distrelec have a wide variety of organisational products to suit your every need in keeping all your business or personal belongings in a need and tidy place."
                },
                {
                  header: "Why should you choose Distrelec for computing and peripherals?",
                  text: "Distrelec offer an extensive range of computing products. Our products are all made by leading brands in the industry and offer some of the highest-quality products at competitive prices. Whether it’s a simple mouse and keyboard set or a 3D printer, Distrelec are an experienced company with a strong team of technical experts who can provide you with the best products. Then once you have purchased your ideal computing device you can talk with a member of our team who will provide the best possible advice in installing and using your product."
                }
              ],
              url: "/office-computing-network-products/c/cat-L1D_379523"
            },
            {
              code: "cat-L3D_542666",
              introText: "Look no further for the finest PCs, notebooks & tablets. We can help to ensure that you stay connected wherever you are. Whether you need high-quality computer equipment for your business or domestic use, browse or top-quality collection today.",
              level: 2,
              name: "PCs, Notebooks, Tablets & Servers",
              nameEN: "PCs, Notebooks, Tablets & Servers",
              selected: false,
              seoMetaDescription: "Distrelec Switzerland stocks a wide range of PCs, Notebooks, Tablets & Servers. Next Day Delivery Available, Friendly Expert Advice & Over 180,000 products in stock.",
              seoMetaTitle: "PCs, Notebooks, Tablets & Servers | $(siteName)",
              seoSections: [
                {
                  text: "We offer a huge array of PCs, notebooks & tablets to suit a multitude of different needs. We have graphic tablets, tablets, notebooks and PCs from brands such as Hewlett Packard and Apple, along with a full range of servers and PC, notebook & tablet accessories. So whether you need an iPad or HP tablet, a Celeron notebook or MacBook or an Intel desktop PC, we are on hand to offer the best products at the most competitive prices.<br/><br/>Our computer hardware range reads like a who’s who of the best in the business and includes the likes of Dell, Fujitsu, Intel, Terra and Hewlett Packard. There are products to suit all budgets and applications and plenty of choice to cater for personal preferences as well. We always endeavour to offer our customers the choices they need and want, backed by our assurance that we only sell products that we would be happy to buy ourselves.<br/><br/>We have graphic tablets, notebooks and tablets from brands such as Acer, Apple, Wacom and Acer and offer a full range of sizes and specifications. When it comes to servers, our collection is packed with quality offerings, including a varied selection of servers and mini servers. If that were not enough, we are also a one-stop-shop for all of your accessory needs and stock everything from laptop bags to locks and mobile interface cards."
                }
              ],
              url: "/office-computing-network-products/pcs-notebooks-tablets-servers/c/cat-L3D_542666"
            },
            {
              code: "cat-L2-3D_3851686",
              introText: "Our range of notebooks & accessories includes a wide selection of portable laptops for users looking for a smaller, lightweight option for work or play. Notebook computers are typically extremely lightweight and suitable for use whilst travelling. They fit neatly into a bag and take up less room on your lap, making them ultra-portable, perfect for making documents anywhere. They usually come with a longer battery life than larger laptops meaning you'll need to top up your battery charge less often. Notebooks are generally preferred by students and frequent traveller professionals where portability is the top priority.",
              level: 3,
              name: "Notebooks & Accessories",
              nameEN: "Notebooks & Accessories",
              selected: false,
              seoMetaDescription: "Distrelec Switzerland stocks a wide range of Notebooks & Accessories. Next Day Delivery Available, Friendly Expert Advice & Over 180,000 products in stock.",
              seoMetaTitle: "Notebooks & Accessories | $(siteName)",
              seoSections: [
                {
                  text: "We also offer a range of accessories for notebook computers, including bags and adapters. Notebooks typically have a smaller form factor with fewer ports, so you may need adapters if you want to plug in a number of devices or set it up as a portable desktop solution. Our laptop locks with anti-theft cables offer greater security when you're travelling with your equipment, and our notebook bags are sized to fit smaller form factor computers."
                },
                {
                  text: "Our range of notebook replacement parts includes a selection of replacement netbook keyboards for those laptops that need some refurbishment. You can use the filters to find the correct manufacturer and model."
                }
              ],
              url: "/office-computing-network-products/pcs-notebooks-tablets-servers/notebooks-accessories/c/cat-L2-3D_3851686"
            },
            {
              code: "cat-DNAV_PL_190801",
              level: 4,
              name: "Notebooks",
              nameEN: "Notebooks",
              selected: false,
              seoMetaDescription: "Distrelec Switzerland stocks a wide range of Notebooks. Next Day Delivery Available, Friendly Expert Advice & Over 180,000 products in stock.",
              seoMetaTitle: "Notebooks | $(siteName)",
              seoSections: [],
              url: "/office-computing-network-products/pcs-notebooks-tablets-servers/notebooks-accessories/notebooks/c/cat-DNAV_PL_190801"
            }
          ],
          buyable: true,
          buyableReplacementProduct: false,
          categories: [
            {
              code: "cat-DNAV_PL_190801",
              level: 4,
              name: "Notebooks",
              nameEN: "Notebooks",
              selected: false,
              seoMetaDescription: "Distrelec Switzerland stocks a wide range of Notebooks. Next Day Delivery Available, Friendly Expert Advice & Over 180,000 products in stock.",
              seoMetaTitle: "Notebooks | $(siteName)",
              seoSections: [],
              url: "/office-computing-network-products/pcs-notebooks-tablets-servers/notebooks-accessories/notebooks/c/cat-DNAV_PL_190801"
            }
          ],
          code: "30378718",
          codeErpRelevant: "30378718",
          configurable: false,
          customsCode: "8471.3000",
          dimensions: "70 x 300 x 460 MM",
          distManufacturer: {
            code: "man_apl",
            name: "Apple",
            nameSeo: "apple",
            urlId: "/manufacturer/apple/man_apl",
            emailAddresses: [],
            image: [
              {
                key: "landscape_large",
                value: {
                  format: "landscape_large",
                  url: "https://pretest.media.distrelec.com/Web/WebShopImages/landscape_large/_e/ps/apple_logo_cmyk.jpg"
                }
              },
              {
                key: "landscape_medium",
                value: {
                  format: "landscape_medium",
                  url: "https://pretest.media.distrelec.com/Web/WebShopImages/landscape_medium/_e/ps/apple_logo_cmyk.jpg"
                }
              },
              {
                key: "landscape_small",
                value: {
                  format: "landscape_small",
                  url: "https://pretest.media.distrelec.com/Web/WebShopImages/landscape_small/_e/ps/apple_logo_cmyk.jpg"
                }
              },
              {
                key: "portrait_small",
                value: {
                  format: "portrait_small",
                  url: "https://pretest.media.distrelec.com/Web/WebShopImages/portrait_small/_e/ps/apple_logo_cmyk.jpg"
                }
              },
              {
                key: "brand_logo",
                value: {
                  format: "brand_logo",
                  url: "https://pretest.media.distrelec.com/Web/WebShopImages/manufacturer_logo/_e/ps/apple_logo_cmyk.jpg"
                }
              },
              {
                key: "portrait_medium",
                value: {
                  format: "portrait_medium",
                  url: "https://pretest.media.distrelec.com/Web/WebShopImages/portrait_medium/_e/ps/apple_logo_cmyk.jpg"
                }
              }
            ],
            phoneNumbers: [],
            productGroups: [],
            promotionText: "",
            seoMetaDescription: "Shop 1308 Apple products at ${siteName}. Next Day Delivery Available, Friendly Expert Advice & Over 180,000 products in stock.",
            seoMetaTitle: "Apple Distributor | ${siteName}",
            webDescription: "",
            websites: []
          },
          ean: "",
          elfaArticleNumber: "30378718",
          eligibleForReevoo: true,
          enumber: "",
          formattedSvhcReviewDate: "17/01/2023",
          grossWeight: 2420,
          grossWeightUnit: "Gram",
          hasSvhc: false,
          images: [
            {
              format: "landscape_small",
              imageType: "PRIMARY",
              url: "https://pretest.media.distrelec.com/Web/WebShopImages/landscape_small/8-/01/Apple-MPHH3D_A-30376978-01.jpg"
            },
            {
              format: "landscape_medium",
              imageType: "PRIMARY",
              url: "https://pretest.media.distrelec.com/Web/WebShopImages/landscape_medium/8-/01/Apple-MPHH3D_A-30376978-01.jpg"
            },
            {
              format: "landscape_large",
              imageType: "PRIMARY",
              url: "https://pretest.media.distrelec.com/Web/WebShopImages/landscape_large/8-/01/Apple-MPHH3D_A-30376978-01.jpg"
            },
            {
              format: "portrait_small",
              imageType: "PRIMARY",
              url: "https://pretest.media.distrelec.com/Web/WebShopImages/portrait_small/8-/01/Apple-MPHH3D_A-30376978-01.jpg"
            },
            {
              format: "portrait_medium",
              imageType: "PRIMARY",
              url: "https://pretest.media.distrelec.com/Web/WebShopImages/portrait_medium/8-/01/Apple-MPHH3D_A-30376978-01.jpg"
            }
          ],
          itemCategoryGroup: "BANC",
          movexArticleNumber: "558520",
          name: "Notebook, MacBook Pro 2023, 14.2\" (36.1 cm), Apple M2 Max, 2.4GHz, 8TB SSD, 32GB LPDDR5, Silver",
          nameEN: "Notebook, MacBook Pro 2023, 14.2\" (36.1 cm), Apple M2 Max, 2.4GHz, 8TB SSD, 32GB LPDDR5, Silver",
          navisionArticleNumber: "558520",
          orderQuantityMinimum: 1,
          orderQuantityStep: 1,
          productFamilyName: "Notebooks, MacBook Pro",
          productFamilyUrl: "/en/office-computing-network-products/pcs-notebooks-tablets-servers/notebooks-accessories/notebooks/c/cat-DNAV_PL_190801?q=*&filter_productFamilyCode=3256501",
          productImages: [
            {}
          ],
          purchasable: true,
          replacementReason: "",
          rohs: "2015/863/EU Conform",
          rohsCode: "15",
          salesStatus: "30",
          salesUnit: "piece",
          signalWord: "",
          stock: {
            isValueRounded: false,
            stockLevel: 0,
            stockLevelStatus: "inStock"
          },
          svhcReviewDate: "2023-01-17T00:00:00+0000",
          transportGroupData: {
            bulky: false,
            code: "1010",
            dangerous: true,
            nameErp: "Std / DG / No Calibration",
            relevantName: "Std / DG / No Calibration"
          },
          typeName: "Z17L-FR20",
          url: "/notebook-macbook-pro-2023-14-36-cm-apple-m2-max-4ghz-8tb-ssd-32gb-lpddr5-silver-apple-z17l-fr20/p/30378718"
        }
      }
    ]
  } as unknown as AddToShoppingListEvent;