import { StaticProvider } from '@angular/core';
import { REQUEST } from '@spartacus/setup/ssr';
import { Request } from 'express';
import { SERVER_REQUEST_ORIGIN, SERVER_REQUEST_URL } from '@spartacus/core';
import { getRequestOrigin } from '../server/utils';

export const fixServerRequestProviders: StaticProvider[] = [
  {
    provide: SERVER_REQUEST_URL,
    useFactory: getRequestUrl,
    deps: [REQUEST],
  },
  {
    provide: SERVER_REQUEST_ORIGIN,
    useFactory: getRequestOrigin,
    deps: [REQUEST],
  },
];

function getRequestUrl(req: Request): string {
  return getRequestOrigin(req) + req.originalUrl;
}
