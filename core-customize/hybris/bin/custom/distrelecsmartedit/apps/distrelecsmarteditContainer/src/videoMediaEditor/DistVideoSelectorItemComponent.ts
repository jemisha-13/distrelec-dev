import {Component, Inject} from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';
import {ITEM_COMPONENT_DATA_TOKEN, ItemComponentData, Media, SelectComponent} from "smarteditcommons";

@Component({
    selector: 'video-attribute-editor',
    templateUrl: './DistVideoSelectorItemComponent.html'
})
export class DistVideoSelectorItemComponent {

    public isSelected: boolean;
    public select: SelectComponent<Media>;

    constructor(@Inject(ITEM_COMPONENT_DATA_TOKEN) public data: ItemComponentData,
                private domSanitizer: DomSanitizer) {
        this.isSelected = data.selected;
        this.select = data.select;
    }

    public getIframeUrl() {
        return this.domSanitizer.bypassSecurityTrustResourceUrl(this.data.item.youtubeUrl.replace(/^http:/, "https:"));
    }

}