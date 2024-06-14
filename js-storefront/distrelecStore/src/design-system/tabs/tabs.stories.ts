import { Meta, moduleMetadata } from '@storybook/angular';
import { TabsComponent } from '@design-system/tabs/tabs.component';
import { TabComponent } from '@design-system/tabs/tab/tab.component';
import { CommonModule } from '@angular/common';

export default {
  title: 'Tabs',
  component: TabsComponent,
  args: {
    size: 'normal',
    type: 'folder',
    isGrouped: false,
    padding: '20px',
  },
  argTypes: {
    size: {
      control: {
        type: 'radio',
        options: ['small', 'normal'],
      },
    },
    type: {
      control: {
        type: 'radio',
        options: ['folder', 'line', 'big-line', 'pill'],
      },
    },
    ngAfterContentInit: {
      table: {
        disable: true,
      },
    },
    selectTab: {
      table: {
        disable: true,
      },
    },
    tabs: {
      table: {
        disable: true,
      },
    },
  },
  decorators: [
    moduleMetadata({
      declarations: [TabComponent], // Declare TabComponent here since it's used by TabsComponent
      imports: [CommonModule],
    }),
  ],
} as Meta;

// More on component templates: https://storybook.js.org/docs/angular/writing-stories/introduction#using-args
const template = (args: TabsComponent) => ({
  props: args,
  template: `
    <app-tabs [size]="size" [type]="type" [isGrouped]="isGrouped" [padding]="padding">
      <app-tab tabTitle="Tab 1" [active]="true">Content of Tab 1</app-tab>
      <app-tab tabTitle="Tab 2">Content of Tab 2</app-tab>
      <app-tab tabTitle="Tab 3">Content of Tab 3</app-tab>
    </app-tabs>
  `,
});

export const tabs = template.bind({});
const parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/sTywg49C2f7JlHf6uF1nL1/Build?type=design&node-id=3157%3A41169&mode=design&t=AMkNAVNv5aH20GN1-1',
  },
};

tabs.parameters = parameters;

const templateTypes = (args: TabsComponent) => ({
  props: args,
  template: `
    <app-tabs [size]="size" type="folder" [isGrouped]="isGrouped" [padding]="padding">
      <app-tab tabTitle="Tab 1" [active]="true">Content of Tab 1</app-tab>
      <app-tab tabTitle="Tab 2">Content of Tab 2</app-tab>
      <app-tab tabTitle="Tab 3">Content of Tab 3</app-tab>
    </app-tabs>
    <hr class="my-5" />
    <app-tabs [size]="size" type="line" [isGrouped]="isGrouped" [padding]="padding">
      <app-tab tabTitle="Tab 1" [active]="true">Content of Tab 1</app-tab>
      <app-tab tabTitle="Tab 2">Content of Tab 2</app-tab>
      <app-tab tabTitle="Tab 3">Content of Tab 3</app-tab>
    </app-tabs>
    <hr class="my-5" />
    <div style="width: 384px">
    <app-tabs [size]="size" type="big-line" [isGrouped]="isGrouped" [padding]="padding">
      <app-tab tabTitle="Tab 1" [active]="true">Content of Tab 1</app-tab>
      <app-tab tabTitle="Tab 2">Content of Tab 2</app-tab>
    </app-tabs>
    </div>
    <hr class="my-5" />
    <app-tabs [size]="size" type="pill" [isGrouped]="isGrouped" [padding]="padding">
      <app-tab tabTitle="Tab 1" [active]="true">Content of Tab 1</app-tab>
      <app-tab tabTitle="Tab 2">Content of Tab 2</app-tab>
      <app-tab tabTitle="Tab 3">Content of Tab 3</app-tab>
    </app-tabs>
  `,
});

export const types = templateTypes.bind({});
types.parameters = parameters;

const templateSmall = (args: TabsComponent) => ({
  props: args,
  template: `
    <app-tabs [size]="size" type="folder" [isGrouped]="isGrouped" [padding]="padding">
      <app-tab tabTitle="Tab 1" [active]="true">Content of Tab 1</app-tab>
      <app-tab tabTitle="Tab 2">Content of Tab 2</app-tab>
      <app-tab tabTitle="Tab 3">Content of Tab 3</app-tab>
    </app-tabs>
    <hr class="my-5" />
    <app-tabs [size]="size" type="line" [isGrouped]="isGrouped" [padding]="padding">
      <app-tab tabTitle="Tab 1" [active]="true">Content of Tab 1</app-tab>
      <app-tab tabTitle="Tab 2">Content of Tab 2</app-tab>
      <app-tab tabTitle="Tab 3">Content of Tab 3</app-tab>
    </app-tabs>
    <hr class="my-5" />
    <app-tabs [size]="size" type="pill" [isGrouped]="isGrouped" [padding]="padding">
      <app-tab tabTitle="Tab 1" [active]="true">Content of Tab 1</app-tab>
      <app-tab tabTitle="Tab 2">Content of Tab 2</app-tab>
      <app-tab tabTitle="Tab 3">Content of Tab 3</app-tab>
    </app-tabs>
  `,
});

export const small = templateSmall.bind({});
small.args = {
  size: 'small',
};
small.parameters = parameters;

const templateGrouped = (args: TabsComponent) => ({
  props: args,
  template: `
    <app-tabs [size]="size" type="folder" isGrouped="true" [padding]="padding">
      <app-tab tabTitle="Tab 1" [active]="true">Content of Tab 1</app-tab>
      <app-tab tabTitle="Tab 2">Content of Tab 2</app-tab>
      <app-tab tabTitle="Tab 3">Content of Tab 3</app-tab>
    </app-tabs>
    <hr class="my-5" />
    <app-tabs [size]="size" type="line" isGrouped="true" [padding]="padding">
      <app-tab tabTitle="Tab 1" [active]="true">Content of Tab 1</app-tab>
      <app-tab tabTitle="Tab 2">Content of Tab 2</app-tab>
      <app-tab tabTitle="Tab 3">Content of Tab 3</app-tab>
    </app-tabs>
  `,
});

export const grouped = templateGrouped.bind({});
grouped.parameters = parameters;
