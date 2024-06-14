import { Injectable } from '@angular/core';
import { WindowRef } from '@spartacus/core';

class MemoryStorage implements Storage {
  [name: string]: any;

  readonly length: number;

  clear(): void {}

  getItem(key: string): string {
    return this[key];
  }

  key(index: number): string | null {
    return undefined;
  }

  removeItem(key: string): void {
    delete this[key];
  }

  setItem(key: string, value: any): void {
    this[key] = value;
  }
}

@Injectable({
  providedIn: 'root',
})
export class SessionStorageService implements Storage {
  [name: string]: any;

  length: number;

  private storage: Storage;

  constructor(private winRef: WindowRef) {
    if (this.winRef.isBrowser()) {
      this.storage = this.winRef.nativeWindow.sessionStorage;
    } else {
      this.storage = new MemoryStorage();
    }
  }

  clear(): void {
    this.storage.clear();
  }

  getItem(key: string): any {
    const value = this.storage.getItem(key);
    return value ? JSON.parse(value) : null;
  }

  key(index: number): string | null {
    return this.storage.key(index);
  }

  removeItem(key: string): void {
    return this.storage.removeItem(key);
  }

  setItem(key: string, value: any): void {
    return this.storage.setItem(key, JSON.stringify(value));
  }
}
