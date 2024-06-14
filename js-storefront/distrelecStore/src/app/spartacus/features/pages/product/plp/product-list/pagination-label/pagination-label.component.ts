import { Component, Input, OnChanges, OnInit } from '@angular/core';
import { PaginationModel } from '@spartacus/core';

@Component({
  selector: 'app-pagination-label',
  templateUrl: './pagination-label.component.html',
  styleUrls: ['./pagination-label.component.scss'],
})
export class PaginationLabelComponent implements OnInit, OnChanges {
  @Input() paginationResults: PaginationModel;

  min?: number;
  max?: number;
  totalResults?: number;

  ngOnInit() {
    this.setValues();
  }

  ngOnChanges(): void {
    this.setValues();
  }

  private setValues() {
    if (!this.paginationResults) {
      return;
    }

    const { currentPage, pageSize, totalResults } = this.paginationResults;

    this.min = pageSize * (currentPage - 1) + 1;
    this.max = pageSize * currentPage > totalResults ? totalResults : pageSize * currentPage;
    this.totalResults = totalResults;
  }
}
