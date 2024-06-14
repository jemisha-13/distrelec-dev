import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Params } from '@angular/router';
import { LanguageService } from '@spartacus/core';
import { ChannelService } from 'src/app/spartacus/site-context/services/channel.service';
import { CountryService } from 'src/app/spartacus/site-context/services/country.service';
import { DistSearchBoxService } from '../../services/dist-search-box.service';
import { combineLatest, Observable } from 'rxjs';
import { FusionSiteContextParams } from '@features/pages/product/core/fusion/model/site-context-params.model';
import { map, take } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class FusionSuggestionQueryService {
  constructor(
    private languageService: LanguageService,
    private countryService: CountryService,
    private channelService: ChannelService,
    private distSearchBoxService: DistSearchBoxService,
  ) {}

  typeaheadQueryParams(): Observable<FusionSiteContextParams> {
    return combineLatest([
      this.countryService.getActive(),
      this.languageService.getActive(),
      this.channelService.getActive(),
    ]).pipe(
      take(1),
      map(([country, language, channel]) => {
        const searchCategoryQuery = this.setParamsSearchBarCategory();
        return { country, language, channel, ...searchCategoryQuery };
      }),
    );
  }

  private setParamsSearchBarCategory(): { fq: string } | null {
    const selectedCategory = this.distSearchBoxService.categoryCode;

    if (selectedCategory) {
      const categoryPrefixKeyTypeahead = 'category1Code';
      return { fq: `${categoryPrefixKeyTypeahead}:"${selectedCategory}"` };
    }

    return null;
  }
}
