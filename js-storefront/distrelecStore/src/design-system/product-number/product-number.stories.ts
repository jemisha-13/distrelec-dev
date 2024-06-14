import { Meta, Story } from '@storybook/angular';
import { ProductNumberComponent } from './product-number.component';
import { moduleMetadata } from '@storybook/angular';
import { CommonModule } from '@angular/common';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { ArticleNumberPipeModule } from '@pipes/article-number-pipe.module';

export default {
  title: 'Product Number',
  component: ProductNumberComponent,
  tags: ['autodoc'],
  decorators: [
    moduleMetadata({
      imports: [CommonModule, DistIconModule, ArticleNumberPipeModule],
    }),
  ],
} as Meta;

const template: Story<ProductNumberComponent> = (args: ProductNumberComponent) => ({
  component: ProductNumberComponent,
  props: args,
});

export const artNr = template.bind({});
artNr.args = {
  artNrId: 'product-card__artNr',
  type: {
    artNr: '30283260',
  },
  title: 'Art.Nr.',
};

export const MPN = template.bind({});
MPN.args = {
  mpnId: 'product-card__mpnId',
  type: {
    MPN: 'MAL250119222E3',
  },
  title: 'MPN.',
};

artNr.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/sTywg49C2f7JlHf6uF1nL1/Build?type=design&node-id=3655-72952&mode=design&t=a3bIyMkqL5ORMjkE-0',
  },
};
