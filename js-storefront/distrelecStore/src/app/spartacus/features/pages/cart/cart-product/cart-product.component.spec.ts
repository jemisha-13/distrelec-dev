import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { DistCartService } from '../../../../services/cart.service';
import { DefaultImageService } from '../../../../services/default-image.service';

import { CartProductComponent } from './cart-product.component';
import { ProductAvailabilityService } from '../../product/core/services/product-availability.service';
import { UntypedFormBuilder } from '@angular/forms';
import { SiteContextConfig } from '@spartacus/core';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';

describe('CartProductComponent', () => {
  let component: CartProductComponent;
  let fixture: ComponentFixture<CartProductComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CartProductComponent],
      imports: [CommonTestingModule],
      providers: [
        { provide: DistCartService, useValue: { getActive: () => of('distrelec_CH') } },
        { provide: ProductAvailabilityService, useValue: { getActive: () => of('distrelec_CH') } },
        UntypedFormBuilder,
        { provide: SiteContextConfig, useValue: { getContext: () => of({ baseSite: ['CH'] }) } },
        { provide: DefaultImageService, useValue: { getDefaultImage: () => of([]) } },
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CartProductComponent);
    component = fixture.componentInstance;

    component.cartReferenceCodeToggleState = {};
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should toggle mobile reference code state to true', () => {
    const refCode = 1;
    const isVisible = true;

    component.toggleMobileReferenceCode(refCode, isVisible);
    expect(component.cartReferenceCodeToggleState[refCode]).toEqual(isVisible);
  });

  it('should toggle mobile reference code state to false', () => {
    const refCode = 2;
    const isVisible = false;

    component.toggleMobileReferenceCode(refCode, isVisible);
    expect(component.cartReferenceCodeToggleState[refCode]).toEqual(isVisible);
  });
});
