import { Component, NgZone, OnInit } from '@angular/core';
import { GlobalMessageEntities, GlobalMessageService, GlobalMessageType } from '@spartacus/core';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

/**
 * Customised version of the cx-global-message component from @spartacus/storefront
 */

enum MessageKey {
  AccountDeactivated = 'httpHandlers.validationErrors.missing.active',
  LoginSuccess = 'account.confirmation.signin.title',
}

@Component({
  selector: 'app-global-message',
  templateUrl: './global-message.component.html',
  styleUrls: ['./global-message.component.scss'],
})
export class GlobalMessageComponent implements OnInit {
  messageKey = MessageKey;

  messages$: Observable<GlobalMessageEntities>;
  messageType: typeof GlobalMessageType = GlobalMessageType;

  constructor(
    private globalMessageService: GlobalMessageService,
    private ngZone: NgZone,
  ) {}

  ngOnInit(): void {
    this.messages$ = this.globalMessageService.get().pipe(
      tap((data: GlobalMessageEntities) => {
        if (this.isMessagesPresent(data)) {
          // HDLS-3896
          this.removeAnyGlobalMsgsAfterTimeout();
        }
      }),
    );
  }

  removeAnyGlobalMsgsAfterTimeout() {
    this.ngZone.run(() => {
      setTimeout(() => {
        this.clearAllMessages();
      }, 7000);
    });
  }

  clearAllMessages(): void {
    this.globalMessageService.remove(this.messageType.MSG_TYPE_CONFIRMATION);
    this.globalMessageService.remove(this.messageType.MSG_TYPE_INFO);
    this.globalMessageService.remove(this.messageType.MSG_TYPE_WARNING);
    this.globalMessageService.remove(this.messageType.MSG_TYPE_ERROR);
  }

  clear(type: GlobalMessageType, index?: number): void {
    this.globalMessageService.remove(type, index);
  }

  isMessagesPresent(messages: GlobalMessageEntities): boolean {
    // TODO: replace with beh subject and async pipe in template
    return (
      messages[this.messageType.MSG_TYPE_CONFIRMATION]?.length > 0 ||
      messages[this.messageType.MSG_TYPE_INFO]?.length > 0 ||
      messages[this.messageType.MSG_TYPE_WARNING]?.length > 0 ||
      messages[this.messageType.MSG_TYPE_ERROR]?.length > 0
    );
  }
}
