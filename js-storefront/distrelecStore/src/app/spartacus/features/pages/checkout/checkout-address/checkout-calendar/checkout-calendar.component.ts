import { ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { faAngleLeft, faAngleRight, faCircle, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { CalendarService } from '@services/calendar.service';
import { CartStoreService } from '@state/cartState.service';
import { BehaviorSubject } from 'rxjs';
import { first } from 'rxjs/operators';
import { Cart } from '@spartacus/cart/base/root';
import { MonthData, PossibleDeliveryDates } from '@model/misc.model';

@Component({
  selector: 'app-checkout-calendar',
  templateUrl: './checkout-calendar.component.html',
  styleUrls: ['./checkout-calendar.component.scss'],
})
export class CheckoutCalendarComponent implements OnInit {
  @Input() billingForm: UntypedFormGroup;

  @Output() isLoading: EventEmitter<boolean> = new EventEmitter<boolean>();

  faAngleLeft: IconDefinition = faAngleLeft;
  faAngleRight: IconDefinition = faAngleRight;
  faCircle: IconDefinition = faCircle;

  calendarObject_: BehaviorSubject<{ days: { date: Date; attribute: string }[]; date: Date }> =
    this.calendarService.calendarObject_;

  scheduledDate: Date | null;

  isNextMonthDisabled = false;
  isPreviousMonthDisabled = false;

  cartData_: BehaviorSubject<Cart> = this.cartStoreService.getCartState();

  constructor(
    private cartStoreService: CartStoreService,
    private calendarService: CalendarService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    // If cart has pre-selected today, open caledar on that date
    if (this.cartData_.getValue().scheduledDeliveryDate) {
      this.scheduledDate = new Date(this.cartData_.getValue().scheduledDeliveryDate);
      this.calendarService.monthDate = this.scheduledDate.getMonth();
      this.calendarService.currentYear = this.scheduledDate.getFullYear();
    }

    if (this.calendarService.possibleDeliveryDates) {
      this.assignDeliveryDates(this.calendarService.possibleDeliveryDates);
      this.ifNextAndBeforeMonthSelectable();
    } else {
      this.calendarService
        .getLaterDeliveryDetails()
        .pipe(first())
        .subscribe((data: PossibleDeliveryDates) => {
          this.assignDeliveryDates(data);
          this.ifNextAndBeforeMonthSelectable();
        });
    }
  }

  assignDeliveryDates(dates: PossibleDeliveryDates): void {
    this.calendarService.todayDate.setHours(0, 0, 0, 0);
    this.defineFirstAvailableDate(new Date(dates.minRequestedDeliveryDateForCurrentCart));
    this.calendarService.lastAvailableDates = new Date(dates.maxRequestedDeliveryDateForCurrentCart);
    this.calendarService.assembleMonthDays(this.scheduledDate);
    this.isLoading.emit(false);
  }

  // HDLS-3022: disable month selection buttons if next or previous month is outside of possible delivery dates
  ifNextAndBeforeMonthSelectable(): void {
    const currentMonthDatesObject: MonthData = this.calendarService.calendarObject_.getValue();
    const firstDayVisible: Date = currentMonthDatesObject.days[0].date;
    const lastDayVisible: Date = currentMonthDatesObject.days[currentMonthDatesObject.days.length - 1].date;

    // flags responsible for disabling these buttons
    this.isPreviousMonthDisabled = firstDayVisible.getTime() < this.calendarService.firstAvailableDates.getTime();
    this.isNextMonthDisabled = lastDayVisible.getTime() > this.calendarService.lastAvailableDates.getTime();
  }

  updateMonth(month: number): void {
    this.calendarService.updateMonth(month, this.scheduledDate);
    this.ifNextAndBeforeMonthSelectable();
  }

  onCalendarDateClick(date: Date): void {
    this.calendarService.onCalendarDateClick(date, this.billingForm);
  }

  defineFirstAvailableDate(firstDateFromBE: Date): void {
    this.calendarService.firstAvailableDates = firstDateFromBE;
  }
}
