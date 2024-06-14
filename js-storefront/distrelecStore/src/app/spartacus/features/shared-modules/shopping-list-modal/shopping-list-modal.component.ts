import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { first, map, switchMap, tap } from 'rxjs/operators';
import { faExclamationTriangle, faFileAlt, faTimes } from '@fortawesome/free-solid-svg-icons';

import { AppendComponentService } from '@services/append-component.service';
import { Observable, Subscription } from 'rxjs';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { ShoppingList, ShoppingListEntry, ShoppingListPayloadItem } from '@model/shopping-list.model';
import { ProductEnhancementsService } from '@features/pages/product/core/services/product-enhancements.service';
import { ShoppingListService } from '@services/feature-services';

@Component({
  selector: 'app-shopping-list-modal',
  templateUrl: './shopping-list-modal.component.html',
  styleUrls: ['./shopping-list-modal.component.scss'],
})
export class ShoppingListModalComponent implements OnInit, OnDestroy {
  @Input() data: {
    payloadItems: ShoppingListPayloadItem[];
    itemListEntity: ItemListEntity;
  };

  faTimes = faTimes;
  faFileAlt = faFileAlt;
  faExclamationTriange = faExclamationTriangle;

  hasError = false;
  checked = false;

  shoppingListsAll$: Observable<ShoppingList[]> = this.shoppingListService
    .getShoppingListsState()
    .pipe(map((lists) => lists.list));

  shoppingListForm: FormGroup = this.fb.group({
    name: new FormControl(),
  });

  subscriptions = new Subscription();

  isInProgress: boolean;

  constructor(
    private fb: FormBuilder,
    private appendComponentService: AppendComponentService,
    private shoppingListService: ShoppingListService,
    private productEnhancementService: ProductEnhancementsService,
  ) {}

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  ngOnInit(): void {
    this.subscriptions.add(
      this.shoppingListsAll$
        .pipe(
          tap((lists: ShoppingList[]) => {
            this.checkIfProductIsInShoppingList(lists);
          }),
        )
        .subscribe(),
    );
  }

  checkIfProductIsInShoppingList(lists: ShoppingList[]): void {
    lists.forEach((list: ShoppingList) => {
      list.entries.map((entry: ShoppingListEntry) => {
        if (entry.product.code === this.data.payloadItems[0].product.code) {
          this.shoppingListForm.addControl(`checkbox-modal-${list.uniqueId}`, this.fb.control(list.uniqueId));

          this.shoppingListForm.get(`checkbox-modal-${list.uniqueId}`).setValue(list.uniqueId);
          this.checked = true;
          return entry;
        }
      });
    });
  }

  closeModal(status): void {
    this.appendComponentService.removeBackdropComponentFromBody();
    this.appendComponentService.removeShoppingListComponentFromBody(status);
  }

  onCheck(listId: string, event): void {
    this.isInProgress = true;

    if (event.target.checked) {
      this.saveProductToShoppingList(listId);
    } else {
      this.removeProductFromShoppingList(listId);
    }
  }

  saveProductToShoppingList(listId: string): void {
    this.subscriptions.add(
      this.shoppingListService
        .addToShoppingList(listId, this.data.payloadItems, this.data.itemListEntity)
        .subscribe(() => {
          this.shoppingListForm.addControl(`checkbox-modal-${listId}`, this.fb.control(listId));
          this.checked = true;
          this.isInProgress = false;
        }),
    );
  }

  removeProductFromShoppingList(listId: string): void {
    this.subscriptions.add(
      this.shoppingListService
        .removeProductFromList(listId, this.data.payloadItems[0].product.code)
        .subscribe((data) => {
          this.shoppingListForm.removeControl(`checkbox-modal-${listId}`);
          this.checked = false;
          this.isInProgress = false;
        }),
    );
  }

  saveProductToNewList(): void {
    this.shoppingListService
      .createShoppingList(this.shoppingListForm.get('name').value)
      .pipe(
        first(),
        switchMap((res) =>
          this.shoppingListService.addToShoppingList(res?.uniqueId, this.data.payloadItems, this.data.itemListEntity),
        ),
        tap(() => {
          this.productEnhancementService.load(this.data.payloadItems.map((entry) => entry.product.code));
          this.showHidePopOver(true);
        }),
      )
      .subscribe();
  }

  showHidePopOver(status: boolean): void {
    this.closeModal(status);
  }

  save(): void {
    this.handleSaveRequest();
  }

  handleSaveRequest(): void {
    if (this.shoppingListForm.get('name').value !== '' && this.shoppingListForm.get('name').value !== null) {
      this.saveProductToNewList();
    } else {
      this.productEnhancementService.load(this.data.payloadItems.map((entry) => entry.product.code));
      this.showHidePopOver(true);
    }
  }
}
