import { Component, Input } from '@angular/core';
import { PageLayoutService } from '@spartacus/storefront';
import { faArrowLeft } from '@fortawesome/free-solid-svg-icons';
import { WindowRef } from '@spartacus/core';
import { navigateBack } from '@helpers/navigation-helper';

export type TitleStyle = 'red';

@Component({
  selector: 'app-page-title',
  templateUrl: './page-title.component.html',
  styleUrls: ['./page-title.component.scss'],
})
export class PageTitleComponent {
  @Input() title?: string;
  @Input() titleI18nKey?: string;

  @Input() variant?: TitleStyle;

  faArrowLeft = faArrowLeft;

  readonly page$ = this.pageLayoutService.page$;

  constructor(
    private pageLayoutService: PageLayoutService,
    private winRef: WindowRef,
  ) {}

  getClass(): string {
    let className = 'page-title';
    if (this.variant) {
      className += ` page-title--${this.variant}`;
    }
    return className;
  }

  navigateBack(event) {
    navigateBack(this.winRef, event);
  }
}
