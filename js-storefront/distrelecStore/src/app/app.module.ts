import { HTTP_INTERCEPTORS, HttpClientModule, provideHttpClient, withFetch } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserModule, Meta } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { ServiceWorkerModule } from '@angular/service-worker';
import { KeyboardFocusModule, PageLayoutModule, PageSlotModule, SkipLinkModule } from '@spartacus/storefront';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import {
  AuthHttpHeaderService,
  AuthService,
  ClientTokenInterceptor,
  CMS_PAGE_NORMALIZER,
  I18nModule,
  WithCredentialsInterceptor,
} from '@spartacus/core';

import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { environment } from '@environment';

import { AppRoutingModule } from './app-routing.module';
import { SpartacusModule } from './spartacus/spartacus.module';

import { preloadMediaDomainProvider } from './spartacus/preload-media/preload-media.provider';
import { DistWithCredentialsInterceptor } from '@interceptors/dist-with-credentials.interceptor';

import { CmsPageNormalizer } from '@converters/cms-page-normalizer';
import { BrowserDefaultLanguageService } from '@services/default-language.browser.service';
import { DefaultLanguageService } from '@services/default-language.service';
import { DistrelecAuthService } from '@services/distrelec-auth.service';
import { DistAuthHttpHeaderService } from '@services/dist-auth-http-header.service';
import { DistCookieService } from '@services/dist-cookie.service';
import { DistBrowserCookieService } from '@services/dist-cookie.browser.service';

import { SiteContextModule } from './spartacus/site-context/site-context.module';
import { SharedModule } from '@features/shared-modules/shared.module';
import { RedirectModule } from '@features/redirect/redirect.module';
import { DistrelecRecaptchaModule } from '@features/shared-modules/recaptcha/recaptcha.module';
import { PopupModule } from '@features/shared-modules/popups/popup.module';
import { DistJsonLdModule } from '@features/shared-modules/directives/dist-json-ld.module';
import { TemplateAwarePageLayoutModule } from '@features/pages/page-layout/template-aware-page-layout.module';

import { AppComponent } from './app.component';
import { ComponentLoadingSpinnerModule } from '@features/shared-modules/component-loading-spinner/component-loading-spinner.module';

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule.withServerTransition({ appId: 'spartacus-app' }),
    HttpClientModule,
    RouterModule,
    AppRoutingModule,
    SiteContextModule,
    ReactiveFormsModule,
    FontAwesomeModule,
    I18nModule,
    StoreModule.forRoot({}),
    EffectsModule.forRoot([]),
    SpartacusModule,
    BrowserAnimationsModule,
    DistrelecRecaptchaModule,
    ComponentLoadingSpinnerModule,
    PageLayoutModule,
    PageSlotModule,
    SharedModule,
    RedirectModule,
    PopupModule,
    DistJsonLdModule,
    !environment.production
      ? StoreDevtoolsModule.instrument({
          maxAge: 100,
          logOnly: false,
          autoPause: true,
          features: {
            pause: false,
            lock: true,
            persist: true,
          },
          connectInZone: true,
        })
      : [],
    KeyboardFocusModule,
    SkipLinkModule,
    environment.production ? ServiceWorkerModule.register('./service-worker.js') : [],
    TemplateAwarePageLayoutModule, // This should be the last imported module so that it can override all configured routes
  ],
  providers: [
    provideHttpClient(withFetch()),
    ...preloadMediaDomainProvider,
    Meta,
    {
      provide: DefaultLanguageService,
      useClass: BrowserDefaultLanguageService,
    },
    {
      provide: DistCookieService,
      useClass: DistBrowserCookieService,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ClientTokenInterceptor,
      multi: true,
    },
    {
      provide: CMS_PAGE_NORMALIZER,
      useClass: CmsPageNormalizer,
      multi: true,
    },
    {
      provide: AuthService,
      useExisting: DistrelecAuthService,
    },
    {
      provide: AuthHttpHeaderService,
      useExisting: DistAuthHttpHeaderService,
    },
    {
      provide: WithCredentialsInterceptor,
      useClass: DistWithCredentialsInterceptor,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
