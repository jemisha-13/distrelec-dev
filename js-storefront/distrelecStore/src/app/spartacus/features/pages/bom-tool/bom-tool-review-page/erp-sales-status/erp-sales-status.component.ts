import { Component, Input, OnInit } from '@angular/core';
import { ProductAvailabilityService } from '@features/pages/product/core/services/product-availability.service';

@Component({
  selector: 'app-erp-sales-status',
  templateUrl: './erp-sales-status.component.html',
  styleUrls: ['./erp-sales-status.component.scss'],
})
export class ErpSalesStatusComponent implements OnInit {
  @Input() productArtNo: string;
  @Input() productStatusCode: string;

  availability$;

  constructor(private availabilityService: ProductAvailabilityService) {}

  ngOnInit() {
    this.availability$ = this.availabilityService.getAvailability(this.productArtNo);
  }
}
