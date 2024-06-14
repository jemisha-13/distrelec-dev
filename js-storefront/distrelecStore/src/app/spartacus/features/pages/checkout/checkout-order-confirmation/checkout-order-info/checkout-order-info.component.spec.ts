import { ComponentFixture, TestBed } from '@angular/core/testing';
import { StoreModule } from '@ngrx/store';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import {
  BaseSiteService,
  LanguageService,
  MockTranslatePipe,
  SiteAdapter,
  SiteContextModule,
  TranslationService,
} from '@spartacus/core';
import { Order } from '@spartacus/order/root';
import { Observable, of } from 'rxjs';

import { CheckoutOrderInfoComponent } from './checkout-order-info.component';

import { EffectsModule } from '@ngrx/effects';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { RetrieveERPCode } from '@model/checkout.model';
import { DistOrderService } from '@features/pages/order/core/dist-order.service';
import { MockTranslationService } from '@features/mocks/mock-translation.service';
import { MOCK_ORDER_DATA } from '@features/mocks/mock-order-data';
import { TransferState } from '@angular/core';
import { ComponentLoadingSpinnerModule } from '@features/shared-modules/component-loading-spinner/component-loading-spinner.module';

const languageServiceMock = {
  getActive: () => of('en'),
};

class MockDistOrderService {
  getCustomerUid(order: any): string {
    return 'mock-customer-uid';
  }

  retrieveErpCodeForOrder(order: any): Observable<RetrieveERPCode> {
    const mockErpCode: RetrieveERPCode = {
      erpCode: 'mock-erp-code',
      status: 'ok',
    };
    return of(mockErpCode);
  }
}

describe('CheckoutOrderInfoComponent', () => {
  let component: CheckoutOrderInfoComponent;
  let fixture: ComponentFixture<CheckoutOrderInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        CommonTestingModule,
        StoreModule.forRoot({}),
        EffectsModule.forRoot([]),
        RouterTestingModule,
        HttpClientTestingModule,
        SiteContextModule,
        ComponentLoadingSpinnerModule,
      ],
      declarations: [CheckoutOrderInfoComponent, MockTranslatePipe],
      providers: [
        TransferState,
        SiteAdapter,
        { provide: LanguageService, useValue: languageServiceMock },
        { provide: DistOrderService, useClass: MockDistOrderService },
        { provide: TranslationService, useValue: MockTranslationService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckoutOrderInfoComponent);
    component = fixture.componentInstance;

    component.order = MOCK_ORDER_DATA as unknown as Order;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
