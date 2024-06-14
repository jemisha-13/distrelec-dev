/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.eprocurement.service.cxml.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.namics.distrelec.b2b.core.eprocurement.data.ariba.DistAribaDataFactory;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.util.DistUtils;
import com.namics.distrelec.b2b.cxml.generated.CXML;
import com.namics.distrelec.b2b.cxml.generated.Classification;
import com.namics.distrelec.b2b.cxml.generated.Credential;
import com.namics.distrelec.b2b.cxml.generated.Description;
import com.namics.distrelec.b2b.cxml.generated.Header;
import com.namics.distrelec.b2b.cxml.generated.ItemDetail;
import com.namics.distrelec.b2b.cxml.generated.ItemIn;
import com.namics.distrelec.b2b.cxml.generated.PunchOutOrderMessage;
import com.namics.distrelec.b2b.cxml.generated.PunchOutOrderMessageHeader;
import com.namics.distrelec.b2b.cxml.generated.Tax;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.TaxValue;

public class CxmlUtil {

    private static final String USER_AGENT = "ELFA cXML Parser v1.1";
    private static final String MESSAGE_HEADER_METHOD = "create";
    private static final String DOCTYPE = "<!DOCTYPE cXML SYSTEM 'http://xml.cXML.org/schemas/cXML/1.2.007/cXML.dtd' >";

    private static final String DEFAULT_EMPTY = "UNSPECIFIED";

    private static final String FIELD_BUYERCOOKIE = "buyercookie";
    private static final String FIELD_PROSUPPLIERID = "prosupplierid";
    private static final String FIELD_PROSUPPLIERNAME = "prosuppliername";

    private static final String CXML_FIELD_VAT = "VATNO";
    private static final String CXML_FIELD_NAME = "NAME";
    private static final String CXML_FIELD_UNSPSC = "UNSPSC";

    public static final String KEY_CUSTOMER_NAME = "customer_name";
    public static final String KEY_CUSTOMER_VAT = "customer_vat";
    public static final String KEY_SHOP_VAT = "shop_vat";

    private static final String CFG_SHOP_VAT = "cxml.shop.vat";

    private static final Logger LOG = Logger.getLogger(CxmlUtil.class);

    private final static DistAribaDataFactory dataFactory = new DistAribaDataFactory();

    public static String generateCXMLData(CartModel cart, CxmlOutboundSection outboundSection) {

        //
        // userDate.put(CxmlUtil.KEY_CUSTOMER_NAME, unit.getDisplayName().toString());
        // userDate.put(CxmlUtil.KEY_CUSTOMER_VAT, unit.getVatID());
        // userDate.put(CxmlUtil.KEY_SHOP_VAT, shopVat);

        if (cart != null && CollectionUtils.isNotEmpty(cart.getEntries())) {

            ClassificationService classificationService = (ClassificationService) Registry.getApplicationContext().getBean("classificationService");
            SiteConfigService siteConfigService = (SiteConfigService) Registry.getApplicationContext().getBean("siteConfigService");
            UserService userService = (UserService) Registry.getApplicationContext().getBean("userService");

            B2BCustomerModel customer = (B2BCustomerModel) userService.getCurrentUser();
            B2BUnitModel unit = customer.getDefaultB2BUnit();

            String shopVat = siteConfigService.getProperty(CFG_SHOP_VAT);

            final CXML cXmlPunchOutOrderMessage = dataFactory.createPunchOutOrderMessage();
            // cXmlPunchOutOrderMessage.setPayloadID(cXmlPunchOutSetupRequest.getPayloadID());
            cXmlPunchOutOrderMessage.setTimestamp(DistUtils.getISO8601DateTime());

            final Header headerPunchOutOrderMessage = dataFactory.getHeader(cXmlPunchOutOrderMessage);
            final PunchOutOrderMessage punchOutOrderMessage = dataFactory.getPunchOutOrderMessage(cXmlPunchOutOrderMessage);

            // set header
            definePunchOutOrderHeader(headerPunchOutOrderMessage, outboundSection, unit, shopVat);

            // set punchOutOrderMessage
            String buyercookie = StringUtils.defaultIfBlank(outboundSection.getField(FIELD_BUYERCOOKIE), DEFAULT_EMPTY);
            punchOutOrderMessage.getBuyerCookie().getContent().add(buyercookie);

            // set punchOutOrderMessageHeader
            definePunchOutOrderMessageHeader(punchOutOrderMessage.getPunchOutOrderMessageHeader(), outboundSection, cart);

            // set ItemIn
            definePunchOutOrderMessageItemIn(punchOutOrderMessage, outboundSection, cart, classificationService);

            // write cXML based on the collected information
            final String punchOutOrderMessageXml = writeOrderMessage(cXmlPunchOutOrderMessage);

            return punchOutOrderMessageXml;

        }
        return "";
    }

