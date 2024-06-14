/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.namics.distrelec.b2b.storefront.controllers;

import com.namics.distrelec.b2b.core.model.cms2.components.*;

import de.hybris.platform.acceleratorcms.model.components.*;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;

/**
 * Class with constants for controllers.
 */
public interface ControllerConstants {

    /**
     * Class with action name constants
     */
    interface Actions {
        interface Cms {
            String _Prefix = "/view/";

            String _Suffix = "Controller";

            /**
             * Default CMS component controller
             */
            String DefaultCMSComponent = _Prefix + "DefaultCMSComponentController";

            /**
             * CMS components that have specific handlers
             */
            String PurchasedProductReferencesComponent = _Prefix + PurchasedProductReferencesComponentModel._TYPECODE + _Suffix;

            String ProductReferencesComponent = _Prefix + ProductReferencesComponentModel._TYPECODE + _Suffix;

            String ProductCarouselComponent = _Prefix + ProductCarouselComponentModel._TYPECODE + _Suffix;

            String MiniCartComponent = _Prefix + MiniCartComponentModel._TYPECODE + _Suffix;

            String ProductFeatureComponent = _Prefix + ProductFeatureComponentModel._TYPECODE + _Suffix;

            String CategoryFeatureComponent = _Prefix + CategoryFeatureComponentModel._TYPECODE + _Suffix;

            String NavigationBarComponent = _Prefix + NavigationBarComponentModel._TYPECODE + _Suffix;

            String CMSLinkComponent = _Prefix + CMSLinkComponentModel._TYPECODE + _Suffix;

            String DistProductCarouselComponent = _Prefix + DistProductCarouselComponentModel._TYPECODE + _Suffix;

            String DistCategoryCarouselComponent = _Prefix + DistCategoryCarouselComponentModel._TYPECODE + _Suffix;

            String DistManufacturerCarouselComponent = _Prefix + DistManufacturerCarouselComponentModel._TYPECODE + _Suffix;

            String DistHeroRotatingTeaserComponent = _Prefix + DistHeroRotatingTeaserComponentModel._TYPECODE + _Suffix;

            String DistExtHeroRotatingTeaserComponent = _Prefix + DistExtHeroRotatingTeaserComponentModel._TYPECODE + _Suffix;

            String DistProductReferencesCarouselComponent = _Prefix + DistProductReferencesCarouselComponentModel._TYPECODE + _Suffix;

            String DistProductCookieCarouselComponent = _Prefix + DistProductCookieCarouselComponentModel._TYPECODE + _Suffix;

            String DistProductFFCarouselComponent = _Prefix + DistProductFFCarouselComponentModel._TYPECODE + _Suffix;

            String DistCarpetComponent = _Prefix + DistCarpetComponentModel._TYPECODE + _Suffix;

            String DistProductBoxComponent = _Prefix + DistProductBoxComponentModel._TYPECODE + _Suffix;

            String DistrelecCategoryManagerCardComponent = _Prefix + DistrelecCategoryManagerCardComponentModel._TYPECODE + _Suffix;

            String DistExtCarpetComponent = _Prefix + DistExtCarpetComponentModel._TYPECODE + _Suffix;

            String DistWarningComponent = _Prefix + DistWarningComponentModel._TYPECODE + _Suffix;

            String DistProductFFCampaignCarouselComponent = _Prefix + DistProductFFCampaignCarouselComponentModel._TYPECODE + _Suffix;

            String DistCategoryThumbsComponent = _Prefix + DistCategoryThumbsComponentModel._TYPECODE + _Suffix;

            String DistIframeComponent = _Prefix + DistIframeComponentModel._TYPECODE + _Suffix;

            String DistFooterComponent = _Prefix + DistFooterComponentModel._TYPECODE + _Suffix;

            String DistrelecCategoryGridComponent = _Prefix + DistrelecCategoryGridComponentModel._TYPECODE + _Suffix;

