// also exported from '@storybook/angular' if you can deal with breaking changes in 6.1
import { Story, Meta } from '@storybook/angular';
import { ProductStatusComponent } from './product-status.component';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { moduleMetadata } from '@storybook/angular';

// More on default export: https://storybook.js.org/docs/angular/writing-stories/introduction#default-export
export default {
  title: 'Product Status',
  component: ProductStatusComponent,
  decorators: [
    moduleMetadata({
      imports: [DistIconModule],
    }),
  ],
} as Meta;

// More on component templates: https://storybook.js.org/docs/angular/writing-stories/introduction#using-args
const template: Story<ProductStatusComponent> = (args: ProductStatusComponent) => ({
  props: args,
});

export const productStatus = template.bind({});
export const availableToOrder = template.bind({});
availableToOrder.args = {
  filter: 'available-to-order',
  text: 'Available to order',
};

export const comingSoon = template.bind({});
comingSoon.args = {
  filter: 'coming-soon',
  text: 'Coming soon',
};

export const preorderNow = template.bind({});
preorderNow.args = {
  filter: 'pre-order-now',
  text: 'Preorder now',
};

export const currentlyNotAvailable = template.bind({});
currentlyNotAvailable.args = {
  filter: 'currently-not-available',
  text: 'Currently unavailable',
};

export const noLongerAvailable = template.bind({});
noLongerAvailable.args = {
  filter: 'no-longer-available',
  text: 'No longer available',
};

productStatus.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/sTywg49C2f7JlHf6uF1nL1/Build?type=design&node-id=3424-66181&mode=design&t=j31ZNmYFxIlGcyiC-0',
  },
};
