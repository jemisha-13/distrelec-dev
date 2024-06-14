import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { GlobalMessageType, HttpErrorHandler, Priority } from '@spartacus/core';

@Injectable({
  providedIn: 'root',
})
export class TooManyRequestsHandler extends HttpErrorHandler {
  responseStatus = 429; // https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/429

  private eventTransIdRegex = /var _event_transid='([^']+)';/;

  handleError(_, response: HttpErrorResponse): void {
    const eventId = this.extractResponseId(response);
    if (eventId) {
      this.globalMessageService.add(
        { key: 'httpHandlers.botProtection', params: { eventId } },
        GlobalMessageType.MSG_TYPE_ERROR,
      );
    } else {
      this.globalMessageService.add({ key: 'form.support_failed' }, GlobalMessageType.MSG_TYPE_ERROR);
    }
  }

  getPriority(): Priority {
    return Priority.NORMAL;
  }

  private extractResponseId({ headers, error }: HttpErrorResponse): string | undefined {
    const contentType = headers.get('Content-Type');
    let eventId;
    if (contentType === 'application/json') {
      eventId = error._event_transid;
    } else {
      const match = this.eventTransIdRegex.exec(error);
      eventId = match[1];
    }
    return eventId;
  }
}
