import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { faList } from '@fortawesome/free-solid-svg-icons';
import { AppendComponentService } from '@services/append-component.service';
import { UserIdService } from '@spartacus/core';
import { Subscription } from 'rxjs';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { ICustomProduct } from '@model/product.model';

@Component({
  selector: 'app-shopping-list-toolbar-item',
  templateUrl: './shopping-list-toolbar-item.component.html',
  styleUrls: ['./shopping-list-toolbar-item.component.scss'],
})
export class ShoppingListToolbarItemComponent implements OnInit, OnDestroy {
  @Input() product: ICustomProduct;
  @Input() location?: string;
  @Input() toolbarId?: string;
  @Input() quantity?: number;
  @Input() reference?: string;

  faList = faList;

  private userId: string;
  private subscription: Subscription;

  constructor(
    private appendComponentService: AppendComponentService,
    private userService: UserIdService,
  ) {}

  ngOnInit(): void {
    this.subscription = this.userService.getUserId().subscribe((userId) => (this.userId = userId));
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  onClick() {
    if (this.userId === 'current') {
      this.appendComponentService.appendBackdropModal();
      this.appendComponentService.appendShoppingListModal(
        [
          {
            product: { code: this.product.productCode ?? this.product.code },
            desired: this.quantity,
            comment: this.reference,
          },
        ],
        ItemListEntity.BOM,
      );
    } else {
      this.appendComponentService.appendBackdropModal();
      this.appendComponentService.appendLoginModal();
    }
  }
}
