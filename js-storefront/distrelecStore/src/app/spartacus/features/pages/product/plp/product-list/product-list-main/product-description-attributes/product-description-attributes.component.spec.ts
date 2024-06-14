import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductDescriptionAttributesComponent } from './product-description-attributes.component';
import { Pipe, PipeTransform } from '@angular/core';
import { Translatable, TranslatableParams } from '@spartacus/core';
import { StripHTMLTagsPipe } from '@features/shared-modules/pipes/strip-html-tags-pipe';
import { MOCK_PRODUCT_DATA } from '@features/mocks/components/product-card-data';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { ProductDescriptionAttributesModule } from '@features/pages/product/plp/product-list/product-list-main/product-description-attributes/product-description-attributes.module';

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

describe('ProductDescriptionAttributesComponent', () => {
  let component: ProductDescriptionAttributesComponent;
  let fixture: ComponentFixture<ProductDescriptionAttributesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductDescriptionAttributesModule, CommonTestingModule],
      declarations: [ProductDescriptionAttributesComponent, StripHTMLTagsPipe, MockTranslatePipe],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProductDescriptionAttributesComponent);
    component = fixture.componentInstance;
    component.product = MOCK_PRODUCT_DATA;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
