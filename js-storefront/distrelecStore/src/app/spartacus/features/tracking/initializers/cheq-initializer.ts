import { Injectable } from '@angular/core';
import { ScriptService } from '@services/script.service';
import { environment } from '@environment';
import { TrackingInitializer } from '../tracking-initializer/tracking-initializer';

@Injectable({
  providedIn: 'root',
})
export class CheqInitializer implements TrackingInitializer {
  constructor(private scriptService: ScriptService) {}

  init(): void {
    if (!environment.production) {
      return;
    }

    this.scriptService.appendScriptToHead({
      id: 'cheq-tracking-script',
      src: 'https://ob.thisgreencolumn.com/i/8a1b68c74fac63154312522bef9ff4e7.js',
      async: true,
    });

    this.scriptService.appendNoscript(
      'https://obs.thisgreencolumn.com/ns/8a1b68c74fac63154312522bef9ff4e7.html?ch=cheq4ppc',
    );
  }
}
