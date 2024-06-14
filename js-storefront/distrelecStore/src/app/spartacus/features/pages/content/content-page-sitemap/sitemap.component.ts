import { Component, OnInit } from '@angular/core';
import { NavigationService } from '@services/navigation.service';
import { DistrelecUserService } from '@services/user.service';
import { CmsComponent, CmsService } from '@spartacus/core';
import { NavigationNode } from '@spartacus/storefront';
import { Observable, combineLatest, forkJoin } from 'rxjs';
import { first, map, switchMap } from 'rxjs/operators';

type SiteMapData = {
  rightSideLinks: CmsComponent[];
  categories: NavigationNode[];
  misc: NavigationNode[];
};

@Component({
  selector: 'app-sitemap',
  templateUrl: './sitemap.component.html',
  styleUrls: ['../content-page.shared.scss', './sitemap.component.scss'],
})
export class SitemapComponent implements OnInit {
  public siteMapData$: Observable<SiteMapData>;

  constructor(
    private navigationService: NavigationService,
    private distUserService: DistrelecUserService,
    private componentService: CmsService,
  ) {}

  ngOnInit() {
    this.siteMapData$ = combineLatest([
      this.componentService.getContentSlot('RightSideCMSLinks').pipe(
        switchMap((cmsData) => {
          const ids = cmsData.components.map((component) => component.uid);
          const observables = ids.map((id) => this.getChildComponentData(id));
          return forkJoin(observables).pipe(map((navLinks) => ({ navLinks })));
        }),
      ),
      this.navigationService.getMultipleNavigationNodes('MainCategoryNavNode'),
      this.navigationService.getMultipleNavigationNodes('FooterNavNode'),
    ]).pipe(
      map(([cmsData, categories, misc]) => ({
        rightSideLinks: cmsData.navLinks,
        categories,
        misc,
      })),
    );
  }

  get isB2B(): boolean {
    return this.distUserService.isB2BAny();
  }

  private getChildComponentData(id): Observable<CmsComponent> {
    return this.componentService.getComponentData(id).pipe(first());
  }
}
