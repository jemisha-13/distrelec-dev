import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { AppendComponentService } from '@services/append-component.service';
import { DistCartService } from '@services/cart.service';
import { BehaviorSubject, of } from 'rxjs';
import { catchError, first, tap } from 'rxjs/operators';

import { ShareCartFormData } from '@model/cart.model';

@Component({
  selector: 'app-form-modal',
  templateUrl: './form-modal.component.html',
  styleUrls: ['./form-modal.component.scss'],
})
export class FormModalComponent implements OnInit {
  @Input() data: {
    title?: { key: string; value: string };
    senderName?: { key: string; value: string };
    senderEmail?: { key: string; value: string };
    receiverName?: { key: string; value: string };
    receiverEmail?: { key: string; value: string };
    message?: { key: string; value: string };
    icon?: string;
  };

  form: UntypedFormGroup;
  isLoading_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  constructor(
    private fb: UntypedFormBuilder,
    private appendComponentService: AppendComponentService,
    private cartService: DistCartService,
  ) {}

  onCancelClick() {
    this.appendComponentService.removeFormModalFromBody();
  }

  onSendClick() {
    this.isLoading_.next(true);
    this.cartService
      .sendCartViaEmail(this.getShareCartForm())
      .pipe(
        first(),
        tap(() => this.onRequestComplete()),
        catchError(() => of(this.onRequestComplete())),
      )
      .subscribe();
  }

  getShareCartForm(): ShareCartFormData {
    return {
      senderName: this.form.controls.senderName.value,
      senderEmail: this.form.controls.senderEmail.value,
      receiverName: this.form.controls.receiverName.value,
      receiverEmail: this.form.controls.receiverEmail.value,
      message: this.form.controls.message.value,
    };
  }

  onRequestComplete() {
    this.isLoading_.next(false);
    this.appendComponentService.removeFormModalFromBody();
    this.appendComponentService.appendSuccessPopup<{ receiverEmail: string; receiverMessage: string }>(
      'form.success',
      'form.message_sent',
      { receiverEmail: this.form.controls.receiverEmail.value, receiverMessage: this.form.controls.message.value },
    );
  }

  ngOnInit() {
    this.form = this.fb.group({
      senderName: new UntypedFormControl(this.data.senderName.value, Validators.required),
      senderEmail: new UntypedFormControl(this.data.senderEmail.value, [Validators.required, Validators.email]),
      receiverName: new UntypedFormControl(this.data.receiverName.value, Validators.required),
      receiverEmail: new UntypedFormControl(this.data.receiverEmail.value, [Validators.required, Validators.email]),
      message: new UntypedFormControl(this.data.message.value, [Validators.required]),
    });
  }
}
