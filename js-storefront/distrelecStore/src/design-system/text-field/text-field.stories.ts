// also exported from '@storybook/angular' if you can deal with breaking changes in 6.1
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { moduleMetadata } from '@storybook/angular';
import { Story, Meta } from '@storybook/angular';
import { TextFieldComponent } from './text-field.component';

// More on default export: https://storybook.js.org/docs/angular/writing-stories/introduction#default-export
export default {
  title: 'Text Field',
  component: TextFieldComponent,
  decorators: [
    moduleMetadata({
      imports: [DistIconModule],
    }),
  ],
  args: {
    isSecure: false,
    isInvalidState: false,
    isValidState: false,
    labelText: undefined,
  },
  argTypes: {
    showSecureText: {
      table: {
        disable: true,
      },
      if: { arg: 'isSecure', truthy: false },
    },
    ngOnInit: {
      table: {
        disable: true,
      },
    },
    ngOnChanges: {
      table: {
        disable: true,
      },
    },
    toggleSecureField: {
      table: {
        disable: true,
      },
    },
    parentFormControlName: {
      control: false,
    },
    parentFormGroup: {
      control: false,
    },
    autoComplete: {
      control: false,
    },
    fieldId: {
      control: false,
    },
    isSecure: {
      control: {
        type: 'boolean',
      },
    },
  },
} as Meta;

// More on component templates: https://storybook.js.org/docs/angular/writing-stories/introduction#using-args
const template: Story<TextFieldComponent> = (args: TextFieldComponent) => ({
  props: args,
});

export const textField = template.bind({});

textField.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/sTywg49C2f7JlHf6uF1nL1/Build?type=design&node-id=3157-40402&mode=design&t=fuAvGdRmLo3iYx87-0',
  },
};
