import { Story, Meta } from '@storybook/angular';
import { RadioComponent } from './radio.component';

// More on default export: https://storybook.js.org/docs/angular/writing-stories/introduction#default-export
export default {
  title: 'Radio',
  component: RadioComponent,
  args: {
    size: 'large',
    type: 'standard',
    checked: false,
    disabled: false,
    primaryLabelText: 'Primary',
    secondaryLabelText: 'Secondary',
  },
  argTypes: {
    size: {
      control: {
        type: 'radio',
        options: ['large', 'small'],
      },
    },
    type: {
      control: {
        type: 'radio',
        options: ['emphasised', 'standard'],
      },
    },
    disabled: {
      control: {
        type: 'boolean',
      },
    },
    checked: {
      control: {
        type: 'boolean',
      },
    },
    primaryLabelText: {
      control: {
        type: 'text',
      },
      defaultValue: 'Primary',
    },
    primaryLabelID: {
      control: false,
    },
    secondaryLabelText: {
      control: {
        type: 'text',
      },
      defaultValue: 'Secondary',
    },
    secondaryLabelID: {
      control: false,
    },
    radioID: {
      control: false,
    },
    radioName: {
      control: false,
    },
    radioValue: {
      control: false,
    },
  },
} as Meta;

// More on component templates: https://storybook.js.org/docs/angular/writing-stories/introduction#using-args
const template: Story<RadioComponent> = (args: RadioComponent) => ({
  props: args,
});

export const radio = template.bind({});

radio.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/sTywg49C2f7JlHf6uF1nL1/Build?node-id=3157%3A40402&mode=dev',
  },
};
