import {
  ApplicationRef,
  ComponentFactoryResolver,
  ComponentRef,
  EmbeddedViewRef,
  Injectable,
  Injector,
  Type,
} from '@angular/core';
import {
  BackdropModuleComponent,
  BackdropOptions,
} from '@features/shared-modules/backdrop-module/backdrop-module.component';
import { LoginModalComponent } from '@features/shared-modules/popups/login-modal/login-modal.component';
import { LoadingLogoComponent } from '@features/shared-modules/loading-logo/loading-logo.component';
import { WindowRef } from '@spartacus/core';
import { BehaviorSubject } from 'rxjs';
import type { CommunicationPrefPopupComponent } from '@features/shared-modules/popups/communication-pref-popup/communication-pref-popup.component';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { ShoppingListPayloadItem } from '@model/shopping-list.model';
import { EnergyEfficiencyImageComponent } from '@features/shared-modules/popups/energy-efficiency-image/energy-efficiency-image.component';

/**
 * Before hard-coding new references in this service, try to implement your popup/modal using the
 * generic appendComponent() and destroyTemproryRef() methods, and passing the component as a parameter.
 *
 * Any new dependencies added here will be included in the main bundle, which is not ideal.
 */
@Injectable({
  providedIn: 'root',
})
export class AppendComponentService {
  temporaryRef: any;

  refBackdrop: ComponentRef<BackdropModuleComponent>[] = [];
  refLogin: ComponentRef<any>;
  refLoading: ComponentRef<any>;
  refShoppingList: ComponentRef<any>;
  refShoppingListDelete: ComponentRef<any>;
  refShippingTracking: ComponentRef<any>;
  refMovPopup: ComponentRef<any>;
  refSuccessPopup: ComponentRef<any>;
  refNewsLetterPopup: ComponentRef<any>;
  refContentPopup: ComponentRef<any>;
  refFormModal: ComponentRef<any>;
  refApprovalReject: ComponentRef<any>;
  refWarningPopup: ComponentRef<any>;
  refConfirmPopup: ComponentRef<any>;
  communicationsPopup: ComponentRef<CommunicationPrefPopupComponent>;
  energyEfficiencyRef: ComponentRef<EnergyEfficiencyImageComponent>;
  private _handleEvent = new BehaviorSubject<any>(0);
  _handleEvent$ = this._handleEvent.asObservable();

  constructor(
    private componentFactoryResolver: ComponentFactoryResolver,
    private appRef: ApplicationRef,
    private injector: Injector,
    private winRef: WindowRef,
  ) {}

  appendComponent<T>(component: Type<T>, optionalInput?: any, target?: HTMLElement) {
    // 1. Create a component reference from the component
    const componentRef = this.createComponent<T>(component, { ...optionalInput, target: target?.nodeName });

    // 2. Attach component to the appRef so that it's inside the ng component tree
    this.appRef.attachView(componentRef.hostView);

    if (this.winRef.isBrowser()) {
      // 3. Get DOM element from component
      const domElem = (componentRef.hostView as EmbeddedViewRef<any>).rootNodes[0] as HTMLElement;

      // 4. Append DOM element to the body
      if (target) {
        target.appendChild(domElem);
      } else {
        this.winRef.document.body.appendChild(domElem);
      }
    }

    return componentRef;
  }

  destroyComponent(componentRef: ComponentRef<any>) {
    if (componentRef) {
      this.appRef.detachView(componentRef.hostView);
      componentRef.destroy();
    }
  }

  destroyTemproryRef(): void {
    this.destroyComponent(this.temporaryRef);
  }

  appendBackdropModal(options?: BackdropOptions, target?: HTMLElement): void {
    this.refBackdrop.push(this.appendComponent(BackdropModuleComponent, options, target));
  }

