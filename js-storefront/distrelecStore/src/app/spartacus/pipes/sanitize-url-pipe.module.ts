import { NgModule } from '@angular/core';
import { SanitizeUrl } from '@pipes/sanitize-url.pipe';

@NgModule({
  declarations: [SanitizeUrl],
  imports: [],
  exports: [SanitizeUrl],
})
export class SanitizeUrlPipeModule {}
