import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductListFilterValuesComponent } from './product-list-filter-values.component';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import {
  CurrencyService,
  FacetValue,
  MockTranslatePipe,
  ProductSearchAdapter,
  TranslationService,
} from '@spartacus/core';
import { SearchQueryService } from '@features/pages/product/core/services/abstract-product-search-query.service';
import { Pipe, PipeTransform, TransferState } from '@angular/core';
import { SessionService } from '@features/pages/product/core/services/abstract-session.service';
import { DistCartService } from '@services/cart.service';
import { ProductListFilterService } from '@features/pages/product/core/services/product-list-filter.service';

import { Actions } from '@ngrx/effects';
import { MockTranslationService } from '@features/mocks/mock-translation.service';
import { CheckboxModule } from '@design-system/checkbox/checkbox.module';
import { DistScrollBarModule } from '@design-system/scroll-bar/scroll-bar.module';
import { provideMockStore } from '@ngrx/store/testing';
import { CATEGORY_PAGE_STATE } from '@testing/mocks/data/category-page-state';

@Pipe({
  name: 'optionFilter',
})
export class OptionFilterPipe implements PipeTransform {
  transform(values: FacetValue[], filterQuery: string, filterKey = 'name'): FacetValue[] {
    return values;
  }
}

describe('ProductListFilterValuesComponent', () => {
  let component: ProductListFilterValuesComponent;
  let fixture: ComponentFixture<ProductListFilterValuesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CommonTestingModule, CheckboxModule, DistScrollBarModule],
      declarations: [ProductListFilterValuesComponent, MockTranslatePipe, OptionFilterPipe],
      providers: [
        ProductSearchAdapter,
        SearchQueryService,
        CurrencyService,
        SessionService,
        DistCartService,
        ProductListFilterService,
        TransferState,
        Actions,
        { provide: TranslationService, useValue: MockTranslationService },
        provideMockStore({ initialState: CATEGORY_PAGE_STATE }),
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProductListFilterValuesComponent);
    component = fixture.componentInstance;
    fixture.componentInstance.facet = {
      hasSelectedElements: true,
      type: 'test',
      unit: 'string',
      code: 'test',
      name: 'test',
      values: [
        {
          count: 1,
          name: 'test',
          code: 'test',
          selected: false,
        },
      ],
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