            String DistrelecManufacturerLinecardComponent = _Prefix + DistrelecManufacturerLinecardComponentModel._TYPECODE + _Suffix;

            String DistMainNavigationComponent = _Prefix + DistMainNavigationComponentModel._TYPECODE + _Suffix;

            String DistFeaturedProductsComponent = _Prefix + DistFeaturedProductsComponentModel._TYPECODE + _Suffix;

            String DistFallbackComponent = _Prefix + DistFallbackComponentModel._TYPECODE + _Suffix;

            String DistRMAGuestReturnsFormComponent = _Prefix + DistRMAGuestReturnsFormComponentModel._TYPECODE + _Suffix;
            
            String DistProductCardComponent = _Prefix + DistProductCardComponentModel._TYPECODE + _Suffix;
        }
    }

    /**
     * Class with view name constants
     */
    interface Views {
        interface Cms {
            String ComponentPrefix = "cms/";
        }

        interface Pages {
            interface Account {
                // Login & Register Pages
                String AccountLoginPage = "pages/account/accountLoginPage";

                String AccountRegisterPage = "pages/account/accountRegisterPage";

                String AccountRegisterPage_b2c = "pages/account/accountRegisterB2CPage";

                String AccountRegisterSuccessPage = "pages/account/accountRegisterSuccessPage";

                String AccountRegisterExistingSuccessPage = "pages/account/accountRegisterExistingSuccessPage";

                String AccountRegisterPage_b2b = "pages/account/accountRegisterB2BPage";

                String AccountRegisterPage_existing = "pages/account/accountRegisterExistingPage";

                String AccountRegisterPage_Consolidated = "pages/account/accountRegisterConsolidatedPage";

                // My-Account Pages
                String AccountLoginDataPage = "pages/account/accountLoginDataPage";

                String AccountPrefrenceCenterPage = "pages/account/accountPrefrenceCenter";

                String AccountAddressesPage = "pages/account/accountAddressesPage";

                String AccountPaymentAndDeliveryOptionsPage = "pages/account/accountPaymentAndDeliveryOptionsPage";

                String AccountOrderHistoryPage = "pages/account/accountOrderHistoryPage";

                String AccountOrderHistoryNextPage = "pages/account/accountOrderHistoryNextPage";

                String AccountQuotationHistoryPage = "pages/account/accountQuotationHistoryPage";

                String AccountQuotationDetails = "pages/account/accountQuotationDetails";

                String AccountSearchQuotationHistoryPage = "pages/account/account-list-quotation-history";

                String AccountQuotationHistoryNextPage = "pages/account/accountQuotationHistoryNextPage";

                String AccountOrderDetailsPage = "pages/account/accountOrderDetailsPage";

                String AccountReturnItemsPage = "pages/account/accountReturnItemsPage";

                String AccountReturnItemsConfirmationPage = "pages/account/accountReturnItemsConfirmationPage";

                String AccountInvoiceHistoryPage = "pages/account/accountInvoiceHistoryPage";

                String AccountInvoiceHistoryNextPage = "pages/account/accountInvoiceHistoryNextPage";

                String AccountOpenOrderHistoryPage = "pages/account/accountOpenOrderHistoryPage";

                String AccountOpenOrderHistoryNextPage = "pages/account/accountOpenOrderHistoryNextPage";

                String AccountOpenOrderDetailsPage = "pages/account/accountOpenOrderDetailsPage";

                String AccountAddEditAddressPage = "pages/account/accountAddEditAddressPage";

                String AccountOrderApprovalPage = "pages/account/accountOrderApprovalPage";

                String AccountOrderApprovalDetailsPage = "pages/account/accountOrderApprovalDetailsPage";

                String AccountCancelActionConfirmationPage = "pages/account/accountCancelActionConfirmationPage";

                String ResendAccountActivationToken = "pages/account/resendAccountActivationToken";

