import { Component, OnDestroy, OnInit } from '@angular/core';
import { Observable, Subscription } from 'rxjs';
import { PageLayoutService } from '@spartacus/storefront';
import { EventService, LoginEvent } from '@spartacus/core';

@Component({
  selector: 'app-bom-tool-upload-page',
  templateUrl: './bom-tool-upload-page.component.html',
  styleUrls: ['./bom-tool-upload-page.component.scss'],
})
export class BomToolUploadPageComponent implements OnInit, OnDestroy {
  readonly templateName$: Observable<string> = this.pageLayoutService.templateName$;
  readonly slots$: Observable<string[]> = this.pageLayoutService.getSlots();

  showLoginSuccessMessage = false;

  private subscription = new Subscription();

  constructor(
    private pageLayoutService: PageLayoutService,
    private eventService: EventService,
  ) {}

  ngOnInit() {
    this.subscription.add(this.eventService.get(LoginEvent).subscribe(() => (this.showLoginSuccessMessage = true)));
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  scroll(element: HTMLElement) {
    element.scrollIntoView({ behavior: 'smooth', block: 'center' });
  }
}
