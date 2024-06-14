import { Component, OnInit } from '@angular/core';
import { PrintPageEvent } from '@features/tracking/events/print-page-event';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { faPrint, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { createFrom, EventService, WindowRef } from '@spartacus/core';

@Component({
  selector: 'app-print-compare',
  templateUrl: './print-compare.component.html',
  styleUrls: ['./print-compare.component.scss'],
})
export class PrintCompareComponent implements OnInit {
  public faPrint: IconDefinition = faPrint;

  constructor(
    private winRef: WindowRef,
    private eventService: EventService,
  ) {}

  printPage(): void {
    this.eventService.dispatch(createFrom(PrintPageEvent, { context: { pageType: ItemListEntity.COMPARE } }));
    this.winRef.nativeWindow.print();
  }

  ngOnInit() {}
}
