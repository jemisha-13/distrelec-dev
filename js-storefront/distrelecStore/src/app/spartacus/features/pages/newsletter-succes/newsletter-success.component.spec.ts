/* tslint:disable:no-unused-variable */
import { ComponentFixture, inject, TestBed, waitForAsync } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { Component, Pipe, PipeTransform } from '@angular/core';
import { NewsletterSuccessComponent } from './newsletter-success.component';
import { DistrelecUserService } from '@services/user.service';
import { RouterTestingModule } from '@angular/router/testing';
import { Translatable, TranslatableParams } from '@spartacus/core';
import { returnSetOfTranslations } from '@features/pages/product/pdp/price-and-stock/stock/tests/stock-test-helper';
import { Router } from '@angular/router';
import { Location } from '@angular/common';

@Pipe({
  name: 'cxTranslate',
})
class MockTranslatePipe implements PipeTransform {
  transform(input: Translatable | string, options: TranslatableParams = {}): Translatable | string {
    return returnSetOfTranslations(input, options);
  }
}
@Component({
  template: '',
})
class EmptyComponent {}

describe('NewsletterSuccessComponent', () => {
  let fixture: ComponentFixture<NewsletterSuccessComponent>;
  let newsletterSuccessComponent: NewsletterSuccessComponent;
  let router: Router;
  let distrelecUserService: DistrelecUserService;
  let loggedInUser: boolean;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([
          { path: 'register', component: EmptyComponent },
          { path: 'my-account/preference-center', component: EmptyComponent },
          { path: 'login', component: EmptyComponent },
        ]),
      ],
      declarations: [NewsletterSuccessComponent, MockTranslatePipe],
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(NewsletterSuccessComponent);
        newsletterSuccessComponent = fixture.componentInstance;
        router = TestBed.inject(Router);
        fixture.detectChanges();
      });
  }));

  it('Should navigate to /my-account/preference-center if user is logged in', waitForAsync(
    inject([Router, Location], (router: Router, location: Location) => {
      loggedInUser = true;
      fixture.detectChanges();

      const anchorLink = fixture.debugElement.query(By.css('a[routerLink="/my-account/preference-center"]'));
      expect(anchorLink.nativeElement.getAttribute('href')).toEqual('/my-account/preference-center');

      anchorLink.nativeElement.click();

      fixture.whenStable().then(() => {
        expect(location.path()).toEqual('/my-account/preference-center');
      });
    }),
  ));

  it('Should navigate to /login if user clicks on Communication preferences and is not logged in', waitForAsync(
    inject([Router, Location], (router: Router, location: Location) => {
      fixture.detectChanges();

      const anchorLink = fixture.debugElement.query(By.css('a[routerLink="/my-account/preference-center"]'));
      expect(anchorLink.nativeElement.getAttribute('href')).toEqual('/my-account/preference-center');

      anchorLink.nativeElement.click();
      router.navigateByUrl('/login');

      fixture.whenStable().then(() => {
        expect(location.path()).toEqual('/login');
      });
    }),
  ));

  it('Should navigate to /register page if user clicks on Register and is not logged in', waitForAsync(
    inject([Router, Location], (router: Router, location: Location) => {
      fixture.detectChanges();

      const anchorLink = fixture.debugElement.query(By.css('a[routerLink="/register"]'));
      expect(anchorLink.nativeElement.getAttribute('href')).toEqual('/register');

      anchorLink.nativeElement.click();

      fixture.whenStable().then(() => {
        expect(location.path()).toEqual('/register');
      });
    }),
  ));

  it('Should navigate to / if user clicks on Register and is logged in', waitForAsync(
    inject([Router, Location], (router: Router, location: Location) => {
      loggedInUser = true;
      fixture.detectChanges();

      const anchorLink = fixture.debugElement.query(By.css('a[routerLink="/register"]'));
      expect(anchorLink.nativeElement.getAttribute('href')).toEqual('/register');

      anchorLink.nativeElement.click();
      router.navigateByUrl('/');

      fixture.whenStable().then(() => {
        expect(location.path()).toEqual('/');
      });
    }),
  ));
});
