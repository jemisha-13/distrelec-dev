import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { SessionService } from '../../services/abstract-session.service';

@Injectable()
export class FusionSessionService extends SessionService {
  private sessionId = new BehaviorSubject<string | null>(null);

  getSessionId(): Observable<string | null> {
    return this.sessionId.asObservable();
  }

  setSessionId(id: string | null): void {
    if (id !== this.sessionId.getValue()) {
      this.sessionId.next(id);
    }
  }
}
