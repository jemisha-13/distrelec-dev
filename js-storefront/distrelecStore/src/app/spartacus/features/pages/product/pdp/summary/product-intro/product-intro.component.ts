import { Component, Input, OnInit } from '@angular/core';
import { WindowRef } from '@spartacus/core';
import { CopyProductNumberService } from '@services/copy-product-number.service';
import { ProductAvailability, StockLevel } from '@model/product-availability.model';
import { Observable } from 'rxjs';
import {
  ManufacturerCodesEnum,
  ProductAvailabilityService,
} from '@features/pages/product/core/services/product-availability.service';
import { copy } from '@assets/icons/icon-index';
import { DistBreakpointService } from '@services/breakpoint.service';

@Component({
  selector: 'app-product-intro',
  templateUrl: './product-intro.component.html',
  styleUrls: ['./product-intro.component.scss'],
})
export class ProductIntroComponent implements OnInit {
  @Input() title: string;
  @Input() brandLogo;
  @Input() code: string;
  @Input() manNumber: string;
  @Input() familyName: string;
  @Input() familyNameUrl: string;
  @Input() reevooEligible: boolean;
  @Input() productFamilyUrl: string;
  @Input() alternativeAliasMPN: string;

  productAvailability$: Observable<ProductAvailability>;

  copy = copy;

  isBrowser: boolean = this.winRef.isBrowser();
  isVisible = false;
  copiedState$ = this.copyProductNumberService.copiedState$;
  brandLogoUrl: string;
  isLessThanDesktop$ = this.breakpointService.isMobileOrTabletBreakpoint();
  isTablet$ = this.breakpointService.isTabletBreakpoint();
  isDesktop$ = this.breakpointService.isDesktopBreakpoint();

  constructor(
    private winRef: WindowRef,
    private copyProductNumberService: CopyProductNumberService,
    private productAvailabilityService: ProductAvailabilityService,
    private breakpointService: DistBreakpointService,
  ) {}

  ngOnInit(): void {
    if (this.brandLogo && this.brandLogo?.image) {
      this.brandLogo?.image.forEach((el) => {
        if (el.key === 'brand_logo') {
          this.brandLogoUrl = el.value.url;
        }
      });
    }

    this.productAvailability$ = this.productAvailabilityService.getAvailability(this.code);
  }

  isRSProduct(productAvailability: ProductAvailability): StockLevel {
    return productAvailability.stockLevels.find(
      (stockLevel) =>
        stockLevel.mview === ManufacturerCodesEnum.RS ||
        ((stockLevel.vendorNumber?.includes('209901') || stockLevel.vendorNumber?.includes('209909')) &&
          this.familyName !== 'RS PRO'),
    );
  }

  formatTitle(manNumber, title, familyName): string {
    return manNumber + ' - ' + title + ', ' + familyName;
  }

  copyText(content: string, key: string, origin?: string): void {
    this.copyProductNumberService.copyNumber(content, key, origin);
  }
}
