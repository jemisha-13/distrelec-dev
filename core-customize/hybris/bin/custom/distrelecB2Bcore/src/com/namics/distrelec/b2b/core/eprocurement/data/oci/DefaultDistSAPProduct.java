/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.eprocurement.data.oci;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.util.HtmlUtils;

import com.namics.distrelec.b2b.core.eprocurement.service.DistEProcurementCustomerConfigService;
import com.namics.distrelec.b2b.core.model.eprocurement.DistCustomerConfigModel;
import com.namics.distrelec.b2b.core.model.eprocurement.OCICustomerConfigModel;
import com.namics.distrelec.b2b.core.service.product.DistCommercePriceService;
import com.namics.distrelec.b2b.core.service.product.DistPriceService;
import com.namics.distrelec.distrelecoci.data.DistSapProduct;
import com.namics.distrelec.distrelecoci.utils.OutboundSection;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.order.price.PriceInformation;

/**
 * Implement for <code>SAPProduct</code> to use models instead of jalo items and provide additional attributes.
 * 
 * @author pbueschi, Namics AG
 */
public class DefaultDistSAPProduct implements DistSapProduct {

    private static final Logger LOG = Logger.getLogger(DefaultDistSAPProduct.class);

    public static final String DELIVERY_TIME = "deliveryTime";

    private final ProductModel product;
    private final DistCustomerConfigModel customerConfig;
    private final OCICustomerConfigModel ociCustomerConfig;

    private final DistPriceService priceService;
    private final DistEProcurementCustomerConfigService customerConfigService;

    private double quantity = 1;
    private AbstractOrderEntryModel cartEntry;
    private Map<String, String> customParameters = new HashMap<String, String>();
    private List<String> parameterNames;
    private final boolean ociSession;

    private DistCommercePriceService commercePriceService;
    /**
     * map with additional fileds for elfa OCI customers
     */
    private static Map<String, String> elfaFields = new HashMap<String, String>();

    static {
        elfaFields.put("vendor", "NEW_ITEM-VENDOR");
        elfaFields.put("suppliermpid", "NEW_ITEM-SUPPLIERMPID");
        elfaFields.put("agreement_no", "NEW_ITEM-EXT_QUOTE_ID");
        elfaFields.put("account_code", "NEW_ITEM-ACCOUNT");
    }

    public DefaultDistSAPProduct(final AbstractOrderEntryModel cartEntry, final boolean ociSession, final DistPriceService priceService,
            final DistEProcurementCustomerConfigService customerConfigService, final DistCommercePriceService commercePriceService) {

        this(cartEntry.getProduct(), ociSession, priceService, customerConfigService, commercePriceService);
        this.cartEntry = cartEntry;

    }

    public DefaultDistSAPProduct(final ProductModel product, final double quantity, final boolean ociSession, final DistPriceService priceService,
            final DistEProcurementCustomerConfigService customerConfigService, final DistCommercePriceService commercePriceService) {
        this(product, ociSession, priceService, customerConfigService, commercePriceService);
        this.quantity = quantity;
    }

    public DefaultDistSAPProduct(final ProductModel product, final boolean ociSession, final DistPriceService priceService,
            final DistEProcurementCustomerConfigService customerConfigService, final DistCommercePriceService commercePriceService) {
        this.product = product;
        this.ociSession = ociSession;
        this.priceService = priceService;
        this.commercePriceService = commercePriceService;
        this.customerConfigService = customerConfigService;
        this.customerConfig = customerConfigService.getCustomerConfig();
        if (this.customerConfig instanceof OCICustomerConfigModel) {
            this.ociCustomerConfig = (OCICustomerConfigModel) this.customerConfig;
        } else {
            this.ociCustomerConfig = null;
        }
        setUpCustomParameters(product);
        getCustomParameterNames();
    }

    @Override
    public String getItemDescription() {
        // public static String html2text(String html) {
        // return Jsoup.parse(html).text();
        // }
        String noHTMLname = "";
        if (product.getName() != null) {
            noHTMLname = product.getName().replaceAll("\\<.*?>", "");
        } else {
            noHTMLname = product.getName();
        }
        final String desc = StringUtils.defaultString(noHTMLname, product.getCodeErpRelevant());
        return popCustomAttribute("NEW_ITEM-DESCRIPTION", desc);
    }

    @Override
    public String getItemMatNr() {
        return popCustomAttribute("NEW_ITEM-MATNR", "");
    }

    @Override
    public double getItemQuantity() {
        return cartEntry == null ? quantity : cartEntry.getQuantity().doubleValue();
    }

    @Override
    public String getItemUnit() {
        String customerConfigUnit = "";
        if (customerConfig != null && StringUtils.isNotBlank(customerConfig.getUnit())) {
            customerConfigUnit = customerConfig.getUnit();
        } else if (product.getUnit() != null) {
            customerConfigUnit = product.getUnit().getCode();
        }
        return popCustomAttribute("NEW_ITEM-UNIT", customerConfigUnit);
    }

