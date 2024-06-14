import { WindowRef } from '@spartacus/core';

export function navigateBack(winRef: WindowRef, event: Event) {
  event.preventDefault();
  winRef.nativeWindow.history.back();
}
