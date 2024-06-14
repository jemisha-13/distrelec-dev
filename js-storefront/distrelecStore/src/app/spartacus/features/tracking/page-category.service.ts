import { Injectable } from '@angular/core';
import { Page } from '@spartacus/core';
import { CategoryPageData } from '@model/category.model';

export enum PageCategory {
  BomTool = 'bom tool',
  Cart = 'cart page',
  Category = 'category plp',
  Checkout = 'checkout page',
  CheckoutDelivery = 'checkout delivery',
  CheckoutPayment = 'checkout payment',
  CheckoutAddress = 'checkout address',
  CheckoutReview = 'checkout review and pay',
  CheckoutDetail = 'checkout detail',
  Feedback = 'feedbackpage',
  Homepage = 'homepage',
  LoginRegistration = 'login-registration page',
  CheckoutRegistration = 'checkout-registration page',
  StandaloneRegistration = 'standalone-registration page',
  MyAccount = 'my account page',
  Manufacturer = 'manufacturer plp',
  NetPromoterScore = 'npspage',
  ProductFamily = 'product family',
  ProductDetails = 'pdp page',
  ResetPassword = 'resetpasswordpage',
  ResetToken = 'resettokenpage',
  Search = 'search results plp',
  Subcategory = 'subcategory plp',
  Transaction = 'transaction page',
}

@Injectable({
  providedIn: 'root',
})
export class PageCategoryService {
  getPageCategory(
    page: Page,
    routeParams?: { [key: string]: string },
    category?: CategoryPageData,
  ): PageCategory | string {
    if (!page) {
      return '';
    }

    const { pageId, type, template, title } = page;

    if (type === 'CategoryPage') {
      if (category?.sourceCategory.level > 1) {
        return PageCategory.Subcategory;
      }
      return PageCategory.Category;
    } else if (type === 'ProductPage') {
      return PageCategory.ProductDetails;
    } else if (type === 'ProductFamilyPage') {
      return PageCategory.ProductFamily;
    } else if (type === 'ContentPage' && (title === 'Welcome' || template === 'RegisterPageTemplate')) {
      return PageCategory.LoginRegistration;
    } else {
      switch (pageId) {
        case 'import-tool-upload':
        case 'import-tool-matching':
        case 'import-tool-review':
        case 'savedBomEntries':
          return PageCategory.BomTool;

        case 'cartPage':
          return PageCategory.Cart;

        case 'orderConfirmationPage':
          return routeParams?.orderCode ? PageCategory.Transaction : PageCategory.Checkout;

        case 'checkout':
          return PageCategory.Checkout;

        case 'checkoutAddressPage':
          return PageCategory.CheckoutAddress;

        case 'checkoutDetailPage':
          return PageCategory.CheckoutDetail;

        case 'checkoutPaymentPage':
          return PageCategory.CheckoutPayment;

        case 'checkoutReviewAndPayPage':
          return PageCategory.CheckoutReview;

        case 'checkoutDeliveryPage':
          return PageCategory.CheckoutDelivery;

        case 'searchFeedbackSent':
          return PageCategory.Feedback;

        case 'homepage2018':
        case 'homepage':
          return PageCategory.Homepage;

        case 'checkout-login':
        case 'login':
          return PageCategory.LoginRegistration;

        case 'checkout-register':
          return PageCategory.CheckoutRegistration;

        case 'registration':
        case 'register':
          return PageCategory.StandaloneRegistration;

        case 'DistManufacturerPage':
          return PageCategory.Manufacturer;

        case 'add-edit-address':
        case 'addresses':
        case 'company-information':
        case 'company/information':
        case 'company-user-management':
        case 'company-user-management-register-employee':
        case 'invoice-history':
        case 'login-data':
        case 'order-approval':
        case 'open-order-details':
        case 'order-details':
        case 'open-orders':
        case 'order-history':
        case 'preference-center':
        case 'payment-and-delivery-options':
        case 'quotation-history':
        case 'quotation-details':
        case 'return-items':
        case 'return-items-confirmation':
          return PageCategory.MyAccount;

        case 'netPromoterScorePage':
          return PageCategory.NetPromoterScore;

        case 'forgottenPassword':
          return PageCategory.ResetPassword;

        case 'resendAccountActivationToken':
          return PageCategory.ResetToken;

        case 'search':
        case 'searchEmpty':
          return PageCategory.Search;

        default:
          return type;
      }
    }
  }
}
