import { mapToCanActivate, Routes } from '@angular/router';
import { AccountUserInformationComponent } from '@features/pages/my-account/account-user-information/account-user-information.component';
import { CmsPageGuard } from '@spartacus/storefront';
import { MyAccountAuthGuard } from '@features/guards/my-account-auth.guard';
import { MyAccountCompanyInformationComponent } from '@features/pages/my-account/my-account-company-information/my-account-company-information.component';
import { AccountCommunicationPreferencesCenterComponent } from '@features/pages/my-account/account-communication-preferences-center/account-communication-preferences-center.component';
import { AccountAddressesComponent } from '@features/pages/my-account/account-addresses/account-addresses.component';
import { AddEditAddressesComponent } from '@features/pages/my-account/account-addresses/add-edit-addresses/add-edit-addresses.component';
import { PaymentAndDeliveryOptionsComponent } from '@features/pages/my-account/payment-and-delivery-options/payment-and-delivery-options.component';
import { OrderHistoryComponent } from '@features/pages/my-account/order-history/order-history.component';
import { OrderDetailsComponent } from '@features/pages/my-account/order-history/order-details/order-details.component';
import { OrderReturnsComponent } from '@features/pages/my-account/order-history/order-returns/order-returns.component';
import { OrderReturnsConfirmationComponent } from '@features/pages/my-account/order-history/order-returns/returns-confirmation.component';
import { InvoiceManagerComponent } from '@features/pages/my-account/invoice-manager/invoice-manager.component';
import { QuoteHistoryComponent } from '@features/pages/my-account/quote-history/quote-history.component';
import { QuoteDetailsComponent } from '@features/pages/my-account/quote-history/quote-details/quote-details.component';
import { OrderApprovalComponent } from '@features/pages/my-account/order-approval/order-approval.component';
import { OrderApprovalDetailsComponent } from '@features/pages/my-account/order-approval/order-details/order-approval-details.component';
import { UserManagementComponent } from '@features/pages/my-account/user-management/user-management.component';
import { AddNewEmployeeComponent } from '@features/pages/my-account/user-management/add-new-employee/add-new-employee.component';
import { AccountInformationComponent } from '@features/pages/my-account/account-information.component';
import { bomToolMyAccountRoutes } from '@features/pages/bom-tool/bom-tool-my-account.routes';
import { orderReturnsGuard } from '@features/guards/orders.guard';
import { addNewEmployeeGuard } from '@features/guards/add-new-employee.guard';

