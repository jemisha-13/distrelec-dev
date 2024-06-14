import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DistRestrictionComponentGroupComponent } from '@features/shared-modules/dist-restriction-component-group/dist-restriction-component-group/dist-restriction-component-group.component';
import { CmsConfig, provideConfig } from '@spartacus/core';
import { PageComponentModule } from '@spartacus/storefront';

@NgModule({
  declarations: [DistRestrictionComponentGroupComponent],
  imports: [CommonModule, PageComponentModule],
  providers: [
    provideConfig(<CmsConfig>{
      cmsComponents: {
        DistRestrictionComponentGroup: {
          component: DistRestrictionComponentGroupComponent,
        },
      },
    }),
  ],
})
export class DistRestrictionComponentGroupModule {}
