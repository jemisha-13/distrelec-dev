import { CommonModule } from '@angular/common';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { I18nModule } from '@spartacus/core';
import { Meta, moduleMetadata, Story } from '@storybook/angular';
import { ChipComponent } from './chip.component';

export default {
  title: 'Chip',
  component: ChipComponent,
  decorators: [
    moduleMetadata({
      imports: [CommonModule, I18nModule, DistIconModule],
    }),
  ],
} as Meta;

const template: Story<ChipComponent> = (args: ChipComponent) => ({
  props: args,
  template: `<app-chip [text]="text" [iconLeft]="iconLeft" [iconRight]="iconRight" [type]="type" [id]="id">
  </app-chip>`,
});

export const primary = template.bind({});
primary.args = {
  text: 'Default Chip',
  type: 'squared',
  iconLeft: false,
  iconRight: true,
};

// Add the link to your Figma design for the Default chip
primary.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/sTywg49C2f7JlHf6uF1nL1/Build?type=design&node-id=3157%3A41148&mode=dev',
  },
};

export const roundedWithIcons = template.bind({});
roundedWithIcons.args = {
  text: 'Rounded Chip',
  type: 'round',
  iconLeft: true,
  iconRight: true,
};
