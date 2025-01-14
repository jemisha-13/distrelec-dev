import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgbDateParserFormatter, NgbDate, NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import { Injectable } from '@angular/core';
import { faCalendar } from '@fortawesome/free-solid-svg-icons';
import { Observable, Subscription } from 'rxjs';

function padNumber(value: number | null) {
  if (!isNaN(value) && value !== null) {
    return `0${value}`.slice(-2);
  }
  return '';
}

@Injectable()
export class NgbDateCustomParserFormatter extends NgbDateParserFormatter {
  parse(value: string): NgbDateStruct | null {
    if (value) {
      const dateParts = value.trim().split('.');

      let dateObj: NgbDateStruct = { day: <any>null, month: <any>null, year: <any>null };
      const dateLabels = Object.keys(dateObj);

      dateParts.forEach((datePart, idx) => {
        dateObj[dateLabels[idx]] = parseInt(datePart, 10) || <any>null;
      });
      return dateObj;
    }
    return null;
  }

  static formatDate(date: NgbDateStruct | NgbDate | null): string {
    return date ? `${padNumber(date.day)}.${padNumber(date.month)}.${date.year || ''}` : '';
  }

  format(date: NgbDateStruct | null): string {
    return NgbDateCustomParserFormatter.formatDate(date);
  }
}

@Component({
  selector: 'ngbd-datepicker-popup',
  templateUrl: './datepicker-popup.html',
  styleUrls: ['./datepicker-popup.scss'],
})
export class NgbdDatepickerPopup {
  model: NgbDateStruct;
  calendarIcon = faCalendar;
  subscription: Subscription;
  @Input() resetDate: Observable<void>;
  @Input() customPlaceHolder: string;
  @Input() customMinDate: NgbDateStruct;
  @Input() customMaxDate: NgbDateStruct;
  @Output() dateSelected = new EventEmitter<NgbDateStruct>();

  ngOnInit(): void {
    this.subscription = this.resetDate.subscribe(() => (this.model = undefined));
  }

  ngOnDestroy(): void {
    if (this.subscription && !this.subscription.closed) {
      this.subscription.unsubscribe();
    }
  }

  sendDateBackToParent() {
    this.dateSelected.emit(this.model);
  }
}
