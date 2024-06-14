import { Story, Meta } from '@storybook/angular';
import { TooltipComponent } from './tooltip.component';
import { moduleMetadata } from '@storybook/angular';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';

export default {
  title: 'Tooltip',
  component: TooltipComponent,
  tags: ['autodocs'],
  decorators: [
    moduleMetadata({
      imports: [DistIconModule],
    }),
  ],
  argTypes: {
    size: {
      control: {
        type: 'radio',
        options: ['sm', 'md'],
      },
    },
    position: {
      control: {
        type: 'radio',
        options: ['left', 'top-left', 'top', 'top-right', 'right', 'none', 'bottom-right', 'bottom', 'bottom-left'],
      },
    },
    tooltipID: {
      control: {
        type: 'text',
      },
    },
  },
} as Meta;

const template: Story<TooltipComponent> = (args: TooltipComponent) => ({
  props: args,
  template: `
  <app-tooltip [size]="size" [position]="position" [tooltipID]="tooltipID">
  <p>Tooltip content</p>
  </app-tooltip>`,
});

export const tooltip = template.bind({});

tooltip.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/sTywg49C2f7JlHf6uF1nL1/Build?type=design&node-id=3157-41174&mode=design&t=We5aHvShmlKVlRqq-0',
  },
};
