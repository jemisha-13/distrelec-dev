// also exported from '@storybook/angular' if you can deal with breaking changes in 6.1
import { Story, Meta } from '@storybook/angular';
import { ProductStatusBadgeComponent } from './product-status-badge.component';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { moduleMetadata } from '@storybook/angular';

// More on default export: https://storybook.js.org/docs/angular/writing-stories/introduction#default-export
export default {
  title: 'Product Status Badge',
  component: ProductStatusBadgeComponent,
  decorators: [
    moduleMetadata({
      imports: [DistIconModule],
    }),
  ],
} as Meta;

// More on component templates: https://storybook.js.org/docs/angular/writing-stories/introduction#using-args
const template: Story<ProductStatusBadgeComponent> = (args: ProductStatusBadgeComponent) => ({
  props: args,
});

export const productStatusBadge = template.bind({});

productStatusBadge.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/sTywg49C2f7JlHf6uF1nL1/Build?type=design&node-id=3424-66181&mode=design&t=j31ZNmYFxIlGcyiC-0',
  },
};
