import { Component, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { ManufactureService } from '@services/manufacture.service';
import { map, take, tap } from 'rxjs/operators';
import { BehaviorSubject, Subscription } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { decode } from '@helpers/encoding';
import {
  FeedbackFormService,
  ZeroSearchResultFeedbackForm,
} from '@features/pages/product/plp/services/feedback-form.service';
import { DISTRELEC_EMAIL_PATTERN } from '@helpers/constants';
import { EmptySearchPageType } from '../../../../../../model';
import { DistProductListComponentService } from '@features/pages/product/core/services/dist-product-list-component.service';
import { EventHelper } from '@features/tracking/event-helper.service';

@Component({
  selector: 'app-product-list-empty',
  templateUrl: './product-list-empty.component.html',
  styleUrls: ['./product-list-empty.component.scss'],
})
export class ProductListEmptyComponent implements OnInit, OnDestroy {
  isB2CLayout$ = this.productListService.model$.pipe(
    map((model) => model.emptyPageType === EmptySearchPageType.B2BOnlyProduct),
  );
  counter: string;
  feedbackForm: UntypedFormGroup;
  manufactures: string[];
  title$: BehaviorSubject<string> = new BehaviorSubject<string>('');
  private subscriptions = new Subscription();

  constructor(
    private fb: UntypedFormBuilder,
    private manufactureService: ManufactureService,
    private activeRoute: ActivatedRoute,
    private feedbackFormService: FeedbackFormService,
    private router: Router,
    private productListService: DistProductListComponentService,
    private eventHelperService: EventHelper,
  ) {
    this.counter = '';
    this.manufactures = [];
    this.feedbackForm = this.fb.group({
      manufacturer: ['Select Manufacturer', [Validators.required]],
      manufacturerIdentifier: ['', [Validators.required]],
      eMail: ['', [Validators.required, Validators.pattern(DISTRELEC_EMAIL_PATTERN)]],
      tellMore: ['', [Validators.maxLength(250)]],
    });
  }

  ngOnInit(): void {
    this.subscriptions.add(
      this.manufactureService
        .getManufactures()
        .pipe(
          take(1),
          map((manufactureResponse) => manufactureResponse.response),
          tap((response) => {
            response.forEach((letter) => {
              letter.manufacturerList.forEach((manufacture) => {
                this.manufactures.push(manufacture.name);
              });
            });
          }),
        )
        .subscribe(),
    );

    this.title$.next(decode(this.activeRoute.snapshot.queryParams.q));
    this.eventHelperService.trackFactFinderNoResultsEvent(decode(this.activeRoute.snapshot.queryParams.q));
  }

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
  }

  isControlInvalid(controlName: string): boolean {
    if (!this.feedbackForm.get(controlName)) {
      return false;
    }
    return (
      this.feedbackForm.get(controlName).invalid &&
      (this.feedbackForm.get(controlName).dirty || this.feedbackForm.get(controlName).touched)
    );
  }

  onSubmit(form: UntypedFormGroup): void {
    const req: ZeroSearchResultFeedbackForm = {
      searchTerm: this.activeRoute.snapshot.queryParams.q,
      manufacturer: form.value.manufacturer,
      manufacturerType: form.value.manufacturerIdentifier,
      manufacturerTypeOtherName: '',
      email: form.value.eMail,
      tellUsMore: form.value.tellMore,
    } as ZeroSearchResultFeedbackForm;

    this.feedbackFormService
      .sendZeroSearchResultFeedback(req)
      .subscribe(() => this.router.navigateByUrl('/searchFeedbackSent'));
  }

  scrollTo(el: HTMLElement) {
    el.scrollIntoView({ behavior: 'smooth' });
  }
}
