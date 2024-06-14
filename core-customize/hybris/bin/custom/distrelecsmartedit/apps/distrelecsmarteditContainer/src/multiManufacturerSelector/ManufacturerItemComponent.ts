import { Component, Inject } from '@angular/core';
import { ITEM_COMPONENT_DATA_TOKEN, ItemComponentData } from 'smarteditcommons';

@Component({
    selector: 'manufacturer-item',
    templateUrl: './ManufacturerItemComponent.html'
})
export class ManufacturerItemComponent {
    constructor(@Inject(ITEM_COMPONENT_DATA_TOKEN) public data: ItemComponentData) {}

}
