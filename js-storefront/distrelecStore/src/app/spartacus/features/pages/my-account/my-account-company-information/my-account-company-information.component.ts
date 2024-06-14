import { Component, OnDestroy, OnInit } from '@angular/core';
import { Observable, Subscription } from 'rxjs';
import { OccEndpointsService } from '@spartacus/core';
import { HttpClient } from '@angular/common/http';
import { shareReplay } from 'rxjs/operators';
import { CountryService } from 'src/app/spartacus/site-context/services/country.service';

@Component({
  selector: 'app-account-company-information',
  templateUrl: './my-account-company-information.component.html',
  styleUrls: ['./my-account-company-information.component.scss'],
})
export class MyAccountCompanyInformationComponent implements OnInit, OnDestroy {
  companyInformation$: Observable<any>;
  activeSiteId = '';
  vatCountryOutput = '';
  activeCountrySubscription: Subscription;

  constructor(
    private http: HttpClient,
    private countryService: CountryService,
    private occEndpoints: OccEndpointsService,
  ) {}

  ngOnInit() {
    this.companyInformation$ = this.http
      .get<any>(this.occEndpoints.buildUrl('/users/current/company-information?fields=BASIC'))
      .pipe(shareReplay({ bufferSize: 1, refCount: true }));
    this.activeCountrySubscription = this.countryService
      .getActive()
      .subscribe((siteId) => (this.activeSiteId = siteId));
  }

  ngOnDestroy(): void {
    if (this.activeCountrySubscription && !this.activeCountrySubscription.closed) {
      this.activeCountrySubscription.unsubscribe();
    }
  }

  // this function would help show correct vatId even for the records where VatID is saved without country code
  getVatId(vatId?: string): string {
    if (vatId) {
      return (this.vatCountryOutput = vatId.substring(0, 2) !== this.activeSiteId ? this.activeSiteId + vatId : vatId);
    }
    return '';
  }
}
