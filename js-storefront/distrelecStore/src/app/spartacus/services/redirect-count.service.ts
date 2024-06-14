import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class RedirectCountService {
  private count = 0;

  increment(): void {
    this.count++;
  }

  exceeds(threshold: number): boolean {
    const exceeds = this.count > threshold;
    if (exceeds) {
      console.warn('Redirect count exceeded threshold ' + threshold);
    }
    return exceeds;
  }

  reset(): void {
    this.count = 0;
  }
}
