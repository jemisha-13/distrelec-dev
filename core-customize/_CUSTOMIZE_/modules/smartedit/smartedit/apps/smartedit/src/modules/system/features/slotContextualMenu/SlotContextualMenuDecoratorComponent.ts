/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    Component,
    DoCheck,
    ElementRef,
    forwardRef,
    Inject,
    Input,
    OnChanges,
    OnDestroy,
    OnInit,
    SimpleChanges
} from '@angular/core';
import { throttle, uniq } from 'lodash';
import { filter } from 'rxjs/operators';

import {
    ComponentAttributes,
    HIDE_SLOT_MENU,
    IContextualMenuButton,
    IContextualMenuService,
    NodeUtils,
    REFRESH_CONTEXTUAL_MENU_ITEMS_EVENT,
    SeCustomComponent,
    SystemEventService,
    SHOW_SLOT_MENU,
    YJQUERY_TOKEN,
    CrossFrameEventService,
    EVENT_PAGE_TREE_SLOT_SELECTED,
    TypedMap,
    IPageInfoService,
    RestServiceFactory,
    PAGE_CONTEXT_SITE_ID,
    PAGE_CONTEXT_CATALOG,
    PAGE_CONTEXT_CATALOG_VERSION
} from 'smarteditcommons';
import { AbstractContextualMenuComponent } from '../AbstractContextualMenuComponent';
import {
    SE_DECORATIVE_BODY_PADDING_LEFT_CLASS,
    SE_DECORATIVE_BODY_PADDING_RIGHT_CLASS,
    SE_DECORATIVE_PANEL_AREA_CLASS,
    SE_DECORATIVE_BODY_AREA_CLASS
} from './constants';

