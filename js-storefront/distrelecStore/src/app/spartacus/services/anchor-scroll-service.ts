import { Injectable } from '@angular/core';
import { WindowRef } from '@spartacus/core';

@Injectable({
  providedIn: 'root',
})
export class AnchorScrollService {
  constructor(private winRef: WindowRef) {}

  hasHash(element: HTMLAnchorElement): boolean {
    return !!element.hash;
  }

  isLinkForCurrentPage(anchorElement: HTMLAnchorElement): boolean {
    const currentPath = this.winRef.location.pathname;
    return anchorElement.pathname.startsWith(currentPath);
  }

  scrollToElement(id: string): void {
    const element = document.querySelector(`${id}`);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth' });
    }
  }

  hasHashLinkForCurrentPage(anchor: HTMLAnchorElement) {
    return this.hasHash(anchor) && this.isLinkForCurrentPage(anchor);
  }

  findFirstParentAnchor(event: Event) {
    let currentElement = event.target as HTMLElement;

    while (currentElement && !(currentElement instanceof HTMLAnchorElement)) {
      currentElement = currentElement.parentElement;
    }
    return currentElement instanceof HTMLAnchorElement ? currentElement : null;
  }
}