  public appendEnergyEfficiencyModal(imageSize: string, url: string, isSearchPopup: boolean): void {
    this.energyEfficiencyRef = this.appendComponent(EnergyEfficiencyImageComponent, {
      eelImageSize: imageSize,
      energyEfficiencyLabelImageUrl: url,
      isSearchPopup,
    });
  }

  appendLoadingLogo() {
    this.refLoading = this.appendComponent(LoadingLogoComponent);
  }

  appendLoginModal(
    pageToRedirect?: string,
    shoppingListPayload?: { payloadItems: ShoppingListPayloadItem[]; itemListEntity: ItemListEntity },
  ) {
    if (this.refLogin) {
      return; // Only allow one login modal at a time
    }
    this.refLogin = this.appendComponent(LoginModalComponent, { pageToRedirect, shoppingListPayload });
  }

  appendShoppingListModal(payloadItems: ShoppingListPayloadItem[], itemListEntity: ItemListEntity) {
    if (this.refBackdrop.length === 0) {
      this.appendBackdropModal();
    }
    import('@features/shared-modules/shopping-list-modal/shopping-list-modal.component').then((m) => {
      this.refShoppingList = this.appendComponent(m.ShoppingListModalComponent, { payloadItems, itemListEntity });
    });
  }

  appendApprovalRejectModal(type, orderEntries?, productId?) {
    import('@features/pages/my-account/order-approval/modal-popup/approval-reject-modal.component').then((m) => {
      this.refApprovalReject = this.appendComponent(m.ApprovalRejectModalComponent, { type, orderEntries, productId });
      this.refApprovalReject.instance.SaveRejectionNote.subscribe(() => {
        this._handleEvent.next(this.refApprovalReject.instance.rejectNote);
        this._handleEvent.next(null);
      });
    });
  }

  appendShippingTrackingModal(deliveryDetails) {
    import('@features/shared-modules/shipping-tracking-modal/shipping-tracking-modal.component').then((m) => {
      this.refShippingTracking = this.appendComponent(m.ShippingTrackingModalComponent, { deliveryDetails });
    });
  }

  appendShoppingListDeleteModal(uid) {
    import('@features/shared-modules/shopping-list-modal/delete/delete-modal.component').then((m) => {
      this.refShoppingListDelete = this.appendComponent(m.ShoppingListDeleteModalComponent, uid);
    });
  }

  appendMOVpopup(movLimit, currentTotalValue, currencyIso) {
    import('@features/shared-modules/popups/mov-popup/mov-popup.component').then((m) => {
      this.refMovPopup = this.appendComponent(m.MovPopupComponent, {
        movLimit,
        currentTotalValue,
        currencyIso,
      });
    });
  }

  appendSuccessPopup<T>(title: string, subtitle: string, extraDetails?: T) {
    import('@features/shared-modules/popups/success-popup/success-popup.component').then((m) => {
      this.refSuccessPopup = this.appendComponent(m.SuccessPopupComponent, {
        title,
        subtitle,
        extraDetails,
      });
    });
  }

  appendWarningPopup(title: string, titleKey: string, subtitle: string, subtitleKey: string, type: string) {
    import('@features/shared-modules/popups/warning-popup/warning-popup.component').then((m) => {
      if (this.refWarningPopup) {
        this.removeWarningPopupComponent();
      }
      this.refWarningPopup = this.appendComponent(m.WarningPopupComponent, {
        title,
        titleKey,
        subtitle,
        subtitleKey,
        type,
      });
    });
  }

  removeWarningPopupComponent() {
    this.destroyComponent(this.refWarningPopup);
  }

  appendConfirmPopup(title: string, content: string, type: string, link: string) {
    import('@features/shared-modules/popups/confirm-popup/confirm-popup.component').then((m) => {
      this.refConfirmPopup = this.appendComponent(m.ConfirmPopupComponent, {
        title,
        content,
        type,
        link,
      });
      this.refConfirmPopup.instance.buttonText = 'OK';
      this.refConfirmPopup.instance.noCancelButton = true;
      this.refConfirmPopup.instance.isAppended = true;
    });
  }

