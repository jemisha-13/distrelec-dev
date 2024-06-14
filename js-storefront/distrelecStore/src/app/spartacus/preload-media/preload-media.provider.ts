import { APP_INITIALIZER, Provider } from '@angular/core';
import { WindowRef } from '@spartacus/core';

const LINK_PRELOAD_MEDIA_ID = 'preloaded-media';
function getMediaDomainValue(url: string): string {
  const hostnameSplit = new URL(url).host.split('.')[0];
  const mediaUrl = 'MEDIA_BACKEND_BASE_URL_VALUE';

  if (hostnameSplit === 'www') {
    return 'api.media.distrelec.com';
  }

  if (hostnameSplit === 'pretest') {
    return 'pretest.media.distrelec.com';
  }

  if (hostnameSplit === 'test') {
    return 'test.media.distrelec.com';
  }

  return mediaUrl;
}

function addLinkTag(winRef: WindowRef): void {
  const link: HTMLElement = winRef.document.createElement('link');
  link.setAttribute('preload', getMediaDomainValue(winRef.location.origin));
  link.setAttribute('id', LINK_PRELOAD_MEDIA_ID);
  winRef.document.head.appendChild(link);
}

function init(winRef: WindowRef) {
  if (hasLinkBeenInjected(winRef)) {
    return;
  }

  return addLinkTag(winRef);
}

function hasLinkBeenInjected(winRef: WindowRef): boolean {
  return winRef.document.getElementById(LINK_PRELOAD_MEDIA_ID) !== null;
}

export const preloadMediaDomainProvider: Provider[] = [
  {
    provide: APP_INITIALIZER,
    useFactory: (winRef: WindowRef) => () => init(winRef),
    deps: [WindowRef],
    multi: true,
  },
];
