import { NgModule } from '@angular/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Store, StoreModule } from '@ngrx/store';
import { OAuthModule, OAuthService, UrlHelperService } from 'angular-oauth2-oidc';
import {
  AuthService,
  BaseSiteService,
  contextServiceMapProvider,
  LanguageService,
  OAuthLibWrapperService,
  RoutingService,
  SiteContextParamsService,
  SiteContextUrlSerializer,
  TranslationService,
} from '@spartacus/core';
import { MockDistBaseSiteService } from '@features/mocks/services/mock-basesite.service';
import { MockLanguageService } from '@features/mocks/services/mock-language.service';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { FontAwesomeTestingModule } from '@fortawesome/angular-fontawesome/testing';
import { MockTranslationService } from '@testing/i18n/mock-translation.service';
import { MockRoutingService } from '@testing/mocks/services/mock-routing-service';

import '@model/index';

@NgModule({
  imports: [
    HttpClientTestingModule,
    RouterTestingModule,
    StoreModule.forRoot({}),
    OAuthModule.forRoot(),
    DistIconModule,
    FontAwesomeTestingModule,
  ],
  providers: [
    Store,
    OAuthService,
    UrlHelperService,
    OAuthLibWrapperService,
    AuthService,
    SiteContextUrlSerializer,
    SiteContextParamsService,
    contextServiceMapProvider,
    { provide: BaseSiteService, useClass: MockDistBaseSiteService },
    { provide: LanguageService, useClass: MockLanguageService },
    { provide: TranslationService, useClass: MockTranslationService },
    { provide: RoutingService, useClass: MockRoutingService },
  ],
  exports: [HttpClientTestingModule, RouterTestingModule, DistIconModule, FontAwesomeTestingModule],
})
export class CommonTestingModule {}
