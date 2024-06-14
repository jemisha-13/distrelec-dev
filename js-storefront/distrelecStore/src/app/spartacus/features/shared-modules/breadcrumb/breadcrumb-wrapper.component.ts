import { Component, HostBinding, makeStateKey, OnInit, TransferState } from '@angular/core';
import { combineLatest, Observable } from 'rxjs';
import { filter, first, map, startWith, tap } from 'rxjs/operators';
import { Breadcrumb } from '@model/breadcrumb.model';
import { NavigationEnd, Router } from '@angular/router';
import {
  BreadcrumbMeta,
  CmsService,
  LanguageService,
  OccEndpointsService,
  Page,
  PageMetaService,
  TranslationService,
  WindowRef,
} from '@spartacus/core';
import { BreadcrumbService } from '@services/breadcrumb.service';
import { Location } from '@angular/common';
import { caretLeft } from '@assets/icons/icon-index';

/* eslint-disable @typescript-eslint/naming-convention */
type SchemaItem = {
  '@type': string;
  position: number;
  item: {
    '@id': string;
    name: string;
  };
};

type BreadcrumbSchema = {
  '@context': string;
  '@type': 'BreadcrumbList';
  itemListElement: SchemaItem[];
};
/* eslint-enable @typescript-eslint/naming-convention */

const BREADCRUMB_KEY = makeStateKey<Breadcrumb[]>('breadcrumb');

@Component({
  selector: 'app-breadcrumb-wrapper',
  templateUrl: './breadcrumb-wrapper.component.html',
  styleUrls: ['./breadcrumb-wrapper.component.scss'],
})
export class BreadcrumbWrapperComponent implements OnInit {
  breadcrumb$: Observable<Breadcrumb[]>;

  schema$: Observable<BreadcrumbSchema>;

  hasSearchQueryParam$: Observable<boolean> = this.router.events.pipe(
    filter((event) => event instanceof NavigationEnd),
    map(() => this.router.parseUrl(this.router.url).queryParams.trackQuery),
  );

  baseUrl: string;
  language: string;
  setPasswordChangePageStyle = false;
  caretLeft = caretLeft;

  isPDP$: Observable<boolean> = this.cmsService.getCurrentPage().pipe(
    filter(Boolean),
    map((pageData: Page) => pageData.template === 'ProductDetailsPageTemplate'),
  );

  @HostBinding('class') get hostClasses() {
    return `${this.setPasswordChangePageStyle ? 'password_change_page' : ''}`;
  }

  constructor(
    private occEndpoints: OccEndpointsService,
    protected pageMetaService: PageMetaService,
    private translation: TranslationService,
    private breadcrumbService: BreadcrumbService,
    private cmsService: CmsService,
    private winRef: WindowRef,
    private router: Router,
    private location: Location,
    private languageService: LanguageService,
    private transferState: TransferState,
  ) {
    this.baseUrl = this.occEndpoints.getBaseUrl({
      baseUrl: true,
      baseSite: false,
      prefix: false,
    });

    this.breadcrumb$ = this.initBreadcrumb$();

    this.schema$ = this.breadcrumb$.pipe(map((breadcrumbs: Breadcrumb[]) => this.buildSchema(breadcrumbs)));

    this.languageService
      .getActive()
      .pipe(first())
      .subscribe((lang) => (this.language = lang));
  }

  ngOnInit() {
    if (this.winRef.location?.pathname?.indexOf('pw/change') > -1) {
      this.setPasswordChangePageStyle = true;
    }
  }

  onNavigateBack() {
    this.location.back();
  }

  private initBreadcrumb$(): Observable<Breadcrumb[]> {
    let initialValue = [];
    if (this.winRef.isBrowser() && this.transferState.hasKey(BREADCRUMB_KEY)) {
      initialValue = this.transferState.get<Breadcrumb[]>(BREADCRUMB_KEY, []);
      this.transferState.remove(BREADCRUMB_KEY);
    }

    return combineLatest([
      this.pageMetaService.getMeta(),
      this.translation.translate('breadcrumb.home'),
      this.cmsService.getCurrentPage().pipe(filter<Page>(Boolean)),
    ]).pipe(
      filter(([meta, textHome, currentPage]) => !this.breadcrumbService.isBreadcrumbHiddenOnPage(currentPage)),
      map(([meta, textHome]) => {
        const data = meta?.breadcrumbs?.map((breadcrumbMeta: BreadcrumbMeta) => ({
          name: breadcrumbMeta.label,
          url: breadcrumbMeta.link as string,
          queryParams: breadcrumbMeta.queryParams,
          disabled: breadcrumbMeta.disabled,
        })) ?? [{ name: textHome, url: '/' }];

        if (!this.winRef.isBrowser()) {
          this.transferState.set(BREADCRUMB_KEY, data);
        }

        return data;
      }),
      tap((data) => this.breadcrumbService.setBreadcrumbs(data)),
      startWith(initialValue),
    );
  }

  private buildSchema(breadcrumbs: Breadcrumb[]): BreadcrumbSchema {
    const itemListElement = breadcrumbs.map((breadcrumb, index) => {
      const url =
        breadcrumb.url === '/search'
          ? `/search?q=${breadcrumb.queryParams.q}&sid=${breadcrumb.queryParams.sid}`
          : breadcrumb.url;

      /* eslint-disable @typescript-eslint/naming-convention */
      return {
        '@type': 'ListItem',
        position: index + 1,
        item: {
          '@id': `${this.winRef.location.origin}/${this.language}${url}`,
          name: breadcrumb.name,
        },
      };
    });

    return {
      '@context': 'http://schema.org',
      '@type': 'BreadcrumbList',
      itemListElement: itemListElement,
    };
  }
}
