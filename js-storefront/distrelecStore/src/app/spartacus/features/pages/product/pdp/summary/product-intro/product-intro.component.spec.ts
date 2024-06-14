import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { Pipe, PipeTransform } from '@angular/core';
import { Translatable, TranslatableParams, TranslationService } from '@spartacus/core';
import { of } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ProductIntroComponent } from './product-intro.component';
import { ProductAvailabilityService } from '@features/pages/product/core/services/product-availability.service';
import { ArticleNumberPipeModule } from '@pipes/article-number-pipe.module';
import { ClipboardService } from 'ngx-clipboard';
import {
  MOCK_IMAGES,
  MOCKED_DIR_PRODUCT_AVAILABILITY,
  MOCKED_RS_PRODUCT_AVAILABILITY,
} from '@features/mocks/mock-product-data';
import { ArticleTooltipModule } from '@features/shared-modules/components/article-tooltip/article-tooltip.module';
import { IfModule } from '@rx-angular/template/if';
import { LetModule } from '@rx-angular/template/let';
import { By } from '@angular/platform-browser';
import { ReevooModule } from '@features/shared-modules/reevoo/reevoo.module';
import { DividerLineModule } from '@design-system/divider-line/divider-line.module';
import { RouterModule } from '@angular/router';
import { DistBreakpointService } from '@services/breakpoint.service';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { TooltipComponentModule } from '@design-system/tooltip/tooltip.module';

@Pipe({
  name: 'cxTranslate',
})
class MockTranslatePipe implements PipeTransform {
  transform(input: Translatable | string, options: TranslatableParams = {}): Translatable | string {
    return input;
  }
}

const translationService = jasmine.createSpyObj('TranslationService', ['translate']);

