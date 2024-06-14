import { Component, OnInit } from '@angular/core';
import {
  CmsNavigationComponent,
  CmsService,
  EventService,
  Page,
  UserIdService,
  WindowRef,
  createFrom,
} from '@spartacus/core';
import { BREAKPOINT, CmsComponentData, NavigationNode } from '@spartacus/storefront';
import { faAngleDown, faCaretDown, faUpload } from '@fortawesome/free-solid-svg-icons';
import { BehaviorSubject, Observable } from 'rxjs';
import { NavigationService } from 'src/app/spartacus/services/navigation.service';
import { Router, UrlSerializer } from '@angular/router';
import { filter, first, tap, map, switchMap } from 'rxjs/operators';
import { SlideDrawerService } from '@design-system/slide-drawer/slide-drawer.service';
import { DistBreakpointService } from '@services/breakpoint.service';
import { CountryService } from 'src/app/spartacus/site-context/services/country.service';
import { HeaderInteractionEvent } from '@features/tracking/events/header-interaction-event';
import { Ga4HeaderInteractionEventType } from '@features/tracking/model/event-ga-header-types';

@Component({
  selector: 'app-main-nav',
  templateUrl: './main-nav.component.html',
  styleUrls: ['./main-nav.component.scss'],
})
export class MainNavComponent implements OnInit {
  public node$: Observable<NavigationNode>;
  public mainNav: Observable<CmsNavigationComponent> = this.componentData.data$;
  public faAngleDown = faAngleDown;
  public faCaretDown = faCaretDown;
  public faUpload = faUpload;
  public pageTemplate$: Observable<Page>;
  public navigationTypes_ = new BehaviorSubject<NavigationNode[]>(null);
  public isTablet = this.breakpointService.isDown(BREAKPOINT.md);

  constructor(
    private cmsService: CmsService,
    private service: NavigationService,
    private componentData: CmsComponentData<CmsNavigationComponent>,
    private winRef: WindowRef,
    private router: Router,
    private urlSerializer: UrlSerializer,
    private userIdService: UserIdService,
    private slideDrawerService: SlideDrawerService,
    private breakpointService: DistBreakpointService,
    private countryService: CountryService,
    private eventService: EventService,
  ) {
    this.pageTemplate$ = this.cmsService.getCurrentPage().pipe(filter<Page>(Boolean));
  }

  public ngOnInit(): void {
    this.node$ = this.userIdService.getUserId().pipe(
      switchMap(() =>
        this.service.createNavigation(this.componentData.data$).pipe(
          first(),
          tap((data: NavigationNode) => {
            this.assignNavigations(data);
          }),
        ),
      ),
    );
  }

  public handleClick(event: any, id: string, item: NavigationNode): void {
    if (item.name.toLowerCase() !== 'knowhow') {
      this.openPanelAndDispatchEvent(event, id, item, Ga4HeaderInteractionEventType.HEADER_MINI_MENU_SELECTION);
      return;
    }

    this.service
      .getNavigationNodeChildren(item.uid)
      .pipe(first())
      .subscribe((data: NavigationNode) => {
        try {
          const url = this.addQueryParamsToUrl(data.children[0].entries[0].localizedUrl, {
            dataLinkValue: data.children[0].uid,
          });
          this.winRef.location.href = url;
        } catch (e) {
          // eslint-disable-next-line no-console
          console.trace(e);
        }
      });
  }

  public get isMobile(): Observable<boolean> {
    return this.breakpointService.isDown(BREAKPOINT.md);
  }

  public get showChannelSwitcher(): Observable<boolean> {
    return this.countryService.getActive().pipe(
      map((country) => country === 'GB' || country === 'XI'),
      map((isUK) => !isUK),
    );
  }

  public openPanelAndDispatchEvent(
    event: PointerEvent,
    id: string,
    item: NavigationNode,
    eventType?: Ga4HeaderInteractionEventType,
  ): void {
    //TODO: Come back to this to stop jumpy transition when getting data since 30015
    this.slideDrawerService.openPanel(event, id);

    if (!item.children) {
      this.getNavChildrenRequest(item);
    }
    this.dispatchHeaderInteractionEvent(eventType ?? Ga4HeaderInteractionEventType.HEADER_OPEN_MANUFACTURERS_MENU);
  }

  public closePanel(): void {
    this.slideDrawerService.closePanel();
  }

  public handleNavigation(event: Event, item: NavigationNode, url: string): void {
    event.stopPropagation();
    this.dispatchHeaderInteractionEvent(Ga4HeaderInteractionEventType.HEADER_TOP_MINIMENU_SUBMENU_ITEM);
    if (item.uid !== 'NavNode_L1D_Manufacturers') {
      this.router
        .navigate([url])
        .then((hasNavigated) => (hasNavigated ? (item.active = false) : this.closePanel()))
        .catch((err) => this.closePanel());
    }
  }

  public addQueryParamsToUrl(url: string, queryParams: any): string {
    const { protocol, domain, urlPart } = this.decomposeUrl(url);

    const urlTree = this.router.createUrlTree([urlPart ? urlPart : '/'], { queryParams });

    // use default serializer for external links - to avoid prepending language tag
    const urlWithParams = domain ? urlTree.toString() : this.urlSerializer.serialize(urlTree);

    return `${protocol ? protocol + '://' : ''}${domain ? domain : ''}${urlWithParams}`;
  }

  public isAbsoluteUrl(url: string): boolean {
    if (!url) {
      return false;
    }

    const regex = /^http(s?)/;
    return regex.test(url);
  }

  onServicesClick(): void {
    this.dispatchHeaderInteractionEvent(Ga4HeaderInteractionEventType.HEADER_TOP_MINIMENU_SUBMENU_ITEM);
  }

  private getNavChildrenRequest(item: NavigationNode): void {
    this.service.getNavigationNodeChildren(item.uid).subscribe((data: NavigationNode) => {
      this.assignLoadedChildrenToNav(item, data);
    });
  }

  private assignLoadedChildrenToNav(item: NavigationNode, data: NavigationNode): void {
    const navigationTypes = this.navigationTypes_.value;
    const navigationIndex = navigationTypes.findIndex((category) => category.uid === item.uid);
    navigationTypes[navigationIndex] = { ...navigationTypes[navigationIndex], children: data.children ?? [] };
    this.navigationTypes_.next(navigationTypes);
  }

  private assignNavigations(data: NavigationNode): void {
    if (data?.children?.[0].uid === 'MainTopNavNode' || data?.children?.[0].uid === 'MainManufacturerNavNode') {
      this.navigationTypes_.next(data?.children?.[0]?.children);
    }
  }

  private decomposeUrl(url: string): { protocol?: string; domain?: string; urlPart: string } {
    const sanitisedUrl = this.sanitiseUrl(url);

    if (this.isAbsoluteUrl(sanitisedUrl)) {
      const regex = /^(http[s?]):\/\/([^\/]+)(\/[\S]*)?$/;
      if (regex.test(sanitisedUrl)) {
        const match = regex.exec(sanitisedUrl);
        return {
          protocol: match[1],
          domain: match[2],
          urlPart: match[3],
        };
      }
      return {
        protocol: '',
        domain: '',
        urlPart: '',
      };
    }
    return {
      urlPart: url,
    };
  }

  private sanitiseUrl(url: string): string {
    // removes everything after double quotes due to invalid cms data
    return url.replace(/".*/, '');
  }

  private dispatchHeaderInteractionEvent(type: Ga4HeaderInteractionEventType): void {
    this.eventService.dispatch(createFrom(HeaderInteractionEvent, { type }));
  }
}
