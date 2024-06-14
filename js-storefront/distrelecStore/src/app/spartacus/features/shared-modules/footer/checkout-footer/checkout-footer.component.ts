import { Component, Input, OnInit } from '@angular/core';
import { CmsService } from '@spartacus/core';
import { first, map, mergeMap, switchMap, toArray } from 'rxjs/operators';
import { from, Observable } from 'rxjs';
import { CmsComponent } from '@spartacus/core/src/model/cms.model';
import { angleLock } from '@assets/icons/icon-index';

@Component({
  selector: 'app-checkout-footer',
  templateUrl: './checkout-footer.component.html',
  styleUrls: ['./checkout-footer.component.scss'],
})
export class CheckoutFooterComponent implements OnInit {
  @Input() componentData?: Observable<CmsComponent>;

  paymentIconUrls: Observable<string[]>;
  angleLock = angleLock;
  currentYear: number;

  constructor(private componentService: CmsService) {}

  ngOnInit() {
    this.paymentIconUrls = this.getPaymentMethodIcons();
    this.currentYear = new Date().getFullYear();
  }

  private getPaymentMethodIcons() {
    return this.componentData?.pipe(
      map((data) => data['paymentMethods'].split(' ')),
      switchMap((componentIds) =>
        this.fetchPaymentComponents(componentIds).pipe(
          map((component) => this.extractPaymentMethodIcon(component)),
          toArray(),
        ),
      ),
    );
  }

  private fetchPaymentComponents(componentIds: string[]): Observable<CmsComponent> {
    return from(componentIds).pipe(mergeMap((id) => this.componentService.getComponentData(id).pipe(first())));
  }

  private extractPaymentMethodIcon(paymentComponent: CmsComponent): string {
    return paymentComponent['icon'].url;
  }
}
