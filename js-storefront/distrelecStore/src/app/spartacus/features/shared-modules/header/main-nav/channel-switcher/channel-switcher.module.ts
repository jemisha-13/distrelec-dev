import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { I18nModule } from '@spartacus/core';
import { ChannelSwitcherComponent } from './channel-switcher.component';
import { IfModule } from '@rx-angular/template/if';
import { ForModule } from '@rx-angular/template/for';
import { LetModule } from '@rx-angular/template/let';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
@NgModule({
  declarations: [ChannelSwitcherComponent],
  imports: [CommonModule, I18nModule, IfModule, ForModule, LetModule, DistIconModule],
  exports: [ChannelSwitcherComponent],
})
export class ChannelSwitcherModule {}
