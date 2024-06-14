import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ElementRef,
  Input,
  OnDestroy,
  Renderer2,
  ViewChild,
} from '@angular/core';
import { WindowRef } from '@spartacus/core';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'app-dist-scroll-bar',
  templateUrl: './scroll-bar.component.html',
  styleUrls: ['./scroll-bar.component.scss'],
})
export class ScrollBarComponent implements AfterViewInit, OnDestroy {
  @ViewChild('scrollContainer') scrollContainerRef: ElementRef;
  @Input() scrollAxis: 'x' | 'y' = 'y';

  scrollbarPosition_: BehaviorSubject<number> = new BehaviorSubject(0);
  showScrollBar_: BehaviorSubject<boolean> = new BehaviorSubject(false);
  scrollbarThumbSize_: BehaviorSubject<number> = new BehaviorSubject(40);

  private isDragging = false;
  private dragStartPosition = 0;
  private initialScrollPosition = 0;
  private ngContentRef: HTMLElement;
  private removeMouseMoveListener: () => void;
  private removeMouseUpListener: () => void;

  constructor(
    private winRef: WindowRef,
    private renderer: Renderer2,
    private cdRef: ChangeDetectorRef,
  ) {}

  ngOnDestroy(): void {
    this.removeListeners();
  }

  ngAfterViewInit(): void {
    if (!this.winRef.isBrowser() || !this.scrollContainerRef || !this.scrollContainerRef.nativeElement.children[0]) {
      return;
    }

    this.ngContentRef = this.scrollContainerRef.nativeElement.children[0];
    this.initializeScrollbar();
    this.attachScrollListener();
  }

  trackScroll(): void {
    if (!this.winRef.isBrowser()) {
      return;
    }

    const { scrollPosition, scrollSize, clientSize } = this.getAxisProperties();
    const scrollRatio = scrollPosition / (scrollSize - clientSize);
    const maxThumbPosition = clientSize - this.scrollbarThumbSize_.getValue();

    this.scrollbarPosition_.next(scrollRatio * maxThumbPosition);
  }

  startDrag(event: MouseEvent): void {
    if (!this.winRef.isBrowser()) {
      return;
    }

    event.preventDefault();
    this.isDragging = true;
    this.dragStartPosition = this.scrollAxis === 'x' ? event.pageX : event.pageY;
    this.initialScrollPosition = this.scrollbarPosition_.getValue();

    this.removeMouseMoveListener = this.renderer.listen('document', 'mousemove', this.onDrag.bind(this));
    this.removeMouseUpListener = this.renderer.listen('document', 'mouseup', this.stopDrag.bind(this));
  }

  private onDrag(event: MouseEvent): void {
    if (!this.isDragging) {
      return;
    }

    const eventPageAxis = this.scrollAxis === 'x' ? event.pageX : event.pageY;
    const draggedPosition = eventPageAxis - this.dragStartPosition;
    this.updateScrollbarPosition(draggedPosition);
  }

  private stopDrag(): void {
    if (!this.isDragging) {
      return;
    }
    this.isDragging = false;
    this.removeListeners();
  }

  private removeListeners(): void {
    if (this.removeMouseMoveListener) {
      this.removeMouseMoveListener();
    }
    if (this.removeMouseUpListener) {
      this.removeMouseUpListener();
    }
  }

  private updateScrollbarPosition(draggedPosition: number): void {
    const { clientSize } = this.getAxisProperties();
    let newPosition = this.initialScrollPosition + draggedPosition;

    newPosition = this.preventScrollBarExceedingRange(newPosition, clientSize);

    this.scrollbarPosition_.next(newPosition);
    this.updateContainerScroll(newPosition, clientSize);
  }

  private updateContainerScroll(newPosition: number, trackScrollSize: number): void {
    const { scrollSize, clientSize } = this.getAxisProperties();
    const scrollPercentage = newPosition / (trackScrollSize - this.scrollbarThumbSize_.getValue());
    const maxScrollable = scrollSize - clientSize;

    if (this.scrollAxis === 'x') {
      this.ngContentRef.scrollLeft = scrollPercentage * maxScrollable;
    } else {
      this.ngContentRef.scrollTop = scrollPercentage * maxScrollable;
    }
  }

  private initializeScrollbar(): void {
    const { clientSize, scrollSize } = this.getAxisProperties();
    const viewportToContentRatio = clientSize / scrollSize;
    const updatedThumbSize = Math.max(viewportToContentRatio * clientSize, 40);
    const scrollDifferenceThreshold = 10;

    this.scrollbarThumbSize_.next(updatedThumbSize);
    this.showScrollBar_.next(scrollSize - clientSize > scrollDifferenceThreshold);
    this.cdRef.detectChanges();
  }

  private getAxisProperties(): { scrollSize: number; clientSize: number; scrollPosition: number } {
    return {
      scrollSize: this.scrollAxis === 'x' ? this.ngContentRef.scrollWidth : this.ngContentRef.scrollHeight,
      clientSize: this.scrollAxis === 'x' ? this.ngContentRef.clientWidth : this.ngContentRef.clientHeight,
      scrollPosition: this.scrollAxis === 'x' ? this.ngContentRef.scrollLeft : this.ngContentRef.scrollTop,
    };
  }

  private preventScrollBarExceedingRange(position: number, trackSize: number): number {
    const maxPosition = trackSize - this.scrollbarThumbSize_.getValue();
    return Math.max(0, Math.min(position, maxPosition));
  }

  private attachScrollListener(): void {
    if (this.ngContentRef) {
      this.ngContentRef.addEventListener('scroll', () => this.trackScroll());
    }
  }
}
