import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AccountInformationComponent } from './account-information.component';
import { CmsConfig, ConfigModule, I18nModule } from '@spartacus/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BreadcrumbWrapperModule } from '@features/shared-modules/breadcrumb/breadcrumb-wrapper.module';
import { NgbdDatepickerPopupModule } from '@features/shared-modules/datepicker/datepicker-popup.module';
import { AccountUserInformationComponent } from './account-user-information/account-user-information.component';
import { AccountUserChangeEmailComponent } from './account-user-change-email/account-user-change-email.component';
import { AccountUserChangePasswordComponent } from './account-user-change-password/account-user-change-password.component';
import { MyAccountCompanyInformationComponent } from './my-account-company-information/my-account-company-information.component';
import { AccountCommunicationPreferencesCenterComponent } from './account-communication-preferences-center/account-communication-preferences-center.component';
import { AccountAddressesComponent } from './account-addresses/account-addresses.component';
import { NgSelectModule } from '@ng-select/ng-select';
import { MyAccountSidebarMenuComponent } from './my-account-sidebar-menu/my-account-sidebar-menu.component';
import { PaymentAndDeliveryOptionsComponent } from './payment-and-delivery-options/payment-and-delivery-options.component';
import { OrderHistoryComponent } from './order-history/order-history.component';
import { InvoiceManagerComponent } from './invoice-manager/invoice-manager.component';
import { UserManagementComponent } from './user-management/user-management.component';
import { AddNewEmployeeComponent } from './user-management/add-new-employee/add-new-employee.component';
import { OrderApprovalComponent } from './order-approval/order-approval.component';
import { QuoteHistoryComponent } from './quote-history/quote-history.component';
import { QuoteDetailsComponent } from './quote-history/quote-details/quote-details.component';
import { RouterModule } from '@angular/router';
import { PageComponentModule } from '@spartacus/storefront';
import { AddEditAddressesComponent } from './account-addresses/add-edit-addresses/add-edit-addresses.component';
import { OrderDetailsComponent } from './order-history/order-details/order-details.component';
import { OrderReturnsComponent } from './order-history/order-returns/order-returns.component';
import { NgbPaginationModule, NgbTooltipModule, NgbCollapseModule } from '@ng-bootstrap/ng-bootstrap';
import { OrderApprovalDetailsComponent } from './order-approval/order-details/order-approval-details.component';
import { ApprovalRejectModalComponent } from './order-approval/modal-popup/approval-reject-modal.component';
import { ActionMessageModule } from '@features/shared-modules/action-message/action-message.module';
import { OrderReturnsConfirmationComponent } from './order-history/order-returns/returns-confirmation.component';
import { ArticleNumberPipeModule } from '@pipes/article-number-pipe.module';
import { SharedModule } from '@features/shared-modules/shared.module';
import { myAccountRoutes } from '@features/pages/my-account/my-account.routes';
import { BomToolSavedEntriesModule } from '@features/pages/bom-tool/bom-tool-saved-entries/bom-tool-saved-entries.module';
import { ConfirmPopupComponentModule } from '@features/shared-modules/popups/confirm-popup/confirm-popup.module';
import { PricePipeModule } from '@features/shared-modules/pipes/price-pipe.module';
import { AtcButtonModule } from '@features/shared-modules/components/atc-button/atc-button.module';

@NgModule({
  declarations: [
    AccountInformationComponent,
    AccountUserInformationComponent,
    AccountUserChangeEmailComponent,
    AccountUserChangePasswordComponent,
    MyAccountSidebarMenuComponent,
    MyAccountCompanyInformationComponent,
    AccountCommunicationPreferencesCenterComponent,
    AccountAddressesComponent,
    PaymentAndDeliveryOptionsComponent,
    AddEditAddressesComponent,
    OrderHistoryComponent,
    OrderDetailsComponent,
    InvoiceManagerComponent,
    QuoteHistoryComponent,
    AddNewEmployeeComponent,
    UserManagementComponent,
    OrderApprovalComponent,
    QuoteDetailsComponent,
    OrderReturnsComponent,
    OrderApprovalDetailsComponent,
    ApprovalRejectModalComponent,
    OrderReturnsConfirmationComponent,
  ],
  imports: [
    CommonModule,
    ActionMessageModule,
    ConfigModule.forRoot({
      cmsComponents: {
        AccountInformationComponent: {
          component: AccountInformationComponent,
        },
      },
    } as CmsConfig),
    FontAwesomeModule,
    ReactiveFormsModule.withConfig({ warnOnNgModelWithFormControl: 'never' }),
    I18nModule,
    BreadcrumbWrapperModule,
    NgSelectModule,
    FormsModule,
    RouterModule,
    ArticleNumberPipeModule,
    SharedModule,
    NgbdDatepickerPopupModule,
    PageComponentModule,
    RouterModule.forChild(myAccountRoutes),
    BomToolSavedEntriesModule,
    PricePipeModule,
    ConfirmPopupComponentModule,
    NgbPaginationModule,
    NgbCollapseModule,
    NgbTooltipModule,
    AtcButtonModule,
  ],
  exports: [
    AccountUserInformationComponent,
    AccountUserChangeEmailComponent,
    AccountUserChangePasswordComponent,
    MyAccountSidebarMenuComponent,
    MyAccountCompanyInformationComponent,
    AccountCommunicationPreferencesCenterComponent,
    AccountAddressesComponent,
    PaymentAndDeliveryOptionsComponent,
    AddEditAddressesComponent,
    OrderHistoryComponent,
    OrderDetailsComponent,
    InvoiceManagerComponent,
    QuoteHistoryComponent,
    AddNewEmployeeComponent,
    UserManagementComponent,
    OrderApprovalComponent,
    QuoteDetailsComponent,
    OrderReturnsComponent,
    OrderReturnsConfirmationComponent,
    NgbPaginationModule,
  ],
})
export class AccountInformationModule {}
