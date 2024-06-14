import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WelcomeMatComponent } from '@features/pages/homepage/welcome-mat/welcome-mat.component';
import { CmsConfig, provideConfig } from '@spartacus/core';

@NgModule({
  imports: [CommonModule],
  declarations: [WelcomeMatComponent],
  exports: [WelcomeMatComponent],
  providers: [
    provideConfig(<CmsConfig>{
      cmsComponents: {
        DistWelcomeMatComponent: {
          component: WelcomeMatComponent,
        },
      },
    }),
  ],
})
export class WelcomeMatComponentModule {}
