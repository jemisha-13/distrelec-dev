import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TabsComponent } from '@design-system/tabs/tabs.component';
import { TabComponent } from '@design-system/tabs/tab/tab.component';

@NgModule({
  declarations: [TabsComponent, TabComponent],
  imports: [CommonModule],
  exports: [TabsComponent, TabComponent],
})
export class TabsModule {}
