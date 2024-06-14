import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { CmsService, Page, PageType, ProductSearchService, SiteContextUrlSerializer } from '@spartacus/core';
import { combineLatest, Observable } from 'rxjs';
import { filter, map, startWith } from 'rxjs/operators';
import { RedirectCountService } from '@services/redirect-count.service';

interface NormalizePattern {
  pattern: RegExp;
  replacement: string;
  maxApply: number;
}

@Injectable({
  providedIn: 'root',
})
export class ContentPageRedirectGuard {
  private static accentedChars = new Map<string, string>([
    ['A', 'a'],
    ['B', 'b'],
    ['C', 'c'],
    ['D', 'd'],
    ['E', 'e'],
    ['F', 'f'],
    ['G', 'g'],
    ['H', 'h'],
    ['I', 'i'],
    ['J', 'j'],
    ['K', 'k'],
    ['L', 'l'],
    ['M', 'm'],
    ['N', 'n'],
    ['O', 'o'],
    ['P', 'p'],
    ['Q', 'q'],
    ['R', 'r'],
    ['S', 's'],
    ['T', 't'],
    ['U', 'u'],
    ['V', 'v'],
    ['W', 'w'],
    ['X', 'x'],
    ['Y', 'y'],
    ['Z', 'z'],
    ['a', 'a'],
    ['b', 'b'],
    ['c', 'c'],
    ['d', 'd'],
    ['e', 'e'],
    ['f', 'f'],
    ['g', 'g'],
    ['h', 'h'],
    ['i', 'i'],
    ['j', 'j'],
    ['k', 'k'],
    ['l', 'l'],
    ['m', 'm'],
    ['n', 'n'],
    ['o', 'o'],
    ['p', 'p'],
    ['q', 'q'],
    ['r', 'r'],
    ['s', 's'],
    ['t', 't'],
    ['u', 'u'],
    ['v', 'v'],
    ['w', 'w'],
    ['x', 'x'],
    ['y', 'y'],
    ['z', 'z'],
    ['0', '0'],
    ['1', '1'],
    ['2', '2'],
    ['3', '3'],
    ['4', '4'],
    ['5', '5'],
    ['6', '6'],
    ['7', '7'],
    ['8', '8'],
    ['9', '9'],
    ['-', ' '],
    ['/', '/'],
    ['_', '-'],
    ['ъ', ' '],
    ['Ь', ' '],
    ['Ъ', ' '],
    ['ь', ' '],
    ['Ă', 'a'],
    ['Ą', 'a'],
    ['À', 'a'],
    ['Ã', 'a'],
    ['Á', 'a'],
    ['Æ', 'a'],
    ['Â', 'a'],
    ['Å', 'a'],
    ['Ä', 'ae'],
    ['Þ', 'b'],
    ['Ć', 'c'],
    ['ץ', 'c'],
    ['Ç', 'c'],
    ['È', 'e'],
    ['Ę', 'e'],
    ['É', 'e'],
    ['Ë', 'e'],
    ['Ê', 'e'],
    ['Ğ', 'g'],
    ['İ', 'i'],
    ['Ï', 'i'],
    ['Î', 'i'],
    ['Í', 'i'],
    ['Ì', 'i'],
    ['Ł', 'l'],
    ['Ñ', 'n'],
    ['Ń', 'n'],
    ['Ø', 'o'],
    ['Ó', 'o'],
    ['Ò', 'o'],
    ['Ô', 'o'],
    ['Õ', 'o'],
    ['Ö', 'oe'],
    ['Ş', 's'],
    ['Ś', 's'],
    ['Ș', 's'],
    ['Š', 's'],
    ['Ț', 't'],
    ['Ù', 'u'],
    ['Û', 'u'],
    ['Ú', 'u'],
    ['Ü', 'ue'],
    ['Ý', 'y'],
    ['Ź', 'z'],
    ['Ž', 'z'],
    ['Ż', 'z'],
    ['â', 'a'],
    ['ǎ', 'a'],
    ['ą', 'a'],
    ['á', 'a'],
    ['ă', 'a'],
    ['ã', 'a'],
    ['Ǎ', 'a'],
    ['а', 'a'],
    ['А', 'a'],
    ['å', 'a'],
    ['à', 'a'],
    ['א', 'a'],
    ['Ǻ', 'a'],
    ['Ā', 'a'],
    ['ǻ', 'a'],
    ['ā', 'a'],
    ['ä', 'ae'],
    ['æ', 'ae'],
    ['Ǽ', 'ae'],
    ['ǽ', 'ae'],
    ['б', 'b'],
    ['ב', 'b'],
    ['Б', 'b'],
    ['þ', 'b'],
    ['ĉ', 'c'],
    ['Ĉ', 'c'],
    ['Ċ', 'c'],
    ['ć', 'c'],
    ['ç', 'c'],
    ['ц', 'c'],
    ['צ', 'c'],
    ['ċ', 'c'],
    ['Ц', 'c'],
    ['Č', 'c'],
    ['č', 'c'],
    ['Ч', 'ch'],
    ['ч', 'ch'],
    ['ד', 'd'],
    ['ď', 'd'],
    ['Đ', 'd'],
    ['Ď', 'd'],
    ['đ', 'd'],
    ['д', 'd'],
    ['Д', 'd'],
    ['ð', 'd'],
    ['є', 'e'],
    ['ע', 'e'],
    ['е', 'e'],
    ['Е', 'e'],
    ['Ə', 'e'],
    ['ę', 'e'],
    ['ĕ', 'e'],
    ['ē', 'e'],
    ['Ē', 'e'],
    ['Ė', 'e'],
    ['ė', 'e'],
    ['ě', 'e'],
    ['Ě', 'e'],
    ['Є', 'e'],
    ['Ĕ', 'e'],
    ['ê', 'e'],
    ['ə', 'e'],
    ['è', 'e'],
    ['ë', 'e'],
    ['é', 'e'],
    ['ф', 'f'],
    ['ƒ', 'f'],
    ['Ф', 'f'],
    ['ġ', 'g'],
    ['Ģ', 'g'],
    ['Ġ', 'g'],
    ['Ĝ', 'g'],
    ['Г', 'g'],
    ['г', 'g'],
    ['ĝ', 'g'],
    ['ğ', 'g'],
    ['ג', 'g'],
    ['Ґ', 'g'],
    ['ґ', 'g'],
    ['ģ', 'g'],
    ['ח', 'h'],
    ['ħ', 'h'],
    ['Х', 'h'],
    ['Ħ', 'h'],
    ['Ĥ', 'h'],
    ['ĥ', 'h'],
    ['х', 'h'],
    ['ה', 'h'],
    ['î', 'i'],
    ['ï', 'i'],
    ['í', 'i'],
    ['ì', 'i'],
    ['į', 'i'],
    ['ĭ', 'i'],
    ['ı', 'i'],
    ['Ĭ', 'i'],
    ['И', 'i'],
    ['ĩ', 'i'],
    ['ǐ', 'i'],
    ['Ĩ', 'i'],
    ['Ǐ', 'i'],
    ['и', 'i'],
    ['Į', 'i'],
    ['י', 'i'],
    ['Ї', 'i'],
    ['Ī', 'i'],
    ['І', 'i'],
    ['ї', 'i'],
    ['і', 'i'],
    ['ī', 'i'],
    ['ĳ', 'ij'],
    ['Ĳ', 'ij'],
    ['й', 'j'],
    ['Й', 'j'],
    ['Ĵ', 'j'],
    ['ĵ', 'j'],
    ['я', 'ja'],
    ['Я', 'ja'],
    ['Э', 'je'],
    ['э', 'je'],
    ['ё', 'jo'],
    ['Ё', 'jo'],
    ['ю', 'ju'],
    ['Ю', 'ju'],
    ['ĸ', 'k'],
    ['כ', 'k'],
    ['Ķ', 'k'],
    ['К', 'k'],
    ['к', 'k'],
    ['ķ', 'k'],
    ['ך', 'k'],
    ['Ŀ', 'l'],
    ['ŀ', 'l'],
    ['Л', 'l'],
    ['ł', 'l'],
    ['ļ', 'l'],
    ['ĺ', 'l'],
    ['Ĺ', 'l'],
    ['Ļ', 'l'],
    ['л', 'l'],
    ['Ľ', 'l'],
    ['ľ', 'l'],
    ['ל', 'l'],
    ['מ', 'm'],
    ['М', 'm'],
    ['ם', 'm'],
    ['м', 'm'],
    ['ñ', 'n'],
    ['н', 'n'],
    ['Ņ', 'n'],
    ['ן', 'n'],
    ['ŋ', 'n'],
    ['נ', 'n'],
    ['Н', 'n'],
    ['ń', 'n'],
    ['Ŋ', 'n'],
    ['ņ', 'n'],
    ['ŉ', 'n'],
    ['Ň', 'n'],
    ['ň', 'n'],
    ['о', 'o'],
    ['О', 'o'],
    ['ő', 'o'],
    ['õ', 'o'],
    ['ô', 'o'],
    ['Ő', 'o'],
    ['ŏ', 'o'],
    ['Ŏ', 'o'],
    ['Ō', 'o'],
    ['ō', 'o'],
    ['ø', 'o'],
    ['ǿ', 'o'],
    ['ǒ', 'o'],
    ['ò', 'o'],
    ['Ǿ', 'o'],
    ['Ǒ', 'o'],
    ['ơ', 'o'],
    ['ó', 'o'],
    ['Ơ', 'o'],
    ['œ', 'oe'],
    ['Œ', 'oe'],
    ['ö', 'oe'],
    ['פ', 'p'],
    ['ף', 'p'],
    ['п', 'p'],
    ['П', 'p'],
    ['ק', 'q'],
    ['ŕ', 'r'],
    ['ř', 'r'],
    ['Ř', 'r'],
    ['ŗ', 'r'],
    ['Ŗ', 'r'],
    ['ר', 'r'],
    ['Ŕ', 'r'],
    ['Р', 'r'],
    ['р', 'r'],
    ['ș', 's'],
    ['с', 's'],
    ['Ŝ', 's'],
    ['š', 's'],
    ['ś', 's'],
    ['ס', 's'],
    ['ş', 's'],
    ['С', 's'],
    ['ŝ', 's'],
    ['Щ', 'sch'],
    ['щ', 'sch'],
    ['ш', 'sh'],
    ['Ш', 'sh'],
    ['ß', 'ss'],
    ['т', 't'],
    ['ט', 't'],
    ['ŧ', 't'],
    ['ת', 't'],
    ['ť', 't'],
    ['ţ', 't'],
    ['Ţ', 't'],
    ['Т', 't'],
    ['ț', 't'],
    ['Ŧ', 't'],
    ['Ť', 't'],
    ['™', 'tm'],
    ['ū', 'u'],
    ['у', 'u'],
    ['Ũ', 'u'],
    ['ũ', 'u'],
    ['Ư', 'u'],
    ['ư', 'u'],
    ['Ū', 'u'],
    ['Ǔ', 'u'],
    ['ų', 'u'],
    ['Ų', 'u'],
    ['ŭ', 'u'],
    ['Ŭ', 'u'],
    ['Ů', 'u'],
    ['ů', 'u'],
    ['ű', 'u'],
    ['Ű', 'u'],
    ['Ǖ', 'u'],
    ['ǔ', 'u'],
    ['Ǜ', 'u'],
    ['ù', 'u'],
    ['ú', 'u'],
    ['û', 'u'],
    ['У', 'u'],
    ['ǚ', 'u'],
    ['ǜ', 'u'],
    ['Ǚ', 'u'],
    ['Ǘ', 'u'],
    ['ǖ', 'u'],
    ['ǘ', 'u'],
    ['ü', 'ue'],
    ['в', 'v'],
    ['ו', 'v'],
    ['В', 'v'],
    ['ש', 'w'],
    ['ŵ', 'w'],
    ['Ŵ', 'w'],
    ['ы', 'y'],
    ['ŷ', 'y'],
    ['ý', 'y'],
    ['ÿ', 'y'],
    ['Ÿ', 'y'],
    ['Ŷ', 'y'],
    ['Ы', 'y'],
    ['ž', 'z'],
    ['З', 'z'],
    ['з', 'z'],
    ['ź', 'z'],
    ['ז', 'z'],
    ['ż', 'z'],
    ['ſ', 'z'],
    ['Ж', 'zh'],
    ['ж', 'zh'],
  ]);

