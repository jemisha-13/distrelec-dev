import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-action-message',
  templateUrl: './action-message.component.html',
  styleUrls: ['./action-message.component.scss'],
})
export class ActionMessageComponent implements OnChanges {
  @Input() type: 'success' | 'info' | 'danger' | 'warning' = 'info';
  @Input() message?: string;
  @Input() hasContent = false; // If ng-content is used instead of message, this should be set to true
  public shouldShow = false;

  ngOnChanges(changes: SimpleChanges) {
    this.shouldShow = this.hasContent || Boolean(changes.message?.currentValue?.length);
  }
}