  appendContentPopup(title: string, content: string) {
    import('@features/shared-modules/popups/content-popup/content-popup.component').then((m) => {
      this.refContentPopup = this.appendComponent(m.ContentPopupComponent, {
        title,
        content,
      });
    });
  }

  appendFormModal(data: any) {
    import('@features/shared-modules/popups/form-modal/form-modal.component').then((m) => {
      this.refFormModal = this.appendComponent(m.FormModalComponent, data);
    });
  }

  appendNewsLetterPopUp() {
    import('@features/shared-modules/popups/newsletter-popup/newsletter-popup.component').then((m) => {
      this.refNewsLetterPopup = this.appendComponent(m.NewsletterPopupComponent);
    });
  }

  appendCommunicationsPopUp() {
    import('@features/shared-modules/popups/communication-pref-popup/communication-pref-popup.component').then((m) => {
      if (this.communicationsPopup) {
        return;
      }
      this.communicationsPopup = this.appendComponent(m.CommunicationPrefPopupComponent);
    });
  }

  removeLoginComponentFromBody() {
    this.appRef.detachView(this.refLogin.hostView);
    this.refLogin.destroy();
    this.refLogin = undefined;
  }

  //cant do below directly in component as we destory it :)
  removeShoppingListComponentFromBody(status) {
    this.destroyComponent(this.refShoppingList);
  }

  //cant do below directly in component as we destory it :)
  removeApprovalRejectComponentFromBody(): void {
    this.appRef.detachView(this.refApprovalReject.hostView);
    this.refApprovalReject.destroy();
  }

  removeShippingTrackingComponentFromBody(): void {
    this.destroyComponent(this.refShippingTracking);
  }

  removeShoppingListDeleteComponent(): void {
    this.destroyComponent(this.refShoppingListDelete);
  }

  // Remove a specific backdrop module if target parent name provided
  removeBackdropComponentFromBody(target?: string): void {
    if (target) {
      const backdrop = this.refBackdrop.find((ref) => ref.instance.data?.target === target);
      this.destroyComponent(backdrop);
      this.refBackdrop.splice(this.refBackdrop.indexOf(backdrop), 1);
    } else {
      this.refBackdrop.forEach((ref) => this.destroyComponent(ref));
      this.refBackdrop = [];
    }
  }

  public removeEnergyEfficiencyModal(): void {
    if (this.energyEfficiencyRef) {
      this.destroyComponent(this.energyEfficiencyRef);
    }
  }

  removeLoadingLogoFromBody() {
    this.destroyComponent(this.refLoading);
  }

  removeMovPopupFromBody() {
    this.destroyComponent(this.refMovPopup);
  }

  removeSuccessPopupFromBody() {
    this.destroyComponent(this.refSuccessPopup);
  }

  removeWarningPopupFromBody() {
    this.destroyComponent(this.refWarningPopup);
  }

  removeContentPopupFromBody() {
    this.destroyComponent(this.refContentPopup);
  }

  removeFormModalFromBody() {
    this.destroyComponent(this.refFormModal);
  }

  removeNewsletterPopupComponent() {
    this.destroyComponent(this.refNewsLetterPopup);
  }

  removeCommunicationPopupComponent() {
    this.destroyComponent(this.communicationsPopup);
  }

  startScreenLoading() {
    this.appendBackdropModal();
    this.appendLoadingLogo();
  }

  stopScreenLoading() {
    this.removeBackdropComponentFromBody();
    this.removeLoadingLogoFromBody();
  }

  private createComponent<T extends { data?: any }>(component: Type<T>, componentData?: unknown): ComponentRef<T> {
    const componentRef = this.componentFactoryResolver.resolveComponentFactory(component).create(this.injector);

    if (componentData) {
      componentRef.instance.data = componentData;
    }
    this.temporaryRef = componentRef;
    return componentRef;
  }
}
