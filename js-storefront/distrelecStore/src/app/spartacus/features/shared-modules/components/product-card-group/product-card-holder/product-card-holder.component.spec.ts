import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { CmsService, Translatable, TranslatableParams } from '@spartacus/core';
import { ProductCardHolderComponent } from '@features/shared-modules/components/product-card-group/product-card-holder/product-card-holder.component';
import { ProductDataService } from '@features/pages/product/core/services/product-data.service';
import { MOCK_PRODUCT_CARD_DATA } from '@features/mocks/mock-product-card-data';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { Pipe, PipeTransform } from '@angular/core';
import { DistProductCardModule } from '@design-system/product-card/product-card.module';

const cmsServiceMock = {
  getComponentData: (uid: string) => of({ orientation: 'landscape' }),
};

const productDataServiceMock = {
  getProductData: (articleNumber: string) => of(MOCK_PRODUCT_CARD_DATA),
};

@Pipe({
  name: 'cxTranslate',
})
class MockTranslatePipe implements PipeTransform {
  transform(input: Translatable | string, options: TranslatableParams = {}): Translatable | string {
    return returnSetOfTranslations(input, options);
  }
}

function returnSetOfTranslations(input: Translatable | string, options: TranslatableParams = {}) {
  // Needs to be updated for relevant translations, see stock helper returnSetOfTranslations
  return input;
}

describe('ProductCardHolderComponent', () => {
  let component: ProductCardHolderComponent;
  let fixture: ComponentFixture<ProductCardHolderComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CommonTestingModule, DistProductCardModule],
      declarations: [ProductCardHolderComponent, MockTranslatePipe],
      providers: [
        { provide: CmsService, useValue: cmsServiceMock },
        { provide: ProductDataService, useValue: productDataServiceMock },
      ],
    });

    fixture = TestBed.createComponent(ProductCardHolderComponent);
    component = fixture.componentInstance;
  });

  it('Should initialize the component', () => {
    expect(component).toBeTruthy();
  });

  it('Should initialize productCardData$ observable', () => {
    component.uid = '388939';

    fixture.detectChanges(); // Trigger ngOnInit

    component.productCardData$.subscribe(([cmsData, productData]) => {
      expect(cmsData.orientation).toBe('landscape');
    });
  });
});
