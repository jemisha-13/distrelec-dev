import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { Observable } from 'rxjs';
import { CmsBannerComponent, EventService } from '@spartacus/core';
import { CmsComponentData } from '@spartacus/storefront';
import { BannerClickEvent } from './banner-click-event';

export interface DistCmsBannerComponent extends CmsBannerComponent {
  localizedUrlLink?: string;
  priority?: string;
}

@Component({
  selector: 'app-simple-banner',
  templateUrl: './simple-banner.component.html',
  styleUrls: ['./simple-banner.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class SimpleBannerComponent implements OnInit {
  cmsData$: Observable<DistCmsBannerComponent> = this.cms.data$;
  loading = 'lazy';
  fetchpriority = 'auto';
  width = '';
  height = '';

  constructor(
    private cms: CmsComponentData<DistCmsBannerComponent>,
    private eventService: EventService,
  ) {}

  ngOnInit() {
    if (this.cms.uid === 'LogoComponent') {
      this.loading = 'eager';
      this.fetchpriority = 'high';
      this.width = '190';
      this.height = '65';
    }
  }

  onLogoClick() {
    const componentUid = this.cms.uid;
    this.eventService.dispatch(new BannerClickEvent(componentUid));
  }
}
