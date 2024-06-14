import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MockTranslatePipe } from '@spartacus/core';

import { BulkDiscountLabelComponent } from './bulk-discount-label.component';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';

describe('BulkDiscountLabelComponent', () => {
  let component: BulkDiscountLabelComponent;
  let fixture: ComponentFixture<BulkDiscountLabelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CommonTestingModule],
      declarations: [BulkDiscountLabelComponent, MockTranslatePipe],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BulkDiscountLabelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
