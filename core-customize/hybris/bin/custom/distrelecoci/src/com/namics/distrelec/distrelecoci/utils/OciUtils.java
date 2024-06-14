/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.distrelecoci.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.namics.distrelec.distrelecoci.data.DistSapProduct;
import com.namics.distrelec.distrelecoci.data.DistSapProductList;
import com.namics.distrelec.distrelecoci.exception.OciException;

import de.hybris.platform.core.CoreAlgorithms;
import de.hybris.platform.util.Base64;

/**
 * OciUtils
 * 
 * @author dathusir, Distrelec
 * @since Distrelec 3.0
 */
public class OciUtils {

    private static DecimalFormat decimalFormat;

    public static String generateHTML(final String data, final OutboundSection outboundSection, final boolean useHtml) throws OciException {
        final StringBuilder htmltemp = new StringBuilder("<HTML>\n<BODY>\n");
        htmltemp.append("<form action=\"").append(outboundSection.getField(outboundSection.getHookURLFieldName()))
                .append("\" method=\"post\" name=\"OrderForm\" accept-charset=\"UTF-8\"");
        htmltemp.append(((outboundSection.getField("~Target") != null) && (outboundSection.getField("~Target").length() > 0)) ? " target=\""
                + encodeHTML(outboundSection.getField("~Target")) + "\"" : "");
        htmltemp.append(">\n");

        if (!useHtml) {
            htmltemp.append("<input type=\"hidden\" name=\"~xmlDocument\" value=\"");
            htmltemp.append(Base64.encodeBytes(data.getBytes(), 8)).append("\">\n");
            htmltemp.append("<input type=\"hidden\" name=\"~xml_type\" value=\"ESAPO\">");
        } else {
            htmltemp.append("<input type=\"hidden\" id=\"").append("~OkCode").append("\" ");
            htmltemp.append("name=\"").append("~OkCode").append("\" ");
            htmltemp.append("value=\"").append(encodeHTML(outboundSection.getField("~OkCode"))).append("\"/>\n");
            htmltemp.append("<input type=\"hidden\" id=\"").append("~Caller").append("\" ");
            htmltemp.append("name=\"").append("~Caller").append("\" ");
            htmltemp.append("value=\"").append(encodeHTML(outboundSection.getField("~Caller"))).append("\"/>\n");
            htmltemp.append("<input type=\"hidden\" id=\"").append("~Target").append("\" ");
            htmltemp.append("name=\"").append("~Target").append("\" ");
            htmltemp.append("value=\"").append(encodeHTML(outboundSection.getField("~Target"))).append("\"/>\n");
            htmltemp.append(data);
        }

        htmltemp.append("<input type=\"submit\" value=\"Submit\" id=\"submit1\" name=\"submit1\">\n</form>\n");
        htmltemp.append("</BODY>\n</HTML>");
        return htmltemp.toString();
    }

    public static String generateXMLData(final DistSapProduct sapproduct, final OutboundSection outboundSection) throws OciException {
        if (outboundSection.getField("OCI_VERSION").startsWith("2.")) {
            checkProductFor20Conform(sapproduct);
        } else if (outboundSection.getField("OCI_VERSION").startsWith("3.")) {
            checkProductFor30Conform(sapproduct);
        } else if (outboundSection.getField("OCI_VERSION").startsWith("4.")) {
            checkProductFor40Conform(sapproduct);
        } else {
            throw new OciException("Found OCI Version " + outboundSection.getField("OCI_VERSION") + ". Supporting only 2.x 3.x or 4.x. Aborting.", 6);
        }

        final StringBuilder xmltemp = new StringBuilder("<?xml version =\"1.0\" encoding=\"utf-8\"?>\n\n<BusinessDocument>\n\t<Catalog>\n");
        xmltemp.append(generateXMLCodeForProduct(sapproduct));
        xmltemp.append("\t</Catalog>\n");
        xmltemp.append("</BusinessDocument>\n");
        return xmltemp.toString();
    }

