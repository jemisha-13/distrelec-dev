import { Injectable } from '@angular/core';
import { OccEndpointsService, UserIdService } from '@spartacus/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { first } from 'rxjs/operators';
import { MainReason } from '@model/returns.model';

@Injectable({
  providedIn: 'root',
})
export class ReturnReasonsService {
  returnReasons: any;
  private userId = 'anonymous';
  private subscriptions = new Subscription();

  constructor(
    private http: HttpClient,
    private occEndpointsService: OccEndpointsService,
    protected router: Router,
    private userIdService: UserIdService,
  ) {
    this.subscriptions.add(
      this.userIdService
        .getUserId()
        .pipe(first())
        .subscribe((id) => {
          if (id !== 'anonymous') {
            this.userId = id?.toString();
          }
        }),
    );
  }

  getMainReasons() {
    return this.http.get<MainReason[]>(
      this.occEndpointsService.buildUrl(`/users/${this.userId}/order-returns/return-reasons/`),
    );
  }
}
