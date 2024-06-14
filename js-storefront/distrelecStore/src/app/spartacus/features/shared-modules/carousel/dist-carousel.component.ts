import { ChangeDetectorRef, Component, ElementRef, Input, TemplateRef, ViewChild } from '@angular/core';
import { arrowHorizontal } from '@assets/icons/icon-index';
import { WindowRef } from '@spartacus/core';
import { DistIcon } from '@model/icon.model';

@Component({
  selector: 'dist-carousel',
  templateUrl: './dist-carousel.component.html',
  styleUrls: ['./dist-carousel.component.scss'],
})
export class DistCarouselComponent {
  @Input() items: unknown[] = [];
  @Input() template: TemplateRef<unknown>;
  @ViewChild('scrollContainer') scrollContainer: ElementRef;
  @ViewChild('item') item: ElementRef;

  arrow: DistIcon = arrowHorizontal;
  canScrollPrev = false;
  canScrollNext = true;

  constructor(
    private cdRef: ChangeDetectorRef,
    private winRef: WindowRef,
  ) {}

  ngAfterViewInit(): void {
    if (!this.winRef.isBrowser()) {
      return;
    }

    this.scrollContainer.nativeElement.addEventListener('scroll', () => {
      this.checkScrollArrows();
    });
  }

  scrollItems(direction: 'prev' | 'next'): void {
    if (!this.winRef.isBrowser()) {
      return;
    }

    const container: HTMLElement = this.scrollContainer.nativeElement;
    const offset = 20;
    const scrollAmount = this.item.nativeElement.offsetWidth + offset;

    if (container && scrollAmount) {
      container.scrollBy({
        left: direction === 'next' ? scrollAmount : -scrollAmount,
        behavior: 'smooth',
      });
    }
  }

  private checkScrollArrows(): void {
    if (!this.winRef.isBrowser()) {
      return;
    }

    const container = this.scrollContainer.nativeElement;
    const maxScrollLeft = container.scrollWidth - container.clientWidth;
    this.canScrollPrev = container.scrollLeft > 0;
    this.canScrollNext = container.scrollLeft < maxScrollLeft;
    this.cdRef.detectChanges();
  }
}
