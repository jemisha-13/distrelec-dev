import { NgModule } from '@angular/core';
import { provideConfig } from '@spartacus/core';
import { PlpRootModule } from '@features/pages/product/plp/plp-root.module';

@NgModule({
  imports: [
    PlpRootModule, // Routes and configuration that must be statically imported
  ],
  providers: [
    provideConfig({
      featureModules: {
        plp: {
          module: () => import('@features/pages/product/plp/plp.module').then((m) => m.PlpModule),
          cmsComponents: [
            'ProductListMainComponent',
            'ProductListFiltersComponent',
            'ProductListTitleComponent',
            'ProductListPaginationComponent',
          ],
        },
        categoryPage: {
          module: () => import('@features/pages/category/category-page.module').then((m) => m.CategoryPageModule),
          cmsComponents: ['DistCategoryThumbsComponent', 'RelatedDataComponent'],
        },
        categoryIndex: {
          module: () =>
            import('@features/pages/category-index/category-index.module').then((m) => m.CategoryIndexModule),
          cmsComponents: ['CategoryIndexComponent'],
        },
        manufacturer: {
          module: () => import('@features/pages/manufacturer/manufacturer.module').then((m) => m.ManufacturerModule),
          cmsComponents: ['ManufacturerDetailTopBarComponent'],
        },
        productFamily: {
          module: () => import('@features/pages/family/family.module').then((m) => m.ProductFamilyModule),
          cmsComponents: ['ProductFamilyDetailTopBarComponent'],
        },
      },
    }),
  ],
})
export class PlpFeatureModule {}