    @Override
    public double getItemPrice() {
        if (cartEntry != null) {
            // DISTRELEC-4652: if list price is null then use the base price.
            return (null != cartEntry.getBasePrice()) ? cartEntry.getBasePrice().doubleValue() : cartEntry.getBaseListPrice().doubleValue();
        }
        if (priceService == null) {
            return 0.0;
        }
        PriceInformation validPriceInformation = null;
        Long minQty = 0L;
        final List<PriceInformation> priceInformations = priceService.getPriceInformationsForProduct(product, true);
        for (final PriceInformation priceInformation : priceInformations) {
            final Long minQtd = getMinQuantity(priceInformation);
            if (minQty < minQtd && minQtd <= quantity) {
                minQty = minQtd;
                validPriceInformation = priceInformation;
            }
        }
        return validPriceInformation == null ? 0 : validPriceInformation.getPriceValue().getValue();
    }

    @Override
    public String getItemCurrency() {
        if (cartEntry != null) {
            return cartEntry.getOrder().getCurrency().getIsocode();
        }
        return JaloSession.getCurrentSession().getSessionContext().getCurrency().getIsocode();
    }

    @Override
    public int getItemPriceUnit() {
        return popCustomAttribute("NEW_ITEM-PRICEUNIT", 1);
    }

    @Override
    public int getItemLeadTime() {
        int customerConfigLeadTime = 1;
        if (customerConfig != null && customerConfig.getLeadTime() != null) {
            customerConfigLeadTime = customerConfig.getLeadTime().intValue();
        } else if (product.getDeliveryTime() != null && product.getDeliveryTime() != null) {
            customerConfigLeadTime = product.getDeliveryTime().intValue();
        }
        return popCustomAttribute("NEW_ITEM-LEADTIME", customerConfigLeadTime);
    }

    @Override
    public String getItemLongtext() {
        if (ociCustomerConfig != null && ociCustomerConfig.getUseLongDesc()) {
            final String longText = popCustomAttribute("NEW_ITEM-LONGTEXT_", StringUtils.defaultString(product.getDescription(), "")).replaceAll(
                    "(<br\\s+/>)|(\r?\n)", ", ");
            return BooleanUtils.isTrue(ociCustomerConfig.getEncodeHTML()) ? HtmlUtils.htmlEscape(HtmlUtils.htmlEscape(longText)) : longText;
        }
        return "";
    }

    @Override
    public String getItemVendor() {
        final String attribute = customerConfig != null && StringUtils.isNotBlank(customerConfig.getVendor()) ? customerConfig.getVendor() : "";
        return popCustomAttribute("NEW_ITEM-VENDOR", attribute);
    }

    @Override
    public String getItemManufactCode() {
        return popCustomAttribute("NEW_ITEM-MANUFACTCODE", "");
    }

    @Override
    public String getItemVendorMat() {
        return popCustomAttribute("NEW_ITEM-VENDORMAT", product.getCodeErpRelevant());
    }

    @Override
    public String getItemManufactMat() {
        return popCustomAttribute("NEW_ITEM-MANUFACTMAT", "");
    }

    @Override
    public String getItemMatGroup() {
        if (customerConfig != null && StringUtils.isNotBlank(customerConfig.getMatGroup())) {
            return popCustomAttribute("NEW_ITEM-MATGROUP", customerConfig.getMatGroup());
        }

        if (customParameters.containsKey("NEW_ITEM-MATGROUP")) {
            return popCustomAttribute("NEW_ITEM-MATGROUP", customParameters.get("NEW_ITEM-MATGROUP"));
        }

        return "";
    }

    @Override
    public boolean isItemService() {
        return popCustomAttribute("NEW_ITEM-SERVICE", false);
    }

    @Override
    public String getItemContract() {
        return popCustomAttribute("NEW_ITEM-CONTRACT", "");
    }

    @Override
    public String getItemContractItem() {
        return popCustomAttribute("NEW_ITEM-CONTRACT_ITEM", "");
    }

    @Override
    public String getItemExtQuoteId() {
        return popCustomAttribute("NEW_ITEM-EXT_QUOTE_ID", "");
    }

    @Override
    public String getItemExtQuoteItem() {
        return popCustomAttribute("NEW_ITEM-EXT_QUOTE_ITEM", "");
    }

    @Override
    public String getItemExtProductId() {
        return popCustomAttribute("NEW_ITEM-EXT_PRODUCT_ID", "");
    }

    @Override
    public String getItemAttachment() {
        return popCustomAttribute("NEW_ITEM-ATTACHMENT", "");
    }

    @Override
    public String getItemAttachmentTitle() {
        return popCustomAttribute("NEW_ITEM-ATTACHMENT_TITLE", "");
    }

