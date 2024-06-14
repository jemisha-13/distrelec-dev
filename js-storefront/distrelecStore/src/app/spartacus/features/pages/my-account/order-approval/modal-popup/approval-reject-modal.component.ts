import { Component, EventEmitter, Input, NgZone, Output } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { WindowRef } from '@spartacus/core';
import { faExclamationTriangle, faFileAlt, faTimes } from '@fortawesome/free-solid-svg-icons';
import { AppendComponentService } from '@services/append-component.service';

@Component({
  selector: 'app-approval-reject-modal',
  templateUrl: './approval-reject-modal.component.html',
  styleUrls: ['./approval-reject-modal.component.scss'],
})
export class ApprovalRejectModalComponent {
  @Output() SaveRejectionNote = new EventEmitter<any>();

  @Input() data: {
    type: string;
    orderEntries?: any;
    productId?: string;
  };
  plpProductCode: string;
  type: string;
  faTimes = faTimes;
  faFileAlt = faFileAlt;
  faExclamationTriange = faExclamationTriangle;
  hasError = false;
  checked = false;
  shoppingListForm: UntypedFormGroup = this.fb.group({
    name: new UntypedFormControl(),
  });

  rejectNote: string;

  constructor(
    private fb: UntypedFormBuilder,
    private appendComponentService: AppendComponentService,
    private winRef: WindowRef,
    private ngZone: NgZone,
  ) {}

  closeModal(): void {
    this.appendComponentService.removeBackdropComponentFromBody();
    this.appendComponentService.removeApprovalRejectComponentFromBody();
  }

  callSaveRejectionNote() {
    if (this.rejectNote) {
      this.hasError = false;
      this.SaveRejectionNote.emit(true);
      this.rejectNote = '';
      this.closeModal();
    } else {
      this.hasError = true;
      this.ngZone.run(() => {
        setTimeout(() => {
          this.hasError = false;
        }, 5000);
      });
    }
  }
}