  private static normalizePatterns: NormalizePattern[] = [
    {
      pattern: /(\s|-)+/,
      replacement: '-',
      maxApply: 1,
    },
    {
      pattern: /(\/.?-)|(-.?\/)/,
      replacement: '/',
      maxApply: 5,
    },
    {
      pattern: /(-.-)+/,
      replacement: '-',
      maxApply: 5,
    },
    {
      pattern: /\/\//,
      replacement: '/',
      maxApply: 5,
    },
    {
      pattern: /^(.?-)|(-.?)$/,
      replacement: '',
      maxApply: 5,
    },
  ];

  constructor(
    private cmsService: CmsService,
    private router: Router,
    private siteContextUrlSerializer: SiteContextUrlSerializer,
    private productSearchService: ProductSearchService,
    private redirectCount: RedirectCountService,
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    if (this.redirectCount.exceeds(1)) {
      return this.allow();
    }

    const pageLabelOrId = route.params.pageIdOrLabel as string;
    const { url } = this.siteContextUrlSerializer.urlExtractContextParameters(state.url);

    return combineLatest([
      this.productSearchService.getResults().pipe(startWith(null)),
      this.cmsService.getPage(
        {
          type: PageType.CONTENT_PAGE,
          id: pageLabelOrId,
        },
        false,
      ),
    ]).pipe(
      // for CMS pages undefined means not fetched, null is not found
      filter(([_, page]) => page !== undefined),
      map(([searchResults, page]) => {
        if (page === null) {
          return this.allow(); // CmsPageGuard will handle the 404
        }

        const normalizedUrl = this.buildNormalizedUrl(page, pageLabelOrId);
        if (url === normalizedUrl || url.startsWith(normalizedUrl + '?')) {
          return this.allow();
        }

        const urlTree = this.router.parseUrl(normalizedUrl);
        urlTree.queryParams = route.queryParams;

        const hasKeyWordRedirectFromSearch = Boolean(searchResults?.keywordRedirectUrl);
        if (hasKeyWordRedirectFromSearch) {
          this.router.navigateByUrl(urlTree, { replaceUrl: hasKeyWordRedirectFromSearch });
          this.redirectCount.increment();
          return false;
        }

        this.redirectCount.increment();
        return urlTree;
      }),
    );
  }

