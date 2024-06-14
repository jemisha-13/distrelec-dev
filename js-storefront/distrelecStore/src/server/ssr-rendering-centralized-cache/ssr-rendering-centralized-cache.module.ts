import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgExpressEngineDecoratorCentralizedCache } from './ng-express-engine-decorator-centralized-cache';

@NgModule({
  declarations: [],
  imports: [CommonModule],
  providers: [NgExpressEngineDecoratorCentralizedCache],
})
export class SsrRenderingCentralizedCacheModule {}
