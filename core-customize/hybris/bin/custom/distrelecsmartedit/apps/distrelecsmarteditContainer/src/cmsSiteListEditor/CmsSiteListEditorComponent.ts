import { Component, Inject, OnInit } from '@angular/core';
import {
    GenericEditorWidgetData,
    GENERIC_EDITOR_WIDGET_DATA,
    RestServiceFactory,
    IRestService,
    ISite,
    SITES_RESOURCE_URI
} from 'smarteditcommons';

interface ISiteDTO {
    sites: ISite[];
}

@Component({
    selector: 'cms-site-list-editor',
    templateUrl: './CmsSiteListEditorComponent.html'
})
export class CmsSiteListEditorComponent implements OnInit {
    selectedAssignableSite: any;
    assignableSites = [];
    allSites = [];
    siteRestService: IRestService<ISiteDTO>;

    constructor(@Inject(GENERIC_EDITOR_WIDGET_DATA) public data: GenericEditorWidgetData<any>, private restServiceFactory: RestServiceFactory) {
        this.siteRestService = restServiceFactory.get<ISiteDTO>(SITES_RESOURCE_URI);
    }

    ngOnInit(): void {
        this.getAccessibleSites().then((sites: ISite[]) => {
            this.allSites = sites.map((site: ISite) => ({
                uid: site.uid.replace("-", "_"),
                name: site.name.en
            }));

            this.calculateAssignableSites();
            this.selectedAssignableSite = null;
        });

        if (!this.data.model.displayOnSites) {
            this.data.model.displayOnSites = [];
        }
    }

    getAccessibleSites(): Promise<ISite[]> {
        return this.siteRestService.get({}).then((sitesDTO: ISiteDTO) => sitesDTO.sites);
    }

    removeSite(site: any): void {
        this.data.model.displayOnSites = this.data.model.displayOnSites.filter(displayOnSite =>
            displayOnSite.uid !== site.uid
        );

        this.calculateAssignableSites();
    }

    calculateAssignableSites(): void {
        this.assignableSites = this.allSites.filter(site =>
            !this.data.model.displayOnSites.some(displayOnSite =>
                displayOnSite.uid === site.uid
            )
        );
        this.assignableSites.sort();
    }

    assignSite(site: any): void {
        this.data.model.displayOnSites.push(site);
        this.selectedAssignableSite = null;
        this.calculateAssignableSites();
    }

}
