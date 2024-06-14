import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductNumberComponent } from '@design-system/product-number/product-number.component';
import { ArticleNumberPipeModule } from '@pipes/article-number-pipe.module';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';

@NgModule({
  imports: [CommonModule, ArticleNumberPipeModule, DistIconModule],
  declarations: [ProductNumberComponent],
  exports: [ProductNumberComponent],
})
export class ProductNumberModule {}
