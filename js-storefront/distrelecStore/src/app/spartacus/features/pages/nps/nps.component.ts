import { HttpClient } from '@angular/common/http';
import { Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { OccEndpointsService, WindowRef, TranslationService } from '@spartacus/core';
import { DistrelecUserService } from '@services/user.service';
import { ActivatedRoute } from '@angular/router';
import { first } from 'rxjs/operators';

@Component({
  selector: 'app-nps-feedback',
  templateUrl: './nps.component.html',
  encapsulation: ViewEncapsulation.None,
  styleUrls: ['./nps.component.scss'],
})
export class NpsComponent implements OnInit {
  changeScore = false;
  npsFeedback = {
    cnumber: '',
    company: '',
    contactnum: '',
    delivery: '',
    email: '',
    feedback: '',
    fname: '',
    id: '',
    namn: '',
    order: '',
    reason: '',
    subreason: '',
    type: '',
    value: 1,
  };

  scoreArray = new Array(10);
  reasons = [];
  subReasons = [];
  returnData;
  responseType = 'danger';
  responseMessage = '';
  duplicate = false;
  submit = false;

  constructor(
    private http: HttpClient,
    private occEndpoints: OccEndpointsService,
    private distrelecUserService: DistrelecUserService,
    private route: ActivatedRoute,
    private translation: TranslationService,
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe((params) => {
      this.npsFeedback.email = params.email;
      this.npsFeedback.fname = params.fname;
      this.npsFeedback.namn = params.namn;
      this.npsFeedback.company = params.company;
      this.npsFeedback.order = params.order;
      this.npsFeedback.cnumber = params.cnumber;
      this.npsFeedback.contactnum = params.contactnum;
      this.npsFeedback.value = params.value;

      //call
      this.http.post(this.occEndpoints.buildUrl(`feedback`), this.npsFeedback).subscribe({
        next: (data) => {
          this.returnData = data;
        },
        error: (e) => {
          e?.error?.errors.forEach((err) => {
            const [firstRes, ...rest] = err.message.split(':');
            if (firstRes === 'feedback.nps.duplicate') {
              const dateTime = rest.join(':');
              this.translation
                .translate(firstRes)
                .pipe(first())
                .subscribe((val) => (this.responseMessage = val));
              this.responseMessage += ' ' + dateTime;
              this.duplicate = true;
            }
          });
        },
      });
    });
  }
  postFeedback() {
    //update the missing fields
    this.npsFeedback.id = this.returnData.id;
    this.http.post(this.occEndpoints.buildUrl(`feedback/submit`), this.npsFeedback).subscribe((data) => {
      this.responseType = 'success';
      this.translation
        .translate('feedback.nps.success')
        .pipe(first())
        .subscribe((val) => (this.responseMessage = val));
      this.submit = true;
    });
  }
  updateScore(score) {
    this.npsFeedback.value = score;
  }

  onChangeMainReason(reason) {
    this.npsFeedback.reason = reason;
  }

  onChangeSubReason(subreason) {
    this.npsFeedback.subreason = subreason;
  }
}
