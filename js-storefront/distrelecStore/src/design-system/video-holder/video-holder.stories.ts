// also exported from '@storybook/angular' if you can deal with breaking changes in 6.1
import { Story, Meta } from '@storybook/angular';
import { VideoHolderComponent } from './video-holder.component';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { moduleMetadata } from '@storybook/angular';

// More on default export: https://storybook.js.org/docs/angular/writing-stories/introduction#default-export
export default {
  title: 'Video Holder',
  component: VideoHolderComponent,
  decorators: [
    moduleMetadata({
      imports: [DistIconModule],
    }),
  ],
} as Meta;

// More on component templates: https://storybook.js.org/docs/angular/writing-stories/introduction#using-args
const template: Story<VideoHolderComponent> = (args: VideoHolderComponent) => ({
  props: args,
});

// eslint-disable-next-line @typescript-eslint/naming-convention
export const VideoHolder = template.bind({});
// More on args: https://storybook.js.org/docs/angular/writing-stories/args

VideoHolder.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/sTywg49C2f7JlHf6uF1nL1/Build?type=design&node-id=3157-41189&mode=design&t=tmQDgJMBQR5Zksk5-0',
  },
};
