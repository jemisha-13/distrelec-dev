import { Component, Input, OnInit } from '@angular/core';
import { AttributeItem } from '@features/pages/compare/components/compare-list/compare-list.component';
import { Attribute, ICustomProduct } from '@model/product.model';

@Component({
  selector: 'app-attribute-list',
  templateUrl: './attribute-list.component.html',
  styleUrls: ['./attribute-list.component.scss'],
})
export class AttributeListComponent implements OnInit {
  @Input() product: ICustomProduct;
  @Input() attributes: AttributeItem[] = [];

  attributesByCode: Record<string, Partial<Attribute>> = {};

  ngOnInit(): void {
    this.collectAttributes();
  }

  private collectAttributes() {
    // Must go first, can be overwritten by the others with actual values
    this.product.possibleOtherAttributes.entry?.forEach((attribute) => {
      this.attributesByCode[attribute.key] = { code: attribute.key, name: attribute.value };
    });

    this.product.otherAttributes?.forEach((attribute) => {
      this.attributesByCode[attribute.value.code] = attribute.value;
    });

    this.product.commonAttrs?.forEach((attribute) => {
      this.attributesByCode[attribute.code] = attribute;
    });
  }
}
