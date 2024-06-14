import { SiteContextParamsService, SiteContextUrlSerializer, UrlTreeWithSiteContext } from '@spartacus/core';
import { Injectable } from '@angular/core';
import { Params } from '@angular/router';
import { SearchExperienceService } from '@features/pages/product/core/services/search-experience.service';

@Injectable()
export class CustomUrlSerializer extends SiteContextUrlSerializer {
  readonly CATEGORY_FILTER_KEY = this.searchExperience.getCategoryKey();

  constructor(
    siteContextParams: SiteContextParamsService,
    private searchExperience: SearchExperienceService,
  ) {
    super(siteContextParams);
  }

  parse(url: string): UrlTreeWithSiteContext {
    const parsed = super.parse(url);

    if (this.treeContainsFilterQueryParam(parsed)) {
      // These PLP search filters parameters should stay encoded because they are used as keys to match applied filters
      parsed.queryParams = this.preserveEncodingOnFilterQueryParams(parsed.queryParams);
    }

    return parsed;
  }

  serialize(tree: UrlTreeWithSiteContext): string {
    let serialized = super.serialize(tree).replace(/%2B/g, '+');

    if (this.stringContainsFilterQueryParam(serialized)) {
      serialized = this.preserveEncodingOnFilterQueryString(serialized);
    }

    return serialized;
  }

  private treeContainsFilterQueryParam(tree: UrlTreeWithSiteContext): boolean {
    return Object.keys(tree.queryParams).some((key) => key.startsWith('filter_') || key === 'q');
  }

  private stringContainsFilterQueryParam(queryParams: string): boolean {
    return queryParams.includes('filter_') || queryParams.includes('q=');
  }

  private preserveEncodingOnFilterQueryParams(queryParams: Params): Params {
    return Object.keys(queryParams).reduce((acc, key) => {
      if ((key.startsWith('filter_') || key === 'q') && !key.includes(this.CATEGORY_FILTER_KEY)) {
        const encodedKey = this.handleFacetCodeEncodedCharacters(encodeURIComponent(key));
        const value = queryParams[key];
        if (Array.isArray(value)) {
          acc[encodedKey] = value.map((e) => this.customFacetEncoding(encodeURIComponent(e)));
        } else {
          acc[encodedKey] = this.customFacetEncoding(encodeURIComponent(value));
        }
      } else {
        acc[key] = queryParams[key];
      }
      return acc;
    }, {});
  }

  private preserveEncodingOnFilterQueryString(serialized: string): string {
    const [url, query] = serialized.split('?');
    if (!query) {
      return serialized;
    }

    const params = query.split('&');
    const encodedParams = params.map((param) => {
      let [key, value] = param.split('=');
      if (key.startsWith('filter_') || key === 'q') {
        key = this.customFacetEncoding(key);
        value = this.customFacetEncoding(value);
      }
      return `${key}=${value}`;
    });

    return `${url}?${encodedParams.join('&')}`;
  }

  private handleFacetCodeEncodedCharacters(value: string): string {
    // Since %20 can be decoded as whitespace and +, we are making sure it's decoded to +, to match logic in FactFinderFacetConverter
    return value.replace(/%20/g, '+');
  }

  private customFacetEncoding(serialized: string): string {
    // Default angular serialize logic:
    // https://github.com/angular/angular/blob/4d18ba1af78dae2ba14f45ed092a4c55335edd9c/packages/router/src/url_tree.ts#L453
    // List of characters and encoded values:
    // https://developer.mozilla.org/en-US/docs/Glossary/percent-encoding
    return (
      serialized
        // making sure we have + instead of whitespaces
        .replace(/%20/g, '+')
        // making sure there are no double encodings like $2526
        .replace(/%25/g, '%')
        //making sure there are no invalid encodings
        .replace(/%(?![A-Fa-f0-9]{2})/g, '%25')
        // making sure parenthesis are encoded in URL
        .replace('(', '%28')
        .replace(')', '%29')
    );
  }
}
