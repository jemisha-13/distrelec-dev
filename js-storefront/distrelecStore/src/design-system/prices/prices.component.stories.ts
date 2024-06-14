import { applicationConfig, Meta, moduleMetadata, Story } from '@storybook/angular';
import { DecimalPlacesPipeModule } from '@features/shared-modules/pipes/decimal-places-pipe.module';
import { I18nModule } from '@spartacus/core';
import { SinglePriceComponentModule } from '@design-system/single-price/single-price.module';
import { PricesComponent } from './prices.component';
import { importProvidersFrom } from '@angular/core';

export default {
  title: 'Prices',
  component: PricesComponent,
  tags: ['autodocs'],
  decorators: [
    applicationConfig({
      providers: [importProvidersFrom(I18nModule)],
    }),
    moduleMetadata({
      imports: [DecimalPlacesPipeModule, I18nModule, SinglePriceComponentModule],
    }),
  ],
} as Meta;

const template: Story<PricesComponent> = (args: PricesComponent) => ({
  props: args,
});

export const prices = template.bind({});

prices.args = {
  currentChannel: {
    language: 'en',
    country: 'CH',
    channel: 'B2B',
  },
  currency: 'CHF',
  formattedBasePrice: 19.83,
  formattedPriceWithVat: 21.36,
  ids: {
    productPrice: 'pdp_product_price',
    productPriceVat: 'pdp_product_price_vat',
    excVatText: 'pdp_product_price_exc_vat',
    incVatText: 'pdp_product_price_inc_vat',
  },
  toDecimalPlaces: 2,
  excVatText: 'exc. VAT',
  incVatText: 'incl. VAT',
};

prices.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/sTywg49C2f7JlHf6uF1nL1/Build?type=design&node-id=2-98&mode=design',
  },
};
