import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class LabelConfigService {
  constructor() {}

  getColorByLabel(label: string): string {
    switch (label) {
      case 'top':
        return 'dark';
      case 'new':
        return 'dark';
      case 'offer':
        return 'neutral';
      case 'noMover':
        return 'success';
      case 'hit':
        return 'warning';
      case 'hotOffer':
        return 'error';
      case 'bestseller':
        return 'error';
      default:
        return 'dark';
    }
  }
}
