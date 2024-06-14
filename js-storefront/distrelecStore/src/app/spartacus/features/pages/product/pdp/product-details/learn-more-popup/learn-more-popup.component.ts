import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-learn-more-popup',
  templateUrl: './learn-more-popup.component.html',
  styleUrls: ['./learn-more-popup.component.scss'],
})
export class LearnMorePopupComponent implements OnInit {
  @Input() data: { rohsCode: string };

  @Output() close = new EventEmitter<void>();

  rohsCode: string;

  ngOnInit(): void {
    this.rohsCode = this.data.rohsCode;
  }

  onCloseButton() {
    this.close.emit();
  }
}