  private allow() {
    this.redirectCount.reset();
    return true;
  }

  private buildNormalizedUrl(page: Page, pageLabelOrId: string) {
    const pageTitle = page.title ?? pageLabelOrId;
    const sourceUrl = `${pageTitle}/cms/`;
    const charCodes = this.getCharCodes(sourceUrl);
    const decodedUrl = this.codesToString(charCodes);
    const url = this.buildUrl(decodedUrl);
    const encodedPageLabelOrId = encodeURIComponent(pageLabelOrId).replace(/%2B/g, '+'); // Replicate the behavior of the UrlSerializer
    return this.normalizeUrl(url).toLowerCase() + encodedPageLabelOrId;
  }

  private getCharCodes(url: string): number[] {
    const charCodes: number[] = [];
    for (let i = 0; i < url.length; i++) {
      const code = url.charCodeAt(i);
      if (code > 127) {
        charCodes.push(63);
      } else {
        charCodes.push(code);
      }
    }
    return charCodes;
  }

  private codesToString(codes: number[]) {
    return String.fromCharCode(...codes);
  }

  private buildUrl(decodedUrl: string) {
    const url = [];
    for (let i = 0; i < decodedUrl.length; i++) {
      url.push(this.replaceSource(decodedUrl.charAt(i)));
    }
    return url.join('');
  }

  private replaceSource(source: string) {
    if (source == null || !ContentPageRedirectGuard.accentedChars.has(source)) {
      return ' ';
    }
    return ContentPageRedirectGuard.accentedChars.get(source);
  }

  private normalizeUrl(url: string) {
    let sequence = url.slice();
    for (const pattern of ContentPageRedirectGuard.normalizePatterns) {
      let steps = 0;
      while (steps < pattern.maxApply && pattern.pattern.test(sequence)) {
        steps++;
        sequence = sequence.replace(new RegExp(pattern.pattern, 'g'), pattern.replacement);
      }
    }
    return sequence;
  }
}
