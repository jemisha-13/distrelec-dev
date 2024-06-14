import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { PageLayoutService } from '@spartacus/storefront';
import { ManufactureService } from '@services/manufacture.service';

@Component({
  selector: 'app-manufacturer-page',
  templateUrl: './manufacturer-page-content.component.html',
  styleUrls: [],
})
export class ManufacturerPageComponent {
  readonly templateName$: Observable<string> = this.pageLayoutService.templateName$;
  readonly slots$: Observable<string[]> = this.pageLayoutService.getSlots();
  hasManufacturerContent$ = this.manufacturerService.hasManufacturerContent$;

  constructor(
    protected pageLayoutService: PageLayoutService,
    private manufacturerService: ManufactureService,
  ) {}
}
