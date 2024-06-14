import { Component, Input, Attribute, AfterViewInit } from '@angular/core';
import { SessionStorageService } from '@services/session-storage.service';
import { arrowUp, arrowDown } from '@assets/icons/icon-index';

@Component({
  selector: 'app-accordion',
  templateUrl: './accordion.component.html',
  styleUrls: ['./accordion.component.scss'],
})
export class AccordionComponent implements AfterViewInit {
  @Input() title = '';
  @Input() collapsed = false;
  @Input() savingCollapseState = true;
  @Input() uniqueIdentifier: string | undefined;
  @Input() titleId = '';

  elementId: string;

  iconArrowUp = arrowUp;
  iconArrowDown = arrowDown;

  constructor(
    @Attribute('id') elementId,
    private sessionStorageService: SessionStorageService,
  ) {
    this.elementId = elementId;
  }

  ngAfterViewInit(): void {
    if (this.savingCollapseState) {
      this.loadCollapseState();
    }
  }

  toggleCollapsedState(): void {
    this.collapsed = !this.collapsed;
    if (this.savingCollapseState) {
      this.saveCollapseState();
    }
  }

  getAccordionSessionId(): string {
    let sessionId = 'accordionColapseState_' + this.elementId;
    if (this.uniqueIdentifier) {
      sessionId = sessionId + '_' + this.uniqueIdentifier;
    }
    return sessionId;
  }

  saveCollapseState(): void {
    this.sessionStorageService.setItem(this.getAccordionSessionId(), this.collapsed ? '1' : '0');
  }

  loadCollapseState(): void {
    const sessionCollapseState = this.sessionStorageService.getItem(this.getAccordionSessionId());
    if (sessionCollapseState) {
      this.collapsed = sessionCollapseState === '1';
    }
  }
}
