import { NgModule } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { LoadingLogoComponent } from './loading-logo.component';

@NgModule({
  imports: [FontAwesomeModule],
  declarations: [LoadingLogoComponent],
  exports: [LoadingLogoComponent],
})
export class LoadingLogoComponentModule {}
