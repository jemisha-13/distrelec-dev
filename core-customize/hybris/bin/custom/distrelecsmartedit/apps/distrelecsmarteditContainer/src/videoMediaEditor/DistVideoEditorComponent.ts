import {Component, Inject, Type, ViewEncapsulation} from '@angular/core';
import {
    GENERIC_EDITOR_WIDGET_DATA,
    GenericEditorWidgetData,
    ICatalogService,
    RestServiceFactory
} from "smarteditcommons";
import {
    DistVideoSelectorItemComponent
} from "./DistVideoSelectorItemComponent";

export interface VideoMedia {
    id: string;
    code: string;
    description: string;
    altText: string;
    url: string;
    downloadUrl: string;
    youtubeUrl: string;
}

@Component({
    selector: 'video-attribute-editor',
    styleUrls: ['./DistVideoEditorComponent.scss'],
    templateUrl: './DistVideoEditorComponent.html',
    encapsulation: ViewEncapsulation.None
})
export class DistVideoEditorComponent {

    public itemComponent: Type<any> = DistVideoSelectorItemComponent;

    public fetchStrategy: {
        fetchPage: any;
        fetchEntity: any;
    };

    constructor(@Inject(GENERIC_EDITOR_WIDGET_DATA) public data: GenericEditorWidgetData<any>,
                private restServiceFactory: RestServiceFactory,
                private catalogService: ICatalogService) {
        this.buildFetchStrategy();
    }

    private buildFetchStrategy(): void {
        this.catalogService.getCatalogVersionUUid()
            .then(currentCatalogVersionUuid => {
                this.fetchStrategy = {
                    fetchEntity: (id) => {
                        return this.restServiceFactory
                            .get("/distrelecsmarteditwebservices/distvideomedia/" + id + "?catalogVersionUuid=" + currentCatalogVersionUuid)
                            .get()
                            .then((response: any) => {
                                return Promise.resolve({
                                    id: response.uuid,
                                    code: response.code,
                                    description: response.description,
                                    altText: response.altText,
                                    url: response.url,
                                    downloadUrl: response.downloadUrl,
                                    youtubeUrl: response.youtubeUrl
                                });
                            });
                    },
                    fetchPage: (search, pageSize, currentPage) => {
                        return this.restServiceFactory.get("/distrelecsmarteditwebservices/distvideomedia?currentPage=" + currentPage + "&mask=" + search + "&pageSize=" + pageSize + "&catalogVersionUuid=" + currentCatalogVersionUuid)
                            .get()
                            .then((response: any) => {
                                return Promise.resolve({
                                    results: response.map((item: any) => ({
                                        id: item.uuid,
                                        code: item.code,
                                        description: item.description,
                                        altText: item.altText,
                                        url: item.url,
                                        downloadUrl: item.downloadUrl,
                                        youtubeUrl: item.youtubeUrl
                                    }))
                                });
                            });
                    }
                }
            });
    }

    protected readonly DistVideoSelectorItemComponent = DistVideoSelectorItemComponent;
}