    @Override
    public char getItemAttachmentPurpose() {
        return popCustomAttribute("NEW_ITEM-ATTACHMENT_PURPOSE", 'C');
    }

    @Override
    public String getItemExtCategoryId() {
        return popCustomAttribute("NEW_ITEM-EXT_CATEGORY_ID", "");
    }

    @Override
    public String getItemExtCategory() {
        return popCustomAttribute("NEW_ITEM-EXT_CATEGORY", "");
    }

    @Override
    public String getItemSLDSysName() {
        return "";
    }

    @Override
    @SuppressWarnings("PMD")
    public String[] getCustomParameterNames() {

        if (customerConfigService.hasShippingProduct() && customerConfigService.isShippingProduct(product)) {
            return getShippingProductFieldNames();
        }

        if (parameterNames == null) {
            // use shipping product custom fields
            parameterNames = new ArrayList<String>();

            // use field configs
            final List<String> fieldConfigDomainNames = customerConfigService.getFieldConfigDomainNames();
            if (CollectionUtils.isNotEmpty(fieldConfigDomainNames)) {
                for (final String fieldConfigDomainName : fieldConfigDomainNames) {
                    parameterNames.add(fieldConfigDomainName);
                }
            }

            for (String fieldName : elfaFields.values()) {
                String value = customParameters.get(fieldName);
                if (StringUtils.isNotEmpty(value)) {
                    parameterNames.add(fieldName);
                }
            }
        }

        return parameterNames.toArray(new String[] {});
    }

    @Override
    public String getCustomParameterValue(final String paramString) {
        if (MapUtils.isNotEmpty(customParameters)) {
            final String value = customParameters.get(paramString);
            return StringUtils.isNotBlank(value) ? value : "";
        }
        return "";
    }

    /**
     * Set up general field configs for all product or field configs only for shipping product
     * 
     * @param product
     *            the product
     */
    protected void setUpCustomParameters(final ProductModel product) {
        if (customerConfigService.hasShippingProduct()) {
            customParameters.putAll(customerConfigService.getFieldsForShippingProduct());
        } else {
            // add special elfa fields
            addElfaAddidionalFileds();
            // add customer configuration after elfa fields to allow overwrite
            customParameters.putAll(customerConfigService.getFieldConfigsForProduct(product));
        }
    }

    /**
     * Add additional outboundSection parameter to the customParameters
     */
    protected void addElfaAddidionalFileds() {

        // only do it for OCI sessions

        if (ociSession) {
            final OutboundSection outboundSection = (OutboundSection) JaloSession.getCurrentSession().getAttribute("OUTBOUND_SECTION_DATA");
            if (outboundSection != null) {
                for (String key : elfaFields.keySet()) {
                    String value = outboundSection.getField(key);
                    if (StringUtils.isNotEmpty(value)) {
                        String fieldName = elfaFields.get(key);
                        customParameters.put(fieldName, value);
                    }
                }
            }
        }

    }

    protected Long getMinQuantity(final PriceInformation priceInfo) {
        final Map qualifiers = priceInfo.getQualifiers();
        final Object minQtdObj = qualifiers.get(PriceRow.MINQTD);
        if (minQtdObj instanceof Long) {
            return (Long) minQtdObj;
        }
        return Long.valueOf(1);
    }

    protected String[] getShippingProductFieldNames() {
        final Map<String, String> shippingProductFields = customerConfigService.getFieldsForShippingProduct();
        if (MapUtils.isNotEmpty(shippingProductFields)) {
            int iCounter = 0;
            final String[] shippingProductFieldNames = new String[shippingProductFields.size()];
            for (final String shippingProductField : shippingProductFields.keySet()) {
                shippingProductFieldNames[iCounter] = shippingProductField;
                iCounter++;
            }
            return shippingProductFieldNames;
        }
        return new String[0];
    }

    protected String popCustomAttribute(final String domain, final String defaultValue) {
        final String attribute = customParameters.get(domain);
        if (parameterNames.contains(domain)) {
            parameterNames.remove(domain);
        }

        return StringUtils.isBlank(attribute) ? defaultValue : attribute;
    }

    protected int popCustomAttribute(final String domain, final int defaultValue) {
        final String attribute = popCustomAttribute(domain, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(attribute);
        } catch (final NumberFormatException e) {
            LOG.error("Could not parse given field config with domain " + domain + ".", e);
        }
        return defaultValue;
    }

    protected boolean popCustomAttribute(final String domain, final boolean defaultValue) {
        final String attribute = popCustomAttribute(domain, "");
        final Boolean value = BooleanUtils.toBooleanObject(attribute);
        return value != null ? value.booleanValue() : defaultValue;
    }

    protected char popCustomAttribute(final String domain, final char defualtValue) {
        final String attribute = popCustomAttribute(domain, String.valueOf(defualtValue));
        return CharUtils.toChar(attribute);
    }

}
