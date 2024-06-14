import { Component, Input } from '@angular/core';
import { caretRight } from '@assets/icons/icon-index';
@Component({
  selector: 'app-breadcrumbs',
  templateUrl: './breadcrumbs.component.html',
  styleUrls: ['./breadcrumbs.component.scss'],
})
export class BreadcrumbsComponent {
  @Input() breadcrumbsContainerID = 'breadcrumbs_container';
  @Input() crumbLinkID: string;
  @Input() crumbNameID: string;
  @Input() breadcrumbsArray;
  caretRight = caretRight;
}