@SeCustomComponent('slot-contextual-menu')
@Component({
    selector: 'slot-contextual-menu',
    templateUrl: './SlotContextualMenuDecoratorComponent.html',
    styleUrls: ['../../../../../styling/sass/contextualMenu.scss'],
    providers: [
        {
            provide: AbstractContextualMenuComponent,
            useExisting: forwardRef(() => SlotContextualMenuDecoratorComponent)
        }
    ]
})
export class SlotContextualMenuDecoratorComponent
    implements AbstractContextualMenuComponent, OnInit, OnDestroy, OnChanges, DoCheck
{
    @Input('data-smartedit-component-type') public smarteditComponentType: string;
    @Input('data-smartedit-component-id') public smarteditComponentId: string;
    @Input('data-smartedit-container-type') public smarteditContainerType: string;
    @Input('data-smartedit-container-id') public smarteditContainerId: string;
    @Input('data-smartedit-slot-id') public smarteditSlotId: string;
    @Input('data-smartedit-slot-uuid') public smarteditSlotUuid: string;
    @Input('data-smartedit-catalog-version-uuid') public smarteditCatalogVersionUuid: string;
    @Input('data-smartedit-element-uuid') public smarteditElementUuid: string;
    @Input('data-smartedit-slot-display-name') public smarteditSlotDisplayName: string;
    @Input() public componentAttributes: ComponentAttributes;
    @Input()
    public set active(_active: string | boolean) {
        if (typeof _active === 'string') {
            this._active = _active === 'true';
        } else {
            this._active = _active;
        }
    }

    public get active(): boolean {
        return this._active;
    }

    public items: IContextualMenuButton[];
    public itemsrc: string;
    public showAtBottom: boolean;
    public remainOpenMap: TypedMap<boolean> = {};

    private oldRightMostOffsetFromPage: number = null;
    private readonly maxContextualMenuItems = 3;
    private showSlotMenuUnregFn: () => void;
    private hideSlotMenuUnregFn: () => void;
    private refreshContextualMenuUnregFn: () => void;
    private readonly hideSlotUnSubscribeFn: () => void;
    private readonly showSlotUnSubscribeFn: () => void;
    private _active: boolean;
    private readonly pageContentSlotsUri = `/cmswebservices/v1/sites/${PAGE_CONTEXT_SITE_ID}/catalogs/${PAGE_CONTEXT_CATALOG}/versions/${PAGE_CONTEXT_CATALOG_VERSION}/pagescontentslots`;

    constructor(
        private readonly element: ElementRef,
        @Inject(YJQUERY_TOKEN) private readonly yjQuery: JQueryStatic,
        private readonly systemEventService: SystemEventService,
        private readonly contextualMenuService: IContextualMenuService,
        private readonly nodeUtils: NodeUtils,
        private readonly crossFrameEventService: CrossFrameEventService,
        private pageInfoService: IPageInfoService,
        private restServiceFactory: RestServiceFactory,
    ) {
        this.showAtBottom = this.element.nativeElement.getAttribute('show-at-bottom') || false;
        const THROTTLE_DELAY = 200;
        this.positionPanelHorizontally = throttle(this.positionPanelHorizontally, THROTTLE_DELAY);
        // now 'positionPanelVertically' isn't triggered via throttle and everything is working without vertical postion determination
        // will decide later whether it should be removed or not
        this.positionPanelVertically = throttle(
            () => this.positionPanelVertically(),
            THROTTLE_DELAY
        );
        this.hidePadding = throttle(this.hidePadding, THROTTLE_DELAY);

        this.crossFrameEventService.subscribe(
            EVENT_PAGE_TREE_SLOT_SELECTED,
            (eventId, data?: any) => {
                if (this.smarteditElementUuid === data.elementUuid) {
                    this._active = data.active;
                } else {
                    this._active = false;
                }
            }
        );
    }

    isHybrisIcon(icon: string): boolean {
        return icon && icon.indexOf('hyicon') >= 0;
    }

    setRemainOpen(key: string, remainOpen: boolean): void {
        this.remainOpenMap[key] = remainOpen;
    }

    showOverlay(active: boolean): boolean {
        if (active) {
            return true;
        }

        return Object.keys(this.remainOpenMap).reduce(
            (isOpen: boolean, key: string) => isOpen || this.remainOpenMap[key],
            false
        );
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes.active) {
            this.hidePadding();
            if (this.active) {
                this.positionPanelVertically();
                this.positionPanelHorizontally();
            }
        }
    }

    ngOnInit(): void {
        this.componentAttributes = this.nodeUtils.collectSmarteditAttributesByElementUuid(
            this.smarteditElementUuid
        );

        this.updateItems();

        this.showSlotMenuUnregFn = this.systemEventService.subscribe(
            this.smarteditComponentId + SHOW_SLOT_MENU,
            (eventId: string, slotId: string): void => {
                this.remainOpenMap.slotMenuButton = true;
                this.positionPanelVertically();
                this.positionPanelHorizontally();
            }
        );

        this.hideSlotMenuUnregFn = this.systemEventService.subscribe(HIDE_SLOT_MENU, (): void => {
            if (this.remainOpenMap.slotMenuButton) {
                delete this.remainOpenMap.slotMenuButton;
            }
            this.hidePadding();
        });

        this.refreshContextualMenuUnregFn = this.systemEventService.subscribe(
            REFRESH_CONTEXTUAL_MENU_ITEMS_EVENT,
            () => this.updateItems()
        );

        this.contextualMenuService.onContextualMenuItemsAdded
            .pipe(filter((type) => type === this.smarteditComponentType))
            .subscribe(() => this.updateItems());

            const contentSlotId = this.smarteditComponentId;
            this.smarteditSlotDisplayName = contentSlotId;
            const pageContentSlotsResource = this.restServiceFactory.get(this.pageContentSlotsUri);
            this.pageInfoService.getPageUID().then((pageId: string) => {
                pageContentSlotsResource
                    .get({pageId})
                    .then((pageContentSlotsResponse: any) => {
                        return Promise.resolve(
                            uniq(pageContentSlotsResponse.pageContentSlotList || [])
                        );
                    })
                    .then((response: any) => {
                        const contentSlotData = response.find((pageContentSlot: any) => {
                            return pageContentSlot.slotId === contentSlotId;
                        });
                        if (contentSlotData) {
                            this.smarteditSlotDisplayName = contentSlotData.name;
                        }
                    });
            });
    }

    ngDoCheck(): void {
        const rightMostOffsetFromPage = this.getRightMostOffsetFromPage();
        if (
            this.active &&
            !isNaN(rightMostOffsetFromPage) &&
            rightMostOffsetFromPage !== this.oldRightMostOffsetFromPage
        ) {
            this.oldRightMostOffsetFromPage = rightMostOffsetFromPage;
            this.positionPanelHorizontally(rightMostOffsetFromPage);
        }
    }

    ngOnDestroy(): void {
        if (this.showSlotMenuUnregFn) {
            this.showSlotMenuUnregFn();
        }
        if (this.hideSlotMenuUnregFn) {
            this.hideSlotMenuUnregFn();
        }
        if (this.refreshContextualMenuUnregFn) {
            this.refreshContextualMenuUnregFn();
        }
        if (this.hideSlotUnSubscribeFn) {
            this.hideSlotUnSubscribeFn();
        }
        if (this.showSlotUnSubscribeFn) {
            this.showSlotUnSubscribeFn();
        }
    }

    updateItems(): void {
        this.contextualMenuService
            .getContextualMenuItems({
                componentType: this.smarteditComponentType,
                componentId: this.smarteditComponentId,
                containerType: this.smarteditContainerType,
                containerId: this.smarteditContainerId,
                componentAttributes: this.componentAttributes,
                iLeftBtns: this.maxContextualMenuItems,
                element: this.yjQuery(this.element.nativeElement)
            })
            .then((newItems) => {
                this.items = [...newItems.leftMenuItems, ...newItems.moreMenuItems];
            });
    }

    triggerMenuItemAction(item: IContextualMenuButton, $event: Event): void {
        item.action.callback(
            {
                componentType: this.smarteditComponentType,
                componentId: this.smarteditComponentId,
                containerType: this.smarteditContainerType,
                containerId: this.smarteditContainerId,
                componentAttributes: this.componentAttributes,
                slotId: this.smarteditSlotId,
                slotUuid: this.smarteditSlotUuid,
                element: this.yjQuery(this.element.nativeElement)
            },
            $event
        );
    }

    private hidePadding(): void {
        this.yjQuery(this.element.nativeElement)
            .find(SE_DECORATIVE_BODY_PADDING_LEFT_CLASS)
            .css('display', 'none');
        this.yjQuery(this.element.nativeElement)
            .find(SE_DECORATIVE_BODY_PADDING_RIGHT_CLASS)
            .css('display', 'none');
    }

    private getRightMostOffsetFromPage(): number {
        const $decorativePanel = this.yjQuery(this.element.nativeElement).find(
            SE_DECORATIVE_PANEL_AREA_CLASS
        );
        return this.yjQuery(this.element.nativeElement).offset().left + $decorativePanel.width();
    }

    private positionPanelHorizontally(rightMostOffsetFromPage?: number): void {
        const $decorativePanel = this.yjQuery(this.element.nativeElement).find(
            SE_DECORATIVE_PANEL_AREA_CLASS
        );

        rightMostOffsetFromPage =
            rightMostOffsetFromPage !== undefined
                ? rightMostOffsetFromPage
                : this.getRightMostOffsetFromPage();

        // Calculate if the slot is overflowing the body width.
        const isOnLeft = rightMostOffsetFromPage >= this.yjQuery('body').width();

        if (isOnLeft) {
            const offset =
                $decorativePanel.outerWidth() -
                this.yjQuery(this.element.nativeElement).find('.se-wrapper-data').width();
            $decorativePanel.css('margin-left', -offset);
            this.yjQuery(this.element.nativeElement)
                .find(SE_DECORATIVE_BODY_PADDING_LEFT_CLASS)
                .css('margin-left', -offset);
        }

        // Hide all paddings and show the left or right one.
        this.hidePadding();
        this.yjQuery(this.element.nativeElement)
            .find(
                isOnLeft
                    ? SE_DECORATIVE_BODY_PADDING_LEFT_CLASS
                    : SE_DECORATIVE_BODY_PADDING_RIGHT_CLASS
            )
            .css('display', 'flex');
    }

    private positionPanelVertically(): void {
        const decorativePanelArea = this.yjQuery(this.element.nativeElement).find(
            SE_DECORATIVE_PANEL_AREA_CLASS
        );
        const decoratorPaddingContainer = this.yjQuery(this.element.nativeElement).find(
            SE_DECORATIVE_BODY_AREA_CLASS
        );
        let marginTop;
        const height = decorativePanelArea.height();
        const marginTopOffset = -32;

        if (this.yjQuery(this.element.nativeElement).offset().top <= height) {
            const borderOffset = 6; // accounts for 3px border around the slots

            marginTop = decoratorPaddingContainer.height() + borderOffset;
            decoratorPaddingContainer.css('margin-top', -(marginTop + height));
        } else {
            marginTop = marginTopOffset;
        }
        decorativePanelArea.css('margin-top', marginTop);
    }
}
