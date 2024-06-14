import { Component } from '@angular/core';
import { PageHelper } from '@helpers/page-helper';

@Component({
  selector: 'app-skeleton-loader-plp',
  templateUrl: './skeleton-loader-plp.component.html',
})
export class SkeletonLoaderPLPComponent {
  constructor(private pageHelper: PageHelper) {}

  isCategoryPage(): boolean {
    return this.pageHelper.isCategoryPage();
  }
}
