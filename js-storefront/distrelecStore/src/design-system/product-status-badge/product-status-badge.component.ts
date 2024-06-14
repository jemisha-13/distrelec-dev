import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-dist-product-badge',
  templateUrl: './product-status-badge.component.html',
  styleUrls: ['./product-status-badge.component.scss'],
})
export class ProductStatusBadgeComponent {
  @Input() filter: 'offer' | 'new-to-us' | 'new' | 'bundle' = 'offer';
  @Input() text = 'OFFER';

  @Input() ids = {
    badgeId: 'badge_id_text',
  };
}
