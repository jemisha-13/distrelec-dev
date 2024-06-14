import { of } from 'rxjs';

export class MockLanguageService {
  getActive() {
    return of('en');
  }
}
