import { ChangeDetectorRef, isDevMode, OnDestroy, Pipe, PipeTransform } from '@angular/core';
import { Translatable, TranslatableParams, TranslationService } from '@spartacus/core';
import { Subscription } from 'rxjs';

interface TranslatableParamsWithDefault extends TranslatableParams {
  defaultValue?: string;
}

@Pipe({
  name: 'cxTranslateWithDefault',
  pure: false,
})
export class TranslateWithDefaultPipe implements PipeTransform, OnDestroy {
  private lastKey: string;
  private lastOptions: any;
  private translatedValue: string;
  private sub: Subscription;
  private key: string;
  private defaultValue?: string;

  constructor(
    private service: TranslationService,
    private cd: ChangeDetectorRef,
  ) {}

  transform(input: Translatable | string, options: TranslatableParamsWithDefault = {}): string {
    if (!input) {
      if (isDevMode()) {
        console.error(`The given input for the cxTranslate pipe (${input}) is invalid and cannot be translated`);
      }
      return '';
    }

    if ((input as Translatable).raw) {
      return (input as Translatable).raw ?? '';
    }

    this.key = typeof input === 'string' ? input : input.key;
    if (typeof input !== 'string') {
      options = { ...options, ...input.params };
    }

    if (options.defaultValue) {
      this.defaultValue = options.defaultValue;
    }

    this.translate(this.key, options);
    return this.translatedValue;
  }

  ngOnDestroy(): void {
    if (this.sub) {
      this.sub.unsubscribe();
    }
  }

  private translate(key: any, options: any) {
    if (key !== this.lastKey || !this.shallowEqualObjects(options, this.lastOptions)) {
      this.lastKey = key;
      this.lastOptions = options;

      if (this.sub) {
        this.sub.unsubscribe();
      }
      this.sub = this.service.translate(key, options, !this.defaultValue).subscribe((val) => this.markForCheck(val));
    }
  }

  private shallowEqualObjects(objA: any, objB: any): boolean {
    if (objA === objB) {
      return true;
    }
    if (!objA || !objB) {
      return false;
    }
    const aKeys = Object.keys(objA);
    const bKeys = Object.keys(objB);
    const aKeysLen = aKeys.length;
    const bKeysLen = bKeys.length;

    if (aKeysLen !== bKeysLen) {
      return false;
    }
    for (let i = 0; i < aKeysLen; i++) {
      const key = aKeys[i];
      if (objA[key] !== objB[key]) {
        return false;
      }
    }
    return true;
  }

  private markForCheck(value: string) {
    this.translatedValue = this.getValueOrDefault(value);
    this.cd.markForCheck();
  }

  private getValueOrDefault(value: string) {
    if (isDevMode()) {
      if (value === `[${this.key}:${this.key}]`) {
        return this.defaultValue ?? value;
      }
    } else {
      if (!value.trim()) {
        return this.defaultValue ?? value;
      }
    }
    return value;
  }
}
