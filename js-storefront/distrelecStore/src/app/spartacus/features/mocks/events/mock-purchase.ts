import { Ga4PurchaseEvent } from "@features/tracking/events/ga4/ga4-purchase-event";
import { PurchaseEvent } from "@features/tracking/events/purchase-event";
import { CheckoutGA4Type } from "@features/tracking/model/checkout-type";
import { AnalyticsCustomerType } from "@features/tracking/model/event-user-details";

export const MOCK_PURCHASE_EVENT: Ga4PurchaseEvent = {
    "event": "purchase",
    "ecommerce": {
        "currency": "CHF",
        "transaction_id": "s2b00008NRN",
        "value": 151.55,
        "shipping": 0,
        "tax": 11.35,
        "items": [
            {
                "item_id": "16950441",
                "item_name": "Spare Battery for Torches, 3.5Ah MagCharger",
                "affiliation": "Distrelec Switzerland",
                "index": 0,
                "item_brand": "Mag-Lite",
                "item_list_id": "suggested_search_list",
                "item_list_name": "Suggested Search List",
                "location_id": "30",
                "price": 140.2,
                "quantity": 1,
                "item_moq": 1,
                "item_category": "Lighting",
                "item_category2": "Lighting Accessories",
                "item_category3": "Miscellaneous Lighting Accessories"
            }
        ]
    },
    "user": {
        "logged_in": false,
        "language": "english",
        "customer_type": AnalyticsCustomerType.B2C,
        "guest_checkout": false,
        email: 'pretest_rada_b2b_ch@mail.com',
        "mg": false
    },
    page: { 
        document_title: 'RND 135-00207 | RND THT LED White 3000K 1.3cd 3mm Cylindrical | Distrelec Switzerland', 
        url: 'https://distrelec-ch.local:4200/en/tht-led-white-3000k-3cd-3mm-cylindrical-rnd-rnd-135-00207/p/30158802?itemList=cart', 
        category: 'pdp page', 
        market: 'CH' 
    },
    checkout_type: CheckoutGA4Type.REGULAR_CHECKOUT
  }

  export const MOCK_PURCHASE_DATA: PurchaseEvent = {
    orderData: {
      type: "orderWsDTO",
      billingAddress: {
        billingAddress: true,
        companyName: "Test Company",
        companyName2: "Test",
        contactAddress: false,
        country: {
          european: false,
          isocode: "CH",
          name: "Switzerland",
          nameEN: "Switzerland"
        },
        defaultAddress: false,
        defaultBilling: false,
        defaultShipping: false,
        email: "pretest_rada_b2b_ch@mail.com",
        erpAddressID: "0004662563",
        formattedAddress: "6 Woodgate close, Bredbury, asdfghjkl;, Test, 1234",
        id: "9336997117975",
        line1: "6 Woodgate close, Bredbury",
        line2: "asdfghjkl;",
        phone: "+41 21 234 56 78",
        phone1: "+41 21 234 56 78",
        postalCode: "1234",
        shippingAddress: true,
        title: "Mr",
        titleCode: "mr",
        town: "Test",
        visibleInAddressBook: true
      },
      calculated: false,
      code: "s2b00008NRN",
      deliveryAddress: {
        billingAddress: true,
        companyName: "Test Company",
        companyName2: "Test",
        contactAddress: false,
        country: {
          european: false,
          isocode: "CH",
          name: "Switzerland",
          nameEN: "Switzerland"
        },
        defaultAddress: false,
        defaultBilling: false,
        defaultShipping: false,
        email: "pretest_rada_b2b_ch@mail.com",
        erpAddressID: "0004662563",
        formattedAddress: "6 Woodgate close, Bredbury, asdfghjkl;, Test, 1234",
        id: "9336997150743",
        line1: "6 Woodgate close, Bredbury",
        line2: "asdfghjkl;",
        phone: "+41 21 234 56 78",
        phone1: "+41 21 234 56 78",
        postalCode: "1234",
        shippingAddress: true,
        title: "Mr",
        titleCode: "mr",
        town: "Test",
        visibleInAddressBook: true
      },
      deliveryCost: {
        currencyIso: "CHF",
        priceType: "BUY",
        value: 0
      },
      deliveryMode: {
        code: "SAP_N1",
        defaultDeliveryMode: true,
        description: "Delivery in 1-2 days.",
        name: "Standard",
        selectable: false,
        translation: "Standard (Delivery in 1-2 days)",
        translationKey: "deliverymode.standard"
      },
      entries: [
        {
          addedFrom: "suggested_search",
          alternateAvailable: false,
          alternateQuantity: 0,
          availabilities: [],
          backOrderProfitable: true,
          backOrderedQuantity: 0,
          baseListPrice: {
            currencyIso: "CHF",
            priceType: "BUY",
            value: 140.2
          },
          basePrice: {
            currencyIso: "CHF",
            priceType: "BUY",
            value: 140.2
          },
          bom: false,
          cancellableQuantity: 1,
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
                code: "cat-L2D_379658",
                introText: "At Distrelec our lighting range offers solutions for electricians in both domestic and commercial work spaces. Should you require anything from a common bulb or light switch, to LED light bars and Indicators, were confident our range can support you.",
                level: 1,
                name: "Lighting",
                nameEN: "Lighting",
                selected: false,
                seoMetaDescription: "Shop our range of Lighting supplies & accessories at Distrelec. Free Next Day Delivery. Get the latest deals on Lighting.",
                seoMetaTitle: "Lighting | Lightbulbs | Tubes | $(siteName)",
                seoSections: [
                  {
                    header: "Lighting",
                    text: "• Bulbs: Distrelec stock a wide range of bulbs that all conform to different lighting sockets. This includes screw and bayonet fixing types. We also offer a range of lighting types, including: fluorescent bulbs, halogen bulbs, incandescent bulbs, LED replacement bulbs and neon bulbs. <br/>• Indicators: Indicator lamps are used in an array of applications, including automotive and aircraft. Distrelec feature incandescent, LED and neon indicators all made by industry-leading manufacturers in lighting. <br/>• LED lighting: Distrelec have a variety of LED light bars, LED modules and LED strips, all of which use high-quality components to ensure you have the best LED lighting for your application - whether it be control panel indicators or custom lighting work for applications. <br/>• Portable lighting: Portable lighting comes in many forms, this can include: camping lanterns, head torches, hobby torches, key fob torches and laser points to name some. All of these lighting products are sold by Distrelec. We also have heavy duty torches in both battery-powered and rechargeable options."
                  },
                  {
                    header: "Accessories and fixtures",
                    text: "Alongside our range of excellent lighting options we also stock accessories and to ensure you have the exact lighting set up you require. Fixtures include: cabinet, desktop, LED flush-mounted fixtures, lighting strips, table feet and workplace lamps. <br/>Our accessories feature an array of LED electrical components such as: LED array holders, LED drivers AC / DC, LED heat sinks, LED lenses and LED reflectors. You can also find ballasts and starters, sockets, fittings and low voltage cable assemblies."
                  }
                ],
                url: "/lighting/c/cat-L2D_379658"
              },
              {
                code: "cat-DNAV_1004",
                introText: "Here at Distrelec, we stock an extensive collection of lighting accessories from highly regarded manufacturers such as Bailey Lights, LediL, MEAN WELL, Osram, and Vossloh Schwabe.",
                level: 2,
                name: "Lighting Accessories",
                nameEN: "Lighting Accessories",
                selected: false,
                seoMetaDescription: "Distrelec Switzerland stocks a wide range of Lighting Accessories. Next Day Delivery Available, Friendly Expert Advice & Over 180,000 products in stock.",
                seoMetaTitle: "Lighting Accessories | $(siteName)",
                seoSections: [
                  {
                    text: "Our selection of lighting accessories should make it easier to find the items you need for your projects. The accessories we stock include ballasts and starters, a wide range of LED accessories such as array holders and drivers, in addition to lamp sockets and fittings. These are available in a selection of specifications and materials to meet your individual project requirements."
                  }
                ],
                url: "/lighting/lighting-accessories/c/cat-DNAV_1004"
              },
              {
                code: "cat-DNAV_PL_110409",
                level: 3,
                name: "Miscellaneous Lighting Accessories",
                nameEN: "Miscellaneous Lighting Accessories",
                selected: false,
                seoMetaDescription: "Distrelec Switzerland stocks a wide range of Miscellaneous Lighting Accessories. Next Day Delivery Available, Friendly Expert Advice & Over 180,000 products in stock.",
                seoMetaTitle: "Miscellaneous Lighting Accessories | $(siteName)",
                seoSections: [],
                url: "/lighting/lighting-accessories/miscellaneous-lighting-accessories/c/cat-DNAV_PL_110409"
              }
            ],
            buyable: true,
            buyableReplacementProduct: false,
            categories: [
              {
                code: "cat-DNAV_PL_110409",
                level: 3,
                name: "Miscellaneous Lighting Accessories",
                nameEN: "Miscellaneous Lighting Accessories",
                selected: false,
                seoMetaDescription: "Distrelec Switzerland stocks a wide range of Miscellaneous Lighting Accessories. Next Day Delivery Available, Friendly Expert Advice & Over 180,000 products in stock.",
                seoMetaTitle: "Miscellaneous Lighting Accessories | $(siteName)",
                seoSections: [],
                url: "/lighting/lighting-accessories/miscellaneous-lighting-accessories/c/cat-DNAV_PL_110409"
              }
            ],
            code: "16950441",
            codeErpRelevant: "16950441",
            configurable: false,
            customsCode: "8507.5000",
            dimensions: "190 x 35 x 35 MM",
            distManufacturer: {
              code: "man_mgl",
              name: "Mag-Lite",
              nameSeo: "mag-lite",
              urlId: "/manufacturer/mag-lite/man_mgl",
              emailAddresses: [],
              image: [
                {
                  key: "landscape_large",
                  value: {
                    format: "landscape_large",
                    url: "https://pretest.media.distrelec.com/Web/WebShopImages/landscape_large/_t/if/maglite_logo_bitmap.jpg"
                  }
                },
                {
                  key: "landscape_medium",
                  value: {
                    format: "landscape_medium",
                    url: "https://pretest.media.distrelec.com/Web/WebShopImages/landscape_medium/_t/if/maglite_logo_bitmap.jpg"
                  }
                },
                {
                  key: "landscape_small",
                  value: {
                    format: "landscape_small",
                    url: "https://pretest.media.distrelec.com/Web/WebShopImages/landscape_small/_t/if/maglite_logo_bitmap.jpg"
                  }
                },
                {
                  key: "portrait_small",
                  value: {
                    format: "portrait_small",
                    url: "https://pretest.media.distrelec.com/Web/WebShopImages/portrait_small/_t/if/maglite_logo_bitmap.jpg"
                  }
                },
                {
                  key: "brand_logo",
                  value: {
                    format: "brand_logo",
                    url: "https://pretest.media.distrelec.com/Web/WebShopImages/manufacturer_logo/_t/if/maglite_logo_bitmap.jpg"
                  }
                },
                {
                  key: "portrait_medium",
                  value: {
                    format: "portrait_medium",
                    url: "https://pretest.media.distrelec.com/Web/WebShopImages/portrait_medium/_t/if/maglite_logo_bitmap.jpg"
                  }
                }
              ],
              phoneNumbers: [],
              productGroups: [],
              promotionText: "",
              seoMetaDescription: "Shop 110 Mag-Lite products at ${siteName}. Next Day Delivery Available, Friendly Expert Advice & Over 180,000 products in stock.",
              seoMetaTitle: "Mag-Lite Distributor | ${siteName}",
              webDescription: "",
              websites: []
            },
            ean: "38739088150",
            elfaArticleNumber: "6950441",
            eligibleForReevoo: true,
            enumber: "",
            formattedSvhcReviewDate: "",
            grossWeight: 445,
            grossWeightUnit: "Gram",
            images: [
              {
                format: "landscape_small",
                imageType: "PRIMARY",
                url: "https://pretest.media.distrelec.com/Web/WebShopImages/landscape_small/1-/01/Mag-Lite_ARXX235C_16950441-01.jpg"
              },
              {
                format: "landscape_medium",
                imageType: "PRIMARY",
                url: "https://pretest.media.distrelec.com/Web/WebShopImages/landscape_medium/1-/01/Mag-Lite_ARXX235C_16950441-01.jpg"
              },
              {
                format: "landscape_large",
                imageType: "PRIMARY",
                url: "https://pretest.media.distrelec.com/Web/WebShopImages/landscape_large/1-/01/Mag-Lite_ARXX235C_16950441-01.jpg"
              },
              {
                format: "portrait_small",
                imageType: "PRIMARY",
                url: "https://pretest.media.distrelec.com/Web/WebShopImages/portrait_small/1-/01/Mag-Lite_ARXX235C_16950441-01.jpg"
              },
              {
                format: "portrait_medium",
                imageType: "PRIMARY",
                url: "https://pretest.media.distrelec.com/Web/WebShopImages/portrait_medium/1-/01/Mag-Lite_ARXX235C_16950441-01.jpg"
              }
            ],
            itemCategoryGroup: "NORM",
            movexArticleNumber: "254311",
            name: "Spare Battery for Torches, 3.5Ah MagCharger",
            nameEN: "Spare Battery for Torches, 3.5Ah MagCharger",
            navisionArticleNumber: "254311",
            orderQuantityMinimum: 1,
            orderQuantityStep: 1,
            productFamilyName: "Accessories",
            productFamilyUrl: "/en/lighting-accessories-mag-lite/pf/3977028",
            productImages: [
              {}
            ],
            purchasable: true,
            replacementReason: "",
            rohs: "Not Applicable",
            rohsCode: "11",
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
            typeName: "ARXX235C",
            url: "/spare-battery-for-torches-5ah-magcharger-mag-lite-arxx235c/p/16950441",
            weeeCategory: "-",
            slug: "spare-battery-for-torches-3.5ah-magcharger",
            nameHtml: "Spare Battery for Torches, 3.5Ah MagCharger"
          },
          quantity: 1,
          returnableQuantity: 0,
          totalListPrice: {
            currencyIso: "CHF",
            priceType: "BUY",
            value: 140.2
          },
          totalPrice: {
            currencyIso: "CHF",
            priceType: "BUY",
            value: 140.2
          },
          orderCode: "s2b00008NRN",
          promotions: []
        }
      ],
      guid: "e2f202b3-2545-4e8a-a27f-c2e569604498",
      net: true,
      subTotal: {
        currencyIso: "CHF",
        priceType: "BUY",
        value: 140.2
      },
      totalItems: 1,
      totalPrice: {
        currencyIso: "CHF",
        priceType: "BUY",
        value: 151.55
      },
      totalPriceWithTax: {
        currencyIso: "CHF",
        priceType: "BUY",
        value: 162.9
      },
      totalTax: {
        currencyIso: "CHF",
        priceType: "BUY",
        value: 11.35
      },
      b2bCustomerData: {
        name: "Test Test",
        uid: "pretest_rada_b2b_ch@mail.com",
        approvers: [],
        billingAddress: {
          billingAddress: true,
          companyName: "Test Company",
          companyName2: "Test",
          contactAddress: false,
          country: {
            european: false,
            isocode: "CH",
            name: "Switzerland",
            nameEN: "Switzerland"
          },
          defaultAddress: false,
          defaultBilling: true,
          defaultShipping: true,
          email: "pretest_rada_b2b_ch@mail.com",
          erpAddressID: "0004662563",
          formattedAddress: "6 Woodgate close, Bredbury, asdfghjkl;, Test, 1234",
          id: "9336997085207",
          line1: "6 Woodgate close, Bredbury",
          line2: "asdfghjkl;",
          phone: "+41 21 234 56 78",
          phone1: "+41 21 234 56 78",
          postalCode: "1234",
          shippingAddress: true,
          title: "Mr",
          titleCode: "mr",
          town: "Test",
          visibleInAddressBook: true
        },
        companyName: "Test Company",
        consentConditionRequired: false,
        contactAddress: {
          billingAddress: false,
          contactAddress: true,
          country: {
            european: false,
            isocode: "CH",
            name: "Switzerland",
            nameEN: "Switzerland"
          },
          defaultAddress: false,
          defaultBilling: false,
          defaultShipping: false,
          email: "pretest_rada_b2b_ch@mail.com",
          firstName: "Test",
          formattedAddress: ", , ",
          id: "9336997052439",
          lastName: "Test",
          line1: "",
          phone: "+41212345678",
          phone1: "+41212345678",
          postalCode: "",
          shippingAddress: false,
          title: "Mr",
          titleCode: "mr",
          town: "",
          visibleInAddressBook: true
        },
        contactId: "0002356095",
        currency: {
          active: true,
          isocode: "CHF",
          name: "Swiss Franc",
          symbol: "CHF"
        },
        customerId: "75da5036-6941-43d4-a8d1-0563a677211a",
        customerType: "B2B",
        defaultAddress: {
          billingAddress: true,
          companyName: "Test Company",
          companyName2: "Test",
          contactAddress: false,
          country: {
            european: false,
            isocode: "CH",
            name: "Switzerland",
            nameEN: "Switzerland"
          },
          defaultAddress: false,
          defaultBilling: true,
          defaultShipping: true,
          email: "pretest_rada_b2b_ch@mail.com",
          erpAddressID: "0004662563",
          formattedAddress: "6 Woodgate close, Bredbury, asdfghjkl;, Test, 1234",
          id: "9336997085207",
          line1: "6 Woodgate close, Bredbury",
          line2: "asdfghjkl;",
          phone: "+41 21 234 56 78",
          phone1: "+41 21 234 56 78",
          postalCode: "1234",
          shippingAddress: true,
          title: "Mr",
          titleCode: "mr",
          town: "Test",
          visibleInAddressBook: true
        },
        displayUid: "pretest_rada_b2b_ch@mail.com",
        doubleOptinActivated: false,
        encryptedUserID: "8e383477827e6bb3280d02756432d45a0f6643580fabb5d984b8e7a7600a504fdc278d8ba428e84d",
        erpSelectedCustomer: false,
        firstName: "Test",
        language: {
          active: true,
          isocode: "en",
          name: "English",
          nativeName: "English",
          rank: 0
        },
        lastName: "Test",
        loginDisabled: false,
        newsletterPopup: false,
        orgUnit: {
          active: true,
          erpCustomerId: "0004662563",
          name: "Test Company",
          uid: "d9ad0d78-01b3-41dd-96bb-178708e7575c"
        },
        registeredAsGuest: false,
        requestQuotationPermission: false,
        roles: [
          "b2bapprovergroup",
          "b2bcustomergroup",
          "b2badmingroup"
        ],
        rsCustomer: false,
        title: "Mr",
        titleCode: "mr"
      },
      canRequestInvoicePaymentMode: false,
      cancellable: true,
      created: "2024-05-16T07:37:11+0000",
      customerType: "B2B",
      exceededBudgetPrice: {
        currencyIso: "CHF",
        priceType: "BUY",
        value: 0
      },
      guestCustomer: false,
      invoicePaymentModeRequested: false,
      openOrder: false,
      orderDate: "2024-05-16T07:37:11+0000",
      paymentCost: {
        currencyIso: "CHF",
        priceType: "BUY",
        value: 0
      },
      paymentMode: {
        code: "Z017_Invoice",
        creditCardPayment: false,
        hop: false,
        iframe: false,
        invoicePayment: true,
        name: "Invoice",
        selectable: true,
        translationKey: "payment.mode.invoice"
      },
      returnable: false,
      salesApplication: "Web",
      status: "CREATED"
    },
    isFastCheckout: false
  } as unknown as PurchaseEvent;