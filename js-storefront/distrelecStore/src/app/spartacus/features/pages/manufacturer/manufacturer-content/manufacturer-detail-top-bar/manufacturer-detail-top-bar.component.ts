import { Component } from '@angular/core';
import { Location } from '@angular/common';
import { ProductSearchPage } from '@spartacus/core';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { faArrowLeft } from '@fortawesome/free-solid-svg-icons';
import { ManufactureService } from '@services/manufacture.service';
import { ManufacturerData } from '@model/manufacturer.model';
import { DistProductListComponentService } from '@features/pages/product/core/services/dist-product-list-component.service';

@Component({
  selector: 'app-manufacturer-detail-top-bar',
  templateUrl: './manufacturer-detail-top-bar.component.html',
  styleUrls: ['./manufacturer-detail-top-bar.component.scss'],
})
export class ManufacturerDetailTopBarComponent {
  manufacturerData$: Observable<ManufacturerData> = this.manufacturerService.getCurrentManufacturerData();
  selectedCategory$: Observable<string> = this.plpService.searchResults$.pipe(
    filter<ProductSearchPage>(Boolean),
    map((searchResults) => {
      if (searchResults.categoryBreadcrumbs?.length) {
        return searchResults.categoryBreadcrumbs[searchResults.categoryBreadcrumbs.length - 1].name;
      }
      return '';
    }),
  );
  faArrowLeft = faArrowLeft;

  constructor(
    private manufacturerService: ManufactureService,
    private location: Location,
    private plpService: DistProductListComponentService,
  ) {}

  goBack(): void {
    this.location.back();
  }
}
