import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { OccEndpointsService } from '@spartacus/core';
import {
  AddressChangeFormInt,
  AddressChangeRespInt,
} from '@features/shared-modules/offline-change-address/offline-change-address.interface';
import { take } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class AddressChangeService {
  constructor(
    private http: HttpClient,
    protected occEndpoints: OccEndpointsService,
  ) {}

  postAddressChange(post: AddressChangeFormInt) {
    const reqObj: AddressChangeRespInt = {
      comment: post.comment,
      customerNumber: post.customerNumber,
      newAddress: {
        companyName: post.newCompanyName,
        town: post.newTown,
        country: post.newCountry,
        department: post.newDepartment,
        firstName: post.newFirstName,
        lastName: post.newLastName,
        number: post.newNumber,
        street: post.newStreet,
        zip: post.newPostalCode,
      },
      oldAddress: {
        companyName: post.oldCompanyName,
        country: post.oldCountry,
        town: post.oldTown,
        department: post.oldDepartment,
        firstName: post.oldFirstName,
        lastName: post.oldLastName,
        number: post.oldNumber,
        street: post.oldStreet,
        zip: post.oldPostalCode,
      },
    };

    const reqStr = JSON.stringify(reqObj);
    const headers = new HttpHeaders().set('Content-Type', 'application/json; charset=utf-8');

    this.http
      .post(this.occEndpoints.buildUrl(`/cms/address-change`), reqStr, { headers: headers })
      .pipe(take(1))
      .subscribe();
  }
}
