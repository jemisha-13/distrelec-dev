import { Component, Input, OnInit } from '@angular/core';
import { AppendComponentService } from 'src/app/spartacus/services/append-component.service';

@Component({
  selector: 'app-mov-popup',
  templateUrl: './mov-popup.component.html',
  styleUrls: ['./mov-popup.component.scss'],
})
export class MovPopupComponent implements OnInit {
  constructor(private appendComponentService: AppendComponentService) {}

  @Input() data: {
    movLimit: number;
    currentTotalValue: number;
    currencyIso: string;
  };

  onOKClick() {
    this.appendComponentService.removeBackdropComponentFromBody();
    this.appendComponentService.removeMovPopupFromBody();
  }

  ngOnInit() {}
}
