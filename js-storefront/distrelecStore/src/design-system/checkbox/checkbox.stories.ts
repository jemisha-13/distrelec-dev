// also exported from '@storybook/angular' if you can deal with breaking changes in 6.1
import { Story, Meta } from '@storybook/angular';
import { CheckboxComponent } from './checkbox.component';

// More on default export: https://storybook.js.org/docs/angular/writing-stories/introduction#default-export
export default {
  title: 'Checkbox',
  component: CheckboxComponent,
  argTypes: {
    size: {
      control: {
        type: 'radio',
        options: ['large', 'small'],
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
    primaryLabel: {
      control: {
        type: 'text',
      },
      defaultValue: 'Primary label',
    },
    primaryLabelID: {
      control: {
        type: 'text',
      },
    },
    secondaryLabel: {
      control: {
        type: 'text',
      },
      defaultValue: 'Secondary label',
    },
    secondaryLabelID: {
      control: {
        type: 'text',
      },
    },
    checkboxID: {
      control: {
        type: 'text',
      },
      defaultValue: 'product-specifications-checkbox',
    },
    checkboxValue: {
      control: {
        type: 'text',
      },
    },
    checkboxName: {
      control: {
        type: 'text',
      },
    },
  },
} as Meta;

// More on component templates: https://storybook.js.org/docs/angular/writing-stories/introduction#using-args
const template: Story<CheckboxComponent> = (args: CheckboxComponent) => ({
  props: args,
});

export const checkbox = template.bind({});

checkbox.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/sTywg49C2f7JlHf6uF1nL1/Build?node-id=3157%3A40402&mode=dev',
  },
};
