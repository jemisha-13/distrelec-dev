import { TranslateWithDefaultPipe } from './translate-with-default.pipe';
import { TranslationService } from '@spartacus/core';
import { ChangeDetectorRef } from '@angular/core';
import createSpy = jasmine.createSpy;

describe('TranslateWithDefaultPipe', () => {
  let pipe: TranslateWithDefaultPipe;
  let service: TranslationService;
  let cd: ChangeDetectorRef;

  beforeEach(() => {
    service = {
      translate: () => {},
    } as any;
    cd = { markForCheck: createSpy('markForCheck') } as any;
    pipe = new TranslateWithDefaultPipe(service, cd);
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });
});