    private static void definePunchOutOrderHeader(Header headerPunchOutOrderMessage, CxmlOutboundSection outboundSection, B2BUnitModel company, String shopVat) {

        // create from
        String prosuppliername = StringUtils.defaultIfEmpty(outboundSection.getField(FIELD_PROSUPPLIERNAME), DEFAULT_EMPTY);
        Credential credentialName = dataFactory.createCredential(FIELD_PROSUPPLIERNAME.toUpperCase(), prosuppliername);
        headerPunchOutOrderMessage.getFrom().getCredential().add(credentialName);

        String prosupplierid = StringUtils.defaultIfEmpty(outboundSection.getField(FIELD_PROSUPPLIERID), DEFAULT_EMPTY);
        Credential credentialId = dataFactory.createCredential(FIELD_PROSUPPLIERID.toUpperCase(), prosupplierid);
        headerPunchOutOrderMessage.getFrom().getCredential().add(credentialId);

        // create to
        Credential credentialToVat = dataFactory.createCredential(CXML_FIELD_VAT, company.getVatID());
        headerPunchOutOrderMessage.getTo().getCredential().add(credentialToVat);

        if (null != company.getDisplayName()) {
            Credential credentialToName = dataFactory.createCredential(CXML_FIELD_NAME, company.getDisplayName().toString());
            headerPunchOutOrderMessage.getTo().getCredential().add(credentialToName);
        }

        // create sender
        Credential shopVAT = dataFactory.createCredential(CXML_FIELD_VAT, shopVat);
        headerPunchOutOrderMessage.getSender().getCredential().add(shopVAT);
        headerPunchOutOrderMessage.getSender().setUserAgent(USER_AGENT);

    }

    private static void definePunchOutOrderMessageHeader(PunchOutOrderMessageHeader punchOutOrderMessageHeader, CxmlOutboundSection outboundSection,
            CartModel cart) {

        // set method
        punchOutOrderMessageHeader.setOperationAllowed(MESSAGE_HEADER_METHOD);

        // create Tax element if value is greater then 0.0 with tax information from cart
        if (Double.compare(cart.getTotalTax().doubleValue(), 0) > 0) {
            punchOutOrderMessageHeader.setTax(dataFactory.createTax());
            punchOutOrderMessageHeader.getTax().getMoney().setCurrency(cart.getCurrency().getIsocode());
            punchOutOrderMessageHeader.getTax().getMoney().setvalue(String.valueOf(cart.getTotalTax()));
        }

        // create Total element with total information from cart
        punchOutOrderMessageHeader.getTotal().getMoney().setCurrency(cart.getCurrency().getIsocode());
        punchOutOrderMessageHeader.getTotal().getMoney().setvalue(String.valueOf(cart.getTotalPrice()));

    }

