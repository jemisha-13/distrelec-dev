/* eslint-disable max-len */
import { formatDate } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { CountryDateFormatEnum } from '@model/site-settings.model';
import { OccEndpointsService, UserIdService } from '@spartacus/core';
import { CartStoreService } from '@state/cartState.service';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, switchMap, tap } from 'rxjs/operators';
import { AllsitesettingsService } from './allsitesettings.service';
import { CheckoutService } from './checkout.service';
import { getDateFormat } from '@helpers/date-helper';
import { Cart } from '@spartacus/cart/base/root';
import { PossibleDeliveryDates } from '@model/misc.model';

@Injectable({
  providedIn: 'root',
})
export class CalendarService {
  possibleDeliveryDates: PossibleDeliveryDates;

  firstAvailableDates: Date;
  lastAvailableDates: Date;

  pastDatesArray: Date[];
  currentDatesArray: Date[];
  futureDatesArray: Date[];

  todayDate = new Date();
  monthDate = this.todayDate.getMonth();
  currentYear: number = this.todayDate.getFullYear();

  calendarObject_ = new BehaviorSubject<{ days: { date: Date; attribute: string }[]; date: Date }>(null);

  countryDateEnum = CountryDateFormatEnum;

  constructor(
    private checkoutService: CheckoutService,
    private cartStoreService: CartStoreService,
    private allSiteSettingsService: AllsitesettingsService,
    private http: HttpClient,
    private occEndpointsService: OccEndpointsService,
    private userIdService: UserIdService,
  ) {}

  getLaterDeliveryDetails(): Observable<PossibleDeliveryDates> {
    return this.userIdService
      .getUserId()
      .pipe(
        switchMap((userId) =>
          this.http
            .get<PossibleDeliveryDates>(
              this.occEndpointsService.buildUrl(
                `/users/${userId}/carts/${this.cartStoreService.getCartId()}/possibleDeliveryDates?fields=DEFAULT`,
                {},
              ),
            )
            .pipe(tap((data: PossibleDeliveryDates) => (this.possibleDeliveryDates = data))),
        ),
      );
  }

  assembleMonthDays(scheduledDate: Date): void {
    const daysInCurrentMonth: number[] = Array.from(
      Array(new Date(this.currentYear, this.monthDate + 1, 0).getDate()).keys(),
    );
    const daysInPreviousMonth: number[] = Array.from(
      Array(new Date(this.currentYear, this.monthDate, 0).getDate()).keys(),
    ).reverse();

    // If the first day of the month is Sunday, set the first day index to 7
    // This variable is used to determine how many past days must be displayed in the calendar
    const firstDayOfTheMonth: number =
      new Date(this.currentYear, this.monthDate, 1).getDay() === 0
        ? 7
        : new Date(this.currentYear, this.monthDate, 1).getDay();

    let concatedDates = [];
    this.pastDatesArray = this.assignPastDatesForCalendar(
      this.currentYear,
      daysInPreviousMonth,
      firstDayOfTheMonth,
      scheduledDate,
    );
    this.currentDatesArray = this.assignCurrentDatesForCalendar(daysInCurrentMonth, scheduledDate);

    concatedDates = concatedDates.concat(this.pastDatesArray.reverse());
    concatedDates = concatedDates.concat(this.currentDatesArray);

    this.futureDatesArray = this.assignFutureDatesForCalendar(this.currentYear, daysInCurrentMonth, scheduledDate);

    concatedDates = concatedDates.concat(this.futureDatesArray);
    this.calendarObject_.next({ days: concatedDates, date: new Date(this.currentYear, this.monthDate) });
  }

  // Create an array of Dates for the past month until the current / first day
  assignPastDatesForCalendar(
    currentYear: number,
    daysInPreviousMonth: number[],
    firstDayOfTheMonth: number,
    scheduledDate: Date,
  ): Date[] {
    const pastDatesArray = [];
    for (let dayIndex = 0; dayIndex < firstDayOfTheMonth - 1; dayIndex++) {
      const date = new Date(this.currentYear, this.monthDate - 1, daysInPreviousMonth[dayIndex] + 1);
      pastDatesArray.push({
        date: new Date(currentYear, this.monthDate - 1, daysInPreviousMonth[dayIndex] + 1),
        attribute: this.defineDateAttributes(date, scheduledDate),
      });
    }
    return pastDatesArray;
  }

  // Create an array of Dates for the curreny month and define the count of dates that must be displayed
  assignCurrentDatesForCalendar(daysInCurrentMonth: number[], scheduledDate: Date): Date[] {
    const currentDatesArray = [];
    for (const dayIndex of daysInCurrentMonth) {
      const date = new Date(this.currentYear, this.monthDate, daysInCurrentMonth[dayIndex] + 1);
      currentDatesArray.push({ date, attribute: this.defineDateAttributes(date, scheduledDate) });
    }
    return currentDatesArray;
  }

  // If more dates can be concatenated to the calendar component, add the next month dates accordingly
  assignFutureDatesForCalendar(currentYear: number, daysInNextMonth: number[], scheduledDate: Date): Date[] {
    const lengthOfCalendarMonth =
      this.pastDatesArray.length + this.currentDatesArray.length < 35
        ? 35 - (this.pastDatesArray.length + this.currentDatesArray.length)
        : 42 - (this.pastDatesArray.length + this.currentDatesArray.length);
    const futureDatesArray = [];
    for (let dayIndex = 0; dayIndex < lengthOfCalendarMonth; dayIndex++) {
      const date = new Date(this.currentYear, this.monthDate + 1, daysInNextMonth[dayIndex] + 1);
      futureDatesArray.push({
        date: new Date(currentYear, this.monthDate + 1, daysInNextMonth[dayIndex] + 1),
        attribute: this.defineDateAttributes(date, scheduledDate),
      });
    }

    return futureDatesArray;
  }

  defineDateAttributes(date: Date, scheduledDate: Date): string {
    let attribute = 'past';
    if (date.getTime() === scheduledDate?.setHours(0, 0, 0)) {
      attribute = 'available';
    } else if (date.getTime() === this.todayDate.getTime()) {
      attribute = 'today';
    } else if (
      date.getTime() >= this.firstAvailableDates.setHours(0, 0, 0, 0) &&
      date.getTime() <= this.lastAvailableDates.setHours(0, 0, 0, 0)
    ) {
      if (date.getDay() !== 6 && date.getDay() !== 0) {
        attribute = 'future';
      }
    }

    return attribute;
  }

  updateMonth(month: number, scheduledDate: Date): void {
    this.monthDate = this.monthDate + month;
    this.assembleMonthDays(scheduledDate);
  }

  onCalendarDateClick(date: Date, billingForm: UntypedFormGroup): void {
    const countryCode = this.allSiteSettingsService.currentChannelData$.getValue().country;
    const languageCode = this.allSiteSettingsService.currentChannelData$.getValue().language;
    const formattedDate = getDateFormat(countryCode, languageCode);
    this.checkoutService.isDeliveryOptLoading_.next(true);
    const passedDate = formatDate(date, formattedDate, languageCode + '-' + countryCode);

    this.checkoutService
      .scheduleDelivery(passedDate)
      .pipe(catchError(() => of(this.checkoutService.isDeliveryOptLoading_.next(false))))
      .subscribe((data: Cart) => {
        this.checkoutService.isDeliveryOptLoading_.next(false);
        billingForm.patchValue({
          selectedDate: data.scheduledDeliveryDate,
        });
        this.cartStoreService.setCartState(data);
      });
  }
}
