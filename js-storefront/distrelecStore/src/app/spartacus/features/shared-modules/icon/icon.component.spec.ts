import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { DistIconModule } from './icon.module';
import { DistIconComponent } from './icon.component';
import { close } from '@assets/icons/icon-index';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';

describe('DistIconComponent', () => {
  let component: DistIconComponent;
  let fixture: ComponentFixture<DistIconComponent>;
  let element: DebugElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [DistIconModule],
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(DistIconComponent);
        component = fixture.componentInstance;
        element = fixture.debugElement;
      });
  }));

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should generate an svg element', () => {
    component.icon = close;

    fixture.detectChanges();

    const svgElement = element.nativeElement.querySelector('svg');
    const pathElement = element.nativeElement.querySelector('path');

    expect(pathElement).toBeTruthy();
    expect(svgElement).toBeTruthy();
  });

  it('should have the correct attributes', () => {
    component.icon = close;

    fixture.detectChanges();

    const svgElement = element.nativeElement.querySelector('svg');
    const pathElement = element.nativeElement.querySelector('path');

    expect(svgElement.getAttribute('height')).toBe(close.dimensions[0].toString());
    expect(svgElement.getAttribute('width')).toBe(close.dimensions[1].toString());
    expect(pathElement.getAttribute('d')).toBe(close.elements[0].properties.d);
    expect(pathElement.getAttribute('fill')).toBe(close.elements[0].properties.fill);
  });

  it('should have modified fill colour attribute', () => {
    component.icon = close;
    component.class = 'red';

    fixture.detectChanges();

    const pathElement = element.nativeElement.querySelector('path');
    const svgElement = element.nativeElement.querySelector('svg');

    expect(pathElement).toBeTruthy();
    expect(svgElement).toBeTruthy();

    expect(svgElement.getAttribute('height')).toBe(close.dimensions[0].toString());
    expect(svgElement.getAttribute('width')).toBe(close.dimensions[1].toString());
    expect(pathElement.getAttribute('d')).toBe(close.elements[0].properties.d);
    expect(pathElement.getAttribute('fill')).toBe('currentColor');
  });

  it('should not show an svg element as no icon has been provided', () => {
    fixture.detectChanges();

    const svgElement = element.nativeElement.querySelector('svg');

    expect(svgElement).toBeFalsy();
  });
});