                String SetInitialPasswordPage = "pages/account/setInitialPasswordPage";

                String AccountReturnRequestPage = "pages/account/accountReturnRequestPage";

                String AccountProductBoxJson = "pages/account/productBoxJson";

                String SavedBomToolPage = "pages/account/savedBomEntries";

                // Return and Claim Page
                String ReturnAndClaimPage = "pages/account/returnAndClaimPage";
            }

            interface Quotation {
                String RequestQuotationPage = "pages/quotation/requestQuotation";
            }

            interface Company {
                String CompanyInformationPage = "pages/account/company/companyInformationPage";

                String UserManagementPage = "pages/account/company/userManagementPage";

                String RegisterNewEmployee = "pages/account/company/companyRegisterB2BPage";

                String EditEmployeePage = "pages/account/company/companyEditEmployeeB2BPage";
            }

            interface MyCompany {
                String MyCompanyLoginPage = "pages/company/myCompanyLoginPage";

                String MyCompanyHomePage = "pages/company/myCompanyHomePage";

                String MyCompanyManageUnitsPage = "pages/company/myCompanyManageUnitsPage";

                String MyCompanyManageUnitEditPage = "pages/company/myCompanyManageUnitEditPage";

                String MyCompanyManageUnitDetailsPage = "pages/company/myCompanyManageUnitDetailsPage";

                String MyCompanyManageUnitCreatePage = "pages/company/myCompanyManageUnitCreatePage";

                String MyCompanyManageBudgetsPage = "pages/company/myCompanyManageBudgetsPage";

                String MyCompanyManageBudgetsViewPage = "pages/company/myCompanyManageBudgetsViewPage";

                String MyCompanyManageBudgetsEditPage = "pages/company/myCompanyManageBudgetsEditPage";

                String MyCompanyManageBudgetsAddPage = "pages/company/myCompanyManageBudgetsAddPage";

                String MyCompanyManageCostCentersPage = "pages/company/myCompanyManageCostCentersPage";

                String MyCompanyCostCenterViewPage = "pages/company/myCompanyCostCenterViewPage";

                String MyCompanyCostCenterEditPage = "pages/company/myCompanyCostCenterEditPage";

                String MyCompanyAddCostCenterPage = "pages/company/myCompanyAddCostCenterPage";

                String MyCompanyManagePermissionsPage = "pages/company/myCompanyManagePermissionsPage";

                String MyCompanyManageUnitUserListPage = "pages/company/myCompanyManageUnitUserListPage";

                String MyCompanyManageUnitApproverListPage = "pages/company/myCompanyManageUnitApproversListPage";

                String MyCompanyManageUserDetailPage = "pages/company/myCompanyManageUserDetailPage";

                String MyCompanyManageUserAddEditFormPage = "pages/company/myCompanyManageUserAddEditFormPage";

                String MyCompanyManageUsersPage = "pages/company/myCompanyManageUsersPage";

                String MyCompanyManageUserDisbaleConfirmPage = "pages/company/myCompanyManageUserDisableConfirmPage";

                String MyCompanyManageUnitDisablePage = "pages/company/myCompanyManageUnitDisablePage";

                String MyCompanySelectBudgetPage = "pages/company/myCompanySelectBudgetsPage";

                String MyCompanyCostCenterDisableConfirm = "pages/company/myCompanyDisableCostCenterConfirmPage";

                String MyCompanyManageUnitAddAddressPage = "pages/company/myCompanyManageUnitAddAddressPage";

                String MyCompanyManageUserPermissionsPage = "pages/company/myCompanyManageUserPermissionsPage";

                String MyCompanyManageUserResetPasswordPage = "pages/company/myCompanyManageUserPassword";

                String MyCompanyBudgetDisableConfirm = "pages/company/myCompanyDisableBudgetConfirmPage";

                String MyCompanyManageUserGroupsPage = "pages/company/myCompanyManageUserGroupsPage";

