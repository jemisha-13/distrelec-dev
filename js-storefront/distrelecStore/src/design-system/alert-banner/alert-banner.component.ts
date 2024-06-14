import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { checkCircle, cross, exclamationCircle, infoIcon, warning } from '@assets/icons/icon-index';
import { DistIcon } from '@model/icon.model';

@Component({
  selector: 'app-alert-banner',
  templateUrl: './alert-banner.component.html',
  styleUrls: ['./alert-banner.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class AlertBannerComponent implements OnInit {
  @Input() bannerType: 'general' | 'positive' | 'warning' | 'negative' | 'more-information' = 'general';
  @Input() alertType: 'system-alert' | 'regular-alert' | 'alert-box' = 'system-alert';

  icon: DistIcon;
  cross = cross;

  alertHidden = false;

  ngOnInit(): void {
    switch (this.bannerType) {
      case 'general':
      case 'more-information':
        this.icon = infoIcon;
        break;
      case 'warning':
        this.icon = exclamationCircle;
        break;
      case 'positive':
        this.icon = checkCircle;
        break;
      case 'negative':
        this.icon = warning;
        break;
    }
  }

  crossClick() {
    this.alertHidden = true;
  }
}
