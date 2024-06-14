/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { MockTranslatePipe } from './mock-translate.pipe';
import { MockTranslationService } from './mock-translation.service';
import { MockDatePipe } from './mock-date.pipe';
import { TranslationService } from '@spartacus/core';

@NgModule({
  declarations: [MockTranslatePipe, MockDatePipe],
  exports: [MockTranslatePipe, MockDatePipe],
  providers: [{ provide: TranslationService, useClass: MockTranslationService }],
})
export class I18nTestingModule {}
