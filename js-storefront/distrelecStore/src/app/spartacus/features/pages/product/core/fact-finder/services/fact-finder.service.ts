import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { combineLatest } from 'rxjs';
import { IFactFinderRecommendations } from '@model/factfinder.model';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { FactFinderSessionService } from './fact-finder-session.service';
import { OrderEntry } from '@spartacus/cart/base/root';
import { map, switchMap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class FactFinderService {
  private selectedCategory: string;

  constructor(
    private http: HttpClient,
    private allSiteSettingsService: AllsitesettingsService,
    private factFinderSessionService: FactFinderSessionService,
  ) {}

  getFactFinderEnvironment() {
    return this.allSiteSettingsService.factFinderEnvironment();
  }

  getSessionId() {
    return this.factFinderSessionService.getSessionId();
  }

  setCategoryCode(categoryCode) {
    this.selectedCategory = categoryCode;
  }

  get categoryCode() {
    return this.selectedCategory;
  }

  //getRecommendations() can be dropped when business gets rid of cart reccomendations so can the internals of the constructor
  getRecommendations(cartEntries: OrderEntry[], maxResults: number) {
    return combineLatest([this.getSessionId(), this.getFactFinderEnvironment()]).pipe(
      map(([sessionId, env]) => {
        let recommenderUrl = `${env.ffrecoUrl}?do=getRecommendation&channel=${env.ffsearchChannel}&format=json&maxResults=${maxResults}&sid=${sessionId}`;

        cartEntries.forEach((entry) => {
          recommenderUrl = recommenderUrl.concat(`&id=${entry.product.code}`);
        });

        return recommenderUrl;
      }),
      switchMap((recommenderUrl) => this.http.get<IFactFinderRecommendations>(recommenderUrl)),
    );
  }

  getFormattedTerm(query: string): string {
    const qryEncWhitespace = query.replace(/ /g, '+');
    return qryEncWhitespace.replace(/-/g, '');
  }
}
