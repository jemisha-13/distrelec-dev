import { Component, ElementRef, HostListener, ViewEncapsulation } from '@angular/core';
import { CmsComponent, CmsService, ContentSlotComponentData, Image, Page, PageContext } from '@spartacus/core';
import { CmsComponentData } from '@spartacus/storefront';
import { combineLatest, Observable } from 'rxjs';
import { filter, map, switchMap, take, tap } from 'rxjs/operators';
import { AnchorScrollService } from '@services/anchor-scroll-service';
import { PageHelper } from '@helpers/page-helper';

interface IDistComponentGroup extends CmsComponent {
  components?: string;
  products?: string;
  DistBannerComponentsList?: string; // eslint-disable-line @typescript-eslint/naming-convention
  listComponents?: string;
  title?: string;
  subtitle?: string;
  topLinkUrl?: string;
  topLinkText?: string;
  bottomLinkText?: string;
  bottomLinkUrl?: string;
  itemHtmlClasses?: string;
  htmlClasses?: string;
  currentLogoImage: Image;
  backgroundImage: Image;
}

@Component({
  selector: 'app-dist-component-group',
  templateUrl: './dist-component-group.component.html',
  styleUrls: ['./dist-component-group.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class DistComponentGroupComponent {
  componentIds: string[] = [];

  parentComponent$: Observable<IDistComponentGroup> = this.cms.data$.pipe(filter<IDistComponentGroup>(Boolean));

  components$: Observable<CmsComponent[]> = combineLatest([
    this.cms.data$,
    this.componentService.getCurrentPage(),
  ]).pipe(
    filter(([componentGroup, page]) => Boolean(componentGroup && page)),
    switchMap(([componentGroup, page]) => {
      this.componentIds = componentGroup.components?.split(' ') ?? [];

      return combineLatest(
        this.componentIds.map((component) =>
          this.getComponentDataBasedOnTemplate(component, page).pipe(
            filter<ContentSlotComponentData>(Boolean),
            map((componentData) => ({ ...componentData, flexType: componentData.flexType || componentData.typeCode })),
          ),
        ),
      );
    }),
  );

  isClearancePage = this.pageHelper.isClearancePage();

  constructor(
    private cms: CmsComponentData<IDistComponentGroup>,
    private componentService: CmsService,
    private anchorScrollService: AnchorScrollService,
    private pageHelper: PageHelper,
    private el: ElementRef,
  ) {}

  @HostListener('click', ['$event'])
  public handleClick(event: Event) {
    const htmlAnchorElement = this.anchorScrollService.findFirstParentAnchor(event);

    if (htmlAnchorElement && this.anchorScrollService.hasHashLinkForCurrentPage(htmlAnchorElement)) {
      event.preventDefault();
      this.anchorScrollService.scrollToElement(htmlAnchorElement.hash);
    }
  }

  // DISTRELEC-30496 and DISTRELEC-30416
  // We need to pass page context for the pages like New Products,
  // otherwise cms components disappear after login on that page
  // But if page context is passed on category pages, then getComponentData returns null
  // Added this check for the temporary fix which will be revisited after spartacus upgrade
  getComponentDataBasedOnTemplate(component: string, page: Page): Observable<ContentSlotComponentData> {
    if (page.template === 'CategoryPageTemplate') {
      return this.componentService.getComponentData<any>(component);
    }
    return this.componentService.getComponentData<any>(component, { id: page.pageId, type: page.type } as PageContext);
  }

  getComponentWrapperClass(parentComponent: IDistComponentGroup, component?: any): string[] {
    const classes = ['dist-component__col'];

    if (parentComponent?.itemHtmlClasses) {
      classes.push(parentComponent.itemHtmlClasses);
    }

    if (parentComponent?.htmlClasses) {
      classes.push(
        `${parentComponent.htmlClasses}-container__col`,
        `${parentComponent.htmlClasses}-container__row__grouped`,
      );
    }

    if (component?.dataAttributes?.aaSectionTitle === 'Spotlight') {
      classes.push('col-md-6');
    }
    return classes;
  }
}
