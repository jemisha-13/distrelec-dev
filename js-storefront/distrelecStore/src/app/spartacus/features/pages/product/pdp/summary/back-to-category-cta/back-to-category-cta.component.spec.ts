import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { Pipe, PipeTransform } from '@angular/core';
import { RouterTestingModule } from '@angular/router/testing';
import { By } from '@angular/platform-browser';
import { BreadcrumbService } from '@services/breadcrumb.service';
import { of } from 'rxjs';
import { Router } from '@angular/router';
import { BackToCategoryComponent } from './back-to-category-cta.component';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { MockTranslatePipe, Translatable, TranslatableParams, TranslationService } from '@spartacus/core';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { DistButtonComponentModule } from '@design-system/button/button.module';

// Define the mock breadcrumbs data
const mockBreadcrumbs = [
  { name: 'Home', url: '/' },
  { name: 'Lighting', url: '/lighting/c/cat-L2D_379658' },
  { name: 'Portable Lighting', url: '/lighting/portable-lighting/c/cat-L3D_525570' },
  { name: 'Torches', url: '/lighting/portable-lighting/torches/c/cat-DNAV_PL_110505' },
];

const lastBreadcrumb = { name: 'Torches', url: '/lighting/portable-lighting/torches/c/cat-DNAV_PL_110505' };

const translationService = jasmine.createSpyObj('TranslationService', ['translate']);

describe('BackToCategoryComponent', () => {
  let component: BackToCategoryComponent;
  let fixture: ComponentFixture<BackToCategoryComponent>;
  let breadcrumbService: BreadcrumbService;
  let router: Router;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [BackToCategoryComponent, MockTranslatePipe],
      imports: [CommonTestingModule, RouterTestingModule, DistIconModule, DistButtonComponentModule],
      providers: [
        {
          provide: BackToCategoryComponent,
          useValue: {
            breadcrumbs$: of(mockBreadcrumbs),
          },
        },
        { provide: TranslationService, useValue: translationService },
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    translationService.translate.and.returnValue(of(''));
    breadcrumbService = TestBed.inject(BreadcrumbService);
    breadcrumbService.setBreadcrumbs(mockBreadcrumbs);

    fixture = TestBed.createComponent(BackToCategoryComponent);
    component = fixture.componentInstance;

    router = TestBed.inject(Router);

    fixture.detectChanges();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should display the "Back to Category" button with the correct label', () => {
    const buttonElement = fixture.debugElement.query(By.css('.back-to-category-cta app-dist-button'));

    expect(buttonElement).toBeTruthy();
    expect(buttonElement.nativeElement.textContent.trim()).toContain('product.accessories.view_all Torches');
  });

  it('should redirect to the correct category', () => {
    const buttonElement = fixture.debugElement.query(By.css('.back-to-category-cta app-dist-button'));

    spyOn(router, 'navigate');
    buttonElement.triggerEventHandler('click', null);

    expect(router.navigate).toHaveBeenCalledWith(['/lighting/portable-lighting/torches/c/cat-DNAV_PL_110505']);
  });
});
