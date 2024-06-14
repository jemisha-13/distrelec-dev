import { SearchExperienceService } from '@features/pages/product/core/services/search-experience.service';

export class MockSearchExperienceService implements Partial<SearchExperienceService> {
  public getCategoryKey(): string {
    return 'categoryCodes';
  }
  public getCategoryParam(): string {
    return '';
  }
}
