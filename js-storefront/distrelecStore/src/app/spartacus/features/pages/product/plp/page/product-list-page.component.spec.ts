import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MockTranslatePipe, TranslationService, WindowRef } from '@spartacus/core';
import { Directive, Input } from '@angular/core';
import { DistProductListComponentService } from '../../core/services/dist-product-list-component.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MockPageLayoutService } from '@testing/mocks/services/mock-page-layout.service';
import { MockTranslationService } from '@features/mocks/mock-translation.service';
import { Observable, of } from 'rxjs';
import { PageLayoutService } from '@spartacus/storefront';
import { ProductListPageComponent } from './product-list-page.component';
import { ViewportScroller } from '@angular/common';

class MockDistProductListComponentService {
  model$ = of({});
  isLoading$ = of(true);
}

@Directive({
  // eslint-disable-next-line @angular-eslint/directive-selector
  selector: '[cxPageTemplateStyle]',
})
class MockPageTemplateDirective {
  @Input() cxPageTemplateStyle: string;
}

@Directive({
  // eslint-disable-next-line @angular-eslint/directive-selector
  selector: '[cxOutletRef]',
})
export class MockOutletRefDirective {}

@Directive({
  // eslint-disable-next-line @angular-eslint/directive-selector
  selector: '[cxOutlet]',
})
export class MockOutletDirective<T = any> {
  @Input() cxOutlet: string;
  @Input() cxOutletContext: T;
}

describe('ProductListPageComponent', () => {
  let component: ProductListPageComponent;
  let fixture: ComponentFixture<ProductListPageComponent>;
  let actions$: Observable<any>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CommonTestingModule, HttpClientTestingModule],
      declarations: [
        ProductListPageComponent,
        MockTranslatePipe,
        MockPageTemplateDirective,
        MockOutletDirective,
        MockOutletRefDirective,
      ],
      providers: [
        { provide: TranslationService, useValue: MockTranslationService },
        { provide: DistProductListComponentService, useClass: MockDistProductListComponentService },
        { provide: PageLayoutService, useClass: MockPageLayoutService },
        ViewportScroller,
        WindowRef,
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProductListPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
