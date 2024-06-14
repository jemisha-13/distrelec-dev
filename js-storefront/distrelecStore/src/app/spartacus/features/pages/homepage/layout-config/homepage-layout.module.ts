import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ConfigModule } from '@spartacus/core';
import { LayoutConfig } from '@spartacus/storefront';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    ConfigModule.withConfig({
      layoutSlots: {
        HomePage2018Template: {
          md: {
            slots: [
              
              'WelcomeMatSlot',
              'BannerWrapperSlot',
              'MainCategoryNavSlot',
              'MainContentSlot',
              'BlogContentSlot',
              'BottomContentSlot',
            ],
          },
          slots: [
            'BannerWrapperSlot',
            'MainCategoryNavSlot',
            'MainContentSlot',
            'BlogContentSlot',
            'BottomContentSlot',
          ],
        },
      },
    } as LayoutConfig),
  ],
})
export class HomePageLayoutModule {}
