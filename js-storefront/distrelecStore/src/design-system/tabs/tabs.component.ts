import { Component, ContentChildren, QueryList, AfterContentInit, Input } from '@angular/core';
import { TabComponent } from '@design-system/tabs/tab/tab.component';

@Component({
  selector: 'app-tabs',
  templateUrl: './tabs.component.html',
  styleUrls: ['./tabs.component.scss'],
})
export class TabsComponent implements AfterContentInit {
  @ContentChildren(TabComponent) tabs: QueryList<TabComponent> = QueryList.prototype;

  @Input() size: 'small' | 'normal' = 'normal';
  @Input() type: 'folder' | 'line' | 'big-line' | 'pill' = 'folder';
  @Input() isGrouped = false;
  @Input() padding?: string;

  ngAfterContentInit() {
    const activeTabs = this.tabs.filter((tabItem) => tabItem.active);
    if (activeTabs.length === 0 && this.tabs.length > 0) {
      this.selectTab(this.tabs.first);
    }
  }

  selectTab(tab: TabComponent) {
    this.tabs.toArray().forEach((tabItem) => (tabItem.active = false));
    tab.active = true;
  }
}
