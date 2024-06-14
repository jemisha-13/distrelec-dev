import { ChangeDetectorRef, Component, Input } from '@angular/core';
import { BomFileMpnEntry } from '@features/pages/bom-tool/model/bom-file';

import { ICustomProduct } from '@model/product.model';

@Component({
  selector: 'app-bom-mpn-duplicate-product',
  templateUrl: './bom-mpn-duplicate-product.component.html',
  styleUrls: ['./bom-mpn-duplicate-product.component.scss'],
})
export class BomMpnDuplicateProductComponent {
  @Input() entry: BomFileMpnEntry;
  @Input() index: number;

  showAlternatives = false;

  constructor(private changeDetector: ChangeDetectorRef) {}

  toggleAlternatives() {
    this.showAlternatives = !this.showAlternatives;
  }

  onSelect(product: ICustomProduct) {
    this.entry.selectedAlternative = product;
    this.showAlternatives = false;
    this.changeDetector.detectChanges();
  }

  onRemove() {
    this.entry.selectedAlternative = undefined;
    this.entry.isSelected = false;
  }

  filterValid(entries: ICustomProduct[]) {
    return entries.filter((entry) => entry.salesStatus);
  }
}
