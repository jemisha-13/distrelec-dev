import { Injectable } from '@angular/core';
import { CxEvent, EventService, WindowRef } from '@spartacus/core';

@Injectable({
  providedIn: 'root',
})
export class IntersectionObserverService {
  constructor(
    private eventService: EventService,
    private winRef: WindowRef,
  ) {}

  observeViewport(element: HTMLElement, callback?: () => void): void {
    if (!this.winRef.isBrowser()) {
      return;
    }

    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.intersectionRatio < 1 && entry.intersectionRatio > 0.5 && entry.isIntersecting) {
            callback();
            observer.unobserve(entry.target);
          }
        });
      },
      {
        threshold: 0.5,
      },
    );

    observer.observe(element);
  }
}
