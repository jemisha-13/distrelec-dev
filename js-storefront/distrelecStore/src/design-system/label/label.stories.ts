// also exported from '@storybook/angular' if you can deal with breaking changes in 6.1
import { Story, Meta } from '@storybook/angular';
import { LabelComponent } from './label.component';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { moduleMetadata } from '@storybook/angular';

// More on default export: https://storybook.js.org/docs/angular/writing-stories/introduction#default-export
export default {
  title: 'Label',
  component: LabelComponent,
  decorators: [
    moduleMetadata({
      imports: [DistIconModule],
    }),
  ],
} as Meta;

// More on component templates: https://storybook.js.org/docs/angular/writing-stories/introduction#using-args
const template: Story<LabelComponent> = (args: LabelComponent) => ({
  props: args,
});

export const label = template.bind({});
// More on args: https://storybook.js.org/docs/angular/writing-stories/args
label.args = {
  labelType: 'primary',
  text: 'Label',
  height: 'h-default',
  ids: {
    labelId: 'label_text',
  },
};

label.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/sTywg49C2f7JlHf6uF1nL1/Build?type=design&node-id=3157-41189&mode=design&t=tmQDgJMBQR5Zksk5-0',
  },
};