    public static String generateXMLData(final DistSapProductList sapproductlist, final OutboundSection outboundSection) throws OciException {
        if (outboundSection.getField("OCI_VERSION").startsWith("2.")) {
            checkCartFor20Conform(sapproductlist);
        } else if (outboundSection.getField("OCI_VERSION").startsWith("3.")) {
            checkCartFor30Conform(sapproductlist);
        } else if (outboundSection.getField("OCI_VERSION").startsWith("4.")) {
            checkCartFor40Conform(sapproductlist);
        } else {
            throw new OciException("Found OCI Version " + outboundSection.getField("OCI_VERSION") + ". Supporting only 2.x 3.x or 4.x. Aborting.", 6);
        }

        final StringBuilder xmltemp = new StringBuilder("<?xml version =\"1.0\" encoding=\"utf-8\"?>\n\n<BusinessDocument>\n\t<Catalog>\n");
        for (int index = 0; index < sapproductlist.size(); ++index) {
            xmltemp.append(generateXMLCodeForProduct(sapproductlist.getProduct(index)));
        }
        xmltemp.append("\t</Catalog>\n");
        xmltemp.append("</BusinessDocument>\n");
        return xmltemp.toString();
    }

    public static String generateHtmlData(final DistSapProductList sapproductlist, final OutboundSection outboundSection) throws OciException {
        final StringBuilder htmltemp = new StringBuilder();

        if (MapUtils.isNotEmpty(sapproductlist.getHeaders())) {
            for (final String key : sapproductlist.getHeaders().keySet()) {
                htmltemp.append("<input type=\"hidden\" name=\"").append(key).append("\" value=\"").append(sapproductlist.getHeaders().get(key))
                        .append("\">\n");
            }
        }

        for (int index = 0; index < sapproductlist.size(); ++index) {
            htmltemp.append(generateHtmlData(sapproductlist.getProduct(index), index + 1, outboundSection));
        }
        return htmltemp.toString();
    }

    public static String generateHtmlData(final DistSapProduct sapproduct, final OutboundSection outboundSection) throws OciException {
        return generateHtmlData(sapproduct, 1, outboundSection);
    }

