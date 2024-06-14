import { NgModule } from '@angular/core';
import { UrlSerializer } from '@angular/router';
import { AuthModule, ExternalRoutesModule } from '@spartacus/core';
import {
  BreakpointService,
  LinkModule,
  LoginRouteModule,
  LogoutModule,
  NavigationEventModule,
  NavigationModule,
} from '@spartacus/storefront';
import { UserAccountModule } from '@spartacus/user';
import { OrderOccModule } from '@spartacus/order/occ';
import { CartPageEventModule } from '@spartacus/cart/base/core';

import { AppRoutingModule } from '../app-routing.module';
import { CustomUrlSerializer } from '@features/routing-config/custom-url-serializer';

import { SmartEditFeatureModule } from '@features/smartedit/smart-edit-feature.module';

import { BreadcrumbWrapperModule } from '@features/shared-modules/breadcrumb/breadcrumb-wrapper.module';
import { FooterModule } from '@features/shared-modules/footer/footer.module';
import { HeaderModule } from '@features/shared-modules/header/header.module';
import { DisruptionMessageModule } from '@features/shared-modules/disruption-message/disruption-message.module';
import { ContentPagesModule } from '@features/pages/content/content-pages.module';
import { RegisterFeatureModule } from '@features/pages/register/register-feature.module';
import { ShoppingListModalModule } from '@features/shared-modules/shopping-list-modal/shopping-list-modal.module';
import { ShippingTrackingModalModule } from '@features/shared-modules/shipping-tracking-modal/shipping-tracking-modal.module';
import { TrackingModule } from '@features/tracking/tracking.module';
import { DistrelecCmsParagraphModule } from '@features/shared-modules/paragraph/paragraph.module';
import { BackOrderFeatureModule } from '@features/pages/back-order/back-order-feature.module';
import { DistWarningModule } from '@features/dist-warning/dist-warning.module';
import { StandAloneWarningModule } from '@features/dist-warning/standalone-warning/standalone-warning.module';
import { BomToolFeatureModule } from '@features/pages/bom-tool/bom-tool-feature.module';
import { BulkDownloadFeatureModule } from '@features/pages/bulk-download/bulk-download-feature.module';
import { ErrorPageModule } from '@features/pages/error-page/error-page.module';
import { DistBreakpointService } from '@services/breakpoint.service';
import { CartFeatureModule } from '@features/pages/cart/cart-feature.module';
import { CheckoutFeatureModule } from '@features/pages/checkout/checkout-feature.module';
import { CompareFeatureModule } from '@features/pages/compare/compare-feature.module';
import { GuestReturnsFormFeatureModule } from '@features/pages/guest-returns-form/guest-returns-form-feature.module';
import { HomePageFeatureModule } from '@features/pages/homepage/homepage-feature.module';
import { LoginFeatureModule } from '@features/pages/login/login-feature.module';
import { MyAccountFeatureModule } from '@features/pages/my-account/my-account-feature.module';
import { NpsFeatureModule } from '@features/pages/nps/nps-feature.module';
import { ShoppingListFeatureModule } from '@features/pages/shopping-list/shopping-list-feature.module';
import { UnsubscribeFeatureModule } from '@features/pages/unsubscribe/unsubscribe-feature.module';
import { UpdatePasswordFeatureModule } from '@features/pages/update-password/update-password-feature.module';
import { DistrelecBannerFeatureModule } from '@features/shared-modules/banners/banner-feature.module';
import { DistHeadlineModule } from '@features/shared-modules/components/headline/headline.module';
import { DistComponentGroupFeatureModule } from '@features/shared-modules/dist-component-group/dist-component-group-feature.module';
import { DistrelecProductCarouselFeatureModule } from '@features/shared-modules/dist-product-carousel/dist-product-carousel-feature.module';
import { NewsletterSuccesModule } from '@features/pages/newsletter-succes/newsletter-succes.module';
import { ProductFeatureModule } from '@features/pages/product/product-feature.module';
import { ResponsiveWithNavFeatureModule } from '@features/pages/content/responsive-content-page-with-nav/responsive-with-nav-feature.module';
import { FullWidthWithoutNavigationFeatureModule } from '@features/pages/content/content-page-full-width-without-nav/without-navigation-feature.module';
import { ContentPageManufacturerFeatureModule } from '@features/pages/content/content-page-manufacturer/page-manufacturer-feature.module';
import { WithNavigationFeatureModule } from '@features/pages/content/content-page-with-navigation/with-navigation-feature.module';
import { WithoutNavigationFeatureModule } from '@features/pages/content/content-page-without-navigation/without-navigation-feature.module';
import { ResponsiveWithoutNavFeatureModule } from '@features/pages/content/responsive-content-page-without-nav/responsive-without-nav-feature.module';
import { ResponsiveWithoutNavFullWidthFeatureModule } from '@features/pages/content/responsive-content-page-without-nav-full-width/responsive-without-nav-feature.module';
import { StorePageContentFeatureModule } from '@features/pages/content/store-page-content/store-page-content-feature.module';
import { ChangeAddressFeatureModule } from '@features/pages/content/content-page-change-address/change-address-feature.module';
import { PageCms3FeatureModule } from '@features/pages/content/content-page-cms3/page-cms3-feature.module';
import { DistProductCardGroupModule } from '@features/shared-modules/components/product-card-group/product-card-group.module';
import { SitemapFeatureModule } from '@features/pages/content/content-page-sitemap/sitemap-feature.module';
import { DistRestrictionComponentGroupFeatureModule } from '@features/shared-modules/dist-restriction-component-group/dist-restriction-component-group/dist-restriction-component-group-feature.module';

