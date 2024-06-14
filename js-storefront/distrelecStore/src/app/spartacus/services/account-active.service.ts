import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AccountActiveService {
  constructor() {}

  isAccountActive = new BehaviorSubject<boolean>(true);
  currentState = this.isAccountActive.asObservable();

  updateAccountState(state: boolean) {
    this.isAccountActive.next(state);
  }
}
