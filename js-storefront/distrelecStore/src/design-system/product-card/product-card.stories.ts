/* eslint-disable @typescript-eslint/naming-convention */
import { applicationConfig, Meta, moduleMetadata, Story } from '@storybook/angular';
import { DistProductCardComponent } from './product-card.component';
import { CommonModule } from '@angular/common';
import { DistButtonComponentModule } from '@design-system/button/button.module';
import { TranslationService } from '@spartacus/core';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { RouterTestingModule } from '@angular/router/testing';
import { SinglePriceComponentModule } from '@design-system/single-price/single-price.module';
import { ProductNumberModule } from '@design-system/product-number/product-number.module';
import { DistLabelComponentModule } from '@design-system/label/label.module';
import { TooltipComponentModule } from '@design-system/tooltip/tooltip.module';
import { importProvidersFrom } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { ArticleNumberPipeModule } from '@pipes/article-number-pipe.module';
import { MinOrderQuantityPopupModule } from '@features/shared-modules/components/min-order-quantity-popup/min-order-quantity-popup.module';
import { StorybookTranslationService } from '@design-system/services/storybook-translation.service';
import { RouterModule } from '@angular/router';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import MockAllsitesettingsService from '@features/mocks/mock-all-site-settings.service';
import { NumericStepperComponentModule } from '@features/shared-modules/components/numeric-stepper/numeric-stepper.module';

