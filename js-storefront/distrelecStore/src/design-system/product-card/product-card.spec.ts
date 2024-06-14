/* eslint-disable @typescript-eslint/naming-convention */
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { DistProductCardModule } from './product-card.module';
import { DistProductCardComponent, ManufacturerImageResult } from './product-card.component';
import { DebugElement } from '@angular/core';
import { DefaultImageService } from '@services/default-image.service';
import { By } from '@angular/platform-browser';
import { mockCmsData, MOCK_PRODUCT_DATA, MOCK_MANUFACTURER_DATA } from '@features/mocks/components/product-card-data';
import MockDefaultImageService from '@features/mocks/mock-default-image.service';
import { RouterTestingModule } from '@angular/router/testing';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import MockAllsitesettingsService from '@features/mocks/mock-all-site-settings.service';
import { take } from 'rxjs/operators';

describe('DistProductCardComponent', () => {
  let component: DistProductCardComponent;
  let fixture: ComponentFixture<DistProductCardComponent>;
  let element: DebugElement;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [DistProductCardModule, RouterTestingModule, CommonTestingModule],
      providers: [
        { provide: DefaultImageService, useClass: MockDefaultImageService },
        { provide: AllsitesettingsService, useClass: MockAllsitesettingsService },
      ],
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(DistProductCardComponent);
        component = fixture.componentInstance;
        element = fixture.debugElement;

        component.buttonType = mockCmsData.buttonType;
        component.customDescription = mockCmsData.customDescription;
        component.customTitle = mockCmsData.customTitle;
        component.isImage = mockCmsData.image;
        component.isTitle = mockCmsData.title;
        component.labelDisplay = mockCmsData.labelDisplay;
        component.orientation = mockCmsData.orientation;
        component.productCode = mockCmsData.productCode;
        component.productData = MOCK_PRODUCT_DATA;
        component.snippet = mockCmsData.snippet;
        component.topDisplay = mockCmsData.topDisplay;
      });
  }));

  it('should create an instance of the component', () => {
    expect(component).toBeTruthy();
  });

  it('should return a suitable image url', () => {
    const expected =
      'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_medium/19/3f/kuhnke-ht-d50-f-24v100-.jpg';

    let result;
    component.getProductImage(MOCK_PRODUCT_DATA.images).pipe(take(1)).subscribe((res) => (result = res));

    expect(result).toBe(expected);
  });

  it('should return default image url if image data does not contain suitable matches', () => {
    const expected = '/app/spartacus/assets/media/img/missing_landscape_medium.png';

    let result;
    component.getProductImage([]).pipe(take(1)).subscribe((res) => (result = res));

    expect(result).toBe(expected);
  });

  it('should return a suitable image url and prefix url with media domain if hasCampaign is true', () => {
    const expected =
      'http://pretest.mock.api.distrelec.com/https://pretest.media.distrelec.com/Web/WebShopImages/landscape_medium/19/3f/kuhnke-ht-d50-f-24v100-.jpg';

    component.hasCampaign = true;
    fixture.detectChanges();

    let result;
    component.getProductImage(MOCK_PRODUCT_DATA.images).pipe(take(1)).subscribe((res) => (result = res));

    expect(result).toBe(expected);
  });

  it('should display correctly in portrait mode', () => {
    fixture.detectChanges();

    expect(element.queryAll(By.css('portrait'))).toBeTruthy();
    expect(element.queryAll(By.css('product_card-image'))).toBeTruthy();
    expect(element.queryAll(By.css('product_card-title'))).toBeTruthy();
    expect(element.queryAll(By.css('product_card-description'))).toBeTruthy();
  });

  it('should display correctly in landscape mode', () => {
    component.customTitle = 'false';
    component.isImage = 'false';

    fixture.detectChanges();

    expect(element.queryAll(By.css('landscape'))).toBeTruthy();
    expect(element.queryAll(By.css('product_card-image'))).toEqual([]);
    expect(element.queryAll(By.css('product_card-title'))).toBeTruthy();
    expect(element.queryAll(By.css('product_card-description'))).toBeTruthy();
  });

  it('should return ManufacturerImageResult type and find image', () => {
    const result = component.getManufacturerImage(MOCK_MANUFACTURER_DATA.distManufacturer);
    const expectedText: ManufacturerImageResult = {
      isText: false,
      value: 'https://pretest.media.distrelec.com/Web/WebShopImages/manufacturer_logo/20/16/kuhnke_logo_RGB_2016.jpg',
    };

    expect(result).toEqual(expectedText);
  });

  it('should return label if topDisplay and labelDisplay is present', () => {
    component.productData = MOCK_MANUFACTURER_DATA;
    fixture.detectChanges();

    const expectedLabel = component.getPromotionText(MOCK_MANUFACTURER_DATA);

    expect(element.nativeElement.querySelector('app-label').textContent).toEqual(expectedLabel);
  });

  it('should return media domain in image url if hasCampaign input is true', () => {
    component.brandAlternateText = 'new product';
    component.productData = MOCK_MANUFACTURER_DATA;
    component.hasCampaign = true;
    fixture.detectChanges();

    const result = component.getManufacturerImage(MOCK_MANUFACTURER_DATA.distManufacturer);

    const expectedText = {
      isText: false,
      value:
        'https://pretest.media.distrelec.com/Web/WebShopImages/manufacturer_logo/20/16/kuhnke_logo_RGB_2016.jpg',
    };
    expect(result).toEqual(expectedText);
  });

  it('should display promotion parameter in template', () => {
    const promotionParameter = '2347_blackfriday23_rnd_homepage-mini-1';

    component.buttonType = mockCmsData.buttonType;
    component.snippet = mockCmsData.snippet;
    component.customDescription = mockCmsData.customDescription;
    component.isTitle = mockCmsData.title;
    component.orientation = mockCmsData.orientation;
    component.topDisplay = mockCmsData.topDisplay;
    component.labelDisplay = mockCmsData.labelDisplay;
    component.productData = MOCK_MANUFACTURER_DATA;
    component.promotionParameter = mockCmsData.promotionParameter;
    fixture.detectChanges();

    const anchorElement = element.nativeElement.querySelector('a');
    const href = anchorElement.getAttribute('href');
    expect(href).toContain(`int_cid=${promotionParameter}`);
  });
});
