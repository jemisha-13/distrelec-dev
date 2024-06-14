import { Component } from '@angular/core';
import { CmsComponentData } from '@spartacus/storefront';
import { CmsHeadlineComponent } from '@model/cms.model';

@Component({
  selector: 'app-headline',
  templateUrl: './headline.component.html',
  styleUrls: ['./headline.component.scss'],
})
export class DistHeadlineComponent {
  constructor(public component: CmsComponentData<CmsHeadlineComponent>) {}
}
