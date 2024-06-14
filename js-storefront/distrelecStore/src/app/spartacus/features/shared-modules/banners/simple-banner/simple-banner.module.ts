import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CmsConfig, ConfigModule } from '@spartacus/core';
import { SimpleBannerComponent } from './simple-banner.component';
import { RouterModule } from '@angular/router';
import { SharedModule } from '@features/shared-modules/shared.module';
import { MediaImageModule } from '@features/shared-modules/media-image/media-image.module';

@NgModule({
  declarations: [SimpleBannerComponent],
  imports: [
    CommonModule,
    RouterModule,
    ConfigModule.forRoot({
      cmsComponents: {
        SimpleBannerComponent: {
          component: SimpleBannerComponent,
        },
      },
    } as CmsConfig),
    SharedModule,
    MediaImageModule,
  ],
  exports: [SimpleBannerComponent],
})
export class SimpleBannerModule {}
