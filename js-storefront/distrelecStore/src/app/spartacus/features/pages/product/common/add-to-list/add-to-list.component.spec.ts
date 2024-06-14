import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { AddToListComponent } from './add-to-list.component';
import { Pipe, PipeTransform } from '@angular/core';
import { Translatable, TranslatableParams } from '@spartacus/core';
import { SessionService } from '../../core/services/abstract-session.service';
import { TranslationService } from '@spartacus/core';
import { MockTranslationService } from '@features/mocks/mock-translation.service';
import { MOCK_PRODUCT_DATA } from '@features/mocks/components/product-card-data';
import { CheckboxModule } from '@design-system/checkbox/checkbox.module';
import { provideMockStore } from '@ngrx/store/testing';
import { PRODUCT_PAGE_STATE } from '@testing/mocks/data/product-page-state';

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

describe('AddToListComponent', () => {
  let component: AddToListComponent;
  let fixture: ComponentFixture<AddToListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CommonTestingModule, CheckboxModule],
      declarations: [AddToListComponent, MockTranslatePipe],
      providers: [
        SessionService,
        { provide: TranslationService, useValue: MockTranslationService },
        provideMockStore({ initialState: PRODUCT_PAGE_STATE }),
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddToListComponent);
    component = fixture.componentInstance;
    component.product = MOCK_PRODUCT_DATA;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
