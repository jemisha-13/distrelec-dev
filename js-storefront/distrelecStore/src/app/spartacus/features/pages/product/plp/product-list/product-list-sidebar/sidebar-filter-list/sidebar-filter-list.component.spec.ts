import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SidebarFilterListComponent } from './sidebar-filter-list.component';
import { StoreModule } from '@ngrx/store';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslationService } from '@spartacus/core';
import { Observable, of } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { DistProductListComponentService } from '@features/pages/product/core/services/dist-product-list-component.service';
import { ProductListFilterService } from '@features/pages/product/core/services/product-list-filter.service';

class MockProductListComponentService {
  pagination$ = of({
    totalResults: 100,
  });
}

class MockProductListFilterService {
  filters$ = of([]);
}

describe('SidebarFilterListComponent', () => {
  let component: SidebarFilterListComponent;
  let fixture: ComponentFixture<SidebarFilterListComponent>;
  let actions$: Observable<any>;
  let translationService;

  beforeEach(async () => {
    translationService = jasmine.createSpyObj('TranslationService', ['translate']);

    await TestBed.configureTestingModule({
      imports: [CommonTestingModule, StoreModule.forRoot({}), RouterTestingModule, HttpClientTestingModule],
      declarations: [SidebarFilterListComponent],
      providers: [
        { provide: TranslationService, useValue: translationService },
        { provide: DistProductListComponentService, useClass: MockProductListComponentService },
        { provide: ProductListFilterService, useClass: MockProductListFilterService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SidebarFilterListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
