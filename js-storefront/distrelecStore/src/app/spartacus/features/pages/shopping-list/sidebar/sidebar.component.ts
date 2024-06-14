import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AbstractControl, Validators } from '@angular/forms';
import { faEdit, faFileAlt, faShoppingCart, faTrash, faWarning } from '@fortawesome/free-solid-svg-icons';
import { RoutingService, WindowRef } from '@spartacus/core';
import { Observable } from 'rxjs';
import { first, take } from 'rxjs/operators';
import { AppendComponentService } from 'src/app/spartacus/services/append-component.service';
import { ShoppingListService } from '../core';
import { UntypedFormGroup, UntypedFormBuilder, UntypedFormControl } from '@angular/forms';
import { ShoppingListComponent } from '../shopping-list.component';

@Component({
  selector: 'app-shopping-list-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
})
export class ShoppingListSidebarComponent {
  @Input() shoppingListAll$: Observable<any>;
  @Output() changeList = new EventEmitter<string>();

  listData$: Observable<any> = this.shoppingListService.getShoppingListsState();

  faShoppingCart = faShoppingCart;
  faFileAlt = faFileAlt;
  faEdit = faEdit;
  faTrash = faTrash;
  faWarning = faWarning;

  shoppingListForm: UntypedFormGroup = this.fb.group({
    name: new UntypedFormControl('', [Validators.minLength(1), Validators.required]),
  });

  constructor(
    private fb: UntypedFormBuilder,
    private shoppingListService: ShoppingListService,
    private appendComponentService: AppendComponentService,
    private winRef: WindowRef,
    private routingService: RoutingService,
    private shoppingListComponentService: ShoppingListComponent,
  ) {}

  get name(): AbstractControl {
    return this.shoppingListForm.get('name');
  }

  closeSiblingEdits(id: string): void {
    for (let i = 0; i < this.winRef.document.querySelectorAll('.edit-name-list').length; i++) {
      if (
        this.winRef.document.querySelectorAll<HTMLElement>('.edit-name-list')[i].dataset.id !==
        `js-edit-name-list-${id}`
      ) {
        this.winRef.document.querySelectorAll('.edit-name-list')[i].classList.add('d-none');
        this.winRef.document.querySelectorAll('.shopping-list-wrap')[i].classList.remove('d-none');
      }
    }
  }

  editList(id: string): void {
    this.closeSiblingEdits(id);
    this.winRef.document.querySelector<HTMLElement>(`[data-id="js-edit-name-list-${id}"]`)?.classList.remove('d-none');
    this.winRef.document.querySelector<HTMLElement>(`[data-id="js-shopping-list-wrap-${id}"]`)?.classList.add('d-none');
  }

  onChangeList(id: string): void {
    this.changeList.emit(id);
  }

  save(list, index): void {
    const listId = list.uniqueId;
    const input = this.winRef.document.querySelector<HTMLInputElement>(`[data-id="js-type-input-${listId}"]`);
    if (input.value !== '' || input.value !== null) {
      this.shoppingListService
        .updateShoppingList(listId, input.value)
        .pipe(take(1))
        .subscribe(() => {
          list.name = input.value;

          if (listId === this.shoppingListService.getCurrentShoppingListId()) {
            this.updateCurrentListNameState(input);
          }

          input.value = '';
          this.winRef.document.querySelectorAll('.edit-name-list')?.[index]?.classList.add('d-none');
          this.winRef.document.querySelectorAll('.shopping-list-wrap')?.[index]?.classList.remove('d-none');
        });
    }
  }

  showDeleteModal(uid: string): void {
    this.appendComponentService.appendBackdropModal();
    this.appendComponentService.appendShoppingListDeleteModal({ uid });
  }

  isActive(id: string): boolean {
    return this.winRef.location.href.indexOf(id) !== -1;
  }

  addNewList(): void {
    this.shoppingListForm.markAllAsTouched();
    if (this.shoppingListForm.valid) {
      this.shoppingListService
        .createShoppingList(this.shoppingListForm.get('name').value)
        .pipe(first())
        .subscribe((value) => {
          this.shoppingListForm.reset();
          this.routingService.go('/shopping/' + value?.uniqueId);
        });
    }
  }

  private updateCurrentListNameState(input: HTMLInputElement): void {
    this.shoppingListComponentService.shoppingList_.next({
      ...this.shoppingListComponentService.shoppingList_.value,
      name: input.value,
    });
  }
}
