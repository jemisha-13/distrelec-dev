import { Component, ElementRef, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild } from '@angular/core';
import { ProductAvailability } from '@model/product-availability.model';
import { QuickOrderEntry } from '@features/pages/homepage/homepage-banner/quick-order/quick-order.model';
import { AppendComponentService } from '@services/append-component.service';
import { ProductAvailabilityService } from '@features/pages/product/core/services/product-availability.service';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { first } from 'rxjs/operators';
import { ArticleNumberPipe } from '@pipes/article-number.pipe';
import { EnergyEfficiencyLabelService } from '@features/shared-modules/energy-efficiency-label/energy-efficiency-label.service';
import { Suggestion } from '@spartacus/core';
import { DistSearchBoxService } from '@features/pages/product/core/services/dist-search-box.service';

@Component({
  selector: 'app-quick-order-row',
  templateUrl: './quick-order-row.component.html',
  styleUrls: ['./quick-order-row.component.scss'],
})
export class QuickOrderRowComponent implements OnInit, OnDestroy {
  @Input() rowNumber: number;
  @Input() signal: BehaviorSubject<null>;
  @Input() submittedEmptyOrder: boolean;
  @Output() data = new EventEmitter<QuickOrderEntry>();

  @ViewChild('qty')
  qty: ElementRef;

  showDropdown = true;
  isQtyDisabled = true;
  productResults$: Observable<Suggestion>;
  minQuantity = 1;
  productCode = '';
  selectedQuantity = 0;
  hasErrors = false;

  private articleNumberPipe: ArticleNumberPipe = new ArticleNumberPipe();

  private subscriptions: Subscription = new Subscription();

  constructor(
    private productAvailabilityService: ProductAvailabilityService,
    private appendComponentService: AppendComponentService,
    private energyEfficencyService: EnergyEfficiencyLabelService,
    private distSearchBoxService: DistSearchBoxService,
  ) {}

  ngOnInit() {
    this.subscriptions.add(
      this.signal.subscribe(() => {
        if (!this.isQtyDisabled) {
          this.validateQuantity(this.selectedQuantity.toString());
        }
        if (this.validateEntry()) {
          this.data.emit(<QuickOrderEntry>{ productCode: this.productCode, selectedQuantity: this.selectedQuantity });
        }
      }),
    );
  }

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
  }

  onSearch(value: string): void {
    this.distSearchBoxService.clearResults();

    if (!value || value.length < 3) {
      this.isQtyDisabled = true;
      this.hasErrors = false;
      this.qty.nativeElement.value = '';
      this.showDropdown = false;
      this.distSearchBoxService.clearResults();

      return;
    }

    if (value.length >= 3) {
      this.showDropdown = true;
      this.distSearchBoxService.searchSuggestions(value, {});
      this.productResults$ = this.distSearchBoxService.getSuggestResults();
    }
  }

  selectedProduct(productCode, minQuantity) {
    this.showDropdown = false;
    this.isQtyDisabled = false;
    this.productCode = this.articleNumberPipe.transform(productCode);
    this.checkStockAvailability(productCode);
    this.minQuantity = minQuantity;
    this.distSearchBoxService.clearResults();
  }

  validateEntry(): boolean {
    return this.productCode && this.selectedQuantity && !this.hasErrors ? true : false;
  }

  validateQuantity(value: string) {
    if (value === '') {
      this.hasErrors = true;
      return;
    }

    // eslint-disable-next-line radix
    const convertedValue = parseInt(value);

    if (convertedValue < this.minQuantity) {
      this.hasErrors = true;
    } else {
      this.hasErrors = false;
      this.selectedQuantity = convertedValue;
    }
  }

  checkStockAvailability(productCode: string) {
    this.productAvailabilityService
      .getAvailability(productCode)
      .pipe(first())
      .subscribe((data: ProductAvailability) => {
        if (data?.stockLevelTotal === 0 && data?.leadTimeErp === 0) {
          this.appendComponentService.appendWarningPopup(
            null,
            'lightboxstatus.error.title',
            null,
            'cart.nonstock.phaseout.product',
            'error',
          );
        }
      });
  }
  getEnergyLabel(energyUrl): void {
    const parsedUrl = JSON.parse(energyUrl);
    return parsedUrl.Energyclasses_LOV;
  }

  getEnergyUrl(uri): string {
    return this.energyEfficencyService.getAbsoluteImageUrl(uri);
  }

  validateEnergy(energyData): boolean {
    if (energyData && energyData !== '{}') {
      return true;
    } else {
      return false;
    }
  }
}
