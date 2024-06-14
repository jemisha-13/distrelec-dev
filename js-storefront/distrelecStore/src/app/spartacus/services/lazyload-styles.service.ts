import { Injectable } from '@angular/core';
import { WindowRef } from '@spartacus/core';

@Injectable({ providedIn: 'root' })
export class LazyLoadStylesService {
  constructor(private winRef: WindowRef) {}

  injectStyles(fileName: string): void {
    const head = this.winRef.document.getElementsByTagName('head')[0];
    const style = this.winRef.document.createElement('link');

    style.rel = 'stylesheet';
    style.href = fileName;

    head.appendChild(style);
  }
}
