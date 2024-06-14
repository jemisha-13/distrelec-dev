import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { TabsComponent } from './tabs.component';
import { TabComponent } from '@design-system/tabs/tab/tab.component';
import { Component } from '@angular/core';

@Component({
  template: `
    <app-tabs>
      <app-tab [active]="true" tabTitle="Tab 1">Content 1</app-tab>
      <app-tab tabTitle="Tab 2">Content 2</app-tab>
    </app-tabs>
  `,
})
class TestHostComponent {}

describe('TabsComponent', () => {
  let component: TabsComponent;
  let fixture: ComponentFixture<TestHostComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TabsComponent, TabComponent, TestHostComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestHostComponent);
    component = fixture.debugElement.query(By.directive(TabsComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should activate the first tab by default', () => {
    const tabInstances = fixture.debugElement.queryAll(By.directive(TabComponent)).map((de) => de.componentInstance);
    expect(tabInstances[0].active).toBeTrue();
  });

  it('should activate selected tab and deactivate others on selectTab call', () => {
    const tabInstances = fixture.debugElement.queryAll(By.directive(TabComponent)).map((de) => de.componentInstance);
    // Activate the second tab
    component.selectTab(tabInstances[1]);
    fixture.detectChanges();

    expect(tabInstances[1].active).toBeTrue();
    expect(tabInstances[0].active).toBeFalse();
  });

  it('should apply input properties correctly', () => {
    component.size = 'small';
    component.type = 'line';
    component.isGrouped = true;
    component.padding = '10px';
    fixture.detectChanges();

    const tabsElement = fixture.debugElement.query(By.css('.tabs')).nativeElement;
    expect(tabsElement.classList.contains('is-line')).toBeTrue();
    expect(tabsElement.classList.contains('is-small')).toBeTrue();
    expect(tabsElement.classList.contains('is-grouped')).toBeTrue();
    expect(tabsElement.classList.contains('is-pill')).toBeFalse();

    const contentElement = fixture.debugElement.query(By.css('div')).nativeElement;
    expect(contentElement.style.padding).toBe('10px');
  });
});
