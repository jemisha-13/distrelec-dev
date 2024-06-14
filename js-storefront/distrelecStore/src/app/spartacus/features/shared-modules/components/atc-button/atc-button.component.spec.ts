import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { AtcButtonComponent } from './atc-button.component';
import { DistCartService } from '@services/cart.service';
import { SiteConfigService } from '@services/siteConfig.service';
import { DistButtonComponent } from '@design-system/button/button.component';
import { DistIconComponent } from '@features/shared-modules/icon/icon.component';
import { Observable, of } from 'rxjs';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { MOCK_CART_BUTTON_DATA } from '@features/shared-modules/components/atc-button/mock-cart-button-data';
import { AddBulkResponse, BulkProducts } from '@model/cart.model';
import { MockTranslatePipe, TranslationService } from '@spartacus/core';
import { MockTranslationService } from '@features/mocks/mock-translation.service';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { Cart } from '@spartacus/cart/base/root';

class MockCartService {
  addProductToCart(
    articleNumber: string,
    quantity: number,
    isCart: boolean,
    pageTrackingType: ItemListEntity,
  ): Observable<any> {
    return of(MOCK_CART_BUTTON_DATA);
  }

  addBulkProductsToCart(bulkItems: BulkProducts, pageTrackingType: ItemListEntity): Observable<AddBulkResponse> {
    return of({
      blockedProducts: [],
      cartModifications: [],
      errorProducts: [],
      phaseOutProducts: [],
      punchOutProducts: [],
    });
  }
}

describe('AtcButtonComponent', () => {
  let component: AtcButtonComponent;
  let fixture: ComponentFixture<AtcButtonComponent>;
  let mockBaseSiteService;
  let mockCartService;

  beforeEach(async () => {
    mockBaseSiteService = jasmine.createSpyObj('SiteConfigService', { getCurrentPageTemplate: 'CartPageTemplate' });
    mockCartService = new MockCartService();

    await TestBed.configureTestingModule({
      imports: [CommonTestingModule],
      declarations: [AtcButtonComponent, DistButtonComponent, DistIconComponent, MockTranslatePipe],
      providers: [
        { provide: SiteConfigService, useValue: mockBaseSiteService },
        { provide: DistCartService, useValue: mockCartService },
        { provide: TranslationService, useValue: MockTranslationService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AtcButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should add product to cart', (done) => {
    spyOn(component, 'addProductToCart').and.callThrough();

    const button = fixture.nativeElement.querySelector('button');

    button.click();

    expect(component.addProductToCart).toHaveBeenCalled();
    done();
  });

  it('should set isAdding to true when addProductToCart is called', (done) => {
    component.isAdding = false;
    spyOn(component, 'addProductToCart').and.callThrough();

    const button = fixture.nativeElement.querySelector('button');
    button.click();

    expect(component.isAdding).toBeTrue();
    done();
  });

  it('should reset isAdding and isAdded flags after animation completes', fakeAsync(() => {
    spyOn(component, 'addProductToCart').and.callThrough();

    const button = fixture.nativeElement.querySelector('button');
    button.click();

    component.isAdding = true;
    component.isAdded = false;

    tick(3400);

    expect(component.isAdding).toBeFalse();
    expect(component.isAdded).toBeFalse();
  }));

  it('should emit added event when product is successfully added to cart', fakeAsync(() => {
    spyOn(component.added, 'emit');
    const button = fixture.nativeElement.querySelector('button');
    button.click();

    tick(3400);

    expect(component.added.emit).toHaveBeenCalledWith(true);
  }));
});
