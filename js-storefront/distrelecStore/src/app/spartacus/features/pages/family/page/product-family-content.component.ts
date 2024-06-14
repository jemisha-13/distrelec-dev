import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { PageLayoutService } from '@spartacus/storefront';

@Component({
  selector: 'app-product-family-page',
  templateUrl: './product-family-content.component.html',
  styleUrls: [],
})
export class ProductFamilyComponent {
  readonly templateName$: Observable<string> = this.pageLayoutService.templateName$;
  readonly slots$: Observable<string[]> = this.pageLayoutService.getSlots();

  constructor(protected pageLayoutService: PageLayoutService) {}
}
