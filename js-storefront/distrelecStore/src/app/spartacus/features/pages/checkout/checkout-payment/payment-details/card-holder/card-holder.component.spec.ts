import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CardHolderComponent } from './card-holder.component';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';

describe('CardHolderComponent', () => {
  let component: CardHolderComponent;
  let fixture: ComponentFixture<CardHolderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CommonTestingModule],
      declarations: [CardHolderComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CardHolderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
