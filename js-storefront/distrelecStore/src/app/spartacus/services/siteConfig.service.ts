import { Injectable, OnDestroy } from '@angular/core';
import { BaseSiteService, CmsService } from '@spartacus/core';
import { Subscription } from 'rxjs';
import { map } from 'rxjs/operators';

export const microsoftUetTagId = [
  { isoCode: 'AT', tagId: '21015422' },
  { isoCode: 'BE', tagId: '21015423' },
  { isoCode: 'CH', tagId: '21015424' },
  { isoCode: 'LI', tagId: '21015424' },
  { isoCode: 'CZ', tagId: '139000543' },
  { isoCode: 'DE', tagId: '21015425' },
  { isoCode: 'DK', tagId: '21015426' },
  { isoCode: 'EE', tagId: '21015437' },
  { isoCode: 'EX', tagId: '139000546' },
  { isoCode: 'FI', tagId: '21015427' },
  { isoCode: 'FR', tagId: '21015428' },
  { isoCode: 'HU', tagId: '139000544' },
  { isoCode: 'IT', tagId: '21015429' },
  { isoCode: 'VA', tagId: '21015429' },
  { isoCode: 'SM', tagId: '21015429' },
  { isoCode: 'LT', tagId: '21015430' },
  { isoCode: 'LV', tagId: '21015431' },
  { isoCode: 'NL', tagId: '21015432' },
  { isoCode: 'NO', tagId: '21015433' },
  { isoCode: 'PL', tagId: '21015434' },
  { isoCode: 'SE', tagId: '21015435' },
  { isoCode: 'SK', tagId: '21015436' },
  { isoCode: 'RO', tagId: '139000545' },
];

@Injectable({
  providedIn: 'root',
})
export class SiteConfigService implements OnDestroy {
  currentSiteID = '';
  currentSiteTemplate = '';

  activeSubscription: Subscription;
  getCurrentPageTemplateSubscription: Subscription;

  constructor(
    private baseSiteService: BaseSiteService,
    private cmsService: CmsService,
  ) {}

  ngOnDestroy(): void {
    this.activeSubscription?.unsubscribe();
    this.getCurrentPageTemplateSubscription?.unsubscribe();
  }

  getCurrentSiteId(): string | '' {
    if (!this.activeSubscription) {
      this.activeSubscription = this.baseSiteService
        .get()
        .pipe(map((item) => item?.uid))
        .subscribe((value) => (this.currentSiteID = value));
    }

    return this.currentSiteID;
  }

  getCurrentPageTemplate(): string | '' {
    if (!this.getCurrentPageTemplateSubscription) {
      this.getCurrentPageTemplateSubscription = this.cmsService.getCurrentPage().subscribe((value) => {
        if (value?.template) {
          this.currentSiteTemplate = value.template;
        }
      });
    }

    return this.currentSiteTemplate;
  }
}
