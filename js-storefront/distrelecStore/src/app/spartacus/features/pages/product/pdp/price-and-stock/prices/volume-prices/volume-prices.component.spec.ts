/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VolumePricesComponent } from './volume-prices.component';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';

describe('VolumePricesComponent', () => {
  let component: VolumePricesComponent;
  let fixture: ComponentFixture<VolumePricesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [CommonTestingModule],
      declarations: [VolumePricesComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VolumePricesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
