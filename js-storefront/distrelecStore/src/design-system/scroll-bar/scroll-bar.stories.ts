import { Meta, Story } from '@storybook/angular';
import { ScrollBarDemoComponent } from './scroll-bar.demo.component';

export default {
  title: 'Scroll Bar',
  component: ScrollBarDemoComponent,
} as Meta<ScrollBarDemoComponent>;

const template: Story<ScrollBarDemoComponent> = (args: ScrollBarDemoComponent) => ({
  props: args,
});

export const verticalScroll = template.bind({});
verticalScroll.args = {
  scrollAxis: 'y',
};

export const horizontalScroll = template.bind({});
horizontalScroll.args = {
  scrollAxis: 'x',
};
