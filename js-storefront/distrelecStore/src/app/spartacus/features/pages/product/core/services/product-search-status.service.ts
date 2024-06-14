import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { distinctUntilChanged } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class ProductSearchStatusService {
  private loadingStatus$ = new BehaviorSubject(true);
  isLoading$ = this.loadingStatus$.asObservable().pipe(distinctUntilChanged()); // eslint-disable-line @typescript-eslint/member-ordering

  setLoading(isLoading: boolean) {
    this.loadingStatus$.next(isLoading);
  }
}
