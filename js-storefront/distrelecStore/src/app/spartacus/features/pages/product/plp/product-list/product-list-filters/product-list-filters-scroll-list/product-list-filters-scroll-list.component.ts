import { AfterViewInit, Component, ElementRef, Input, OnDestroy, ViewChild } from '@angular/core';
import { BehaviorSubject, fromEvent, Subscription } from 'rxjs';
import { Facet } from '@spartacus/core';
import { debounceTime, map, tap } from 'rxjs/operators';
import { arrowHorizontal } from '@assets/icons/icon-index';

@Component({
  selector: 'app-product-list-filters-scroll-list',
  templateUrl: './product-list-filters-scroll-list.component.html',
  styleUrls: ['./product-list-filters-scroll-list.component.scss'],
})
export class ProductListFiltersScrollListComponent implements OnDestroy, AfterViewInit {
  @Input() filters: Facet[] = [];

  @ViewChild('scrollList') scrollList: ElementRef<HTMLUListElement>;

  arrow = arrowHorizontal;

  isArrowVisible = new BehaviorSubject<{ left: boolean; right: boolean }>({
    left: false,
    right: true,
  });

  private subs: Subscription[] = [];

  ngAfterViewInit() {
    this.subs.push(
      fromEvent(this.scrollList.nativeElement, 'scroll')
        .pipe(
          debounceTime(100),
          map((event) => event.target as HTMLUListElement),
          map((el) => ({
            left: el.scrollLeft > 0,
            right: el.scrollWidth - el.scrollLeft > el.clientWidth,
          })),
          tap((isArrowVisible) => this.isArrowVisible.next(isArrowVisible)),
        )
        .subscribe(),
    );
  }

  ngOnDestroy(): void {
    this.subs.forEach((sub) => sub.unsubscribe());
    this.subs = null;
  }
}
