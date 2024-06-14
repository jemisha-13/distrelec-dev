import { Injectable } from '@angular/core';
import { SearchExperienceFactory } from '@features/pages/product/core/dynamic/search-experience.factory';
import { SessionService } from '../../services/abstract-session.service';

@Injectable()
export class DynamicSessionService implements SessionService {
  private sessionService;

  constructor(private factory: SearchExperienceFactory) {}

  getSessionId() {
    if (!this.sessionService) {
      this.createSessionService();
    }

    return this.sessionService.getSessionId();
  }

  setSessionId(id: string): void {
    if (!this.sessionService) {
      this.createSessionService();
    }

    this.sessionService.setSessionId(id);
  }

  private createSessionService(): void {
    this.sessionService = this.factory.createSessionService();
  }
}