    private static void definePunchOutOrderMessageItemIn(PunchOutOrderMessage punchOutOrderMessage, CxmlOutboundSection outboundSection, CartModel cart,
            ClassificationService classificationService) {

        String language = getCurrentCartLanguage(cart);
        // create ItemIn element for all entries in cart
        for (final AbstractOrderEntryModel entry : cart.getEntries()) {
            final ItemIn itemIn = dataFactory.createItemIn();
            itemIn.setQuantity(String.valueOf(entry.getQuantity()));

            // create itemID
            itemIn.getItemID().setSupplierPartID(entry.getProduct().getCode());

            ItemDetail itemDetail = itemIn.getItemDetail();

            // create itemDetail
            itemDetail.getUnitPrice().getMoney().setCurrency(cart.getCurrency().getIsocode());
            itemDetail.getUnitPrice().getMoney().setvalue(String.valueOf(entry.getBasePrice()));

            Collection<TaxValue> taxValues = entry.getTaxValues();
            if (taxValues != null && !taxValues.isEmpty()) {
                TaxValue taxValue = taxValues.iterator().next();

                itemIn.setTax(dataFactory.createTax());
                Tax tax = itemIn.getTax();

                tax.getMoney().setCurrency(taxValue.getCurrencyIsoCode());
                tax.getMoney().setvalue(String.valueOf(taxValue.getAppliedValue()));

                final Description description = dataFactory.createDescription(language, "VAT");
                tax.setDescription(description);

                if (!taxValue.isAbsolute()) {
                    // TODO: add tax details if needed
                }

            }

            itemDetail.setUnitOfMeasure(entry.getUnit().getCode());
            final Description description = dataFactory.createDescription(language, entry.getProduct().getName());
            itemDetail.getDescription().add(description);

            addItemDetailClassification(itemIn.getItemDetail(), entry.getProduct(), classificationService);

            addItemDetailManufacturer(itemIn.getItemDetail(), entry.getProduct().getManufacturer(), language);

            punchOutOrderMessage.getItemIn().add(itemIn);
        }

    }

    private static void addItemDetailClassification(ItemDetail itemDetail, ProductModel product, ClassificationService classificationService) {
        FeatureList features = classificationService.getFeatures(product);
        Feature unspsc = features.getFeatureByCode(CXML_FIELD_UNSPSC);
        if (null != unspsc && null != unspsc.getValue()) {
            final Classification classification = dataFactory.createClassification(CXML_FIELD_UNSPSC, unspsc.getValue().toString());
            itemDetail.getClassification().add(classification);
        }

    }

    private static void addItemDetailManufacturer(final ItemDetail itemDetail, final DistManufacturerModel manufacturer, String language) {
        if (manufacturer != null) {
            itemDetail.setManufacturerName(dataFactory.createManufacturerName(language, manufacturer.getName()));
            itemDetail.setManufacturerPartID(manufacturer.getCode());
        }
    }

    protected String expandXmlString(final File xmlFile) {
        try {
            final FileInputStream fileInputStream = new FileInputStream(xmlFile);
            final StringBuilder expandedXmlString = new StringBuilder();
            expandedXmlString.append("<!DOCTYPE cXML SYSTEM \"http://xml.cxml.org/schemas/cXML/1.1.010/cXML.dtd\">");
            expandedXmlString.append(IOUtils.toString(fileInputStream, "utf-8"));
            return expandedXmlString.toString();
        } catch (FileNotFoundException fnfe) {
            LOG.error("An execpetion occured while expanding the XML file content", fnfe);
        } catch (IOException ioe) {
            LOG.error("An execpetion occured while expanding the XML file content", ioe);
        }
        return "";
    }

    private static String writeOrderMessage(final CXML cXmlPunchOutOrderMessage) {
        String message = "";
        try {
            // create JAXB context and initializing Marshaller
            final JAXBContext jaxbContext = JAXBContext.newInstance(CXML.class);
            final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            // marshal DTO and return XML string

            ByteArrayOutputStream output = new ByteArrayOutputStream();

            jaxbMarshaller.marshal(cXmlPunchOutOrderMessage, output);

            final String orderMessageXml = DOCTYPE + "\n" + output.toString();

            message = Base64.encodeBase64String(orderMessageXml.getBytes());
        } catch (JAXBException e) {
            LOG.error("An exception occured while writing the XML response", e);
        }
        return message;
    }

    private static String getCurrentCartLanguage(CartModel cart) {
        String language = "";

        if (cart.getLanguage() != null) {
            language = cart.getLanguage().getIsocode();
        }

        return language;
    }

}
