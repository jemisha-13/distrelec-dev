package com.namics.distrelec.b2b.facades.adobe.datalayer;

import java.util.List;

import org.springframework.ui.Model;

import com.namics.distrelec.b2b.core.rma.CreateRMARequestForm;
import com.namics.distrelec.b2b.core.rma.GuestRMACreateRequestForm;
import com.namics.distrelec.b2b.core.service.rma.data.CreateRMAResponseData;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.*;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;

import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.AbstractOrderModel;

/**
 *
 * @author datjadhava
 *
 */
public interface DistDigitalDatalayerFacade {

    /**
     * Method to populate Datalayer search data
     *
     * @param searchQuery
     * @param searchPageData
     * @param digitalDatalayer
     */
    void populateDTMSearchData(final String searchQuery,
                               final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData,
                               final DigitalDatalayer digitalDatalayer);

    /**
     * Method to populate Datalayer product object
     *
     * @param productData
     * @return LinkedProduct
     */
    Product populateProductDTMObjects(final ProductData productData);

    /**
     * Method to populate Datalayer registration info
     *
     * @param digitalDatalayer
     * @param registrationInfo
     */
    void populateRegistrationType(final DigitalDatalayer digitalDatalayer, final RegistrationInfo registrationInfo);

    /**
     * Method to populate Datalayer registration info
     *
     * @param digitalDatalayer
     * @param createRMARequestForm
     * @param createRMAResponseData
     */
    void populateReturnResponse(final DigitalDatalayer digitalDatalayer,
                                final CreateRMARequestForm createRMARequestForm, final CreateRMAResponseData createRMAResponseData);

    /**
     * Method to populate Datalayer registration info
     *
     * @param digitalDatalayer
     * @param orderData
     */
    void populateOrderReturnData(final DigitalDatalayer digitalDatalayer,
                                 final de.hybris.platform.commercefacades.order.data.OrderData orderData);

    /**
     * Method to populate Datalayer registration info
     *
     * @param digitalDatalayer
     * @param orderListData
     */
    void populateReturnData(final DigitalDatalayer digitalDatalayer,
                            final SearchPageData<OrderHistoryData> orderListData);

    /**
     * Method to populate Datalayer registration info
     *
     * @param digitalDatalayer
     * @param createRMARequestForm
     */
    void populateGuestCreateReturnResponse(final DigitalDatalayer digitalDatalayer,
                                           final GuestRMACreateRequestForm createRMARequestForm);

    List<Product> getProductsDTMDataLayer(final List<ProductData> results);

    void storeDigitalDatalayerElements(final DigitalDatalayer digitalDatalayer, final AbstractPageModel cmsPage,
                                       String teaserTrackingId);

    void getCartForDTM(final DigitalDatalayer digitalDatalayer, final AbstractOrderModel orderData,
                       final String customerId);

    void storePaymentErrorData(final DigitalDatalayer digitalDatalayer, final String errorCode,
                               final String errorPage);

    void storeCheckoutStep(final DigitalDatalayer digitalDatalayer, final String page);

    void populatePrimaryPageCategoryAndPageType(final DigitalDatalayer digitalDatalayer,
                                                final String primaryCategory, final String pageType);

    void populatePageType(final DigitalDatalayer digitalDatalayer, final String pageType);

    void populatePrimaryPageCategoryStepAndPageType(final DigitalDatalayer digitalDatalayer,
                                                    final String primaryCategory, final String stepName, final String pageType);

    /**
     * @param digitalDatalayer
     *            Datalayer DTO
     * @param model
     *            Model map
     * @param position
     *            Position on template
     * @param prefix
     *            Optional prefix prepended to component uid
     */
    void populateBannersFromPosition(DigitalDatalayer digitalDatalayer, Model model, String position, String prefix);

    List<RemoveCart> populateCartRemoval(CartModificationData cartData);

    void populatePageCategoryAndPageType(final DigitalDatalayer digitalDatalayer, final String primaryCategory,
                                         final String subCategoryL1, final String pageType);

    /**
     *
     * @param digitalDatalayer
     * @param cartData
     * @param customerId
     * @return
     */
    Transaction getTransactionData(DigitalDatalayer digitalDatalayer, AbstractOrderModel cartData, String customerId);

    void populateFFTrackingAttribute(final DigitalDatalayer digitalDatalayer, final Boolean pdp);

    void addProductFamilyNameToSubcategory(PageCategory pageCategory, List<String> breadcrumbData, String productFamilyName);
}
