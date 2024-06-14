import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CheckoutOrderConfirmationComponent } from './checkout-order-confirmation.component';
import { StoreModule } from '@ngrx/store';
import { RouterTestingModule } from '@angular/router/testing';
import { DistCartService } from '@services/cart.service';
import { ContextServiceMap, LanguageService, SiteContextParamsService, TranslationService } from '@spartacus/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TransferState } from '@angular/core';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { SessionService } from '@features/pages/product/core/services/abstract-session.service';
import { Actions } from '@ngrx/effects';
import { MockTranslationService } from '@features/mocks/mock-translation.service';
import { MockLanguageService } from '@features/mocks/services/mock-language.service';
import { OrderAdapter, OrderConnector } from '@spartacus/order/core';
import { ComponentLoadingSpinnerModule } from '@features/shared-modules/component-loading-spinner/component-loading-spinner.module';

describe('CheckoutOrderConfirmRedesignComponent', () => {
  let component: CheckoutOrderConfirmationComponent;
  let fixture: ComponentFixture<CheckoutOrderConfirmationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        CommonTestingModule,
        StoreModule.forRoot({}),
        RouterTestingModule,
        HttpClientTestingModule,
        ComponentLoadingSpinnerModule,
      ],
      declarations: [CheckoutOrderConfirmationComponent],
      providers: [
        DistCartService,
        TransferState,
        SiteContextParamsService,
        ContextServiceMap,
        SessionService,
        Actions,
        OrderConnector,
        OrderAdapter,
        { provide: TranslationService, useValue: MockTranslationService },
        { provide: LanguageService, useClass: MockLanguageService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckoutOrderConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