export const myAccountRoutes: Routes = [
  {
    path: 'my-account-information',
    component: AccountUserInformationComponent,
    canActivate: mapToCanActivate([CmsPageGuard, MyAccountAuthGuard]),
    pathMatch: 'full',
    data: {
      parent: [
        {
          url: 'my-account/company/information',
          label: 'form.account_details',
        },
        {
          url: null,
          label: 'form.my_account_information',
        },
      ],

      key: 'text.preferences.account.info',
      breadcrumb: 'Account information',
      isChild: true,
      hideLastLabel: true,
      pageLabel: '/my-account/my-account-information',
    },
  },
  {
    path: 'company/information',
    component: MyAccountCompanyInformationComponent,
    canActivate: mapToCanActivate([CmsPageGuard, MyAccountAuthGuard]),
    pathMatch: 'full',
    data: {
      parent: [
        {
          url: 'my-account/company/information',
          label: 'form.account_details',
        },
        {
          url: null,
          label: 'form.company_information',
        },
      ],
      key: 'text.account.company.information',
      breadcrumb: 'Company Information',
      isChild: true,
      hideLastLabel: true,
      pageLabel: '/my-account/company/information',
    },
  },
  {
    path: 'preference-center',
    component: AccountCommunicationPreferencesCenterComponent,
    canActivate: mapToCanActivate([CmsPageGuard, MyAccountAuthGuard]),
    pathMatch: 'full',
    data: {
      parent: [
        {
          url: 'my-account/company/information',
          label: 'form.account_details',
        },
        {
          url: null,
          label: 'form.com_preferences',
        },
      ],

      key: 'form.com_preferences',
      breadcrumb: 'form.com_preferences',
      isChild: true,
      hideLastLabel: true,
      pageLabel: '/my-account/preference-center',
    },
  },
  {
    path: 'addresses',
    component: AccountAddressesComponent,
    canActivate: mapToCanActivate([CmsPageGuard, MyAccountAuthGuard]),
    pathMatch: 'full',
    data: {
      parent: [
        {
          url: 'my-account/company/information',
          label: 'form.account_details',
        },
        {
          url: null,
          label: 'form.address',
        },
      ],

      key: 'common.text.account.addresses',
      breadcrumb: 'Adresses',
      isChild: true,
      hideLastLabel: true,
      pageLabel: '/my-account/addresses',
    },
  },
  {
    path: 'add-address',
    component: AddEditAddressesComponent,
    canActivate: mapToCanActivate([CmsPageGuard, MyAccountAuthGuard]),
    pathMatch: 'full',
    data: {
      parent: [
        {
          url: 'my-account/company/information',
          label: 'form.account_details',
        },
        {
          url: 'my-account/addresses',
          label: 'form.address',
        },
        {
          url: null,
          label: 'form.add_edit_address',
        },
      ],

      key: 'common.text.account.addresses.addEditAddress',
      breadcrumb: 'Address',
      isChild: true,
      hideLastLabel: true,
      pageLabel: '/my-account/add-address',
    },
  },
  {
    path: 'edit-address/:addressType/:addressId',
    component: AddEditAddressesComponent,
    canActivate: mapToCanActivate([CmsPageGuard, MyAccountAuthGuard]),
    pathMatch: 'full',
    data: {
      parent: [
        {
          url: 'my-account/company/information',
          label: 'form.account_details',
        },
        {
          url: 'my-account/addresses',
          label: 'form.address',
        },
        {
          url: null,
          label: 'form.add_edit_address',
        },
      ],

      breadcrumb: 'Order',
      isChild: true,
      hideLastLabel: true,
      pageLabel: '/my-account/edit-address',
    },
  },
  {
    path: 'payment-and-delivery-options',
    component: PaymentAndDeliveryOptionsComponent,
    canActivate: mapToCanActivate([CmsPageGuard, MyAccountAuthGuard]),
    pathMatch: 'full',
    data: {
      parent: [
        {
          url: 'my-account/company/information',
          label: 'form.payment_and_delivery_label',
        },
        {
          url: null,
          label: 'form.payment_options',
        },
      ],

      breadcrumb: 'Payment Options',
      isChild: true,
      hideLastLabel: true,
      pageLabel: '/my-account/payment-and-delivery-options',
    },
  },
  {
    path: 'order-history',
    component: OrderHistoryComponent,
    canActivate: mapToCanActivate([CmsPageGuard, MyAccountAuthGuard]),
    pathMatch: 'full',
    data: {
      parent: [
        {
          url: 'my-account/company/information',
          label: 'text.account.orderManager',
        },
      ],

      key: 'form.order_history',
      breadcrumb: 'form.order_history',
      isChild: true,
      pageLabel: '/my-account/order-history',
    },
  },
  {
    path: 'order-history/order-details/:orderCode',
    component: OrderDetailsComponent,
    canActivate: mapToCanActivate([CmsPageGuard, MyAccountAuthGuard]),
    pathMatch: 'full',
    data: {
      parent: [
        {
          url: 'my-account/company/information',
          label: 'text.account.orderManager',
        },
        {
          url: 'my-account/order-history',
          label: 'form.order_history',
        },
      ],

      breadcrumb: 'Order',
      isChild: true,
      selectedParam: 'orderCode',
      pageLabel: '/my-account/order-history/order-details',
    },
  },
  {
    path: 'order-history/order-return/:orderCode/return-items',
    component: OrderReturnsComponent,
    canActivate: [...mapToCanActivate([CmsPageGuard, MyAccountAuthGuard]), orderReturnsGuard],
    pathMatch: 'full',
    data: {
      parent: [
        {
          url: 'my-account/company/information',
          label: 'text.account.orderManager',
        },
        {
          url: 'my-account/order-history',
          label: 'form.order_history',
        },
        {
          url: 'my-account/order-history/order-details/:orderCode',
          prefix: 'form.order_label',
          type: 'param',
          param: 'orderCode',
        },
      ],

      key: 'form.return_items',
      breadcrumb: 'Return Items',
      isChild: true,
      pageLabel: '/return-items',
    },
  },
  {
    path: 'order-history/order-return/:orderCode/confirmation',
    component: OrderReturnsConfirmationComponent,
    canActivate: mapToCanActivate([CmsPageGuard, MyAccountAuthGuard]),
    pathMatch: 'full',
    data: {
      parent: [
        {
          url: 'my-account/company/information',
          label: 'text.account.orderManager',
        },
        {
          url: 'my-account/order-history',
          label: 'text.account.orderHistory',
        },
        {
          url: 'my-account/order-history/order-details/:orderCode',
          prefix: 'form.order_label',
          type: 'param',
          param: 'orderCode',
        },
      ],

      key: 'form.return_items',
      breadcrumb: 'Return Items',
      isChild: true,
      pageLabel: '/return-items',
    },
  },
  {
    path: 'invoice-history',
    component: InvoiceManagerComponent,
    canActivate: mapToCanActivate([CmsPageGuard, MyAccountAuthGuard]),
    pathMatch: 'full',
    data: {
      parent: [
        {
          url: 'my-account/company/information',
          label: 'text.account.orderManager',
        },
      ],

      key: 'text.account.invoiceHistory',
      breadcrumb: 'Invoice Manager',
      isChild: true,
      pageLabel: '/my-account/invoice-history',
    },
  },
  {
    path: 'quote-history',
    component: QuoteHistoryComponent,
    canActivate: mapToCanActivate([CmsPageGuard, MyAccountAuthGuard]),
    pathMatch: 'full',
    data: {
      parent: [
        {
          url: 'my-account/company/information',
          label: 'text.account.orderManager',
        },
      ],

      key: 'form.my_quotes',
      breadcrumb: 'My Quotes',
      isChild: true,
      pageLabel: '/my-account/quote-history',
    },
  },
  {
    path: 'quote-history/quote-details/:quotationId',
    component: QuoteDetailsComponent,
    canActivate: mapToCanActivate([CmsPageGuard, MyAccountAuthGuard]),
    pathMatch: 'full',
    data: {
      parent: [
        {
          url: 'my-account/company/information',
          label: 'text.account.orderManager',
        },
        {
          url: null,
          label: 'form.my_quotes',
        },
      ],

      key: 'form.my_quotes',
      breadcrumb: 'My Quotes',
      isChild: true,
      hideLastLabel: true,
      pageLabel: '/my-account/quote-history/quote-details',
    },
  },
  {
    path: 'order-approval',
    component: OrderApprovalComponent,
    canActivate: mapToCanActivate([CmsPageGuard, MyAccountAuthGuard]),
    pathMatch: 'full',
    data: {
      parent: [
        {
          url: 'my-account/company/information',
          label: 'text.account.orderManager',
        },
        {
          url: null,
          label: 'text.account.orderApprovalDashboard',
        },
      ],

      breadcrumb: 'Order Approval',
      isChild: true,
      hideLastLabel: true,
      pageLabel: '/my-account/order-approval',
    },
  },
  {
    path: 'order-approval-requests',
    component: OrderApprovalComponent,
    canActivate: mapToCanActivate([CmsPageGuard, MyAccountAuthGuard]),
    pathMatch: 'full',
    data: {
      parent: [
        {
          url: 'my-account/company/information',
          label: 'text.account.orderManager',
        },
        {
          url: null,
          label: 'text.account.orderApprovalDashboard',
        },
      ],

      key: 'form.order_approval',
      breadcrumb: 'Order Approval',
      isChild: true,
      hideLastLabel: true,
      pageLabel: '/my-account/order-approval-requests',
    },
  },
  {
    path: 'order-approval/order-details/:orderCode/workflow/:workFlowCode',
    component: OrderApprovalDetailsComponent,
    canActivate: mapToCanActivate([CmsPageGuard, MyAccountAuthGuard]),
    pathMatch: 'full',
    data: {
      parent: [
        {
          url: 'my-account/company/information',
          label: 'text.account.orderManager',
        },
        {
          url: 'my-account/order-approval',
          label: 'text.account.orderApprovalDashboard',
        },
      ],

      breadcrumb: 'Order',
      isChild: true,
      selectedParam: 'orderCode',
      pageLabel: '/my-account/order-approval',
    },
  },
  {
    path: 'order-approval-requests/order-details/:orderCode/workflow/:workFlowCode',
    component: OrderApprovalDetailsComponent,
    canActivate: mapToCanActivate([CmsPageGuard, MyAccountAuthGuard]),
    pathMatch: 'full',
    data: {
      parent: [
        {
          url: 'my-account/company/information',
          label: 'text.account.orderManager',
        },
        {
          url: 'my-account/order-approval-requests',
          label: 'text.account.orderApprovalDashboard',
        },
      ],

      breadcrumb: 'Order',
      isChild: true,
      selectedParam: 'orderCode',
      pageLabel: '/my-account/order-approval-requests',
    },
  },
  {
    path: 'quote-history',
    component: QuoteHistoryComponent,
    canActivate: mapToCanActivate([CmsPageGuard, MyAccountAuthGuard]),
    pathMatch: 'full',
    data: {
      pageLabel: '/my-account/quote-history',
    },
  },
  {
    path: 'company/user-management',
    component: UserManagementComponent,
    canActivate: mapToCanActivate([CmsPageGuard, MyAccountAuthGuard]),
    pathMatch: 'full',
    data: {
      parent: [
        {
          url: 'my-account/company/information',
          label: 'form.account_details',
        },
      ],

      key: 'text.account.company.userManagement',
      breadcrumb: 'User Management',
      isChild: true,
      pageLabel: '/my-account/company/user-management',
    },
  },
  {
    path: 'company/edit/employee/:email',
    component: AddNewEmployeeComponent,
    canActivate: mapToCanActivate([CmsPageGuard, MyAccountAuthGuard]),
    pathMatch: 'full',
    data: {
      parent: [
        {
          url: 'my-account/company/information',
          label: 'form.account_details',
        },
        {
          url: null,
          label: 'text.account.company.userManagement',
        },
      ],

      breadcrumb: 'User Management',
      isChild: true,
      hideLastLabel: true,
      pageLabel: '/my-account/company/edit/employee',
    },
  },
  {
    path: 'company/create/newemployee',
    component: AddNewEmployeeComponent,
    canActivate: [...mapToCanActivate([CmsPageGuard, MyAccountAuthGuard]), addNewEmployeeGuard],
    pathMatch: 'full',
    data: {
      parent: [
        {
          url: 'my-account/company/information',
          label: 'form.account_details',
        },
      ],

      key: 'text.account.company.userManagement',
      breadcrumb: 'User Management',
      isChild: true,
      pageLabel: '/my-account/company/create/newemployee',
    },
  },
  {
    path: 'not-authorized',
    component: AccountInformationComponent,
    canActivate: [CmsPageGuard],
    pathMatch: 'full',
    data: {
      pageLabel: '/my-account/not-authorized',
    },
  },
  ...bomToolMyAccountRoutes,
];
