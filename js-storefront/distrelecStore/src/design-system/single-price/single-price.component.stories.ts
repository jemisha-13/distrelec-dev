import { applicationConfig, Meta, moduleMetadata, Story } from '@storybook/angular';
import { DecimalPlacesPipeModule } from '@features/shared-modules/pipes/decimal-places-pipe.module';
import { I18nModule } from '@spartacus/core';
import { SinglePriceComponent } from './single-price.component';
import { importProvidersFrom } from '@angular/core';

export default {
  title: 'Price',
  component: SinglePriceComponent,
  tags: ['autodocs'],
  decorators: [
    applicationConfig({
      providers: [importProvidersFrom(I18nModule)],
    }),
    moduleMetadata({
      imports: [DecimalPlacesPipeModule, I18nModule],
    }),
  ],
} as Meta;

const template: Story<SinglePriceComponent> = (args: SinglePriceComponent) => ({
  props: args,
});

export const price = template.bind({});

price.args = {
  price: 15.52,
  currency: 'EUR',
  channelData: {
    language: 'en',
    country: 'CH',
    channel: 'B2B',
  },
  quantity: 1,
  componentId: 'pdp_accessories_',
  toDecimalPlaces: 2,
  priceLabel: 'Inc. Vat.',
};

price.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/sTywg49C2f7JlHf6uF1nL1/Build?type=design&node-id=2-98&mode=design',
  },
};
