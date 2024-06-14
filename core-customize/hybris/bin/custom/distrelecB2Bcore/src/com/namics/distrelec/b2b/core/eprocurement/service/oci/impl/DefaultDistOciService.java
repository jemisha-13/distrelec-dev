/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.eprocurement.service.oci.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.eprocurement.data.oci.DefaultDistSAPProductList;
import com.namics.distrelec.b2b.core.eprocurement.service.DistEProcurementCustomerConfigService;
import com.namics.distrelec.b2b.core.eprocurement.service.oci.DistCatalogLoginPerformer;
import com.namics.distrelec.b2b.core.eprocurement.service.oci.DistOciService;
import com.namics.distrelec.b2b.core.inout.erp.OrderCalculationService;
import com.namics.distrelec.b2b.core.service.product.DistCommercePriceService;
import com.namics.distrelec.b2b.core.service.product.DistPriceService;
import com.namics.distrelec.distrelecoci.constants.DistrelecOciConstants;
import com.namics.distrelec.distrelecoci.data.DistSapProduct;
import com.namics.distrelec.distrelecoci.data.DistSapProductList;
import com.namics.distrelec.distrelecoci.exception.OciException;
import com.namics.distrelec.distrelecoci.utils.OciUtils;
import com.namics.distrelec.distrelecoci.utils.OutboundSection;

import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloConnectException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Base64;
import de.hybris.platform.util.WebSessionFunctions;

/**
 * Default implementation for <code>DistOciService</code>.
 * 
 * @author pbueschi, Namics AG
 */
public class DefaultDistOciService extends AbstractBusinessService implements DistOciService {

    private static final Logger LOG = Logger.getLogger(DefaultDistOciService.class);

    @Autowired
    private ClassificationService classificationService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private DistPriceService priceService;

    @Autowired
    @Qualifier("erp.orderCalculationService")
    private OrderCalculationService orderCalculationService;

    @Autowired
    @Qualifier("distEProcurementCustomerConfigService")
    private DistEProcurementCustomerConfigService customerConfigService;

    @Autowired
    private UrlResolver<ProductModel> productModelUrlResolver;

    @Autowired
    private DistCatalogLoginPerformer disCatalogLoginPerformer;

