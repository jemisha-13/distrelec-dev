import { NgModule } from '@angular/core';
import { mapToCanActivate, RouterModule } from '@angular/router';
import { CmsPageGuard, PageLayoutComponent, ViewConfig } from '@spartacus/storefront';
import { CategoryRedirectGuard } from '@features/pages/category/guards/category-redirect.guard';
import { CategoryPageComponent } from '@features/pages/category/page/category-page.component';
import { provideConfig } from '@spartacus/core';
import { ProductListPageComponent } from '@features/pages/product/plp/page/product-list-page.component';
import { ProductListPageTemplateModule } from '@features/pages/product/plp/page/product-list-page-template.module';
import { ManufacturerPageComponent } from '@features/pages/manufacturer/page/manufacturer-page-content.component';
import { ProductFamilyComponent } from '@features/pages/family/page/product-family-content.component';
import { ManufacturerRedirectGuard } from '@features/pages/manufacturer/guards/manufacturer-redirect.guard';

@NgModule({
  imports: [
    ProductListPageTemplateModule,
    // These routes are copied from @spartacus/storefront ProductListingPageModule, customised here to use our own
    // CategoryPageComponent and ProductListPageComponent instead of the default PageLayoutComponent.
    RouterModule.forChild([
      {
        path: null,
        canActivate: mapToCanActivate([CmsPageGuard]),
        component: ProductListPageComponent,
        data: { pageLabel: 'search', cxRoute: 'search' },
      },
      {
        path: null,
        canActivate: mapToCanActivate([CmsPageGuard]),
        component: PageLayoutComponent,
        data: { cxRoute: 'brand' },
      },
      {
        // The 'category' route  may include a greedy suffix url matcher '**/c/:categoryCode'
        // So not to shadow the specific 'brand' route, the 'category' is the last route in the sequence.
        path: null,
        canActivate: mapToCanActivate([CategoryRedirectGuard, CmsPageGuard]),
        component: CategoryPageComponent, // Custom page layout component to dynamically switch content slots
        data: { cxRoute: 'category' },
      },
      {
        path: null,
        canActivate: mapToCanActivate([CmsPageGuard]),
        component: ProductFamilyComponent, // Custom page layout component to dynamically switch content slots
        data: { cxRoute: 'productFamily' },
      },
      {
        path: null,
        canActivate: mapToCanActivate([ManufacturerRedirectGuard, CmsPageGuard]),
        component: ManufacturerPageComponent, // Custom page layout component to dynamically switch content slots
        data: { cxRoute: 'manufacturer' },
      },
    ]),
  ],
  providers: [provideConfig(<ViewConfig>{ view: { defaultPageSize: 50 } })],
})
export class PlpRootModule {}
