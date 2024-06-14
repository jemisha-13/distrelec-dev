import { ChangeDetectorRef, Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { faAngleRight } from '@fortawesome/free-solid-svg-icons';
import { CmsComponent, CmsService } from '@spartacus/core';
import { combineLatest, Observable, ReplaySubject } from 'rxjs';
import { catchError, first, map, take, takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-with-navigation',
  templateUrl: './with-navigation.component.html',
  styleUrls: ['../content-page.shared.scss', './with-navigation.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class WithNavigationComponent implements OnInit, OnDestroy {
  pageData;
  pageComponents$: Observable<CmsComponent | [unknown]>;
  sidebarData$: Observable<CmsComponent | [unknown]>;

  faAngleRight = faAngleRight;
  componentsContentArray: string[] = [];
  rightNavArray: string[] = [];
  hasRightNav: boolean;
  componentsTeaserContentArray: string[] = [];
  hasTeaserContent = false;
  teaserContentHeader: string;

  navSideBarData = [];

  get dataAvailable(): boolean {
    return this.navSideBarData.length > 0;
  }

  private destroyed$ = new ReplaySubject<void>(1);

  constructor(private componentService: CmsService, private changeDetectorRef: ChangeDetectorRef) {}

  ngOnInit() {
    this.componentService
      .getCurrentPage()
      .pipe(takeUntil(this.destroyed$))
      .subscribe((pageData) => {
        this.pageData = pageData;

        // this resolves the duplication error, on some routes the nav options were added to the array rather than replacing items
        if (this.navSideBarData.length > 0) {
          this.navSideBarData = [];
        }

        //instances of this template where object doesnt exist
        if (this.pageData?.slots?.TeaserContent?.components) {
          this.hasTeaserContent = true;
          this.componentsTeaserContentArray = this.pageData?.slots?.TeaserContent?.components ?? [];

          const sidebar = this.pageData?.slots?.TeaserContent.components.map((item) =>
            this.mapCompIdsToObservable(item),
          );

          this.sidebarData$ = combineLatest(sidebar).pipe(take(1));

          this.sidebarData$
            .pipe(
              map((item) => {
                this.teaserContentHeader = item?.[0].title;
                item[0]?.linkComponents.split(' ').forEach((link) => {
                  this.getChildComponentData(link)
                    .pipe(
                      map((data) => {
                        this.navSideBarData.push(data);
                        this.changeDetectorRef.detectChanges(); // needed so that the values update otherwise dataAvailable stays false
                      }),
                    )
                    .pipe(first())
                    .subscribe();
                });
              }),
            )
            .pipe(first())
            .subscribe();
        }

        this.componentsContentArray = this.pageData?.slots?.Content?.components ?? [];
        this.rightNavArray = this.pageData?.slots?.RightNavigation?.components ?? [];
        this.hasRightNav = this.rightNavArray?.length > 0;
      });
  }

  ngOnDestroy() {
    this.destroyed$.next();
    this.destroyed$.complete();
  }

  //create url from content page label or id, cms content has different versions of this field, sometimes a correct url is provided
  configureUrl(item): string {
    let url: string;

    if (item?.localizedUrl) {
      url = item.localizedUrl;
    } else {
      url =
        item.contentPageLabelOrId.includes('/') === true
          ? '/cms' + item.contentPageLabelOrId
          : '/cms/' + item.contentPageLabelOrId;
    }
    return url;
  }

  private mapCompIdsToObservable(group): Observable<CmsComponent> {
    return this.componentService.getComponentData(group?.uid);
  }

  private getChildComponentData(id): Observable<CmsComponent> {
    return this.componentService.getComponentData(id).pipe(
      first(),
      catchError(() => null),
    );
  }
}
