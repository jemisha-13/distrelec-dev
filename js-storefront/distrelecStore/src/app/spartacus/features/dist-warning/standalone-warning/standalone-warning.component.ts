import { Component } from '@angular/core';
import { CmsWarningComponent } from '@model/cms.model';
import { CmsComponentData } from '@spartacus/storefront';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';

@Component({
  selector: 'app-standalone-warning',
  templateUrl: './standalone-warning.component.html',
  styleUrls: ['./standalone-warning.component.scss'],
})
export class StandaloneWarningComponent {
  bannerComponent$: Observable<CmsWarningComponent> = this.cms.data$.pipe(
    filter(Boolean),
    map((data: CmsWarningComponent) => {
      if (!data?.body && !data?.headline) {
        return null;
      }

      if (!data.visibleToDate) {
        return data;
      }

      const visibleUntilDate = Date.parse(data.visibleToDate);
      const currentDate = Date.now();

      return visibleUntilDate - currentDate > 0 ? data : null;
    }),
  );

  constructor(private cms: CmsComponentData<CmsWarningComponent>) {}
}
