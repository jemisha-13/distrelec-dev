import { Inject, Injectable } from '@angular/core';
import { DOCUMENT } from '@angular/common';

export interface LinkDefinition {
  rel?: string;
  href: string;
  hreflang?: string;
}

@Injectable({
  providedIn: 'root',
})
export class LinkService {
  constructor(@Inject(DOCUMENT) private doc: HTMLDocument) {}

  addLink(definition: LinkDefinition) {
    // @ts-ignore
    const linkElement = this.createLinkElement(definition);
    this.doc.head.appendChild(linkElement);
  }

  getLink(target: string): NodeListOf<HTMLLinkElement> {
    return this.doc.head.querySelectorAll(`link[${target}]`);
  }

  updateLink(definition: LinkDefinition, target: string) {
    const existingLinkElements = this.doc.querySelectorAll(`link[${target}]`);
    const newLinkElement = this.createLinkElement(definition);
    existingLinkElements.forEach((element) => this.doc.head.replaceChild(newLinkElement, element));
  }

  removeLink(target: string) {
    const existingLinkElements = this.doc.querySelectorAll(`link[${target}]`);
    existingLinkElements.forEach((element) => this.doc.head.removeChild(element));
  }

  private createLinkElement(definition: LinkDefinition): HTMLLinkElement {
    const linkElement: HTMLLinkElement = this.doc.createElement('link');
    if (definition.rel) {
      linkElement.rel = definition.rel ?? null;
    }
    linkElement.href = definition.href;
    if (definition.hreflang) {
      linkElement.hreflang = definition.hreflang;
    }
    return linkElement;
  }
}
