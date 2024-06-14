import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CmsConfig, provideConfig } from '@spartacus/core';
import { DistBannerComponent } from './dist-banner.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { SharedModule } from '@features/shared-modules/shared.module';
import { MediaImageModule } from '@features/shared-modules/media-image/media-image.module';

@NgModule({
  declarations: [DistBannerComponent],
  imports: [CommonModule, FontAwesomeModule, SharedModule, MediaImageModule],
  exports: [DistBannerComponent],
  providers: [
    provideConfig(<CmsConfig>{
      cmsComponents: {
        DistBannerComponent: {
          component: DistBannerComponent,
        },
      },
    }),
  ],
})
export class DistBannerModule {}