                String MyCompanyManageUsergroupViewPage = "pages/company/myCompanyManageUsergroupViewPage";

                String MyCompanyManageUsergroupEditPage = "pages/company/myCompanyManageUsergroupEditPage";

                String MyCompanyManageUsergroupCreatePage = "pages/company/myCompanyManageUsergroupCreatePage";

                String MyCompanyManageUsergroupDisableConfirmationPage = "pages/company/myCompanyManageUsergroupDisableConfirmationPage";

                String MyCompanyManagePermissionDisablePage = "pages/company/myCompanyManagePermissionDisablePage";

                String MyCompanyManagePermissionsViewPage = "pages/company/myCompanyManagePermissionsViewPage";

                String MyCompanyManagePermissionsEditPage = "pages/company/myCompanyManagePermissionsEditPage";

                String MyCompanyManagePermissionTypeSelectPage = "pages/company/myCompanyManagePermissionTypeSelectPage";

                String MyCompanyManagePermissionAddPage = "pages/company/myCompanyManagePermissionAddPage";

                String MyCompanyManageUserCustomersPage = "pages/company/myCompanyManageUserCustomersPage";

                String MyCompanyManageUserGroupPermissionsPage = "pages/company/myCompanyManageUserGroupPermissionsPage";

                String MyCompanyManageUserGroupMembersPage = "pages/company/myCompanyManageUserGroupMembersPage";

                String MyCompanyRemoveDisableConfirmationPage = "pages/company/myCompanyRemoveDisableConfirmationPage";

                String MyCompanyManageUserB2BUserGroupsPage = "pages/company/myCompanyManageUserB2BUserGroupsPage";
            }

            interface Checkout {
                String CheckoutLoginPage = "pages/checkout/checkoutLoginPage";

                String CheckoutRegistrationPage = "pages/checkout/checkoutRegistrationPage";

                String CheckoutDeliveryPage = "pages/checkout/checkoutDeliveryPage";

                String CheckoutReviewAndPayPage = "pages/checkout/checkoutReviewAndPayPage";

                String CheckoutConfirmationPage = "pages/checkout/checkoutConfirmationPage";

                String CheckoutPaymentFinalizePage = "pages/checkout/checkoutPaymentFinalizePage";

                String CheckoutPaymentUnknownErrorPage = "pages/checkout/checkoutPaymentUnknownErrorPage";

                String CheckoutConfirmationApprovalPage = "pages/checkout/checkoutConfirmationApprovalPage";

                String CheckoutConfirmationPickupPage = "pages/checkout/checkoutConfirmationPickupPage";
            }

            interface Password {
                String PasswordResetRequestPage = "pages/password/passwordResetRequestPage";

                String PasswordResetChangePage = "pages/password/passwordResetChangePage";
            }

            interface Error {
                String ErrorBadRequestPage = "pages/error/errorBadRequestPage";

                String ERROR_BAD_REQUEST_400 = ErrorBadRequestPage;

                String ErrorNotFoundPage = "pages/error/errorNotFoundPage";

                String ERROR_NOT_FOUND_404 = ErrorNotFoundPage;

                String UnknownErrorPage = "pages/error/unknownErrorPage";

                String HomePage = "/";

                String CatplusAccessDeniedErrorPage = "pages/error/catplusAccessDeniedErrorPage";

                String ErrorPageLog = "error_page_log";

                String ErrorNotAuthorized = "pages/error/errorNotAuthorize";

                String ERROR_NOT_FOUND_403 = ErrorNotAuthorized;

            }

            interface Cart {
                String CartPage = "pages/cart/cartPage";
            }

            interface Shopping {
                String ShoppingListPage = "/shopping";
            }

            interface StoreFinder {
                String StoreFinderSearchPage = "pages/storeFinder/storeFinderSearchPage";

                String StoreFinderDetailsPage = "pages/storeFinder/storeFinderDetailsPage";
            }

