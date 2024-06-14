import { Component, EventEmitter, HostListener, Input, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { NotifyMeService } from '@services/notify-me.service';
import { AppendComponentService } from '@services/append-component.service';
import { faTimes } from '@fortawesome/free-solid-svg-icons';
import { BehaviorSubject } from 'rxjs';
import { first } from 'rxjs/operators';
import { DistrelecUserService } from '@services/user.service';
import { DISTRELEC_EMAIL_PATTERN } from '@helpers/constants';

@Component({
  selector: 'app-notify-me',
  templateUrl: './notify-me.component.html',
  styleUrls: ['./notify-me.component.scss'],
})
export class NotifyMeComponent implements OnInit {
  @Input() productCode: string;

  @Output() close = new EventEmitter<void>();

  userRegistrationForm: UntypedFormGroup;

  displaySuccessMessage = false;
  displayErrorMessage = false;
  isClickInsideComponent = false;
  showForm = true;

  faTimes = faTimes;

  userEmail: string;

  constructor(
    private fb: UntypedFormBuilder,
    public appendComponentService: AppendComponentService,
    private notifyMeService: NotifyMeService,
    private userService: DistrelecUserService,
  ) {}

  @HostListener('click')
  handleClick() {
    this.isClickInsideComponent = true;
  }

  @HostListener('document:click')
  handleDocumentClick() {
    if (!this.isClickInsideComponent) {
      this.hideNotifyModal();
    }
    this.isClickInsideComponent = false;
  }

  hideNotifyModal() {
    this.close.emit();
  }

  submitNotifyMe() {
    if (this.userRegistrationForm.get('email').errors?.required || this.userRegistrationForm.invalid) {
      return;
    }
    this.notifyMeService
      .getNotifyMe([this.productCode], this.userRegistrationForm.value.email)
      .pipe(first())
      .subscribe();
    this.displaySuccessMessage = true;
    this.showForm = false;
  }

  ngOnInit() {
    this.userRegistrationForm = this.fb.group({
      email: ['', [Validators.required, Validators.pattern(DISTRELEC_EMAIL_PATTERN)]],
    });

    const userDetails = this.userService.getUserDetails().getValue();
    if (userDetails) {
      this.userEmail = userDetails.contactAddress.email;
    }
  }
}
