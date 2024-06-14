import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import {
  CURRENCY_CONTEXT_ID,
  CurrencyService,
  getContextParameterDefault,
  LANGUAGE_CONTEXT_ID,
  LanguageService,
  OccEndpointsService,
  SiteContextConfig,
} from '@spartacus/core';
import { Observable } from 'rxjs';
import { ChannelService } from 'src/app/spartacus/site-context/services/channel.service';
import { CountryService } from 'src/app/spartacus/site-context/services/country.service';
import { Params } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class SiteContextInterceptor implements HttpInterceptor {
  activeLang: string;
  activeCurr: string;
  activeChannel: string;
  activeCountry: string;

  constructor(
    private languageService: LanguageService,
    private currencyService: CurrencyService,
    private occEndpoints: OccEndpointsService,
    private config: SiteContextConfig,
    private channelService: ChannelService,
    private countryService: CountryService,
  ) {
    this.activeLang = getContextParameterDefault(this.config, LANGUAGE_CONTEXT_ID);
    this.activeCurr = getContextParameterDefault(this.config, CURRENCY_CONTEXT_ID);

    this.languageService.getActive().subscribe((data) => (this.activeLang = data));

    this.currencyService.getActive().subscribe((data) => (this.activeCurr = data));

    this.channelService.getActive().subscribe((channel) => (this.activeChannel = channel));

    this.countryService.getActive().subscribe((data) => (this.activeCountry = data));
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (request.url.includes(this.occEndpoints.getBaseUrl())) {
      request = request.clone({
        setParams: this.getContextParams(),
      });
    }

    return next.handle(request);
  }

  private getContextParams(): Params {
    const contextParams: Params = {
      lang: this.activeLang,
      curr: this.activeCurr,
    };

    if (this.activeChannel) {
      contextParams.channel = this.activeChannel;
    }

    if (this.activeCountry) {
      contextParams.country = this.activeCountry;
    }

    return contextParams;
  }
}
