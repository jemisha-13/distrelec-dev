import { Component, EventEmitter, Input, Output } from '@angular/core';
import { IconProp } from '@fortawesome/fontawesome-svg-core';

type SupportedAttributes = 'aainteraction' | 'location';

@Component({
  selector: 'app-toolbar-item',
  templateUrl: './toolbar-item.component.html',
  styleUrls: ['./toolbar-item.component.scss'],
})
export class ToolbarItemComponent {
  @Input() icon?: IconProp;
  @Input() labelTranslationKey: string;
  @Input() dataAttributes?: Record<SupportedAttributes, string>;
  @Input() class?: string;
  @Input() toolbarId?: string;
  @Output() click = new EventEmitter();

  onClick(event) {
    event.stopPropagation();
    this.click.emit(event);
  }
}