    private static String generateHtmlData(final DistSapProduct sapproduct, final int index, final OutboundSection outboundSection) throws OciException {
        if (outboundSection.getField("OCI_VERSION").startsWith("2.")) {
            checkProductFor20Conform(sapproduct);
        } else if (outboundSection.getField("OCI_VERSION").startsWith("3.")) {
            checkProductFor30Conform(sapproduct);
        } else if (outboundSection.getField("OCI_VERSION").startsWith("4.")) {
            checkProductFor40Conform(sapproduct);
        } else {
            throw new OciException("Found OCI Version " + outboundSection.getField("OCI_VERSION") + ". Supporting only 2.x 3.x or 4.x. Aborting.", 6);
        }

        final StringBuilder stringBuilder = new StringBuilder("<input type=\"hidden\" name=\"NEW_ITEM-DESCRIPTION[").append(index).append("]\" value=\"");
        stringBuilder.append(encodeHTML(sapproduct.getItemDescription())).append("\">\n");

        stringBuilder.append("<input type=\"hidden\" name=\"NEW_ITEM-MATNR[").append(index).append("]\" value=\"");
        stringBuilder.append(encodeHTML(sapproduct.getItemMatNr())).append("\">\n");

        stringBuilder.append("<input type=\"hidden\" name=\"NEW_ITEM-MATGROUP[").append(index).append("]\" value=\"");
        stringBuilder.append(encodeHTML(sapproduct.getItemMatGroup())).append("\">\n");

        stringBuilder.append("<input type=\"hidden\" name=\"NEW_ITEM-QUANTITY[").append(index).append("]\" value=\"");
        stringBuilder.append(doubleToString(sapproduct.getItemQuantity())).append("\">\n");

        stringBuilder.append("<input type=\"hidden\" name=\"NEW_ITEM-UNIT[").append(index).append("]\" value=\"");
        stringBuilder.append(encodeHTML(sapproduct.getItemUnit())).append("\">\n");

        stringBuilder.append("<input type=\"hidden\" name=\"NEW_ITEM-PRICE[").append(index).append("]\" value=\"");
        stringBuilder.append(doubleToString(sapproduct.getItemPrice())).append("\">\n");

        stringBuilder.append("<input type=\"hidden\" name=\"NEW_ITEM-PRICEUNIT[").append(index).append("]\" value=\"");
        stringBuilder.append(String.valueOf(sapproduct.getItemPriceUnit())).append("\">\n");

        stringBuilder.append("<input type=\"hidden\" name=\"NEW_ITEM-CURRENCY[").append(index).append("]\" value=\"");
        stringBuilder.append(encodeHTML(sapproduct.getItemCurrency())).append("\">\n");

        stringBuilder.append("<input type=\"hidden\" name=\"NEW_ITEM-LEADTIME[").append(index).append("]\" value=\"");

        stringBuilder.append(sapproduct.getItemLeadTime() == -1 ? "" : String.valueOf(sapproduct.getItemLeadTime())).append("\">\n");

        stringBuilder.append("<input type=\"hidden\" name=\"NEW_ITEM-VENDOR[").append(index).append("]\" value=\"");
        stringBuilder.append(encodeHTML(sapproduct.getItemVendor())).append("\">\n");

        stringBuilder.append("<input type=\"hidden\" name=\"NEW_ITEM-VENDORMAT[").append(index).append("]\" value=\"");
        stringBuilder.append(encodeHTML(sapproduct.getItemVendorMat())).append("\">\n");

        stringBuilder.append("<input type=\"hidden\" name=\"NEW_ITEM-MANUFACTCODE[").append(index).append("]\" value=\"");
        stringBuilder.append(encodeHTML(sapproduct.getItemManufactCode())).append("\">\n");

        stringBuilder.append("<input type=\"hidden\" name=\"NEW_ITEM-MANUFACTMAT[").append(index).append("]\" value=\"");
        stringBuilder.append(encodeHTML(sapproduct.getItemManufactMat())).append("\">\n");

        stringBuilder.append("<input type=\"hidden\" name=\"NEW_ITEM-CONTRACT[").append(index).append("]\" value=\"");
        stringBuilder.append(encodeHTML(sapproduct.getItemContract())).append("\">\n");

        stringBuilder.append("<input type=\"hidden\" name=\"NEW_ITEM-CONTRACT_ITEM[").append(index).append("]\" value=\"");
        stringBuilder.append(encodeHTML(sapproduct.getItemContractItem())).append("\">\n");

        stringBuilder.append("<input type=\"hidden\" name=\"NEW_ITEM-SERVICE[").append(index).append("]\" value=\"");
        stringBuilder.append(sapproduct.isItemService() ? "Service" : "").append("\">\n");

        stringBuilder.append("<input type=\"hidden\" name=\"NEW_ITEM-EXT_QUOTE_ID[").append(index).append("]\" value=\"");
        stringBuilder.append(encodeHTML(sapproduct.getItemExtQuoteId())).append("\">\n");

        stringBuilder.append("<input type=\"hidden\" name=\"NEW_ITEM-EXT_QUOTE_ITEM[").append(index).append("]\" value=\"");
        stringBuilder.append(encodeHTML(sapproduct.getItemExtQuoteItem())).append("\">\n");

        stringBuilder.append("<input type=\"hidden\" name=\"NEW_ITEM-EXT_PRODUCT_ID[").append(index).append("]\" value=\"");
        stringBuilder.append(encodeHTML(sapproduct.getItemExtProductId())).append("\">\n");

        stringBuilder.append("<input type=\"hidden\" name=\"NEW_ITEM-LONGTEXT_").append(index).append(":132[]\" value=\"");
        /*
         * The encoding is customizable.
         * 
         * @see com.namics.distrelec.b2b.core.eprocurement.data.oci.DefaultDistSAPProduct#getItemLongtext()
         */
        stringBuilder.append(sapproduct.getItemLongtext()).append("\">\n");

        stringBuilder.append("<input type=\"hidden\" name=\"NEW_ITEM-ATTACHMENT[").append(index).append("]\" value=\"");
        stringBuilder.append(encodeHTML(sapproduct.getItemAttachment())).append("\">\n");

        stringBuilder.append("<input type=\"hidden\" name=\"NEW_ITEM-ATTACHMENT_TITLE[").append(index).append("]\" value=\"");
        stringBuilder.append(encodeHTML(sapproduct.getItemAttachmentTitle())).append("\">\n");

        stringBuilder.append("<input type=\"hidden\" name=\"NEW_ITEM-ATTACHMENT_PURPOSE[").append(index).append("]\" value=\"");
        stringBuilder.append(sapproduct.getItemAttachmentPurpose()).append("\">\n");

        stringBuilder.append("<input type=\"hidden\" name=\"NEW_ITEM-EXT_CATEGORY_ID[").append(index).append("]\" value=\"");
        stringBuilder.append(encodeHTML(sapproduct.getItemExtCategoryId())).append("\">\n");

        stringBuilder.append("<input type=\"hidden\" name=\"NEW_ITEM-EXT_CATEGORY[").append(index).append("]\" value=\"");
        stringBuilder.append(encodeHTML(sapproduct.getItemExtCategory())).append("\">\n");

        if (sapproduct.getCustomParameterNames() != null) {
            for (int customParameterIndex = 0; customParameterIndex < sapproduct.getCustomParameterNames().length; ++customParameterIndex) {
                final String key = sapproduct.getCustomParameterNames()[customParameterIndex];
                final String value = sapproduct.getCustomParameterValue(sapproduct.getCustomParameterNames()[customParameterIndex]);
                stringBuilder.append("<input type=\"hidden\" name=\"").append(key).append("[").append(index).append("]").append("\" value=\"")
                        .append(encodeHTML(value)).append("\">\n");
            }
        }
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    private static String encodeHTML(final String stringToEncode) {
        if (stringToEncode == null) {
            return "";
        }

        final int length = stringToEncode.length();
        final StringBuilder stringBuilder = new StringBuilder(length * 2);
        for (int i = 0; i < length; ++i) {
            final char character = stringToEncode.charAt(i);
            switch (character) {
            case '"':
                stringBuilder.append("&quot;");
                break;
            case '\'':
                stringBuilder.append("&#39;");
                break;
            case '&':
                stringBuilder.append("&amp;");
                break;
            case '<':
                stringBuilder.append("&lt;");
                break;
            case '>':
                stringBuilder.append("&gt;");
                break;
            default:
                stringBuilder.append(character);
            }

        }

        return stringBuilder.toString();
    }

    private static String wrapInXML(final String value) {
        if (StringUtils.isNotEmpty(value)) {
            return "<![CDATA[" + value.trim() + "]]>";
        }

        if (value != null) {
            return value.trim();
        }

        return "";
    }

    private static String generateXMLCodeForProduct(final DistSapProduct sapproduct) {
        final StringBuilder xmltemp = new StringBuilder();
        if (sapproduct.isItemService()) {
            xmltemp.append("\t\t<Product ProductType = \"Service\">\n");
        } else {
            xmltemp.append("\t\t<Product ProductType = \"Product\">\n");
        }

        xmltemp.append("\t\t\t<ProductID Code = \"Buyer\">").append(wrapInXML(sapproduct.getItemMatNr())).append("</ProductID>\n");
        xmltemp.append("\t\t\t<CatalogKey>").append(wrapInXML(sapproduct.getItemExtProductId())).append("</CatalogKey>\n");
        xmltemp.append("\t\t\t<ParentCategoryID>").append(wrapInXML(sapproduct.getItemMatGroup())).append("</ParentCategoryID>\n");
        xmltemp.append("\t\t\t<Description>").append(wrapInXML(sapproduct.getItemDescription())).append("</Description>\n");
        xmltemp.append("\t\t\t<Attachment>\n");
        xmltemp.append("\t\t\t\t<URL>").append(wrapInXML(sapproduct.getItemAttachment())).append("</URL>\n");
        xmltemp.append("\t\t\t\t<Description>").append(wrapInXML(sapproduct.getItemAttachmentTitle())).append("</Description>\n");
        xmltemp.append("\t\t\t\t<Purpose>").append(sapproduct.getItemAttachmentPurpose()).append("</Purpose>\n");
        xmltemp.append("\t\t\t</Attachment>\n");
        xmltemp.append("\t\t\t<ShoppingBasketItem RefVendorDescription = \"0\" RefManufacturerDescription = \"1\"> <!-- Reference to VendorDescription ID and ManufacturerDescription ID below -->\n");
        xmltemp.append("\t\t\t\t<Quantity UoM = \"").append(sapproduct.getItemUnit()).append("\">").append(doubleToString(sapproduct.getItemQuantity()))
                .append("</Quantity>\n");
        xmltemp.append("\t\t\t\t<NetPrice>\n");
        xmltemp.append("\t\t\t\t\t<Price Currency = \"").append(sapproduct.getItemCurrency()).append("\">").append(doubleToString(sapproduct.getItemPrice()))
                .append("</Price>\n");
        xmltemp.append("\t\t\t\t\t<PriceUnit>").append(String.valueOf(sapproduct.getItemPriceUnit())).append("</PriceUnit>\n");
        xmltemp.append("\t\t\t\t</NetPrice>\n");
        xmltemp.append("\t\t\t\t<LeadTime>").append((sapproduct.getItemLeadTime() == -1) ? "" : String.valueOf(sapproduct.getItemLeadTime()))
                .append("</LeadTime>\n");
        xmltemp.append("\t\t\t\t<Quote>\n");
        xmltemp.append("\t\t\t\t\t<QuoteID>").append(wrapInXML(sapproduct.getItemExtQuoteId())).append("</QuoteID>\n");
        xmltemp.append("\t\t\t\t\t<QuoteItemID>").append(wrapInXML(sapproduct.getItemExtQuoteItem())).append("</QuoteItemID>\n");
        xmltemp.append("\t\t\t\t</Quote>\n");
        xmltemp.append("\t\t\t\t<ItemText>").append(wrapInXML(sapproduct.getItemLongtext())).append("</ItemText>\n");
        xmltemp.append("\t\t\t</ShoppingBasketItem>\n");
        xmltemp.append("\t\t\t<ManufacturerDescription ID = \"1\">\n");
        xmltemp.append("\t\t\t\t<PartnerProductID Code = \"Other\">").append(wrapInXML(sapproduct.getItemManufactMat())).append("</PartnerProductID>\n");
        xmltemp.append("\t\t\t\t<PartnerID Code = \"Other\">").append(wrapInXML(sapproduct.getItemManufactCode())).append("</PartnerID>\n");
        xmltemp.append("\t\t\t</ManufacturerDescription>\n");
        xmltemp.append("\t\t\t<VendorDescription ID = \"0\">\n");
        xmltemp.append("\t\t\t\t<PartnerProductID Code = \"Other\">").append(wrapInXML(sapproduct.getItemVendorMat())).append("</PartnerProductID>\n");
        xmltemp.append("\t\t\t\t<PartnerID Code = \"Other\">").append(wrapInXML(sapproduct.getItemVendor())).append("</PartnerID>\n");
        xmltemp.append("\t\t\t\t<BuyerContract>\n");
        xmltemp.append("\t\t\t\t\t<ContractID>").append(wrapInXML(sapproduct.getItemContract())).append("</ContractID>\n");
        xmltemp.append("\t\t\t\t\t<ContractItemID>").append(wrapInXML(sapproduct.getItemContractItem())).append("</ContractItemID>\n");
        xmltemp.append("\t\t\t\t</BuyerContract>\n");
        xmltemp.append("\t\t\t</VendorDescription>\n");
        if (sapproduct.getCustomParameterNames() != null) {
            for (int index2 = 0; index2 < sapproduct.getCustomParameterNames().length; ++index2) {
                xmltemp.append("\t\t\t<").append(sapproduct.getCustomParameterNames()[index2]).append(">")
                        .append(wrapInXML(sapproduct.getCustomParameterValue(sapproduct.getCustomParameterNames()[index2]))).append("</")
                        .append(sapproduct.getCustomParameterNames()[index2]).append(">\n");
            }
        }
        xmltemp.append("\t\t</Product>\n");
        return xmltemp.toString();
    }

    private static void checkCartFor40Conform(final DistSapProductList sapproductlist) throws OciException {
        for (int index = 0; index < sapproductlist.size(); ++index) {
            checkProductFor40Conform(sapproductlist.getProduct(index));
        }
    }

    private static void checkCartFor30Conform(final DistSapProductList sapproductlist) throws OciException {
        for (int index = 0; index < sapproductlist.size(); ++index) {
            checkProductFor30Conform(sapproductlist.getProduct(index));
        }
    }

    private static void checkCartFor20Conform(final DistSapProductList sapproductlist) throws OciException {
        for (int index = 0; index < sapproductlist.size(); ++index) {
            checkProductFor20Conform(sapproductlist.getProduct(index));
        }
    }

    private static void checkProductFor40Conform(final DistSapProduct sapproduct) throws OciException {
        if (StringUtils.isEmpty(sapproduct.getItemDescription()) && StringUtils.isEmpty(sapproduct.getItemMatNr())) {
            throw new OciException(
                    "Either NEW_ITEM-DESCRIPTION[n] or NEW_ITEM_MATNR[n] must be filled. Only one of the two should be filled. Both fields are null or empty.",
                    6);
        }

        if (StringUtils.isEmpty(sapproduct.getItemMatNr()) && StringUtils.isEmpty(sapproduct.getItemUnit())) {
            throw new OciException("NEW_ITEM-UNIT[n] is required and must been filled if NEW-ITEM-MATNR[n] is empty or null.", 6);
        }

        if (StringUtils.isEmpty(sapproduct.getItemCurrency())) {
            throw new OciException("NEW_ITEM-CURRENCY[n] is required. (because NEW_ITEM-PRICE[n] != null)", 6);
        }

        if (StringUtils.isNotEmpty(sapproduct.getItemExtQuoteItem()) && StringUtils.isEmpty(sapproduct.getItemExtQuoteId())) {
            throw new OciException("NEW_ITEM-EXT_QUOTE_ITEM[n] is filled, so NEW_ITEM-EXT_QUOTE_ITEM_ID[n] is required. ", 6);
        }

        if (StringUtils.isEmpty(sapproduct.getItemContractItem()) || StringUtils.isNotEmpty(sapproduct.getItemContract())) {
            return;
        }
        throw new OciException("NEW_ITEM-CONTRACT_ITEM[n] is filled, so NEW_ITEM-CONTRACT[n] is required. ", 6);
    }

    private static void checkProductFor30Conform(final DistSapProduct sapproduct) throws OciException {
        if (StringUtils.isEmpty(sapproduct.getItemDescription()) && StringUtils.isEmpty(sapproduct.getItemMatNr())) {
            throw new OciException(
                    "Either NEW_ITEM-DESCRIPTION[n] or NEW_ITEM_MATNR[n] must be filled. One of the two should at least be filled. Both fields are null or empty.",
                    6);
        }

        if (StringUtils.isEmpty(sapproduct.getItemMatNr()) && StringUtils.isEmpty(sapproduct.getItemUnit())) {
            throw new OciException("NEW_ITEM-UNIT[n] is required if NEW-ITEM-MATNR[n] has not been filled. ", 6);
        }

        if (StringUtils.isNotEmpty(sapproduct.getItemCurrency())) {
            return;
        }
        throw new OciException("NEW_ITEM-PRICE[n] is filled (it is not null), so NEW_ITEM-CURRENCY[n] is required. ", 6);
    }

    private static void checkProductFor20Conform(final DistSapProduct sapproduct) throws OciException {
        checkProductFor30Conform(sapproduct);
    }

    private static String doubleToString(final double number) {
        return getNumberFormat().format(CoreAlgorithms.round(number, 3));
    }

    private static DecimalFormat getNumberFormat() {
        if (decimalFormat == null) {
            decimalFormat = new DecimalFormat();
            final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.GERMANY);
            symbols.setDecimalSeparator('.');
            decimalFormat.setDecimalFormatSymbols(symbols);
            decimalFormat.applyPattern("##########0.000");
            decimalFormat.setDecimalSeparatorAlwaysShown(true);
        }

        return decimalFormat;
    }

}
