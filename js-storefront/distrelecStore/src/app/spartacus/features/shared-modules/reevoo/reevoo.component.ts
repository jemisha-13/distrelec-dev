import { Component, ElementRef, Input, OnInit } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { BaseSite, BaseSiteService, LanguageService, WindowRef } from '@spartacus/core';
import { map, take } from 'rxjs/operators';
import { ScriptService } from 'src/app/spartacus/services/script.service';
import { CountryService } from 'src/app/spartacus/site-context/services/country.service';
import { zip } from 'rxjs';
import { IntersectionObserverService } from '@services/intersection-observer';
import { ViewPdpReviewsEvent } from '@features/tracking/events/ga4/pdp-review-event';

@Component({
  selector: 'app-reevoo',
  templateUrl: './reevoo.component.html',
  styleUrls: ['./reevoo.component.scss'],
})
export class ReevooComponent implements OnInit {
  @Input() isEligible: boolean;
  @Input() productCode: string;
  @Input() reevooType: string;

  countryCode: string;
  isActivatedWebshop: boolean;
  language = 'en';
  reevooHtmlTag?: SafeHtml;
  countriesWithLanguage = ['CH', 'NO'];

  constructor(
    private siteService: BaseSiteService,
    private countryService: CountryService,
    private domSanitizer: DomSanitizer,
    private winRef: WindowRef,
    private scriptService: ScriptService,
    private languageService: LanguageService,
    private el: ElementRef,
    private intersectionObserver: IntersectionObserverService,
  ) {}

  ngOnInit() {
    if (this.winRef.isBrowser()) {
      zip(
        this.countryService.getActive(),
        this.siteService.get().pipe(map((baseSite: BaseSite) => baseSite.reevooActivated ?? false)),
        this.languageService.getActive(),
      )
        .pipe(take(1))
        .subscribe(([countryCode, isActivatedWebshop, language]) => {
          this.countryCode = countryCode;
          this.isActivatedWebshop = isActivatedWebshop;
          this.language = language;
          this.loadReevoo();
        });
    }
  }

  loadReevoo() {
    // we cant do this in the html as angular treats this as a component local to angular
    if (this.reevooType === 'product-badge') {
      this.reevooHtmlTag = this.domSanitizer.bypassSecurityTrustHtml(`
        <reevoo-product-badge
          variant='PDP'
          SKU="${this.productCode}"
          id="reevoo_badge_pdp"
          on-click="
          window.scroll({ top: document.querySelector('#reevoo_tabbed').offsetTop - 100, behavior: 'smooth' });"
          reevoo-click-action="no_action">
        </reevoo-product-badge>
      `);
    } else if (this.reevooType === 'product-badge-plp') {
      this.reevooHtmlTag = this.domSanitizer.bypassSecurityTrustHtml(`
        <reevoo-product-badge
          variant='PDP'
          SKU="${this.productCode}">
        </reevoo-product-badge>
      `);
    } else if (this.reevooType === 'product-reviews') {
      this.reevooHtmlTag = this.domSanitizer.bypassSecurityTrustHtml(`
        <reevoo-product-reviews product-id="${this.productCode}" per-page="5"></reevoo-product-reviews>
      `);
      this.intersectionObserver.observeViewport(this.el.nativeElement, () => new ViewPdpReviewsEvent());
    }

    //load reevoo script in to page dynamically only if product is eligible and activated for webshop
    if (this.winRef.isBrowser()) {
      if (this.isActivatedWebshop && !this.winRef.document.querySelector('#reevoo-script')) {
        let isoCodePath: string;
        const countryLangFormat = `${this.countryCode}-${this.language.toUpperCase()}`;

        //SCRIPT SRC works different for CH-EN not  just CH
        if (this.countriesWithLanguage.includes(this.countryCode)) {
          isoCodePath = countryLangFormat;
        } else if (countryLangFormat === 'SE-EN') {
          // Script src for SE-EN is expecting SE-FI
          isoCodePath = 'SE-FI';
        } else {
          isoCodePath = this.countryCode;
        }

        this.scriptService.appendScript({
          src: `https://widgets.reevoo.com/loader/DIS-${isoCodePath}.js`,
          type: 'text/javascript',
          id: 'reevoo-script',
        });
      }
    }
  }
}