            interface Misc {
                String MiscRobotsPage = "pages/misc/miscRobotsPage";

                String MiscRobotsSecurePage = "pages/misc/miscRobotsSecurePage";
            }

            interface Manufacturer {
                String ManufacturerStoresPage = "pages/manufacturer/manufacturerStoresPage";

                String ManufacturerStoreDetailPage = "pages/manufacturer/manufacturerStoreDetailPage";

                String ManufacturerMenuPage = "pages/manufacturer/manufacturerMenuPage";
            }

            interface Newsletter {
                String NewsletterRegisterPage = "pages/newsletter/newsletterRegisterPage";

                String NewsletterSuccessPage = "pages/newsletter/newsletterSuccessPage";

                String NewsletterConfirmationPage = "pages/newsletter/newsletterConfirmationPage";

                String NewsletterUnsubscribeFeedbackPage = "pages/newsletter/newsletterunsubscribefeedbackPage";

                String NewsletterUnsubscribeFeedbackSuccessPage = "pages/newsletter/newsletterfeedbackSuccessPage";
            }

            interface Feedback {
                String NetPromoterScorePage = "pages/feedback/netPromoterScorePage";
            }

            interface ImportTool {
                String ImportToolUploadPage = "pages/importTool/importToolUploadPage";

                String ImportToolMatchingPage = "pages/importTool/importToolMatchingPage";

                String ImportToolReviewPage = "pages/importTool/importToolReviewPage";
            }

            interface BomTool {
                String BomToolUploadPage = "pages/importTool/importToolUploadPage";

                String BomToolMatchingPage = "pages/importTool/importToolMatchingPage";

                String BomToolReviewPage = "pages/importTool/importToolReviewPage";
            }

            interface EProcurement {
                interface OCI {
                    String OciPage = "pages/eprocurement/ociPage";

                    String OciSuccessPage = "pages/eprocurement/ociSuccessPage";
                }

                interface CXML {
                    String CXmlPage = "pages/eprocurement/cxmlPage";
                }

                interface Ariba {
                    String AribaPage = "pages/eprocurement/aribaPage";

                    String AribaLoginPath = "/ariba/authenticate";
                }
            }

            interface InfoCenter {
                String AddressChangePage = "pages/infoCenter/addressChangePage";

                String SeminarRegistrationPage = "pages/infoCenter/seminarRegistrationPage";

                String catalogOrderPage = "pages/infoCenter/catalogOrderPage";
            }

            interface Education {
                String EducationRegistrationPage = "pages/education/educationRegistrationPage";

                String EducationRegistrationSuccessPage = "pages/education/educationRegistrationSuccessPage";
            }

            interface Product {
                String ProductInformationPage = "pages/product/productInfo";

                String ProductInformationAllCountriesPage = "pages/product/pInfoAllCountries";
            }

            interface Survey {
                String OnlineSurveyPage = "pages/survey/onlineSurveyPage";

                String OnlineSurveySuccessPage = "pages/survey/onlineSurveySuccessPage";

                String OnlineSurveyErrorPage = "pages/survey/onlineSurveyErrorPage";
            }
        }

        interface Fragments {
            interface Account {
                String ShowMoreOrders = "fragments/account/showMoreOrders";

                String ShowMoreInvoices = "fragments/account/showMoreInvoices";

                String SetDefaultAddressesJson = "fragments/account/setDefaultAddresses";
            }

            interface Cart {
                String AddToCartPopup = "fragments/cart/addToCartPopup";

                String AddToCartExt = "fragments/cart/addToCartExt";

                String MiniCartPanel = "fragments/cart/miniCartPanel";

                String MiniCartErroCartRecalculateJsonCartRecalculateJsonCartRecalculateJsonCartRecalculateJsonCartRecalculateJsonCartRecalculateJsonCartRecalculateJsonCartRecalculateJsonrPanel = "fragments/cart/miniCartErrorPanel";

