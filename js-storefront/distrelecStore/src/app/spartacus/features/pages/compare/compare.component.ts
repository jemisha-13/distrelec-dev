import { Component } from '@angular/core';
import { faAngleLeft, faAngleRight } from '@fortawesome/free-solid-svg-icons';
import { CompareService } from './core';
import { WindowRef } from '@spartacus/core';

@Component({
  selector: 'app-compare',
  templateUrl: './compare.component.html',
  styleUrls: ['./compare.component.scss'],
})
export class CompareComponent {
  compareList$ = this.compareService.getCompareListState();

  faArrowLeft = faAngleLeft;
  faArrowRight = faAngleRight;

  isTabletDown: boolean = this.winRef.isBrowser() ? this.winRef.nativeWindow.innerWidth <= 768 : false;

  constructor(
    private compareService: CompareService,
    private winRef: WindowRef,
  ) {}

  prev(): void {
    this.winRef.document.getElementById('compare-list').scrollBy({
      left: -200,
      behavior: 'smooth',
    });
  }

  next(): void {
    const scrollLeft = (this.winRef.document.getElementById('compare-list').clientWidth * 25) / 100;
    this.winRef.document.getElementById('compare-list').scrollBy({
      left: scrollLeft,
      behavior: 'smooth',
    });
  }
}
