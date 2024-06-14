import { Story, Meta } from '@storybook/angular';
import { AlertBannerComponent } from './alert-banner.component';
import { moduleMetadata } from '@storybook/angular';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { CommonModule } from '@angular/common';

export default {
  title: 'AlertBanner',
  component: AlertBannerComponent,
  tags: ['autodocs'],
  decorators: [
    moduleMetadata({
      imports: [CommonModule, DistIconModule],
      providers: [],
    }),
  ],
} as Meta;

const template: Story<AlertBannerComponent> = (args: AlertBannerComponent) => ({
  props: args,
  template: `<app-alert-banner [bannerType]="bannerType" [alertType]="alertType">    <div class="alert-container">
  <span class="alert-title" >Alert Message</span>
  <ng-container *ngIf="alertType === 'alert-box'">
      <span class="alert-content">Alert content</span>
  </ng-container>
</div></app-alert-banner>`,
});

export const alertBanner = template.bind({});

alertBanner.args = {
  alertType: 'system-alert',
  bannerType: 'general',
};

alertBanner.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/sTywg49C2f7JlHf6uF1nL1/Build?type=design&node-id=8359%3A227881&mode=design&t=aQ18W49n1AbHJ19V-1',
  },
};