                String CartPopup = "fragments/cart/cartPopup";

                String CartJson = "fragments/cart/cartJson";

                String CartRecalculateJson = "fragments/cart/cartRecalculateJson";

                String DirectOrderSearch = "fragments/cart/directOrderSearchJson";

                String SendToFriendJson = "fragments/cart/sendToFriendJson";

                String DangerousProductsMessage = "fragments/cart/dangerousProductsMessage";

                String ProductOnlinePrice = "fragments/cart/productOnlinePrice";

                String CartReplaceProducts = "fragments/cart/cartReplaceProducts";

                String CartPriceDiff = "fragments/cart/cartPriceDiff";
            }

            interface Checkout {
                String DeliveryAddressFormPopup = "fragments/checkout/deliveryAddressFormPopup";

                String PaymentDetailsFormPopup = "fragments/checkout/paymentDetailsFormPopup";

                String OrderAttributeJson = "fragments/checkout/orderAttributeJson";

                String HiddenPaymentForm = "fragments/checkout/hiddenPaymentForm";

                String ActionStatus = "fragments/checkout/actionStatus";

                String ValidateCheckout = "fragments/checkout/validateCheckout";

                String PickupAvailability = "fragments/checkout/pickupAvailability";

                String RicevutaBancariaJson = "fragments/checkout/ricevutaBancariaJson";

                String CheckOrderCode = "fragments/checkout/checkOrderCode";

                String LoadAddresses = "fragments/checkout/loadAddresses";

                String UpdateUserProfilePopup = "fragments/checkout/updateUserProfilePopup";
            }

            interface Product {
                String QuickViewPopup = "fragments/product/quickViewPopup";

                String ZoomImagesPopup = "fragments/product/zoomImagesPopup";

                String ReviewsTab = "fragments/product/reviewsTab";

                String Availability = "fragments/product/availability";

                String ShowMoreProducts = "fragments/product/showMoreProducts";

                String CarouselProducts = "fragments/product/carouselProducts";

                String OptivoProduct = "fragments/product/optivoProduct";

                String SimilarProducts = "fragments/product/similarProducts";

                String AlternateProducts = "fragments/product/alternateProducts";

                String ProductAccessories = "fragments/product/productAccessories";

                String ProductDownloads = "fragments/product/productDownloads";

                String ProductEnergyEfficency = "fragments/product/productEnergyEfficency";

                String ProductsForFacetSearch = "fragments/product/loadProductsForFacetSearch";

                String AdditionalFacetForFacetSearch = "fragments/product/loadAdditionalFacetGroup";

                String ProductFeedbackCampaigns = "fragments/product/productFeedbackCampaigns";

                String FFproducts = "fragments/product/ffProducts";

                String ProductRecommendationList = "fragments/product/productrecommendationlist";

            }

            interface Wishlist {
                String ComparePopup = "fragments/wishlist/comparePopup";

                String ShoppingPopup = "fragments/wishlist/shoppingPopup";

                String CalculateShoppingListJson = "fragments/wishlist/shoppingListCalculateJson";

                String ActionStatus = "fragments/wishlist/actionStatus";

                String WishlistTogglesJson = "/fragments/wishlist/wishlistToggles";

                String CompareProductsJson = "/fragments/wishlist/compareProductsJson";
            }

            interface Register {
                String CustomerIdVatIdJson = "/fragments/register/customerIdVatIdJson";
            }

            interface Feedback {
                String NpsFormJson = "/fragments/feedback/npsFormJson";
            }

            interface General {
                String ContactValidation = "/fragments/general/contactValidation";
            }

            interface Quality {
                String EnvironmentalDocumentationDownloadPage = "pages/quality/environmentalDocumentationDownloadPage";

                String EnvironmentalDocumentationDownloadPageUrl = "/environmental-documentation-download";
            }

        }
    }
}
