import { Injectable } from '@angular/core';
import { UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { DistCookieService } from '@services/dist-cookie.service';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import { BomToolService } from '@features/pages/bom-tool/bom-tool.service';
import { DistrelecUserService } from '@services/user.service';
import {
  AuthStorageService,
  OCC_USER_ID_ANONYMOUS,
  OCC_USER_ID_CURRENT,
  OCC_USER_ID_GUEST,
  User,
  UserIdService,
} from '@spartacus/core';
import { ShoppingListService } from '@services/feature-services';
import { DashboardContents } from '@model/my-account.model';

export type UserType = typeof OCC_USER_ID_CURRENT | typeof OCC_USER_ID_ANONYMOUS | typeof OCC_USER_ID_GUEST;

@Injectable()
export class HeaderLoginService {
  userType$: Observable<UserType> = this.getUserIdAndGetLists();
  count$: Observable<DashboardContents> = this.getDashboardContents();
  numBomFiles$: Observable<number> = this.bomToolService.getListSize();

  userData_: BehaviorSubject<User> = this.distrelecUserService.userDetails_;
  listTotal_: Observable<number> = this.shoppingListService
    .getShoppingListsState()
    .pipe(map((lists) => lists.list.length));

  constructor(
    private authStorageService: AuthStorageService,
    private bomToolService: BomToolService,
    private cookieService: DistCookieService,
    private distrelecUserService: DistrelecUserService,
    private userIdService: UserIdService,
    private shoppingListService: ShoppingListService,
  ) {}

  getDashboardContents(): Observable<DashboardContents> {
    return this.userIdService.getUserId().pipe(
      filter((userId) => userId === 'current'),
      switchMap(() => this.distrelecUserService.getCurrentDashboardContents$()),
    );
  }

  getUserIdAndGetLists(): Observable<UserType> {
    return this.userIdService.getUserId().pipe(switchMap((userId: UserType) => this.assignUser(userId)));
  }

  assignUser(userId: UserType): Observable<UserType> {
    if (userId === OCC_USER_ID_CURRENT) {
      return of(OCC_USER_ID_CURRENT);
    }

    return userId === OCC_USER_ID_ANONYMOUS && this.checkIfGuest() ? of(OCC_USER_ID_GUEST) : of(OCC_USER_ID_ANONYMOUS);
  }

  setHeaderLoginForm(): UntypedFormGroup {
    return new UntypedFormGroup({
      email: new UntypedFormControl(this.cookieService.get('email') !== '' ? this.cookieService.get('email') : '', [
        Validators.required,
      ]),
      password: new UntypedFormControl(''),
      rememberMe: new UntypedFormControl(
        this.cookieService.get('rememberMe') !== '' ? JSON.parse(this.cookieService.get('rememberMe')) : '',
      ),
    });
  }

  setUserIdAnonymous(): void {
    this.userIdService.setUserId(OCC_USER_ID_ANONYMOUS);
  }

  checkIfGuest(): boolean {
    return !!this.authStorageService.getItem('guest');
  }

  isB2B(): boolean {
    return this.distrelecUserService.isB2BAny();
  }
}
