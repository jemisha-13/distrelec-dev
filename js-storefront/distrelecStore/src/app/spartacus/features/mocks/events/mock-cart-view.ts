import { CartViewEvent } from "@features/tracking/events/view-event";

export const MOCK_CART_VIEW_EVENT: CartViewEvent = {
    cart: {
      type: "cartWsDTO",
      calculated: true,
      code: "s2b00008J51",
      deliveryCost: {
        currencyIso: "CHF",
        priceType: "BUY",
        value: 8
      },
      deliveryMode: {
        code: "SAP_N1",
        defaultDeliveryMode: false,
        deliveryCost: {
          currencyIso: "CHF",
          priceType: "BUY",
          value: 8
        },
        description: "Delivery in 1-2 days.",
        name: "Standard",
        selectable: false,
        shippingCost: {
          currencyIso: "CHF",
          formattedValue: "8.00",
          priceType: "BUY",
          value: 8
        },
        translation: "Standard (Delivery in 1-2 days)",
        translationKey: "deliverymode.standard"
      },
      entries: [
        {
          addedFrom: "suggested_search",
          alternateAvailable: false,
          alternateQuantity: 0,
          availabilities: [
            {
              estimatedDate: "2024-05-15T00:00:00+0000",
              quantity: 50
            }
          ],
          backOrderProfitable: true,
          backOrderedQuantity: 0,
          baseListPrice: {
            currencyIso: "CHF",
            priceType: "BUY",
            value: 0.109
          },
          basePrice: {
            currencyIso: "CHF",
            priceType: "BUY",
            value: 0.109
          },
          bom: false,
          cancellableQuantity: 0,
          deliveryQuantity: 0,
          dummyItem: false,
          entryNumber: 0,
          isBackOrder: false,
          isQuotation: false,
          mandatoryItem: false,
          mview: "F",
          pendingQuantity: 0,
          product: {
            alternativeAliasMPN: "",
            availableInSnapEda: false,
            baseOptions: [],
            batteryComplianceCode: "N",
            breadcrumbs: [
              {
                code: "cat-L2-3D_525341",
                introText: "Distrelec have a wide range of products when it comes to sourcing, detecting and controlling light. These electrical-to-optical and optical-to-electrical transducers and instruments will cover all your optronics needs.",
                level: 1,
                name: "Optoelectronics",
                nameEN: "Optoelectronics",
                selected: false,
                seoMetaDescription: "Shop our range of Optoelectronics supplies & accessories at Distrelec. Free Next Day Delivery. Get the latest deals on Optoelectronics.",
                seoMetaTitle: "Optoelectronics | Displays | LEDs | $(siteName)",
                seoSections: [
                  {
                    header: "Different types of display technology",
                    text: "• TFT (Thin Film Transistor) <br/>• LED (Light Emitting Diodes) <br/>• VFD (Vacuum Fluorescent Display) <br/>• OLED (Organic Light Emitting Diodes)"
                  },
                  {
                    header: "Optoelectronic technology available in our range:",
                    text: "• Fibre optics: transmitters, receiver, transceivers and connectors <br/>• Infrared: photodiodes, phototransistors, receivers, proximity and pyroelectric sensors, conversion cards, transceivers, emitters <br/>• Laser equipment: laser modules, laser diodes, lenses and optics for laser diodes as well as laser goggles <br/>• Photocouplers: IGBT/MOSFET output, transistor output, Darlington output, TRIAC output, AC input and high speed/logic optocouplers"
                  },
                  {
                    header: "LED technology",
                    text: "Our extensive range of LED technology covers everything including: COB LEDs, LED arrays, sub-miniature LEDs, LED testers, flashing LEDs, LEDs with series resistors, alternating current LEDs, IR LEDs, UV LEDs, LED clusters, LED light bars, light pipes, SMD LEDs, through hole LEDs, panel mounted LED indicators, PBC LED indicators and mounting materials for LEDs."
                  }
                ],
                url: "/optoelectronics/c/cat-L2-3D_525341"
              },
              {
                code: "cat-L3D_525297",
                introText: "LEDs - or light emitting diodes - are semiconductors that emit light when current flows through them and are a popular part of today’s electronic landscape.",
                level: 2,
                name: "LEDs",
                nameEN: "LEDs",
                selected: false,
                seoMetaDescription: "Distrelec Switzerland stocks a wide range of LEDs. Next Day Delivery Available, Friendly Expert Advice & Over 180,000 products in stock.",
                seoMetaTitle: "LEDs | $(siteName)",
                seoSections: [
                  {
                    text: "Naturally, when it comes to quality LEDs we offer a wide variety from some of the most trusted names in the business, such as Everlight Electronics, Osram Opto Semiconductors, Kingbright, Intelligent LED Solutions, and RND Components. When it comes to value and durability, our carefully selected range can be counted on.<br/><br/>We stock popular LED light bars in red and green, in different sizes with one or two segments, and if you need an IR LED, we have many in a range of categories. These are available in the standard plastic housing, and we also have a number of IR LED Array Boards in different sizes and handling different voltages. LED Array Boards can be useful in a range of applications, including systems for driver assistance, surveillance, machine vision and illumination for cameras. Our robust and durable models can deliver tens of thousands of hours of service, with up to 70 per cent of the original illumination. They have holes for ease of mounting, and can also be linked together in chains.<br/><br/>We stock multicoloured, single colour and white SMD LEDs, and LEDs with through holes or series resistors. For mounting your LEDs, we have a range of accessories, including reflectors, holders, clips, frames, and spacers so you have the tools to arrange your LED displays exactly as you need them."
                  },
                  {
                    text: "All the LEDs in our range are manufactured to the highest specifications and conform to industry standards. If you need more information about which LED product is best for your needs, please get in touch with one of our friendly and expert team members who will be happy to advise you."
                  }
                ],
                url: "/optoelectronics/leds/c/cat-L3D_525297"
              },
              {
                code: "cat-L2-3D_1914325",
                introText: "Light emitting diodes (LEDs) are all around us. They are used in everything from mobile phones to cars, streetlights, televisions, and much more. Many different types are available, including through hole LEDs.",
                level: 3,
                name: "LEDs - Through Hole",
                nameEN: "LEDs - Through Hole",
                selected: false,
                seoMetaDescription: "Distrelec Switzerland stocks a wide range of LEDs - Through Hole. Next Day Delivery Available, Friendly Expert Advice & Over 180,000 products in stock.",
                seoMetaTitle: "LEDs - Through Hole | $(siteName)",
                seoSections: [
                  {
                    text: "These are inserted through holes drilled into printed circuit boards and are particularly prevalent in applications that require switches, general status indication and icon backlighting. Here at Distrelec, we offer a vast range of these LEDs in a variety of sizes and colours.<br/><br/>We offer through hole LEDs in multicolour, single colour or white from trusted and quality brands including Kingbright, Everlight Electronics, and Vishay. There are also LED components from Distrelec’s own brand RND Components, which promises top-quality products at competitive prices. <br/><br/>Our multicolour through hole varieties provide red, green and yellow lights and come in bulb sizes of 3mm, 5mm and 10mm in diameter. Diffused, transparent, water clear and white diffused lens colours are available, along with an impressive selection of luminous intensities."
                  },
                  {
                    text: "Our single colour through hole models are available in blue, green, yellow, orange and red. These and our white options come in bulb sizes ranging from 1.8mm to 20mm. A vast array of lens shapes is available in both our single colour and white LEDs, including cylindrical, dome, round and parabolic. Peak wavelengths for these LEDs range from 428nm to an impressive 700nm. <br/><br/>Distrelec has 45 years of experience in the components industry. We are confident that we know what our customers want and can offer high-quality products. Whatever LEDs you need for your project, browse our impressive online collection and order today for prompt and reliable delivery."
                  }
                ],
                url: "/optoelectronics/leds/leds-through-hole/c/cat-L2-3D_1914325"
              },
              {
                code: "cat-DNAV_PL_2234782",
                level: 4,
                name: "Through Hole LEDs - White Colour",
                nameEN: "Through Hole LEDs - White Colour",
                selected: false,
                seoMetaDescription: "Distrelec Switzerland stocks a wide range of Through Hole LEDs - White Colour. Next Day Delivery Available, Friendly Expert Advice & Over 180,000 products in stock.",
                seoMetaTitle: "Through Hole LEDs - White Colour | $(siteName)",
                seoSections: [],
                url: "/optoelectronics/leds/leds-through-hole/through-hole-leds-white-colour/c/cat-DNAV_PL_2234782"
              }
            ],
            buyable: true,
            buyableReplacementProduct: false,
            categories: [
              {
                code: "cat-DNAV_PL_2234782",
                level: 4,
                name: "Through Hole LEDs - White Colour",
                nameEN: "Through Hole LEDs - White Colour",
                selected: false,
                seoMetaDescription: "Distrelec Switzerland stocks a wide range of Through Hole LEDs - White Colour. Next Day Delivery Available, Friendly Expert Advice & Over 180,000 products in stock.",
                seoMetaTitle: "Through Hole LEDs - White Colour | $(siteName)",
                seoSections: [],
                url: "/optoelectronics/leds/leds-through-hole/through-hole-leds-white-colour/c/cat-DNAV_PL_2234782"
              }
            ],
            code: "30158807",
            codeErpRelevant: "30158807",
            configurable: false,
            customsCode: "8541.4100",
            dimensions: "40 x 8 x 7 MM",
            distManufacturer: {
              code: "man_rnd",
              name: "RND",
              nameSeo: "rnd",
              urlId: "/manufacturer/rnd/man_rnd",
              emailAddresses: [],
              image: [
                {
                  key: "brand_logo",
                  value: {
                    format: "brand_logo",
                    url: "https://pretest.media.distrelec.com/Web/WebShopImages/manufacturer_logo/tm/ap/rnd_logo_bitmap.jpg"
                  }
                }
              ],
              phoneNumbers: [],
              productGroups: [],
              seoMetaDescription: "Shop 0 RND products at ${siteName}. Next Day Delivery Available, Friendly Expert Advice & Over 180,000 products in stock.",
              seoMetaTitle: "RND Distributor | ${siteName}",
              websites: []
            },
            ean: "",
            elfaArticleNumber: "30158807",
            eligibleForReevoo: false,
            enumber: "",
            formattedSvhcReviewDate: "",
            grossWeight: 0.035,
            grossWeightUnit: "Gram",
            hasSvhc: false,
            images: [
              {
                format: "landscape_small",
                imageType: "PRIMARY",
                url: "https://pretest.media.distrelec.com/Web/WebShopImages/landscape_small/7-/01/30158807-01.jpg"
              },
              {
                format: "landscape_medium",
                imageType: "PRIMARY",
                url: "https://pretest.media.distrelec.com/Web/WebShopImages/landscape_medium/7-/01/30158807-01.jpg"
              },
              {
                format: "landscape_large",
                imageType: "PRIMARY",
                url: "https://pretest.media.distrelec.com/Web/WebShopImages/landscape_large/7-/01/30158807-01.jpg"
              },
              {
                format: "portrait_small",
                imageType: "PRIMARY",
                url: "https://pretest.media.distrelec.com/Web/WebShopImages/portrait_small/7-/01/30158807-01.jpg"
              },
              {
                format: "portrait_medium",
                imageType: "PRIMARY",
                url: "https://pretest.media.distrelec.com/Web/WebShopImages/portrait_medium/7-/01/30158807-01.jpg"
              }
            ],
            itemCategoryGroup: "NORM",
            movexArticleNumber: "375808",
            name: "THT LED White 5500K 2.2cd 4.8mm Dome",
            nameEN: "THT LED White 5500K 2.2cd 4.8mm Dome",
            navisionArticleNumber: "375808",
            orderQuantityMinimum: 50,
            orderQuantityStep: 1,
            productFamilyName: "LEDs Round 3-10mm",
            productFamilyUrl: "/en/leds-round-10mm-rnd/pf/2245767",
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
            transportGroupData: {
              bulky: false,
              code: "1000",
              dangerous: false,
              nameErp: "Std / Non DG / No Calibration",
              relevantName: "Std / Non DG / No Calibration"
            },
            typeName: "RND 135-00212",
            url: "/tht-led-white-5500k-2cd-8mm-dome-rnd-rnd-135-00212/p/30158807",
            weeeCategory: "-"
          },
          quantity: 50,
          returnableQuantity: 0,
          totalListPrice: {
            currencyIso: "CHF",
            priceType: "BUY",
            value: 5.45
          },
          totalPrice: {
            currencyIso: "CHF",
            priceType: "BUY",
            value: 5.45
          }
        }
      ],
      guid: "3c0088b0-f5bb-462d-a2b1-bc693de0db89",
      subTotal: {
        currencyIso: "CHF",
        priceType: "BUY",
        value: 5.45
      },
      totalDiscounts: {
        currencyIso: "CHF",
        priceType: "BUY",
        value: 0
      },
      totalItems: 1,
      totalPrice: {
        currencyIso: "CHF",
        priceType: "BUY",
        value: 14.55
      },
      totalPriceWithTax: {
        currencyIso: "CHF",
        value: 14.55
      },
      totalTax: {
        currencyIso: "CHF",
        priceType: "BUY",
        value: 1.1
      },
      user: {
        name: "Anonymous",
        uid: "anonymous"
      },
      blockedProducts: "",
      completeDelivery: false,
      creditBlocked: false,
      endOfLifeProducts: [],
      movCurrency: "CHF",
      movLimit: 25,
      paymentMode: {
        code: "CreditCard",
        creditCardPayment: true,
        hop: true,
        icons: [
          {
            url: "https://pretest.media.distrelec.com/medias/LGO-Accept-MasterCard-min.png?context=bWFzdGVyfHJvb3R8MjU5N3xpbWFnZS9wbmd8aDMxL2hlOS84ODE1MjI5MDQyNzE4LnBuZ3xkYTcxNjdiMGU5MTBmZGMxMWE1ZDJjOTYxMGRmNTFiMzRkZWQ4ZGUwMmE1YzIwN2NlNjExZTBkODdhMTRhZDlk"
          },
          {
            url: "https://pretest.media.distrelec.com/medias/LGO-Accept-Visa-Online-darkBG-min.png?context=bWFzdGVyfHJvb3R8OTQxfGltYWdlL3BuZ3xoMzIvaGU5Lzg4MTUyMjkwNzU0ODYucG5nfDUxNTE5NmMwMjE0NmI4MTlhNDIxMjBmODU5MzMyMDg2MjhmY2Y3ODQwYzQ2ZDM2YTRmMTNiYzc3NDdhODdhYzg"
          }
        ],
        iframe: true,
        invoicePayment: false,
        name: "Credit Card",
        selectable: true,
        translationKey: "payment.mode.cc",
        url: "https://spg.evopayments.eu/pay/payssl.aspx"
      },
      phasedOutProducts: [],
      punchedOutProducts: "",
      reevooEligible: false,
      updatedMOQProducts: [],
      waldom: false
    }
  } as unknown as CartViewEvent;