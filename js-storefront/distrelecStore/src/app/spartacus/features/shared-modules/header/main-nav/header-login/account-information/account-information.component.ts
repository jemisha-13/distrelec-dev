import { Component, EventEmitter, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { User } from '@spartacus/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { HeaderLoginService } from '../header-login.service';
import { CompareService, ShoppingListService } from '@services/feature-services';
import { map } from 'rxjs/operators';
import { DashboardContents } from '@model/my-account.model';

@Component({
  selector: 'app-header-account-information',
  templateUrl: './account-information.component.html',
  styleUrls: ['./account-information.component.scss', '../header-login-modal.scss'],
  providers: [HeaderLoginService],
})
export class HeaderAccountInformationComponent {
  @Output() logoutEvent = new EventEmitter<void>();

  headerLoginForm: FormGroup;
  numBomFiles$: Observable<number> = this.headerLoginService.numBomFiles$;
  count$: Observable<DashboardContents> = this.headerLoginService.count$;
  userData_: BehaviorSubject<User> = this.headerLoginService.userData_;
  compareList$ = this.compareService.getCompareListState();
  listTotal_: Observable<number> = this.headerLoginService.listTotal_;
  shoppingList$ = this.shoppingListService.getShoppingListsState().pipe(map((lists) => lists.list[0] ?? []));

  constructor(
    private headerLoginService: HeaderLoginService,
    private compareService: CompareService,
    private shoppingListService: ShoppingListService,
  ) {}

  isB2B(): boolean {
    return this.headerLoginService.isB2B();
  }

  onLogout(): void {
    this.logoutEvent.emit();
  }
}
