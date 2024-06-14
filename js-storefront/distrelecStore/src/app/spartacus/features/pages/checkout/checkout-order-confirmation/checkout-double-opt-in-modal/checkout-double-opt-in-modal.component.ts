import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { faCircleInfo } from '@fortawesome/free-solid-svg-icons';
import { AppendComponentService } from '@services/append-component.service';

@Component({
  selector: 'app-checkout-double-opt-in-modal',
  templateUrl: './checkout-double-opt-in-modal.component.html',
  styleUrls: ['./checkout-double-opt-in-modal.component.scss'],
})
export class CheckoutDoubleOptInModalComponent implements OnInit, OnDestroy {
  @Input() email: string;
  @Output() doubleOptInModalVisibility = new EventEmitter<boolean>();

  faCircleInfo = faCircleInfo;

  constructor(private appendComponentService: AppendComponentService) {}

  ngOnInit(): void {
    this.appendComponentService.appendBackdropModal();
  }

  ngOnDestroy(): void {
    this.appendComponentService.removeBackdropComponentFromBody();
  }

  closeModal(): void {
    this.doubleOptInModalVisibility.emit(false);
  }
}
