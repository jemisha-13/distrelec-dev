/* eslint-disable guard-for-in */
import { Injectable } from '@angular/core';
import { WindowRef } from '@spartacus/core';

@Injectable({ providedIn: 'root' })
export class ScriptService {
  constructor(private winRef: WindowRef) {}

  public appendScript(attributes: Partial<HTMLScriptElement>, isHeadScript?: boolean): HTMLScriptElement {
    const script = this.winRef.document.createElement('script');
    for (const attribute in attributes) {
      script[attribute] = attributes[attribute];
    }
    this.winRef.document.body.appendChild(script);
    return script;
  }

  public appendScriptToHead(attributes: Partial<HTMLScriptElement>): HTMLScriptElement {
    const script = this.winRef.document.createElement('script');
    for (const attribute in attributes) {
      script[attribute] = attributes[attribute];
    }
    this.winRef.document.head.appendChild(script);
    return script;
  }

  public appendNoscript(noScriptUrl: string): HTMLElement {
    const noscript = this.winRef.document.createElement('noscript');

    const iframe = this.winRef.document.createElement('iframe');
    iframe.setAttribute('src', noScriptUrl);
    iframe.setAttribute('width', '0');
    iframe.setAttribute('height', '0');
    iframe.style.display = 'none';

    noscript.appendChild(iframe);

    this.winRef.document.body.insertBefore(noscript, this.winRef.document.body.firstChild);

    return noscript;
  }
}
