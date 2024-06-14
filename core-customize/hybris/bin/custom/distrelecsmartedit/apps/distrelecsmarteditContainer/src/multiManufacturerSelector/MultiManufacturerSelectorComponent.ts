import { Component, Inject, OnInit, Type } from '@angular/core';
import { GenericEditorWidgetData, GENERIC_EDITOR_WIDGET_DATA, RestServiceFactory } from 'smarteditcommons';
import { ManufacturerItemComponent } from './ManufacturerItemComponent';

@Component({
    selector: 'multi-manufacturer-selector',
    templateUrl: './MultiManufacturerSelectorComponent.html'
})
export class MultiManufacturerSelectorComponent implements OnInit {

    public itemComponent: Type<any> = ManufacturerItemComponent;
    public itemsFetchStrategy: {
        fetchPage: any;
        fetchEntity: any;
    };

    constructor(@Inject(GENERIC_EDITOR_WIDGET_DATA) public data: GenericEditorWidgetData<any>, private restServiceFactory: RestServiceFactory) {}

    ngOnInit(): void {
        this.itemsFetchStrategy = {
            fetchPage: (mask: string, pageSize: number, currentPage: number) => {
                return this.restServiceFactory.get("/distrelecsmarteditwebservices/manufacturers?currentPage=" + currentPage + "&mask=" + mask + "&pageSize=" + pageSize).get().then((response: any) => {
                    return Promise.resolve({
                        results: response.map((item: any) => ({
                            id: item.code,
                            name: item.name
                        }))
                    });
                });
            },
            fetchEntity: (code: any) => {
                return this.restServiceFactory.get("/distrelecsmarteditwebservices/manufacturers/" + code).get().then((response: any) => {
                    return Promise.resolve({
                        id: response.code,
                        name: response.name
                    });
                });
            }
        };
    }

    onItemsSelectorChange(): void {}

    resetItemsListSelector(): void {}

}