export default {
  title: 'Product Card',
  component: DistProductCardComponent,
  tags: ['autodoc'],
  argTypes: {
    productCode: {
      control: {
        type: 'text',
      },
    },
    orientation: {
      control: {
        type: 'text',
      },
    },
    snippet: {
      control: {
        type: 'text',
      },
    },
    isTitle: {
      control: {
        type: 'text',
      },
    },
    isImage: {
      control: {
        type: 'text',
      },
    },
    customTitle: {
      control: {
        type: 'text',
      },
    },
    buttonType: {
      control: {
        type: 'text',
      },
    },
    productData: {
      control: {
        type: 'object',
      },
    },
    customDescription: {
      control: {
        type: 'text',
      },
    },
    isPrice: {
      control: {
        type: 'text',
      },
    },
    priceData: {
      control: {
        type: 'object',
      },
    },
    channelData: {
      control: {
        type: 'object',
      },
    },
    channel: {
      control: {
        type: 'text',
      },
    },
    isArticle: {
      control: {
        type: 'text',
      },
    },
    topDisplay: {
      control: {
        type: 'text',
      },
    },
    labelDisplay: {
      control: {
        type: 'text',
      },
    },
    brandLogo: {
      control: {
        type: 'text',
      },
    },
    brandAlternateText: {
      control: {
        type: 'text',
      },
    },
    addToCartText: {
      control: {
        type: 'text',
      },
    },
    excVatText: {
      control: {
        type: 'text',
      },
    },
    incVatText: {
      control: {
        type: 'text',
      },
    },
    artNrText: {
      control: {
        type: 'text',
      },
    },
    mpnText: {
      control: {
        type: 'text',
      },
    },
    copiedText: {
      control: {
        type: 'boolean',
      },
    },
  },
  args: {
    productCode: '15461058',
    orientation: 'PORTRAIT',
    snippet: 'true',
    isTitle: 'true',
    isImage: 'true',
    isPrice: 'true',
    customTitle: 'This is a custom title',
    buttonType: 'ADD_TO_CART',
    channelData: {
      channel: 'B2B',
    },
    productData: {
      code: '15461058',
      activePromotionLabels: [
        {
          code: 'noMover',
          label: 'Bundle',
          nameEN: 'Bundle',
          priority: 1,
          rank: 1,
        },
      ],
      allowBulk: false,
      alternativeAliasMPN: '',
      availableInSnapEda: false,
      availableToB2B: true,
      availableToB2C: true,
      baseOptions: [],
      batteryComplianceCode: 'N',
      businessOnlyProduct: false,
      buyable: true,
      buyablePhaseoutProduct: false,
      buyableReplacementProduct: false,
      codeErpRelevant: '15461058',
      configurable: false,
      countryOfOrigin: {
        european: true,
        isocode: 'DE',
        name: 'Germany',
        nameEN: 'Germany',
      },
      customsCode: '8505.9092',
      description: 'Product Name: Holding Solenoid',
      dimensions: '30 x 51 x 51 MM',
      distManufacturer: {
        code: 'man_kuh',
        name: 'Kuhnke333',
        nameSeo: 'kuhnke',
        urlId: '/manufacturer/kuhnke/man_kuh',
        emailAddresses: [],
        image: [
          {
            key: 'landscape_large',
            value: {
              format: 'landscape_large',
              url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_large/20/16/kuhnke_logo_RGB_2016.jpg',
            },
          },
          {
            key: 'landscape_medium',
            value: {
              format: 'landscape_medium',
              url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_medium/20/16/kuhnke_logo_RGB_2016.jpg',
            },
          },
          {
            key: 'landscape_small',
            value: {
              format: 'landscape_small',
              url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_small/20/16/kuhnke_logo_RGB_2016.jpg',
            },
          },
          {
            key: 'portrait_small',
            value: {
              format: 'portrait_small',
              url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_small/20/16/kuhnke_logo_RGB_2016.jpg',
            },
          },
          {
            key: 'brand_logo',
            value: {
              format: 'brand_logo',
              url: 'https://pretest.media.distrelec.com/Web/WebShopImages/manufacturer_logo/20/16/kuhnke_logo_RGB_2016.jpg',
            },
          },
          {
            key: 'portrait_medium',
            value: {
              format: 'portrait_medium',
              url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_medium/20/16/kuhnke_logo_RGB_2016.jpg',
            },
          },
        ],
        phoneNumbers: [],
        productGroups: [],
        promotionText: '',
        seoMetaDescription:
          'Shop 82 Kuhnke products at ${siteName}. Next Day Delivery Available, Friendly Expert Advice & Over 180,000 products in stock.',
        seoMetaTitle: 'Kuhnke Distributor | ${siteName}',
        webDescription: '',
        websites: [],
      },
      ean: '4030727312267',
      elfaArticleNumber: '5461058',
      eligibleForReevoo: true,
      enumber: '',
      formattedSvhcReviewDate: '',
      grossWeight: 317,
      grossWeightUnit: 'Gram',
      hasSvhc: false,
      hazardStatements: [],
      images: [
        {
          format: 'landscape_small',
          imageType: 'PRIMARY',
          url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_small/19/3f/kuhnke-ht-d50-f-24v100-.jpg',
        },
        {
          format: 'landscape_medium',
          imageType: 'PRIMARY',
          url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_medium/19/3f/kuhnke-ht-d50-f-24v100-.jpg',
        },
        {
          format: 'landscape_large',
          imageType: 'PRIMARY',
          url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_large/19/3f/kuhnke-ht-d50-f-24v100-.jpg',
        },
        {
          format: 'portrait_small',
          imageType: 'PRIMARY',
          url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_small/19/3f/kuhnke-ht-d50-f-24v100-.jpg',
        },
        {
          format: 'portrait_medium',
          imageType: 'PRIMARY',
          url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_medium/19/3f/kuhnke-ht-d50-f-24v100-.jpg',
        },
        {
          format: 'landscape_small',
          imageType: 'GALLERY',
          url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_small/_t/if/kuhnke-ht-d50-f-24v100-01.jpg',
        },
        {
          format: 'landscape_medium',
          imageType: 'GALLERY',
          url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_medium/_t/if/kuhnke-ht-d50-f-24v100-01.jpg',
        },
        {
          format: 'landscape_large',
          imageType: 'GALLERY',
          url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_large/_t/if/kuhnke-ht-d50-f-24v100-01.jpg',
        },
        {
          format: 'portrait_small',
          imageType: 'GALLERY',
          url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_small/_t/if/kuhnke-ht-d50-f-24v100-01.jpg',
        },
        {
          format: 'portrait_medium',
          imageType: 'GALLERY',
          url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_medium/_t/if/kuhnke-ht-d50-f-24v100-01.jpg',
        },
        {
          format: 'landscape_small',
          imageType: 'GALLERY',
          url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_small/-d/50/kuhnke-ht-d50-f-24v100-02.jpg',
        },
        {
          format: 'landscape_medium',
          imageType: 'GALLERY',
          url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_medium/-d/50/kuhnke-ht-d50-f-24v100-02.jpg',
        },
        {
          format: 'landscape_large',
          imageType: 'GALLERY',
          url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_large/-d/50/kuhnke-ht-d50-f-24v100-02.jpg',
        },
        {
          format: 'portrait_small',
          imageType: 'GALLERY',
          url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_small/-d/50/kuhnke-ht-d50-f-24v100-02.jpg',
        },
        {
          format: 'portrait_medium',
          imageType: 'GALLERY',
          url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_medium/-d/50/kuhnke-ht-d50-f-24v100-02.jpg',
        },
      ],
      images360: [],
      inShoppingList: false,
      isDangerousGoods: false,
      isProductBatteryCompliant: false,
      isRNDProduct: false,
      isROHSComplaint: false,
      isROHSConform: false,
      isROHSValidForCountry: false,
      itemCategoryGroup: 'NORM',
      movexArticleNumber: '378193',
      name: 'Holding Solenoid 750N 11W',
      nameEN: 'Holding Solenoid 750N 11W',
      navisionArticleNumber: '378193',
      numberOfReviews: 0,
      orderQuantityMinimum: 1,
      orderQuantityStep: 1,
      productFamilyName: 'Holding Solenoids, HTD',
      productFamilyUrl: '/en/holding-solenoids-htd/pf/357662',
      productInformation: {
        articleDescription: [],
        articleDescriptionBullets: [],
        familyDescription: ['Test'],
        familyDescriptionBullets: ['100 % operating time with air cooling', 'In cylindrical design'],
        paperCatalogPageNumber_16_17: '1399',
        seriesDescription: [],
        seriesDescriptionBullets: [],
        usageNote: [],
      },
      purchasable: true,
      replacementReason: '',
      rohs: 'Under review',
      rohsCode: '99',
      salesStatus: '30',
      salesUnit: 'piece',
      signalWord: '',
      stock: {
        isValueRounded: false,
        stockLevel: 0,
        stockLevelStatus: 'inStock',
      },
      summary: '',
      supplementalHazardInfos: [],
      technicalAttributes: [
        {
          key: 'Solenoid Type',
          value: 'Cylindrical Design',
        },
        {
          key: 'Retention Force',
          value: '750 N',
        },
        {
          key: 'Holding Magnet Diameter',
          value: '50 mm',
        },
        {
          key: 'Rated Voltage',
          value: '24 VDC',
        },
        {
          key: 'Rated Power',
          value: '11 W',
        },
        {
          key: 'Time of Application',
          value: '100 %',
        },
        {
          key: 'Termination Type',
          value: 'Flying Leads, 200 mm',
        },
        {
          key: 'Height',
          value: '30 mm',
        },
      ],
      transportGroupData: {
        bulky: false,
        code: '1000',
        dangerous: false,
        nameErp: 'Std / Non DG / No Calibration',
        relevantName: 'Std / Non DG / No Calibration',
      },
      typeName: 'HT -D 50-F   -      - 24VDC 100%ED',
      unspsc5: '46171608',
      url: '/holding-solenoid-750n-11w-kuhnke-ht-50-24vdc-100-ed/p/15461058',
      slug: 'holding-solenoid-750n-11w',
      nameHtml: 'Holding Solenoid 750N 11W',
      topLabel: {
        code: 'noMover',
        label: 'Bundle',
        nameEN: 'Bundle',
        priority: 1,
        rank: 1,
      },
    },
    priceData: {
      inShoppingList: false,
      price: {
        basePrice: 5,
        currencyIso: 'CHF',
        formattedPriceWithVat: '5.39',
        minQuantity: 1,
        priceType: 'BUY',
        priceWithVat: 5.385,
        value: 5,
        vatPercentage: 7.7,
        vatValue: 0.385,
      },
      volumePrices: [],
      volumePricesMap: [
        {
          key: 5,
          value: [
            {
              key: 'custom',
              value: {
                basePrice: 5,
                currencyIso: 'CHF',
                formattedValue: '5.00',
                minQuantity: 1,
                pricePerX: 0,
                pricePerXBaseQty: 0,
                pricePerXUOM: 'PC',
                pricePerXUOMDesc: 'piece',
                pricePerXUOMQty: 0,
                priceType: 'BUY',
                priceWithVat: 5.385,
                value: 5,
                vatPercentage: 7.7,
                vatValue: 0.385,
              },
            },
          ],
        },
      ],
    },
    addToCartText: 'Add to Cart',
    customDescription: 'custom description',
    channel: 'B2B',
    isArticle: 'true',
    topDisplay: 'true',
    labelDisplay: 'true',
    brandLogo: 'true',
    brandAlternateText: 'Prod_23',
    excVatText: '0302030030330',
    incVatText: '3232323232323',
    artNrText: 'Art test text',
    mpnText: 'MPN text test',
    copiedText: 'Copied text test',
  },
  decorators: [
    applicationConfig({
      providers: [
        { provide: TranslationService, useClass: StorybookTranslationService },
        { provide: AllsitesettingsService, useClass: MockAllsitesettingsService },
        importProvidersFrom(CommonModule),
        importProvidersFrom(ArticleNumberPipeModule),
        importProvidersFrom(FontAwesomeModule),
        importProvidersFrom(MinOrderQuantityPopupModule),
      ],
    }),
    moduleMetadata({
      imports: [
        CommonModule,
        RouterModule,
        RouterTestingModule,
        NumericStepperComponentModule,
        DistButtonComponentModule,
        DistIconModule,
        SinglePriceComponentModule,
        DistLabelComponentModule,
        ProductNumberModule,
        TooltipComponentModule,
      ],
    }),
  ],
} as Meta;

const template: Story<DistProductCardComponent> = (args: DistProductCardComponent) => ({
  props: args,
});

export const productCard = template.bind({});

productCard.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/sTywg49C2f7JlHf6uF1nL1/Build?type=design&node-id=3655%3A72951&mode=design&t=2nzdvhnekTf9hHJ5-1',
  },
};
