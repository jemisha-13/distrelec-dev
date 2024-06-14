import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { OccEndpointsService, SiteContextUrlSerializer, WindowRef } from '@spartacus/core';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { CurrentSiteSettings } from '@model/site-settings.model';

@Injectable({
  providedIn: 'root',
})
export class DocumentRedirectGuard  {
  constructor(
    private occEndpointsService: OccEndpointsService,
    private winRef: WindowRef,
    private settingService: AllsitesettingsService,
    private siteContextUrlSerializer: SiteContextUrlSerializer,
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | Observable<boolean> {
    const { url, params } = this.siteContextUrlSerializer.urlExtractContextParameters(state.url);
    if (url.includes('compliance-document/')) {
      const baseUrl = this.occEndpointsService.getBaseUrl();
      const documentPath = this.transformDocumentPath(url);
      this.winRef.location.href = `${baseUrl}/${documentPath}?lang=${params.language ?? ''}`;
      return true;
    }

    if (url.startsWith('medias/')) {
      const documentPath = url;

      this.settingService.init();
      return this.settingService.currentChannelData$.pipe(
        filter<CurrentSiteSettings>(Boolean),
        map((settings: CurrentSiteSettings) => {
          this.winRef.location.href = `${settings.mediaDomain}/${documentPath}`;
          return true;
        }),
      );
    }

    return true;
  }

  private transformDocumentPath(documentPath: string): string {
    // nginx on prod envs will rewrite . to __ because by default it will try and serve a static file if it detects an extension in the route
    // Path rewrite configuration is in core-customize/hybris/cloudportal_config/redirect_sets/redirect_headless.txt
    return documentPath.replace(/^(\/compliance-document\/.*)__([0-9a-z]{3,4})$/, '$1.$2');
  }
}
