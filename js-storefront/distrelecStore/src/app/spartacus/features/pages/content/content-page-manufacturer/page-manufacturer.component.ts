import { Component, ViewEncapsulation } from '@angular/core';
import { CmsService, WindowRef } from '@spartacus/core';
import { Observable } from 'rxjs';
import { ManufactureService } from 'src/app/spartacus/services/manufacture.service';
import { ManufacturerListResponse } from '@model/manufacturer.model';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-content-manufacturer',
  templateUrl: './page-manufacturer.component.html',
  styleUrls: ['../content-page.shared.scss', './page-manufacturer.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class PageManufacturerComponent {
  title$ = this.pageService.getCurrentPage().pipe(map((p) => p.title));
  manufacturers$: Observable<ManufacturerListResponse> = this.manufacturerService.getManufactures();

  constructor(
    private pageService: CmsService,
    private manufacturerService: ManufactureService,
    private winRef: WindowRef,
  ) {}

  scrollTo(location: string) {
    if (this.winRef.isBrowser()) {
      if (this.winRef.document.getElementById(`${location}`) !== null) {
        this.winRef.document.getElementById(`${location}`).scrollIntoView({
          behavior: 'smooth',
          block: 'start',
          inline: 'nearest',
        });
      }
    }
  }
}
