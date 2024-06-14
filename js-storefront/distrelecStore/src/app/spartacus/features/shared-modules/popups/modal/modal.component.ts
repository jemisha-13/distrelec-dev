import { Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewEncapsulation } from '@angular/core';
import { AppendComponentService } from '@services/append-component.service';
import { WindowRef } from '@spartacus/core';

type ModalSize = 'sm' | 'md' | 'lg' | 'xl';

@Component({
  selector: 'app-modal',
  templateUrl: './modal.component.html',
  styleUrls: ['./modal.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ModalComponent implements OnInit, OnDestroy {
  @Input() size?: ModalSize = 'md';
  @Output() close = new EventEmitter<void>();

  constructor(
    private appendComponentService: AppendComponentService,
    private winRef: WindowRef,
  ) {}

  ngOnInit(): void {
    this.winRef.document.body.classList.add('modal-open');
    this.appendComponentService.appendBackdropModal();
  }

  ngOnDestroy() {
    this.winRef.document.body.classList.remove('modal-open');
    this.appendComponentService.removeBackdropComponentFromBody();
  }

  onClose() {
    this.close.emit();
  }
}
