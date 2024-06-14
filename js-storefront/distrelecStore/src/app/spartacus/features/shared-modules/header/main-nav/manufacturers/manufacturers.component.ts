import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { distinct, filter, map, mergeMap, take, tap, toArray } from 'rxjs/operators';
import { ManufactureService } from 'src/app/spartacus/services/manufacture.service';
import { ManufacturerListResponse } from '@model/manufacturer.model';
import { CmsComponent, CmsService, EventService, RoutingService, WindowRef, createFrom } from '@spartacus/core';
import { SlideDrawerService } from '@design-system/slide-drawer/slide-drawer.service';
import { HeaderInteractionEvent } from '@features/tracking/events/header-interaction-event';
import { Ga4HeaderInteractionEventType } from '@features/tracking/model/event-ga-header-types';

interface DistMainNavigationComponentData extends CmsComponent {
  distBannerComponentsList?: string;
}

const featuredManufacturersCount = 6;

@Component({
  selector: 'app-manufacturers',
  templateUrl: './manufacturers.component.html',
  styleUrls: ['./manufacturers.component.scss'],
})
export class ManufacturersComponent implements OnInit, OnDestroy {
  @Input() manufacturerLogos: Observable<DistMainNavigationComponentData>;

  public isDataLoaded$ = new BehaviorSubject<boolean>(false);
  public parentComponents$: Observable<CmsComponent[]>;
  public manufacturers$: Observable<ManufacturerListResponse[]> = this.manufactureService.getManufactures().pipe(
    map((item: any) => item.response),
    tap(() => this.isDataLoaded$.next(true)),
  );

  private subscriptions: Subscription = new Subscription();

  constructor(
    private manufactureService: ManufactureService,
    private componentService: CmsService,
    private winRef: WindowRef,
    private routeingService: RoutingService,
    private slideDrawerService: SlideDrawerService,
    private eventService: EventService,
  ) {}

  public scrollToKey(event: PointerEvent, key: string): void {
    event.preventDefault();
    const target: HTMLElement | null = this.winRef.document.getElementById(`man-starts-with-${key}`);
    if (target !== null) {
      target.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }

  public ngOnInit(): void {
    this.parentComponents$ = this.manufacturerLogos.pipe(
      distinct((item: DistMainNavigationComponentData) => item?.uid),
      mergeMap((item: DistMainNavigationComponentData) => item?.distBannerComponentsList.split(' ')),
      mergeMap((componentId: string) =>
        this.componentService.getComponentData(componentId).pipe(filter<CmsComponent>(Boolean), take(1)),
      ),
      take(featuredManufacturersCount),
      toArray(),
    );
  }

  public ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  public navigate(url: string): void {
    this.routeingService.goByUrl(url);
  }

  public closePanel(): void {
    this.slideDrawerService.closePanel();
  }

  dispatchHeaderInteractionEvent(): void {
    this.eventService.dispatch(
      createFrom(HeaderInteractionEvent, { type: Ga4HeaderInteractionEventType.HEADER_MANUFACTURER_MENU_LINK_CLICK }),
    );
  }
}
