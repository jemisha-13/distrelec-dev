import { Injectable } from '@angular/core';
import { BomFileEntry } from '@features/pages/bom-tool/model/bom-file';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { distinctUntilChanged, map, startWith } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class BomToolReviewService {
  private entries = new Map<number, BomFileEntry>();
  private entrySelectSignal = new Subject<void>();
  private changeSignal = new Subject<void>();

  private allSelected = new BehaviorSubject<boolean>(false);
  private detailedView = new BehaviorSubject<boolean>(false);

  reset() {
    this.entries.clear();
  }

  getEntries() {
    return Array.from(this.entries)
      .sort((a, b) => a[0] - b[0])
      .map(([_, bomFileEntry]) => bomFileEntry);
  }

  setEntry(entry: BomFileEntry) {
    this.entries.set(entry.position, entry);
    this.triggerSelectChange();
  }

  getSelected() {
    return this.getEntries().filter((entry) => entry.isSelected && entry.isValid);
  }

  isAnySelected(): Observable<boolean> {
    return this.entrySelectSignal.pipe(
      startWith(false),
      map(() => this.getSelected().length > 0),
      distinctUntilChanged(),
    );
  }

  areAllSelected(): boolean {
    return this.getSelected().length === this.entries.size;
  }

  setAllSelected(status: boolean) {
    this.allSelected.next(status);
    this.triggerSelectChange();
  }

  getAllSelected(): Observable<boolean> {
    return this.allSelected;
  }

  setDetailedView(status: boolean) {
    this.detailedView.next(status);
  }

  getDetailedView(): Observable<boolean> {
    return this.detailedView;
  }

  triggerSelectChange() {
    this.entrySelectSignal.next();
  }

  getSelectSignal() {
    return this.entrySelectSignal;
  }

  setChanged() {
    this.changeSignal.next();
  }

  getChangeSignal(): Observable<void> {
    return this.changeSignal;
  }

  markSelectedAsAddedToCart() {
    this.entries.forEach((entry) => {
      if (entry.isSelected) {
        entry.isAddedToCart = true;
      }
    });
  }
}
