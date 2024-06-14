import { Component, Inject } from '@angular/core';
import { GenericEditorWidgetData, GENERIC_EDITOR_WIDGET_DATA } from 'smarteditcommons';

@Component({
    selector: 'map-attribute-editor',
    templateUrl: './MapAttributeEditorComponent.html'
})
export class MapAttributeEditorComponent {
    constructor(@Inject(GENERIC_EDITOR_WIDGET_DATA) public data: GenericEditorWidgetData<any>) {}

    addRow(): void {
        this.data.model[this.data.field.qualifier].push({
            "key": "",
            "value": ""
        });
    }

    removeRow(index: number): void {
        this.data.model[this.data.field.qualifier].splice(index, 1);
    }
}
