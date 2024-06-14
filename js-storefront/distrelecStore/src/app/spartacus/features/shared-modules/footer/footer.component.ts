import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { CmsComponent, CmsService, createFrom, EventService, Page, WindowRef } from '@spartacus/core';
import { CmsComponentData, NavigationNode } from '@spartacus/storefront';
import { Observable } from 'rxjs';
import { filter, map, switchMap, tap } from 'rxjs/operators';
import { facebookLg, twitterLg, youtubeLg, linkedinLg } from '@assets/icons';
import { NavigationService } from '@services/navigation.service';
import { DistrelecUserService } from '@services/user.service';
import { FooterInteractionEvent } from '@features/tracking/events/ga4/ga4-footer-interaction-event';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class FooterComponent implements OnInit {
  pageTemplate$: Observable<Page> = this.cmsService.getCurrentPage().pipe(
    filter<Page>(Boolean),
    tap((page) => (this.isNewsletterDisplayed = this.shouldNewsletterBeDisplayed(page?.label))),
    tap((page) => (this.isCheckoutPage = this.isCheckoutPageTemplate(page.template))),
  );
  componentData$: Observable<CmsComponent> = this.cms.data$;
  footerNav$: Observable<NavigationNode[][]>;

  fullYear: number;
  yearMonth: string;
  currentTime: string;
  isMobile: boolean = this.winRef.isBrowser() ? this.winRef.nativeWindow.innerWidth < 992 : false;
  isNewsletterDisplayed = true;
  isCheckoutPage = false;

  facebookLg = facebookLg;
  twitterLg = twitterLg;
  youtubeLg = youtubeLg;
  linkedinLg = linkedinLg;

  private readonly componentSlotIds: string[] = [
    'paymentMethods',
    'socialMedias',
    'trademarks',
    'impressumLinks',
    'countryLinks',
  ];

  constructor(
    private cms: CmsComponentData<CmsComponent>,
    private componentService: CmsService,
    private cmsService: CmsService,
    private navigationService: NavigationService,
    private winRef: WindowRef,
    private distUserService: DistrelecUserService,
    private eventService: EventService,
  ) {}

  ngOnInit(): void {
    this.footerNav$ = this.distUserService.userDetails_.pipe(
      switchMap(() =>
        this.navigationService
          .createNavigation(this.cms.data$)
          .pipe(map((item) => item.children.map((k) => k.children))),
      ),
    );
    const date = new Date();
    this.fullYear = date.getFullYear();
    this.yearMonth = date.toISOString().replace('-', '/').split('T')[0].replace('-', '/');

    this.currentTime = date.toLocaleTimeString([], { timeStyle: 'short' } as any); // as any due to TS bug: https://github.com/microsoft/TypeScript/issues/38266
  }

  onResize(): void {
    this.isMobile = this.winRef.isBrowser() ? this.winRef.nativeWindow.innerWidth < 992 : false;
  }

  isCheckoutPageTemplate(pageTemplate: string): boolean {
    return pageTemplate === 'CheckoutPageTemplate';
  }

  getComponentsBySlotId(): Observable<Map<string, Observable<CmsComponent>[]>> {
    return this.componentData$.pipe(
      map((footerComponentData) => {
        const componentsBySlotId = new Map<string, Observable<CmsComponent>[]>();

        this.componentSlotIds.forEach((componentSlotId) => {
          componentsBySlotId.set(
            componentSlotId,
            footerComponentData[componentSlotId]?.split(' ').map((id) => this.componentService.getComponentData(id)) ??
              [],
          );
        });

        return componentsBySlotId;
      }),
    );
  }

  icon(iconKey: string) {
    switch (iconKey) {
      case 'fab-facebook':
        return facebookLg;
      case 'fab-linkedin':
        return linkedinLg;
      case 'fab-twitter':
        return twitterLg;
      case 'fab-youtube':
        return youtubeLg;
    }
  }
  dispatchInteractionEvent(menuName: string, menuItem: string): void {
    this.eventService.dispatch(
      createFrom(FooterInteractionEvent, {
        context: {
          // eslint-disable-next-line @typescript-eslint/naming-convention
          menu_name: menuName,
          // eslint-disable-next-line @typescript-eslint/naming-convention
          menu_item: menuName === 'Social Icons' ? menuItem.split('-')[1] : menuItem,
        },
      }),
    );
  }

  private shouldNewsletterBeDisplayed(url: string): boolean {
    return !!url && !url.includes('account/password/setinitialpw') && !url.includes('/login/pw/request');
  }
}
