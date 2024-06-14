import { Component, ElementRef, OnDestroy, OnInit } from '@angular/core';
import {
  AuthService,
  CmsBannerComponent,
  CmsService,
  ContentSlotComponentData,
  Page
} from '@spartacus/core';

import { SiteConfigService } from '@services/siteConfig.service';
import { distinct, filter, map, mergeMap, switchMap } from 'rxjs/operators';
import { Observable, Subscription } from 'rxjs';
import { DistrelecBasesitesService } from '@services/basesites.service';

@Component({
  selector: 'app-homepage-banner',
  templateUrl: './homepage-banner.component.html',
  styleUrls: ['./homepage-banner.component.scss'],
})
export class HomepageBannerComponent implements OnInit, OnDestroy {
  constructor(
    private siteConfigService: SiteConfigService,
    private pageService: CmsService,
    private distBaseSiteService: DistrelecBasesitesService,
    protected authService: AuthService,
    private elementRef: ElementRef,
  ) {}

  userLoggedIn$ = this.authService.isUserLoggedIn();

  bannerData: CmsBannerComponent[] = [];

  pageTemplate: string = this.siteConfigService.getCurrentPageTemplate();

  isAddToCartEnabled$: Observable<boolean> = this.distBaseSiteService.isAddToCartEnabledForActiveSite();

  private subscription = new Subscription();
  ngOnInit(): void {
    this.subscription.add(
      this.pageService
        .getCurrentPage()
        .pipe(
          filter<Page>(Boolean),
          map((pageData) => this.getBannerComponents(pageData)),
          mergeMap((item) => item),
          switchMap((component) => this.pageService.getComponentData<CmsBannerComponent>(component.uid)),
          distinct((component) => component?.uid),
        )
        .subscribe((res) => {
          this.bannerData.push(res);
        }),
    );
  }

  private getBannerComponents(pageData: Page): ContentSlotComponentData[] {
    const mainComponents =
      pageData.slots?.BannerContentSlot?.components?.filter((item) => item.typeCode === 'DistBannerComponent') ?? [];
    if (pageData.template === 'HomePage2018Template') {
      const firstComponents =
        pageData.slots?.['BannerContentSlot-Hero1']?.components.filter(
          (item) => item.typeCode === 'DistBannerComponent',
        ) ?? [];
      const secondComponents =
        pageData.slots?.['BannerContentSlot-Hero2']?.components.filter(
          (item) => item.typeCode === 'DistBannerComponent',
        ) ?? [];
      return [...mainComponents, ...firstComponents, ...secondComponents];
    }
    return mainComponents;
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
}
