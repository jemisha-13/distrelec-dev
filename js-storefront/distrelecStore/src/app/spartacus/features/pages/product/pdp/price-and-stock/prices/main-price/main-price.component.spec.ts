import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MainPriceComponent } from './main-price.component';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { MOCK_PRICE_OBJECT } from '@features/mocks/mock-cart-store.service';
import { MOCK_CHANNEL_DATA_PRETEST_CH } from '@features/mocks/mock-all-site-settings.service';
import { DecimalPlacesPipeModule } from '@features/shared-modules/pipes/decimal-places-pipe.module';
import { VolumePricePipeModule } from '@features/shared-modules/pipes/volume-price-pipe.module';
import { MockTranslatePipe } from '@spartacus/core';

describe('MainPriceComponent', () => {
  let component: MainPriceComponent;
  let fixture: ComponentFixture<MainPriceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CommonTestingModule, DecimalPlacesPipeModule, VolumePricePipeModule],
      declarations: [MainPriceComponent, MockTranslatePipe],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MainPriceComponent);
    component = fixture.componentInstance;
    component.price = MOCK_PRICE_OBJECT;
    component.currentChannel = MOCK_CHANNEL_DATA_PRETEST_CH;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
