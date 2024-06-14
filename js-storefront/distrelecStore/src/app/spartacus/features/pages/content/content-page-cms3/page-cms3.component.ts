import { Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { faAngleRight } from '@fortawesome/free-solid-svg-icons';
import { CmsComponent, CmsService } from '@spartacus/core';
import { combineLatest, Observable, ReplaySubject, Subscription } from 'rxjs';
import { take, takeUntil } from 'rxjs/operators';

interface PageCms3ComponentData extends CmsComponent {
  listComponents?: string;
}

@Component({
  selector: 'app-page-cms3',
  templateUrl: './page-cms3.component.html',
  styleUrls: ['../content-page.shared.scss', './page-cms3.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class PageCms3Component implements OnInit, OnDestroy {
  cms3Components: CmsComponent[] = [];
  faAngleRight = faAngleRight;

  pageComponents$: Observable<CmsComponent[] | [unknown]>;

  private destroyed$ = new ReplaySubject<void>(1);
  private subscription: Subscription = new Subscription();

  constructor(private cmsService: CmsService) {}

  ngOnInit(): void {
    this.cmsService
      .getCurrentPage()
      .pipe(takeUntil(this.destroyed$))
      .subscribe((pageData) => {
        // eslint-disable-next-line guard-for-in
        for (const slot in pageData?.slots) {
          const slotObject = pageData?.slots[slot];

          if (slotObject?.components !== undefined) {
            for (const component of slotObject?.components) {
              this.subscription.add(
                this.cmsService.getComponentData<PageCms3ComponentData>(component?.uid).subscribe((componentData) => {
                  this.cms3Components.push(componentData);

                  // additional component call is needed as component id's are returned in original call
                  if (componentData?.typeCode === 'DesignList') {
                    const observables = componentData?.listComponents
                      .split(' ')
                      .map((item) => this.mapCompIdsToObservable(item));

                    this.pageComponents$ = combineLatest(observables).pipe(take(1));
                  }
                }),
              );
            }
          }
        }
      });
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
    this.destroyed$.next();
    this.destroyed$.complete();
  }

  private mapCompIdsToObservable(componentId): Observable<CmsComponent> {
    return this.cmsService.getComponentData(componentId);
  }
}
