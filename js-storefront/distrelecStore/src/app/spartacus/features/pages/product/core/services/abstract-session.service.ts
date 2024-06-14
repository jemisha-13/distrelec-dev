import { Observable } from 'rxjs';

export abstract class SessionService {
  abstract getSessionId(): Observable<string | null>;

  abstract setSessionId(sessionId: string): void;
}
