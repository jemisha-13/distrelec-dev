import { Component, OnInit } from '@angular/core';
import { take } from 'rxjs/operators';
import { Location } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { DistrelecUserService } from '@services/user.service';
import { LocalStorageService } from '@services/local-storage.service';
import { faAngleLeft } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-my-account-order-confirmation',
  templateUrl: './returns-confirmation.component.html',
  styleUrls: ['./returns-confirmation.component.scss'],
})
export class OrderReturnsConfirmationComponent implements OnInit {
  responseType = '';
  responseMessage = '';
  loadedOrderDetails = false;
  orderCode: string;
  userDetails$ = this.userService.userDetails_;
  userDetails: any;
  returnCaseNumber: string;
  faAngleLeft = faAngleLeft;

  constructor(
    private route: ActivatedRoute,
    private location: Location,
    private userService: DistrelecUserService,
    private localStorageService: LocalStorageService,
  ) {}

  ngOnInit() {
    this.orderCode = this.route.snapshot.paramMap.get('orderCode');
    this.userDetails$.pipe(take(1)).subscribe((data) => {
      this.userDetails = data;
      this.loadedOrderDetails = true;
    });

    this.returnCaseNumber = this.localStorageService.getItem('rmaNumber');
    this.localStorageService.removeItem('rmaNumber');
  }

  goBack() {
    this.location.back();
  }
}
