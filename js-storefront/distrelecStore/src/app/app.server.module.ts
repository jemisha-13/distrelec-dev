import { NgModule } from '@angular/core';
import { ServerModule, ServerTransferStateModule } from '@angular/platform-server';

import { AppModule } from './app.module';
import { AppComponent } from './app.component';
import { fixServerRequestProviders } from './serverRequestProviders';

import { DefaultLanguageService } from '@services/default-language.service';
import { ServerDefaultLanguageService } from '@services/default-language.server.service';
import { DistCookieService } from '@services/dist-cookie.service';
import { DistServerCookieService } from '@services/dist-cookie.server.service';
import { provideServer } from '@spartacus/setup/ssr';

@NgModule({
  imports: [
    // The AppServerModule should import your AppModule followed
    // by the ServerModule from @angular/platform-server.
    AppModule,
    ServerModule,
    ServerTransferStateModule,
  ],
  providers: [
    fixServerRequestProviders,
    {
      provide: DefaultLanguageService,
      useClass: ServerDefaultLanguageService,
    },
    {
      provide: DistCookieService,
      useClass: DistServerCookieService,
    },
    ...provideServer({
      serverRequestOrigin: process.env['SERVER_REQUEST_ORIGIN'],
    }),
  ],
  bootstrap: [AppComponent],
})
export class AppServerModule {}
