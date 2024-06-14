import { Injectable } from '@angular/core';
import { BreadcrumbService } from '@services/breadcrumb.service';
import {
  BasePageMetaResolver,
  BreadcrumbMeta,
  CmsService,
  PageBreadcrumbResolver,
  TranslationService,
} from '@spartacus/core';
import { combineLatest, Observable } from 'rxjs';
import { map, filter, tap } from 'rxjs/operators';
import { DistContentPageMetaResolver } from './content-page-meta.resolver';
import { ShoppingListService } from '@services/feature-services';
import { DistBasePageMetaResolver } from './dist-base-page-meta.resolver';

interface CustomBreadcrumbMeta extends BreadcrumbMeta {
  disabled?: boolean;
}

@Injectable({
  providedIn: 'root',
})
export class DistShoppingListPageMetaResolver extends DistContentPageMetaResolver implements PageBreadcrumbResolver {
  protected homeBreadcrumb$: Observable<BreadcrumbMeta> = this.translation
    .translate('breadcrumb.home')
    .pipe(map((label) => ({ label, link: '/' })));

  protected shoppingListBreadcrumb$: Observable<BreadcrumbMeta> = this.translation
    .translate('metahd.lists')
    .pipe(map((label) => ({ label, link: '/shopping', disabled: true })));

  constructor(
    basePageMetaResolver: DistBasePageMetaResolver,
    protected cmsService: CmsService,
    protected translation: TranslationService,
    protected breadCrumbService: BreadcrumbService,
    protected shoppingListService: ShoppingListService,
  ) {
    super(basePageMetaResolver, cmsService, translation, breadCrumbService);
    this.pageTemplate = 'ShoppingListPageTemplate';
  }

  resolveBreadcrumbs(): Observable<BreadcrumbMeta[]> {
    return combineLatest([
      this.homeBreadcrumb$,
      this.shoppingListBreadcrumb$,
      this.shoppingListService.getShoppingListsState().pipe(
        filter((lists) => lists?.list?.length > 0),
        map((lists) =>
          lists.list.find((list) => list.uniqueId === this.shoppingListService.getCurrentShoppingListId()),
        ),
        tap((list) => {
          if (list?.name === 'Shopping List') {
            this.translation
              .translate('shoppingList.shoppinglist_list_title')
              .subscribe((value) => (list.name = value));
          }
        }),
        map((list): CustomBreadcrumbMeta => ({ label: list.name, link: '/shopping/' + list.uniqueId, disabled: true })),
      ),
    ]).pipe(map((values) => values.flat()));
  }
}
