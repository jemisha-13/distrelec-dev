/* eslint-disable @typescript-eslint/naming-convention */
import { Meta } from '@storybook/angular';
import { moduleMetadata } from '@storybook/angular';
import { RouterTestingModule } from '@angular/router/testing';

import { SlideDrawerModule } from '@design-system/slide-drawer/slide-drawer.module';
import { DistButtonComponentModule } from '@design-system/button/button.module';

import { SlideDrawerComponent } from './slide-drawer.component';
import { SlideDrawerDemoComponent } from './slide-drawer-demo.component';

const meta = {
  title: 'Slide Drawer',
  component: SlideDrawerComponent,
  decorators: [
    moduleMetadata({
      imports: [SlideDrawerModule, RouterTestingModule, DistButtonComponentModule],
    }),
  ],
  parameters: {
    layout: 'fullscreen',
    backgrounds: {
      default: 'grey',
      values: [
        {
          name: 'grey',
          value: '#f7f9fc',
        },
      ],
    },
    design: [
      {
        type: 'figma',
        url: 'https://www.figma.com/file/sTywg49C2f7JlHf6uF1nL1/Build?type=design&node-id=5083-189524&mode=dev',
      },
      {
        type: 'figma',
        url: 'https://www.figma.com/file/sTywg49C2f7JlHf6uF1nL1/Build?type=design&node-id=3157-41164&mode=dev',
      },
    ],
  },
  argTypes: {
    direction: {
      options: ['LEFT', 'RIGHT', 'TOP', /*'BOTTOM',*/ 'FROM_SEARCH'],
      control: { type: 'radio' },
    },
    title: {
      defaultValue: 'Title',
      control: { type: 'text' },
    },
    secondTitle: {
      defaultValue: 'Second Title',
      control: { type: 'text' },
    },
    uid: {
      control: { type: 'text' },
    },
    enableSecondPanel: {
      control: { type: 'boolean' },
    },
  },
} as Meta;
export default meta;

const Template = (args) => ({
  component: SlideDrawerDemoComponent,
  props: args,
  template: `
    <app-slide-drawer-demo
      [direction]="args.direction"
      [title]="args.title"
      [secondTitle]="args.secondTitle"
      [uid]="args.uid"
      [enableSecondPanel]="args.enableSecondPanel"
    ></app-slide-drawer-demo>
  `,
});

export const Left = Template.bind({});
Left.args = {
  args: {
    direction: 'LEFT',
    title: 'Title',
    uid: 'demo-left',
    enableSecondPanel: false,
  },
};

export const Right = Template.bind({});
Right.args = {
  args: {
    direction: 'RIGHT',
    title: 'Title',
    uid: 'demo-right',
    enableSecondPanel: false,
  },
};

export const Top = Template.bind({});
Top.args = {
  args: {
    direction: 'TOP',
    title: 'Title',
    uid: 'demo-top',
    enableSecondPanel: false,
  },
};

// This one isn't implemented
// export const Bottom = Template.bind({});
// Bottom.args = {
//   args: {
//     direction: 'BOTTOM',
//     title: 'Title',
//     uid: 'demo-bottom',
//     enableSecondPanel: false,
//   },
// };

export const MultiPanel = Template.bind({});
MultiPanel.args = {
  args: {
    direction: 'LEFT',
    title: 'First Title',
    secondTitle: 'Second Title',
    uid: 'demo-multipanel',
    enableSecondPanel: true,
  },
};

export const Search = Template.bind({});
Search.args = {
  args: {
    direction: 'FROM_SEARCH',
    title: 'Title',
    uid: 'demo-search',
    enableSecondPanel: false,
  },
};
