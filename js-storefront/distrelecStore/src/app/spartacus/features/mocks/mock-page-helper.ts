export const mockPageHelper = jasmine.createSpyObj('PageHelper', [
  'isSearchPage',
  'isCategoryPage',
  'isManufacturerDetailPage',
  'isProductFamilyPage',
  'isNewProductsPage',
  'isClearancePage',
]);
