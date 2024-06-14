import { Meta, moduleMetadata, Story } from '@storybook/angular';
import { AccordionComponent } from './accordion.component';
import { I18nModule } from '@spartacus/core';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { arrowDown, arrowUp } from '@assets/icons/icon-index';

export default {
  title: 'Accordion',
  component: AccordionComponent,
  tags: ['autodocs'],
  decorators: [
    moduleMetadata({
      imports: [I18nModule, DistIconModule],
    }),
  ],
} as Meta;

const template: Story<AccordionComponent> = (args: AccordionComponent) => ({
  props: args,
  template: `
  <app-accordion id="pdp_product_specifications" [title]="title" [collapsed]="collapsed">
  </app-accordion>`,
});

export const accordion = template.bind({});

accordion.args = {
  title: 'Accordion',
  savingCollapseState: true,
  collapsed: false,
  iconArrowUp: arrowUp,
  iconArrowDown: arrowDown,
};

accordion.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/sTywg49C2f7JlHf6uF1nL1/Build?type=design&node-id=8359%3A227881&mode=design&t=aQ18W49n1AbHJ19V-1',
  },
};
