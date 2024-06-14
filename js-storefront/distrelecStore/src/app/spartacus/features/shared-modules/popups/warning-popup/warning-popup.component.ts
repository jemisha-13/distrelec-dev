import { Component, Input } from '@angular/core';
import { AppendComponentService } from '@services/append-component.service';

@Component({
  selector: 'app-warning-popup',
  templateUrl: './warning-popup.component.html',
  styleUrls: ['./warning-popup.component.scss'],
})
export class WarningPopupComponent {
  @Input() data: {
    title?: string;
    titleKey?: string;
    subtitle: string;
    subtitleKey: string;
    type: string;
  };

  constructor(private appendComponentService: AppendComponentService) {}

  onCloseButton() {
    this.appendComponentService.removeWarningPopupComponent();
  }
}
