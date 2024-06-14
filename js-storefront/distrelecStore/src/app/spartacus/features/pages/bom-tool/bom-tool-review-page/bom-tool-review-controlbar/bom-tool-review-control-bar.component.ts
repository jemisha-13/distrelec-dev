import { Component, OnInit } from '@angular/core';
import { BomToolReviewService } from '@features/pages/bom-tool/bom-tool-review.service';

@Component({
  selector: 'app-bom-tool-review-control-bar',
  templateUrl: './bom-tool-review-control-bar.component.html',
  styleUrls: ['./bom-tool-review-control-bar.component.scss'],
})
export class BomToolReviewControlBarComponent implements OnInit {
  isDetailedView$ = this.service.getDetailedView();
  areAllSelected$ = this.service.getAllSelected();

  constructor(private service: BomToolReviewService) {}

  ngOnInit() {
    this.service
      .getAllSelected()
      .subscribe((areAllSelected) => (areAllSelected = areAllSelected))
      .unsubscribe();
  }

  onDetailViewChange(event) {
    const checkbox = event.target as HTMLInputElement;
    this.service.setDetailedView(checkbox.checked);
  }

  onSelectAllChange(event) {
    const checkbox = event.target as HTMLInputElement;
    this.service.setAllSelected(checkbox.checked);
  }
}