import { orderProviders } from '@features/pages/order/order-providers';
import { cmsProviders } from '@features/cms/cms-providers';
import { resolversProviders } from './resolvers/resolvers-providers';
import { handlersProviders } from '@handlers/handlers-providers';
import { distI18nextProviders } from './i18n/i18next/dist-i18next-providers';

import './model/index';

@NgModule({
  declarations: [],
  imports: [
    // Auth Core
    AuthModule.forRoot(),
    LogoutModule,
    LoginRouteModule,
    // Basic Cms Components
    LinkModule,
    NavigationModule,
    // User UI,
    UserAccountModule,

    // CartModule.forRoot(),
    // CartOccModule,
    OrderOccModule,
    // Page Events,
    NavigationEventModule,
    CartPageEventModule,
    // External routes,
    ExternalRoutesModule.forRoot(),

    AppRoutingModule.forRoot(),

    // ** Custom Modules

    // * Pages -- All *FeatureModules are lazy-loaded
    HomePageFeatureModule,
    LoginFeatureModule,
    UpdatePasswordFeatureModule,
    RegisterFeatureModule,

    ProductFeatureModule,

    CartFeatureModule,
    BackOrderFeatureModule,
    CheckoutFeatureModule,
    CompareFeatureModule,
    ShoppingListFeatureModule,
    BomToolFeatureModule,
    UnsubscribeFeatureModule,
    NpsFeatureModule,
    MyAccountFeatureModule,
    GuestReturnsFormFeatureModule,
    NewsletterSuccesModule,

    // Content Page Templates
    ChangeAddressFeatureModule,
    PageCms3FeatureModule,
    FullWidthWithoutNavigationFeatureModule,
    ContentPageManufacturerFeatureModule,
    WithNavigationFeatureModule,
    WithoutNavigationFeatureModule,
    ResponsiveWithNavFeatureModule,
    ResponsiveWithoutNavFeatureModule,
    ResponsiveWithoutNavFullWidthFeatureModule,
    StorePageContentFeatureModule,
    SitemapFeatureModule,

    ErrorPageModule,
    ContentPagesModule,

    // * Page Fragments
    HeaderModule,
    FooterModule,
    BreadcrumbWrapperModule,
    ShoppingListModalModule,

    // * CMS Components
    DistComponentGroupFeatureModule,
    DistRestrictionComponentGroupFeatureModule,
    DistrelecProductCarouselFeatureModule,
    DistrelecBannerFeatureModule,
    BulkDownloadFeatureModule,
    DistProductCardGroupModule,

    DisruptionMessageModule,
    ShippingTrackingModalModule,
    DistrelecCmsParagraphModule,
    DistWarningModule,
    StandAloneWarningModule,
    DistHeadlineModule,

    // Features / Configuration
    TrackingModule,

    // Smartedit
    SmartEditFeatureModule,
  ],
  providers: [
    {
      provide: BreakpointService,
      useExisting: DistBreakpointService,
    },
    {
      provide: UrlSerializer,
      useClass: CustomUrlSerializer,
    },
    ...orderProviders,
    ...cmsProviders,
    ...resolversProviders,
    ...handlersProviders,
    ...distI18nextProviders,
  ],
})
export class SpartacusFeaturesModule {}
