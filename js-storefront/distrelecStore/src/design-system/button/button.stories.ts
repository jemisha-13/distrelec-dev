// also exported from '@storybook/angular' if you can deal with breaking changes in 6.1
import { Story, Meta } from '@storybook/angular';
import { DistButtonComponent } from './button.component';

// More on default export: https://storybook.js.org/docs/angular/writing-stories/introduction#default-export
export default {
  title: 'Example/Button',
  component: DistButtonComponent,
  // More on argTypes: https://storybook.js.org/docs/angular/api/argtypes
} as Meta;

// More on component templates: https://storybook.js.org/docs/angular/writing-stories/introduction#using-args
const template: Story<DistButtonComponent> = (args: DistButtonComponent) => ({
  props: args,
  template: `<app-dist-button [type]="type" [width]="width" [height]="height" [size]="size">
  {{label}}
  </app-dist-button>`,
});

export const primary = template.bind({});
// More on args: https://storybook.js.org/docs/angular/writing-stories/args
primary.args = {
  primary: true,
  type: 'primary',
  width: 'w-medium',
  label: 'Button',
  height: 'h-default',
};

primary.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/mPxjSUjaXMaI3fN105tr5Q',
  },
};

export const secondary = template.bind({});
secondary.args = {
  label: 'Button',
  type: 'secondary',
  height: 'h-default',
};

export const outlined = template.bind({});
outlined.args = {
  label: 'Button',
  type: 'outlined',
  height: 'h-default',
};

export const large = template.bind({});
large.args = {
  label: 'Button',
  height: 'h-large',
};

export const small = template.bind({});
small.args = {
  label: 'Button',
  height: 'h-small',
};
