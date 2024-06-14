import { Component, ViewEncapsulation } from '@angular/core';
import { CmsComponentData } from '@spartacus/storefront';
import { Observable } from 'rxjs';
import { CmsWelcomeMatComponent } from '@model/cms.model';

@Component({
  selector: 'app-welcome-mat',
  templateUrl: './welcome-mat.component.html',
  styleUrls: ['./welcome-mat.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class WelcomeMatComponent {
  data$: Observable<CmsWelcomeMatComponent> = this.component.data$;

  constructor(public component: CmsComponentData<CmsWelcomeMatComponent>) {}
}
