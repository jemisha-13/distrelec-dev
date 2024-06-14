import { of } from 'rxjs';

export class MockPageLayoutService {
  get page$() {
    return of({ template: 'LandingPage2Template' });
  }
  get templateName$() {
    return of('LandingPage2Template');
  }

  getSlots() {
    return of(['Section1', 'Section2']);
  }

  getPageFoldSlot() {
    return of(undefined);
  }
}
