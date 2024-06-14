import { Component } from '@angular/core';
import { CmsWarningComponent } from '@model/cms.model';
import { CmsComponentData } from '@spartacus/storefront';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-disruption-message',
  templateUrl: './disruption-message.component.html',
  styleUrls: ['./disruption-message.component.scss'],
})
export class DisruptionMessageComponent {
  bannerComponent$: Observable<CmsWarningComponent> = this.cms.data$;
  constructor(private cms: CmsComponentData<CmsWarningComponent>) {}
}
