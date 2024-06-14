import { Observable, of } from 'rxjs';

/* eslint-disable @typescript-eslint/naming-convention */
export const MockTranslationService = {
  translate(key: string): Observable<string> {
    return of('Home');
  },
};
