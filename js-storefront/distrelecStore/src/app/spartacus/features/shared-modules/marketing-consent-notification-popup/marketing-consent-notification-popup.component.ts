import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { LocalStorageService } from '@services/local-storage.service';
import { Observable, ReplaySubject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';
import { NavigationEnd, Router } from '@angular/router';
import { TranslationService } from '@spartacus/core';

@Component({
  selector: 'app-marketing-consent-notification-popup',
  templateUrl: './marketing-consent-notification-popup.component.html',
  styleUrls: ['./marketing-consent-notification-popup.component.scss'],
})
export class MarketingConsentNotificationPopupComponent implements OnInit, OnDestroy {
  @Input()
  confirmEmail: string;
  @Input()
  showAfterNavigation = true;
  showConfirmModal = false;
  destroy$ = new ReplaySubject();

  @Output()
  modalClosed = new EventEmitter<void>();

  constructor(
    private localStorage: LocalStorageService,
    private router: Router,
    private translationService: TranslationService,
  ) {}

  ngOnInit(): void {
    if (!this.showAfterNavigation) {
      this.showConfirmModal = true;
      return;
    }
    this.router.events
      .pipe(
        filter((event) => event instanceof NavigationEnd),
        takeUntil(this.destroy$),
      )
      .subscribe({
        next: (_) => {
          this.showConfirmModal = true;
        },
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }

  onModalClose() {
    this.showConfirmModal = false;
    this.localStorage.removeItem('consentEmail');
    this.modalClosed.emit();
    this.modalClosed.complete();
  }

  getConfirmEmail(): Observable<string> {
    return this.translationService.translate('text.preferences.updated.email.sent', {
      email: '<br>' + this.confirmEmail,
    });
  }
}
