import { applicationConfig, Meta, moduleMetadata, Story } from '@storybook/angular';
import { SiteContextUrlSerializer } from '@spartacus/core';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { importProvidersFrom } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { BreadcrumbsComponent } from './breadcrumbs.component';
import { AbsoluteRouterLinkModule } from '@features/shared-modules/directives/absolute-router-link.module';

export default {
  title: 'Breadcrumbs',
  component: BreadcrumbsComponent,
  decorators: [
    applicationConfig({
      providers: [
        { provide: SiteContextUrlSerializer, useValue: { serialize: (url) => url } },
        importProvidersFrom(RouterModule),
      ],
    }),
    moduleMetadata({
      imports: [CommonModule, RouterModule, RouterTestingModule, DistIconModule, AbsoluteRouterLinkModule],
    }),
  ],
} as Meta;

const template: Story<BreadcrumbsComponent> = (args: BreadcrumbsComponent) => ({
  component: BreadcrumbsComponent,
  props: args,
});

export const breadcrumbs = template.bind({});
breadcrumbs.args = {
  breadcrumbsArray: [
    { name: 'Home', url: '/' },
    { name: 'Optoelectronics', url: '/optoelectronics/c/cat-L2-3D_525341' },
    { name: 'LEDs', url: '/optoelectronics/leds/c/cat-L3D_525297' },
    {
      name: 'LEDs - SMD',
      url: '/optoelectronics/leds/leds-smd/c/cat-L2-3D_1914323',
    },
    {
      name: 'SMD LEDs - White Colour',
      url: '/optoelectronics/leds/leds-smd/smd-leds-white-colour/c/cat-DNAV_PL_2234781',
    },
  ],
};

breadcrumbs.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/sTywg49C2f7JlHf6uF1nL1/Build?node-id=3157%3A40454&mode=dev',
  },
};
