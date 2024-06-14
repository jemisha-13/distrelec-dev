import { Component, Input, ViewEncapsulation } from '@angular/core';
import { AppendComponentService } from 'src/app/spartacus/services/append-component.service';
import { close } from '@assets/icons/icon-index';
import { WindowRef } from '@spartacus/core';

@Component({
  selector: 'app-content-popup',
  templateUrl: './content-popup.component.html',
  styleUrls: ['./content-popup.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ContentPopupComponent {
  @Input() data: {
    title: string;
    content: string;
  };

  close = close;

  constructor(
    private appendComponentService: AppendComponentService,
    private winRef: WindowRef,
  ) {}

  onCloseButton() {
    this.appendComponentService.removeContentPopupFromBody();
  }

  goToOrderingPage() {
    this.winRef.nativeWindow.location.href = 'https://www.distrelec.ch/en/ordering/cms/ordering?marketingPopup=false';
  }
}
