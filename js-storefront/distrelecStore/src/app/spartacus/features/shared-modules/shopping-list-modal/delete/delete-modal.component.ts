import { Component, Input } from '@angular/core';
import { faTimes } from '@fortawesome/free-solid-svg-icons';
import { ShoppingListService } from '@services/feature-services';
import { first } from 'rxjs/operators';
import { AppendComponentService } from 'src/app/spartacus/services/append-component.service';

@Component({
  selector: 'app-shopping-list-modal-delete',
  templateUrl: './delete-modal.component.html',
  styleUrls: ['./delete-modal.component.scss'],
})
export class ShoppingListDeleteModalComponent {
  @Input() data;

  faTimes = faTimes;

  constructor(
    private appendComponentService: AppendComponentService,
    private shoppingListService: ShoppingListService,
  ) {}

  delete(): void {
    this.shoppingListService
      .deleteShoppingList(this.data.uid)
      .pipe(first())
      .subscribe((res) => {
        if (res?.status) {
          this.shoppingListService.redirectToFirstShoppingList();
          this.closeModal();
        }
      });
  }

  closeModal(): void {
    this.appendComponentService.removeBackdropComponentFromBody();
    this.appendComponentService.removeShoppingListDeleteComponent();
  }
}