    @Autowired
    @Qualifier("commercePriceService")
    private DistCommercePriceService commercePriceService;

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.eprocurement.service.oci.DistOciService#isOciSession(de.hybris.platform.jalo.JaloSession)
     */
    @Override
    public boolean isOciSession(final JaloSession jaloSession) {
        return jaloSession.getAttribute(DistrelecOciConstants.IS_OCI_LOGIN) != null
                && !(jaloSession.getAttribute(DistrelecOciConstants.IS_OCI_LOGIN).equals(Boolean.FALSE));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.eprocurement.service.oci.DistOciService#isOciCustomer()
     */
    @Override
    public boolean isOciCustomer() {
        final UserModel currentUser = getUserService().getCurrentUser();
        final UserGroupModel userGroup = getUserService().getUserGroupForUID(DistConstants.User.OCICUSTOMERGROUP_UID);
        if (currentUser == null || userGroup == null) {
            return false;
        }
        return getUserService().isMemberOfGroup(currentUser, userGroup) && isOciSession(JaloSession.getCurrentSession());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.eprocurement.service.oci.DistOciService#hasMegaFlyOutDisabled()
     */
    @Override
    public boolean hasMegaFlyOutDisabled() {
        return isOciCustomer() && getCustomerConfigService().hasMegaFlyOutDisabled();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.eprocurement.service.oci.DistOciService#isCustomFooterEnabled()
     */
    @Override
    public boolean isCustomFooterEnabled() {
        return isOciCustomer() && getCustomerConfigService().isCustomFooter();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.eprocurement.service.oci.DistOciService#openInNewWindow()
     */
    @Override
    public boolean openInNewWindow() {
        return isOciCustomer() && getCustomerConfigService().openInNewWindow();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.eprocurement.service.oci.DistOciService#generateOciForm()
     */
    @Override
    public String generateOciForm() throws OciException, CalculationException {
        final DistSapProductList sapProductList = new DefaultDistSAPProductList(getCartService().getSessionCart(),
                isOciSession(JaloSession.getCurrentSession()), getPriceService(), getOrderCalculationService(), getCustomerConfigService(), getModelService(),
                getCommercePriceService());
        return createOciBuyerButton(sapProductList, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.eprocurement.service.oci.DistOciService#getOciRedirectUrl()
     */
    @Override
    public String getOciRedirectUrl() {
        final String functionField = getOutboundSectionField(DistrelecOciConstants.FUNCTION);
        if (StringUtils.isNotBlank(functionField)) {
            if (StringUtils.equals(functionField, DistrelecOciConstants.DETAIL)) {
                final String productCode = getOutboundSectionField(DistrelecOciConstants.PRODUCTID);
                final ProductModel product = getProductService().getProductForCode(productCode);
                return getProductModelUrlResolver().resolve(product);
            }
        }

        return isOciCustomer() && getCustomerConfigService().getDefaultRedirectURL() != null ? getCustomerConfigService().getDefaultRedirectURL() : "/";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.eprocurement.service.oci.DistOciService#getOciFunctionForm(java.util.List)
     */
    @Override
    public String getOciFunctionForm(final List<ProductModel> backgroundSearchPlist) throws OciException {
        final StringBuilder data = new StringBuilder("");
        final String functionField = getOutboundSectionField(DistrelecOciConstants.FUNCTION);
        if (StringUtils.equals(functionField, DistrelecOciConstants.VALIDATE) || StringUtils.equals(functionField, DistrelecOciConstants.BACKGROUND_SEARCH)) {

            boolean ociSession = isOciSession(JaloSession.getCurrentSession());
            if (StringUtils.equals(functionField, DistrelecOciConstants.VALIDATE)) {
                final DistSapProduct sapproduct = getDisCatalogLoginPerformer().getProductInfoForValidation(
                        getOutboundSectionField(DistrelecOciConstants.PRODUCTID), Double.parseDouble(getOutboundSectionField(DistrelecOciConstants.QUANTITY)),
                        ociSession);
                data.append(sapproduct == null ? "" : OciUtils.generateHtmlData(sapproduct, getOutboundSection()));
            }

            if (StringUtils.equals(functionField, DistrelecOciConstants.BACKGROUND_SEARCH)) {
                final DistSapProductList sapproductlist = getDisCatalogLoginPerformer().backgroundSearch(backgroundSearchPlist, ociSession);
                data.append(sapproductlist == null || sapproductlist.size() == 0 ? "" : OciUtils.generateHtmlData(sapproductlist, getOutboundSection()));
            }

            return OciUtils.generateHTML(data.toString(), getOutboundSection(), true);
        }
        return "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.eprocurement.service.oci.DistOciService#doOciLogin(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void doOciLogin(final HttpServletRequest request, final HttpServletResponse response) throws OciException {
        try {
            ociLogin(request, response, getDisCatalogLoginPerformer(), true, false);
        } catch (final OciException e) {
            LOG.error("OCI login failed", e);
            throw new OciException("OCI login failed", e);
        } catch (final JaloConnectException e) {
            LOG.error("OCI login failed", e);
            throw new OciException("OCI login failed", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.eprocurement.service.oci.DistOciService#ociLogin(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, com.namics.distrelec.b2b.core.eprocurement.service.oci.DistCatalogLoginPerformer, boolean,
     * boolean)
     */
    @Override
    public void ociLogin(final HttpServletRequest request, final HttpServletResponse response, final DistCatalogLoginPerformer catalogLoginPerformer,
            final boolean disableRedirect, final boolean useHtml) throws OciException, JaloConnectException {
        final JaloSession jalo = WebSessionFunctions.getSession(request);
        final OutboundSection outboundSection = new OutboundSection(getRequestParameters(request), catalogLoginPerformer.getHookURLFieldName());

        if (StringUtils.isEmpty(catalogLoginPerformer.getHookURLFieldName())) {
            throw new OciException("Fieldname \"" + catalogLoginPerformer.getHookURLFieldName()
                    + "\" (defaultname: HOOK_URL) from SRM Server to shop is null or empty. I need this field for returning my data to SRM Server", 3);
        }

        if (StringUtils.isEmpty(outboundSection.getField(catalogLoginPerformer.getHookURLFieldName()))) {
            throw new OciException("Value of Field \"" + catalogLoginPerformer.getHookURLFieldName()
                    + "\" (defaultname: HOOK_URL) from SRM Server to shop is null or empty. I need this field for returning my data to SRM Server", 3);
        }

        catalogLoginPerformer.login(request, response, outboundSection);
        jalo.setAttribute(DistrelecOciConstants.IS_OCI_LOGIN, Boolean.TRUE);
        jalo.setAttribute(DistrelecOciConstants.OUTBOUND_SECTION_DATA, outboundSection);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.eprocurement.service.oci.DistOciService#createOciBuyerButton(com.namics.distrelec.distrelecoci.data.
     * DistSapProductList)
     */
    @Override
    public String createOciBuyerButton(final DistSapProductList sapproductlist) throws OciException {
        return createOciBuyerButton(sapproductlist, null, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.eprocurement.service.oci.DistOciService#createOciBuyerButton(com.namics.distrelec.distrelecoci.data.
     * DistSapProductList, boolean)
     */
    @Override
    public String createOciBuyerButton(final DistSapProductList sapproductlist, final boolean useHtml) throws OciException {
        return createOciBuyerButton(sapproductlist, null, useHtml);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.eprocurement.service.oci.DistOciService#createOciBuyerButton(com.namics.distrelec.distrelecoci.data.
     * DistSapProductList, java.lang.String)
     */
    @Override
    public String createOciBuyerButton(final DistSapProductList sapproductlist, final String ociButton) throws OciException {
        return createOciBuyerButton(sapproductlist, ociButton, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.eprocurement.service.oci.DistOciService#createOciBuyerButton(com.namics.distrelec.distrelecoci.data.
     * DistSapProductList, java.lang.String, boolean)
     */
    @Override
    public String createOciBuyerButton(final DistSapProductList sapproductlist, final String ociButton, final boolean useHtml) throws OciException {
        if (!isOciSession(JaloSession.getCurrentSession())) {
            throw new OciException("The current jaloSession is not an Oci Session", 7);
        }

        final StringBuilder tempForm = new StringBuilder();
        final OutboundSection outboundSection = (OutboundSection) JaloSession.getCurrentSession().getAttribute(DistrelecOciConstants.OUTBOUND_SECTION_DATA);

        tempForm.append("<form action=\"").append(outboundSection.getField(outboundSection.getHookURLFieldName()))
                .append("\" method=\"post\" name=\"OrderForm\" accept-charset=\"UTF-8\"");
        tempForm.append(((outboundSection.getField("~Target") != null) && (outboundSection.getField("~Target").length() > 0))
                ? " target=\"" + outboundSection.getField("~Target") + "\"" : "");
        tempForm.append(">\n");

        if (!useHtml) {
            tempForm.append("<input type=\"hidden\" name=\"~xmlDocument\" value=\"");
            tempForm.append(Base64.encodeBytes(OciUtils.generateXMLData(sapproductlist, outboundSection).getBytes(), 8)).append("\">\n");
            tempForm.append("<input type=\"hidden\" name=\"~xml_type\" value=\"ESAPO\">");
        } else {
            tempForm.append("<input type=\"hidden\" id=\"").append("~OkCode").append("\" ");
            tempForm.append("name=\"").append("~OkCode").append("\" ");
            tempForm.append("value=\"").append(outboundSection.getField("~OkCode")).append("\"/>\n");

            tempForm.append("<input type=\"hidden\" id=\"").append("~Caller").append("\" ");
            tempForm.append("name=\"").append("~Caller").append("\" ");
            tempForm.append("value=\"").append(outboundSection.getField("~Caller")).append("\"/>\n");

            tempForm.append("<input type=\"hidden\" id=\"").append("~Target").append("\" ");
            tempForm.append("name=\"").append("~Target").append("\" ");
            tempForm.append("value=\"").append(outboundSection.getField("~Target")).append("\"/>\n");

            tempForm.append(OciUtils.generateHtmlData(sapproductlist, outboundSection));
        }

        if (ociButton == null || ociButton.length() == 0) {
            tempForm.append("<input type=\"submit\" value=\"").append("SAP OCI Buyer").append("\" id=submit1 name=submit1>").append("\n</form>\n");
        } else if (ociButton.indexOf(60) == -1 && ociButton.indexOf(62) == -1) {
            tempForm.append("<input type=\"submit\" value=\"").append(ociButton).append("\" id=submit1 name=submit1>").append("\n</form>\n");
        } else {
            tempForm.append(ociButton).append("\n</form>\n");
        }

        return tempForm.toString();
    }

    /**
     * 
     * @param field
     * @return
     */
    private String getOutboundSectionField(final String field) {
        final Object sessionAttribute = JaloSession.getCurrentSession().getAttribute(DistrelecOciConstants.OUTBOUND_SECTION_DATA);
        if (sessionAttribute != null) {
            return ((OutboundSection) sessionAttribute).getField(field);
        }
        return "";
    }

    /**
     * 
     * @return
     */
    private OutboundSection getOutboundSection() {
        final Object sessionAttribute = JaloSession.getCurrentSession().getAttribute(DistrelecOciConstants.OUTBOUND_SECTION_DATA);
        if (sessionAttribute != null) {
            return (OutboundSection) sessionAttribute;
        }
        return null;
    }

    /**
     * 
     * @param request
     * @return
     */
    private static Map<String, String> getRequestParameters(final HttpServletRequest request) {
        return request.getParameterMap().entrySet().stream() //
                .filter(entry -> entry.getValue() != null && entry.getValue().length > 0) //
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()[0]));
    }

    // Getters & Setters

    public CartService getCartService() {
        return cartService;
    }

    public void setCartService(final CartService cartService) {
        this.cartService = cartService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }

    public DistPriceService getPriceService() {
        return priceService;
    }

    public void setPriceService(final DistPriceService priceService) {
        this.priceService = priceService;
    }

    public OrderCalculationService getOrderCalculationService() {
        return orderCalculationService;
    }

    public void setOrderCalculationService(final OrderCalculationService orderCalculationService) {
        this.orderCalculationService = orderCalculationService;
    }

    public DistEProcurementCustomerConfigService getCustomerConfigService() {
        return customerConfigService;
    }

    public void setCustomerConfigService(final DistEProcurementCustomerConfigService customerConfigService) {
        this.customerConfigService = customerConfigService;
    }

    public UrlResolver<ProductModel> getProductModelUrlResolver() {
        return productModelUrlResolver;
    }

    public void setProductModelUrlResolver(final UrlResolver<ProductModel> productModelUrlResolver) {
        this.productModelUrlResolver = productModelUrlResolver;
    }

    public DistCatalogLoginPerformer getDisCatalogLoginPerformer() {
        return disCatalogLoginPerformer;
    }

    public void setDisCatalogLoginPerformer(final DistCatalogLoginPerformer disCatalogLoginPerformer) {
        this.disCatalogLoginPerformer = disCatalogLoginPerformer;
    }


    public ClassificationService getClassificationService() {
        return classificationService;
    }


    public void setClassificationService(final ClassificationService classificationService) {
        this.classificationService = classificationService;
    }


    public DistCommercePriceService getCommercePriceService() {
        return commercePriceService;
    }


    public void setCommercePriceService(final DistCommercePriceService commercePriceService) {
        this.commercePriceService = commercePriceService;
    }
}
