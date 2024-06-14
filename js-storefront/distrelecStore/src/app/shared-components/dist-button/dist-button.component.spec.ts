import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DistButtonComponent } from './dist-button.component';

describe('DistButtonComponent', () => {
  let component: DistButtonComponent;
  let fixture: ComponentFixture<DistButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DistButtonComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DistButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
