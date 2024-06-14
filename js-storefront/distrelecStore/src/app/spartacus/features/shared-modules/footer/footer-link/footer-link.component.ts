import { Component, Input } from '@angular/core';
import { FooterInteractionEvent } from '@features/tracking/events/ga4/ga4-footer-interaction-event';
import { CmsLinkComponent, createFrom, EventService, WindowRef } from '@spartacus/core';

declare global {
  interface Window {
    // eslint-disable-next-line @typescript-eslint/naming-convention
    Bootstrapper: any;
  }
}

@Component({
  selector: 'app-footer-link',
  templateUrl: './footer-link.component.html',
})
export class FooterLinkComponent {
  @Input() component: CmsLinkComponent;
  @Input() id?: string;

  constructor(
    private winRef: WindowRef,
    private eventService: EventService,
  ) {}

  openPrivacyBanner() {
    if (this.winRef.isBrowser()) {
      try {
        this.winRef.nativeWindow.Bootstrapper.privacy.openBanner();
      } catch (e) {
        console.error(e);
      }
    }
  }

  dispatchInteractionEvent(): void {
    this.eventService.dispatch(
      createFrom(FooterInteractionEvent, {
        context: {
          // eslint-disable-next-line @typescript-eslint/naming-convention
          menu_name: 'Links',
          // eslint-disable-next-line @typescript-eslint/naming-convention
          menu_item: this.component?.linkName,
        },
      }),
    );
  }
}