describe('Product Intro Component', () => {
  let component: ProductIntroComponent;
  let clipboardService: ClipboardService;
  let productAvailabilityService: ProductAvailabilityService;
  let breakpointService: DistBreakpointService;

  let fixture: ComponentFixture<ProductIntroComponent>;

  beforeEach(async(() => {
    clipboardService = jasmine.createSpyObj('ClipboardService', ['copyFromContent']);
    productAvailabilityService = jasmine.createSpyObj('ProductAvailabilityService', ['getAvailability']);
    breakpointService = jasmine.createSpyObj('DistBreakpointService', [
      'isMobileOrTabletBreakpoint',
      'isTabletBreakpoint',
      'isDesktopBreakpoint',
    ]);
    (breakpointService.isMobileOrTabletBreakpoint as jasmine.Spy).and.returnValue(of(false));
    (breakpointService.isTabletBreakpoint as jasmine.Spy).and.returnValue(of(false));
    (breakpointService.isDesktopBreakpoint as jasmine.Spy).and.returnValue(of(true));

    TestBed.configureTestingModule({
      imports: [
        CommonTestingModule,
        HttpClientTestingModule,
        ArticleNumberPipeModule,
        ArticleTooltipModule,
        IfModule,
        LetModule,
        ReevooModule,
        DividerLineModule,
        RouterModule.forRoot([]),
        TooltipComponentModule,
      ],
      declarations: [ProductIntroComponent, MockTranslatePipe],
      providers: [
        { provide: ProductAvailabilityService, useValue: productAvailabilityService },
        { provide: DistBreakpointService, useValue: breakpointService },
        { provide: TranslationService, useValue: translationService },
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    translationService.translate.and.returnValue(of(''));

    fixture = TestBed.createComponent(ProductIntroComponent);

    component = fixture.componentInstance;
    component.title = 'THT LED White 11000K 5cd 5mm Round';
    component.brandLogo = {
      image: MOCK_IMAGES,
    };
    component.code = '30158817';
    component.manNumber = 'RND 135-00222';
    component.familyName = 'RND Components';
    component.familyNameUrl = '/manufacturer/rnd-components/man_rnp';
    component.reevooEligible = true;
    component.productFamilyUrl = '/en/leds-round-10mm-rnd/pf/2245767';
    component.alternativeAliasMPN = '180-7055';
    component.productAvailability$ = of(MOCKED_RS_PRODUCT_AVAILABILITY);

    fixture.detectChanges();
  });

  it('should show and copy article number', () => {
    const articleNumberTitle = fixture.debugElement.nativeElement.querySelector(
      '#pdp_product_intro_article_number_title',
    ).textContent;
    const articleNumber = fixture.debugElement.nativeElement.querySelector('#js-productcode').textContent;
    const articleCopyButton = fixture.debugElement.nativeElement.querySelector('#pdp_product_code_copy_button');

    expect(articleNumberTitle).toBe(' product.product_intro.article_number_title ');
    expect(articleNumber).toContain('301-58-817');
    spyOn(component, 'copyText').and.callThrough();

    articleCopyButton.click();
    fixture.detectChanges();

    const articleTooltip = fixture.debugElement.nativeElement.querySelector('app-tooltip')?.textContent;

    expect(component.copyText).toHaveBeenCalledWith('30158817', 'article');
    expect(articleTooltip).toContain('30158817');
  });

  it('should show and copy manufacturer number', () => {
    const manNumberTitle = fixture.debugElement.nativeElement.querySelector(
      '#pdp_product_intro_man_number_title',
    )?.textContent;
    const manNumber = fixture.debugElement.nativeElement.querySelector('#pdp_manufacturer_number')?.textContent;
    const manCopyButton = fixture.debugElement.nativeElement.querySelector('#pdp_manufacturer_copy_button');

    expect(manNumberTitle).toBe(' product.product_intro.man_number_title ');
    expect(manNumber).toBe('RND 135-00222');
    spyOn(component, 'copyText').and.callThrough();

    manCopyButton.click();
    fixture.detectChanges();

    const articleTooltip = fixture.debugElement.nativeElement.querySelector('app-tooltip')?.textContent;

    expect(component.copyText).toHaveBeenCalledWith('RND 135-00222', 'manufacturer', 'manufacturer');
    expect(articleTooltip).toContain('RND 135-00222');
  });

  it('should show brand and product family links', () => {
    const brandLogo = fixture.debugElement.query(By.css('img')).nativeElement;

    expect(brandLogo.src).toBe(
      'https://pretest.media.distrelec.com/Web/WebShopImages/manufacturer_logo/cm/yk/rnd_components_cmyk.jpg',
    );
  });

  it('should show and copy rs alias number', () => {
    (productAvailabilityService.getAvailability as jasmine.Spy).and.returnValue(of(MOCKED_RS_PRODUCT_AVAILABILITY));
    component.productAvailability$ = of(MOCKED_RS_PRODUCT_AVAILABILITY);
    fixture.detectChanges();

    const rsAliasTitle = fixture.debugElement.nativeElement.querySelector('#pdp_product_intro_rs_title')?.textContent;
    const aliasNumber = fixture.debugElement.nativeElement.querySelector('#pdp_alias')?.textContent;
    const aliasCopyButton = fixture.debugElement.nativeElement.querySelector('#pdp_alias_copy_button');

    expect(rsAliasTitle).toBe(' product.rs.title: ');
    expect(aliasNumber).toBe('180-7055');
    spyOn(component, 'copyText').and.callThrough();

    aliasCopyButton.click();
    fixture.detectChanges();

    const articleTooltip = fixture.debugElement.nativeElement.querySelector('app-tooltip')?.textContent;

    expect(component.copyText).toHaveBeenCalledWith('180-7055', 'mpnAlias', 'mpnAlias');
    expect(articleTooltip).toContain('180-7055');
  });

  it('should show and copy not RS alias number', () => {
    (productAvailabilityService.getAvailability as jasmine.Spy).and.returnValue(of(MOCKED_DIR_PRODUCT_AVAILABILITY));

    component.productAvailability$ = of(MOCKED_DIR_PRODUCT_AVAILABILITY);
    fixture.detectChanges();

    const aliasTitle = fixture.debugElement.nativeElement.querySelector('#pdp_product_intro_alias_title')?.textContent;
    const aliasNumber = fixture.debugElement.nativeElement.querySelector('#pdp_alias')?.textContent;
    const aliasCopyButton = fixture.debugElement.nativeElement.querySelector('#pdp_alias_copy_button');

    expect(aliasTitle).toBe(' product.alias.text: ');
    expect(aliasNumber).toBe('180-7055');
    spyOn(component, 'copyText').and.callThrough();

    aliasCopyButton.click();
    fixture.detectChanges();

    const articleTooltip = fixture.debugElement.nativeElement.querySelector('app-tooltip')?.textContent;

    expect(component.copyText).toHaveBeenCalledWith('180-7055', 'mpnAlias', 'mpnAlias');
    expect(articleTooltip).toContain('180-7055');
  });
});
