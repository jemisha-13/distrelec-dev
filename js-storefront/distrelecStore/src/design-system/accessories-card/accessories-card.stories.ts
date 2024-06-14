// also exported from '@storybook/angular' if you can deal with breaking changes in 6.1
import { applicationConfig, Meta, moduleMetadata, Story } from '@storybook/angular';
import { DistAccessoriesCardComponent } from './accessories-card.component';
import { CommonModule } from '@angular/common';
import { DistButtonComponentModule } from '@design-system/button/button.module';
import { BaseSiteService, LanguageService, TranslationService } from '@spartacus/core';
import { SinglePriceComponentModule } from '@design-system/single-price/single-price.module';
import { product } from '@features/mocks/mock-product-with-pricing';
import { HttpClientModule } from '@angular/common/http';
import { addToCart } from '@assets/icons/definitions/add-to-cart';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { importProvidersFrom } from '@angular/core';
import { StorybookTranslationService } from '@design-system/services/storybook-translation.service';
import { RouterModule } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { NumericStepperComponentModule } from '@features/shared-modules/components/numeric-stepper/numeric-stepper.module';
import { MockBaseSiteService, MockDistBaseSiteService } from '@features/mocks/services/mock-basesite.service';
import { DistrelecBasesitesService } from '@services/basesites.service';
import { ProductAvailabilityService } from '@features/pages/product/core/services/product-availability.service';
import { MockDistAvailabilityService } from '@features/mocks/services/mock-availability.service';
import { Store } from '@ngrx/store';
import { MockDistCartService } from '@features/mocks/mock-cart-store.service';
import { DistCartService } from '@services/cart.service';
import { MockLanguageService } from '@features/mocks/services/mock-language.service';
import { AtcButtonModule } from '@features/shared-modules/components/atc-button/atc-button.module';
import { SiteConfigService } from '@services/siteConfig.service';
import { MockSiteConfigService } from '@features/mocks/services/mock-siteconfig.service';

// More on default export: https://storybook.js.org/docs/angular/writing-stories/introduction#default-export
export default {
  title: 'Product Accessories Card',
  component: DistAccessoriesCardComponent,
  tags: ['autodocs'],
  // More on argTypes: https://storybook.js.org/docs/angular/api/argtypes
  decorators: [
    applicationConfig({
      providers: [
        { provide: TranslationService, useClass: StorybookTranslationService },
        { provide: DistrelecBasesitesService, useClass: MockDistBaseSiteService },
        { provide: BaseSiteService, useClass: MockBaseSiteService },
        { provide: ProductAvailabilityService, useClass: MockDistAvailabilityService },
        { provide: DistCartService, useClass: MockDistCartService },
        { provide: LanguageService, useClass: MockLanguageService },
        { provide: SiteConfigService, useClass: MockSiteConfigService },
        importProvidersFrom(HttpClientModule),
        importProvidersFrom(RouterModule),
        Store,
      ],
    }),
    moduleMetadata({
      imports: [
        CommonModule,
        RouterModule,
        RouterTestingModule,
        DistButtonComponentModule,
        SinglePriceComponentModule,
        DistIconModule,
        NumericStepperComponentModule,
        AtcButtonModule,
      ],
    }),
  ],
} as Meta;

// More on component templates: https://storybook.js.org/docs/angular/writing-stories/introduction#using-args
const accessories: Story<DistAccessoriesCardComponent> = () => ({
  props: {
    product,
    currentChannel: {
      language: 'en',
      country: 'CH',
      channel: 'B2B',
    },
    index: '0',
    itemListEntity: 'optional',
    addToCart,
  },
});
// eslint-disable-next-line @typescript-eslint/naming-convention
export const PDPAccessories = accessories.bind({});
// More on args: https://storybook.js.org/docs/angular/writing-stories/args

PDPAccessories.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/sTywg49C2f7JlHf6uF1nL1/Build?type=design&node-id=5030%3A194405&t=JwvLCD9n7SaHMbip-1',
  },
};
