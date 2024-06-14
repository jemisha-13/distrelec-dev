/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { mockTranslate } from './mock-translate';
import { TranslationService } from '@spartacus/core';

@Injectable()
export class MockTranslationService implements TranslationService {
  translate(key: string, options: any = {}, _whitespaceUntilLoaded = false): Observable<string> {
    return new Observable<string>((subscriber) => {
      const value = mockTranslate(key, options);
      subscriber.next(value);
      subscriber.complete();
    });
  }

  loadChunks(_chunks: string | string[]): Promise<any> {
    return Promise.resolve();
  }
}
