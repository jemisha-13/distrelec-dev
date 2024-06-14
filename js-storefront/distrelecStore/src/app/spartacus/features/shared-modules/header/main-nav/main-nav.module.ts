import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MainNavComponent } from './main-nav.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { ManufacturersModule } from './manufacturers/manufacturers.module';
import { RouterModule } from '@angular/router';
import { CategoryNavModule } from './category-nav/category-nav.module';
import { ConfigModule, CmsConfig, I18nModule } from '@spartacus/core';
import { LayoutConfig } from '@spartacus/storefront';
import { ChannelSwitcherModule } from './channel-switcher/channel-switcher.module';
import { SharedModule } from '@features/shared-modules/shared.module';
import { SlideDrawerModule } from '@design-system/slide-drawer/slide-drawer.module';
import { HeaderCartModule } from './header-cart/header-cart.module';
import { HeaderSearchModule } from './search/search.module';
import { ForModule } from '@rx-angular/template/for';
import { IfModule } from '@rx-angular/template/if';
import { SiteSettingsModule } from './sitesettings/sitesettings.module';

@NgModule({
  declarations: [MainNavComponent],
  imports: [
    CommonModule,
    FontAwesomeModule,
    ManufacturersModule,
    RouterModule,
    CategoryNavModule,
    SiteSettingsModule,
    I18nModule,
    ChannelSwitcherModule,
    SharedModule,
    SlideDrawerModule,
    HeaderCartModule,
    HeaderSearchModule,
    ConfigModule.forRoot({
      cmsComponents: {
        DistMainNavigationComponent: {
          component: MainNavComponent,
        },
      },
    } as CmsConfig),
    ConfigModule.withConfig({
      layoutSlots: {
        navigation: {
          slots: ['BottomHeader', 'SearchBox'],
        },
        topHeader: {
          slots: ['TopHeader'],
        },
        mobileHeader: {
          slots: ['MobileHeader'],
        },
        warningHeader: {
          slots: ['TitleContent'],
        },
      },
    } as LayoutConfig),
  ],
  exports: [MainNavComponent, SiteSettingsModule, CategoryNavModule, FontAwesomeModule, IfModule, ForModule],
})
export class MainNavModule {}
