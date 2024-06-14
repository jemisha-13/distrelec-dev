import {NgModule} from '@angular/core';
import {EditorFieldMappingService, moduleUtils, SelectModule} from "smarteditcommons";
import {DistVideoEditorComponent} from "./DistVideoEditorComponent";
import {DistVideoSelectorItemComponent} from "./DistVideoSelectorItemComponent";
import {CommonModule} from "@angular/common";

@NgModule({
    imports: [SelectModule, CommonModule],
    declarations: [DistVideoEditorComponent, DistVideoSelectorItemComponent],
    entryComponents: [DistVideoEditorComponent, DistVideoSelectorItemComponent],
    providers: [
        moduleUtils.bootstrap(
            (editorFieldMappingService: EditorFieldMappingService) => {
                editorFieldMappingService.addFieldMapping('VideoMedia', null, null, {
                    component: DistVideoEditorComponent
                });
            },
            [EditorFieldMappingService]
        )
    ]
})
export class DistVideoEditorModule {
}
