import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotifyMeComponent } from './notify-me.component';
import { ReactiveFormsModule } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { I18nModule } from '@spartacus/core';
import { IfModule } from '@rx-angular/template/if';

@NgModule({
  imports: [CommonModule, ReactiveFormsModule, FontAwesomeModule, I18nModule, IfModule],
  declarations: [NotifyMeComponent],
  exports: [NotifyMeComponent],
})
export class NotifyMeModule {}
