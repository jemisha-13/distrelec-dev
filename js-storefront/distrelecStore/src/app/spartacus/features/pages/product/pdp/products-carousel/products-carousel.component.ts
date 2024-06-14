import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProductReferences } from '@model/product-reference.model';
import { ProductDataService } from '@features/pages/product/core/services/product-data.service';
import { ProductReferencesService } from '@features/pages/product/core/services/product-references.service';
import { Observable } from 'rxjs';
import { first } from 'rxjs/operators';
import { ICustomProduct } from '@model/product.model';

@Component({
  selector: 'app-products-carousel',
  templateUrl: './products-carousel.component.html',
  styleUrls: ['./products-carousel.component.scss'],
})
export class ProductsCarouselComponent implements OnInit {
  productCode: string;
  reevooEligible: boolean;
  productReferences$: Observable<ProductReferences>;
  isLoading$: Observable<boolean>;

  constructor(
    private activatedRoute: ActivatedRoute,
    private productDataService: ProductDataService,
    public productReferenceService: ProductReferencesService,
  ) {}

  ngOnInit() {
    this.productCode = this.activatedRoute.snapshot.params.productCode;
    this.isLoading$ = this.productDataService.isLoading(this.productCode);
    this.productReferences$ = this.productReferenceService.getProductReferencesData(this.productCode);

    this.productDataService
      .getProductData(this.productCode)
      .pipe(first())
      .subscribe((data: ICustomProduct) => (this.reevooEligible = data?.eligibleForReevoo));
  }
}
