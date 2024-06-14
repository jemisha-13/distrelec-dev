import { inject } from '@angular/core';
import { UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { CmsService, PageType } from '@spartacus/core';
import { map, tap } from 'rxjs/operators';

export const contentPageGuard = (): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> => {
  const cmsService = inject(CmsService);

  return cmsService
    .getPage(
      {
        id: 'smartedit-preview',
        type: PageType.CONTENT_PAGE,
      },
      true,
    )
    .pipe(map((page) => page.type === PageType.CONTENT_PAGE));
};
