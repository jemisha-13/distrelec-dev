import { Component, Input, OnInit } from '@angular/core';
import { faCheckCircle } from '@fortawesome/free-solid-svg-icons';
import { AppendComponentService } from 'src/app/spartacus/services/append-component.service';

@Component({
  selector: 'app-success-popup',
  templateUrl: './success-popup.component.html',
  styleUrls: ['./success-popup.component.scss'],
})
export class SuccessPopupComponent implements OnInit {
  constructor(private appendComponentService: AppendComponentService) {}

  faCheckCircle = faCheckCircle;

  @Input() data: {
    title: string;
    subtitle: string;
    extraDetails: string[];
  };

  onCloseButton() {
    this.appendComponentService.removeSuccessPopupFromBody();
  }

  ngOnInit() {}
}
