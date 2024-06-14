import { Story, Meta } from '@storybook/angular';
import { DividerLineComponent } from './divider-line.component';
import { moduleMetadata } from '@storybook/angular';

export default {
  title: 'Divider Line',
  component: DividerLineComponent,
  tags: ['autodocs'],
  decorators: [
    moduleMetadata({
      declarations: [DividerLineComponent],
      imports: [],
    }),
  ],
  argTypes: {
    weight: {
      control: {
        type: 'radio',
        options: ['thin', 'thick'],
      },
    },
    type: {
      control: {
        type: 'radio',
        options: ['dark', 'light'],
      },
    },
    marginTop: {
      control: {
        type: 'number',
      },
    },
    marginBottom: {
      control: {
        type: 'number',
      },
    },
  },
} as Meta;

const template: Story<DividerLineComponent> = (args: DividerLineComponent) => ({
  props: args,
});

export const dividerLine = template.bind({});

dividerLine.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/sTywg49C2f7JlHf6uF1nL1/Build?node-id=3490%3A69088&mode=dev',
  },
};
