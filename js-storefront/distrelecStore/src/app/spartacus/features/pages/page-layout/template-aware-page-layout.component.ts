import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { BehaviorSubject, from, Observable, of } from 'rxjs';
import { distinctUntilChanged, switchMap } from 'rxjs/operators';
import { PageLayoutService } from '@spartacus/storefront';
import { PageTemplateService } from '@features/pages/page-layout/page-template.service';
@Component({
  selector: 'app-page-layout',
  templateUrl: './template-aware-page-layout.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TemplateAwarePageLayoutComponent {
  @Input() set section(value: string) {
    this.section$.next(value);
  }

  readonly section$ = new BehaviorSubject<string | undefined>(undefined);

  readonly templateName$: Observable<string> =
    this.pageLayoutService.templateName$;

  readonly layoutName$: Observable<string> = this.section$.pipe(
    switchMap((section) => (section ? of(section) : this.templateName$))
  );

  readonly slots$: Observable<string[]> = this.section$.pipe(
    switchMap((section) => this.pageLayoutService.getSlots(section))
  );

  readonly pageFoldSlot$: Observable<string | undefined> =
    this.templateName$.pipe(
      switchMap((templateName) =>
        this.pageLayoutService.getPageFoldSlot(templateName)
      ),
      distinctUntilChanged()
    );

  pageTemplateComponent$  = this.templateName$.pipe(
    switchMap(templateName => from(this.pageTemplateService.getTemplateComponent(templateName))),
  )

  constructor(
    protected pageLayoutService: PageLayoutService,
    protected pageTemplateService: PageTemplateService,
  ) {}
}
