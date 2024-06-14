import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ProductSpecificationsService {
  previouslyCheckedFeatureCheckboxes: string[] = []; // implemented because of: https://jira.distrelec.com/browse/HDLS-2310

  constructor() {}

  isProductFeaturesCheckboxPreviouslyChecked(checkboxId: string): boolean {
    return this.previouslyCheckedFeatureCheckboxes?.includes(checkboxId);
  }

  resetCheckBoxState(): void {
    this.previouslyCheckedFeatureCheckboxes = [];
  }

  saveCheckedCheckboxId(checkboxId: string): void {
    this.previouslyCheckedFeatureCheckboxes.push(checkboxId);
  }

  removeCheckedCheckboxId(checkboxId: string): void {
    const index = this.previouslyCheckedFeatureCheckboxes.indexOf(checkboxId, 0);
    if (index > -1) {
      this.previouslyCheckedFeatureCheckboxes.splice(index, 1);
    }
  }
}
