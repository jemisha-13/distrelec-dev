/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {FormsModule} from "@angular/forms";
import {EditorFieldMappingService, IFileValidation, IMediaToUpload, moduleUtils, SeEntryModule, SelectModule} from 'smarteditcommons';
import {CmsSiteListEditorComponent} from './cmsSiteListEditor';
import {MapAttributeEditorComponent} from './mapAttributeEditor';
import {ManufacturerItemComponent, MultiManufacturerSelectorComponent} from './multiManufacturerSelector';
import {DistFileValidationService} from './fileValidation';
import {DistMediaUploaderService} from './mediaService';
import {DistVideoEditorModule} from "./videoMediaEditor/DistVideoEditorModule";

@SeEntryModule('distrelecsmarteditContainer')
@NgModule({
    imports: [BrowserModule, FormsModule, SelectModule, DistVideoEditorModule],
    declarations: [MapAttributeEditorComponent, CmsSiteListEditorComponent, MultiManufacturerSelectorComponent, ManufacturerItemComponent],
    entryComponents: [MapAttributeEditorComponent, CmsSiteListEditorComponent, MultiManufacturerSelectorComponent, ManufacturerItemComponent],
    providers: [
        {
            provide: IFileValidation,
            useClass: DistFileValidationService
        },
        {
            provide: IMediaToUpload,
            useClass: DistMediaUploaderService
        },
        moduleUtils.bootstrap(
            (editorFieldMappingService: EditorFieldMappingService) => {
                editorFieldMappingService.addFieldMapping('Map', null, null, {
                    component: MapAttributeEditorComponent
                });

                editorFieldMappingService.addFieldMapping('CMSSiteList', null, null, {
                    component: CmsSiteListEditorComponent
                });

                editorFieldMappingService.addFieldMapping('MultiManufacturerSelector', null, null, {
                    component: MultiManufacturerSelectorComponent
                });
            },
            [EditorFieldMappingService]
        )
    ]
})
export class DistrelecsmarteditContainerModule {

    constructor() {
        this.loadScript("/distrelecsmartedit/js/ckEditorFix.js");
    }

    private loadScript(scriptUrl: string) {
        let chatScript = document.createElement("script");
        chatScript.type = "text/javascript";
        chatScript.async = true;
        chatScript.src = scriptUrl;
        document.body.appendChild(chatScript);
    }

}
