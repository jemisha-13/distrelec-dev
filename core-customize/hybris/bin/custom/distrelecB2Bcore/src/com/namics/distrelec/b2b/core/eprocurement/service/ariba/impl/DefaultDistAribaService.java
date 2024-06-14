/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.eprocurement.service.ariba.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.util.Assert;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.enums.DistFieldLocation;
import com.namics.distrelec.b2b.core.eprocurement.data.ariba.DistAribaDataFactory;
import com.namics.distrelec.b2b.core.eprocurement.service.DistEProcurementCustomerConfigService;
import com.namics.distrelec.b2b.core.eprocurement.service.ariba.DistAribaService;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.model.eprocurement.AribaCartModel;
import com.namics.distrelec.b2b.core.model.eprocurement.DistFieldConfigModel;
import com.namics.distrelec.b2b.core.service.order.DistCommerceCartService;
import com.namics.distrelec.b2b.core.util.DistUtils;
import com.namics.distrelec.b2b.cxml.generated.CXML;
import com.namics.distrelec.b2b.cxml.generated.Classification;
import com.namics.distrelec.b2b.cxml.generated.Credential;
import com.namics.distrelec.b2b.cxml.generated.Description;
import com.namics.distrelec.b2b.cxml.generated.Extrinsic;
import com.namics.distrelec.b2b.cxml.generated.Header;
import com.namics.distrelec.b2b.cxml.generated.ItemDetail;
import com.namics.distrelec.b2b.cxml.generated.ItemIn;
import com.namics.distrelec.b2b.cxml.generated.ItemOut;
import com.namics.distrelec.b2b.cxml.generated.PostalAddress;
import com.namics.distrelec.b2b.cxml.generated.PunchOutOrderMessage;
import com.namics.distrelec.b2b.cxml.generated.PunchOutOrderMessageHeader;
import com.namics.distrelec.b2b.cxml.generated.PunchOutSetupRequest;
import com.namics.distrelec.b2b.cxml.generated.PunchOutSetupResponse;
import com.namics.distrelec.b2b.cxml.generated.Response;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.security.SecureToken;
import de.hybris.platform.commerceservices.security.SecureTokenService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartFactory;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;
import de.hybris.platform.servicelayer.user.AddressService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.localization.Localization;

/**
 * Default implementation for <code>DistAribaService</code>.
 * 
 * @author pbueschi, Namics AG
 * @since Distrelec 1.0
 */
public class DefaultDistAribaService extends AbstractBusinessService implements DistAribaService {

    private static final Logger LOG = Logger.getLogger(DefaultDistAribaService.class);

    @Autowired
    private CommonI18NService commonI18NService;

    @Autowired
    private UserService userService;

    @Autowired
    private SecureTokenService secureTokenService;

    @Autowired
    private CartService cartService;

    @Autowired
    private DistCommerceCartService commerceCartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    @Qualifier("distEProcurementCustomerConfigService")
    private DistEProcurementCustomerConfigService customerConfigService;

    @Autowired
    @Qualifier("distAribaCartFactory")
    private CartFactory distAribaCartFactory;

    private DistAribaDataFactory dataFactory;

    private long tokenValidity;

    // to add dummy articled which should be ignored when coming from Ariba
    private final static Set<String> dummyArticles = new HashSet<String>();

    private final static String DG_PROFILE = "DG_PROFILE";

    static {
        dummyArticles.add("AAA");
        dummyArticles.add("dummy");
    }

    @Override
    public boolean isAribaCustomer() {
        final UserModel currentUser = getUserService().getCurrentUser();
        final UserGroupModel userGroup = getUserService().getUserGroupForUID(DistConstants.User.ARIBACUSTOMERGROUP_UID);
        if (currentUser == null || userGroup == null) {
            return false;
        }
        return getUserService().isMemberOfGroup(currentUser, userGroup);
    }

    /*
     * (non-Javadoc)
     * @see com.namics.distrelec.b2b.core.eprocurement.service.ariba.DistAribaService#parseAribaSetupRequest()
     */
    @Override
    public CXML parseAribaSetupRequest() {
        // DISTRELEC-14147
        // always take setup-request from the customer not from the session, otherwise reccuring sessions or edit will take obsolate data from the session

        String aribaSetupRequest = ((B2BCustomerModel) getUserService().getCurrentUser()).getAribaSetupRequest();
        getSessionService().setAttribute(DistConstants.Ariba.Session.ARIBA_CUSTOMER_SETUP_REQUEST, aribaSetupRequest);
        return parseAribaSetupRequest(aribaSetupRequest);
    }

    /*
     * (non-Javadoc)
     * @see com.namics.distrelec.b2b.core.eprocurement.service.ariba.DistAribaService#parseAribaSetupRequest(java.lang.String)
     */
    @Override
    public CXML parseAribaSetupRequest(final String aribaSetupRequest) {
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(CXML.class);

            XMLInputFactory xif = XMLInputFactory.newFactory();
            xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
            XMLStreamReader xsr = xif.createXMLStreamReader(new ByteArrayInputStream(aribaSetupRequest.getBytes()));

            final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (CXML) jaxbUnmarshaller.unmarshal(xsr);
        } catch (final JAXBException | XMLStreamException e) {
            LOG.error("PunchOutSetupRequest could not be parsed", e);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see com.namics.distrelec.b2b.core.eprocurement.service.ariba.DistAribaService#getAribaToken(java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    public String getAribaToken(final String userId, final String password, final String setupRequest) {
        final B2BCustomerModel customer = getUserService().getUserForUID(userId.toLowerCase(), B2BCustomerModel.class);
        final SecureToken data = new SecureToken(customer.getUid(), new Date().getTime());
        final String token = getSecureTokenService().encryptData(data);
        customer.setAribaCustomerToken(token);
        // We store the setup request in the session.
        getSessionService().setAttribute(DistConstants.Ariba.Session.ARIBA_CUSTOMER_SETUP_REQUEST, setupRequest);
        customer.setAribaSetupRequest(setupRequest);
        getModelService().save(customer);
        return token;
    }

    @Override
    public UserModel aribaLogin(final String token) {
        final UserModel user = checkCredentials(token);
        getUserService().setCurrentUser(user);
        return user;
    }

    @Override
    public UserModel checkCredentials(final String token) {
        Assert.notNull(token, "Token cannot be null.");

        final SecureToken data = getSecureTokenService().decryptData(token);
        final B2BCustomerModel customer = getUserService().getUserForUID(data.getData(), B2BCustomerModel.class);
        if (!getUserService().isMemberOfGroup(customer, getUserService().getUserGroupForUID(DistConstants.User.ARIBACUSTOMERGROUP_UID))) {
            throw new DisabledException("User is not member of 'aribaCustomerGroup' and can therefore not login as ariba login.");
        } else if (token.equals(customer.getAribaCustomerToken())) {
            customer.setAribaCustomerToken(null);
            getModelService().save(customer);
            final long delta = new Date().getTime() - data.getTimeStamp();
            if (delta / 1000 < tokenValidity) {
                return customer;
            } else {
                throw new CredentialsExpiredException("Your token is expired.");
            }
        } else {
            throw new BadCredentialsException("Given token is wrong");
        }
    }

    @Override
    public boolean isCustomFooterEnabled() {
        return isAribaCustomer() && getCustomerConfigService().isCustomFooter();
    }

    @Override
    public void logout() {
        getSessionService().closeCurrentSession();
    }

    @Override
    public void parseAribaSetupResponse(final HttpStatus httpStatus, final String url, final String payloadId, final Writer writer) {
        final CXML punchOutSetupResponse = getDataFactory().createPunchOutSetupResponse();
        punchOutSetupResponse.setPayloadID(payloadId);
        punchOutSetupResponse.setTimestamp(DistUtils.getISO8601DateTime());
        ((Response) punchOutSetupResponse.getHeaderOrMessageOrRequestOrResponse().get(0)).getStatus().setCode(String.valueOf(httpStatus.value()));
        ((Response) punchOutSetupResponse.getHeaderOrMessageOrRequestOrResponse().get(0)).getStatus().setText(httpStatus.getReasonPhrase());
        ((PunchOutSetupResponse) ((Response) punchOutSetupResponse.getHeaderOrMessageOrRequestOrResponse().get(0))
                                                                                                                  .getProfileResponseOrPunchOutSetupResponseOrGetPendingResponseOrSubscriptionListResponseOrSubscriptionContentResponseOrSupplierListResponseOrSupplierDataResponse()
                                                                                                                  .get(0)).getStartPage().getURL()
                                                                                                                          .setvalue(url);
        writeSetupResponse(punchOutSetupResponse, writer);
    }

    protected void writeSetupResponse(final CXML responseData, final Writer writer) {
        try {
            // create JAXB context and initializing Marshaller
            final JAXBContext jaxbContext = JAXBContext.newInstance(CXML.class);
            final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            // for getting nice formatted output
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            // marshal DTO and write as XML
            final File xmlFile = new File("PunchOutSetupResponse.xml");
            jaxbMarshaller.marshal(responseData, xmlFile);
            writer.write(expandXmlString(xmlFile));
        } catch (JAXBException jaxbe) {
            LOG.error("An exception occured while writing the XML response", jaxbe);
        } catch (IOException ioe) {
            LOG.error("An exception occured while writing the XML response", ioe);
        }
    }

    @Override
    public Map<String, String> getAribaSetupRequestParameters(final CXML cXmlPunchOutSetupRequest) {
        final Map<String, String> setupRequestParams = new HashMap<String, String>();
        final PunchOutSetupRequest punchOutSetupRequest = getDataFactory().getPunchOutSetupRequest(cXmlPunchOutSetupRequest);
        if (punchOutSetupRequest != null) {
            setupRequestParams.put(DistConstants.Ariba.SetupRequestParams.EDIT_CART,
                                   String.valueOf(!"inspect".equalsIgnoreCase(punchOutSetupRequest.getOperation())));

            setupRequestParams.put(DistConstants.Ariba.SetupRequestParams.BUYER_COOKIE, "");
            if (punchOutSetupRequest.getBuyerCookie() != null) {
                setupRequestParams.put(DistConstants.Ariba.SetupRequestParams.BUYER_COOKIE, getContent(punchOutSetupRequest.getBuyerCookie().getContent()));
            }

            setupRequestParams.put(DistConstants.Ariba.SetupRequestParams.BROWSER_FORM_POST, "");
            if (punchOutSetupRequest.getBrowserFormPost() != null && punchOutSetupRequest.getBrowserFormPost().getURL() != null) {
                setupRequestParams.put(DistConstants.Ariba.SetupRequestParams.BROWSER_FORM_POST, punchOutSetupRequest.getBrowserFormPost().getURL().getvalue());
            }

            setupRequestParams.put(DistConstants.Ariba.SetupRequestParams.PRODUCT_CODE, "");
            if (punchOutSetupRequest.getSelectedItem() != null && punchOutSetupRequest.getSelectedItem().getItemID() != null) {

                String supplierPartID = punchOutSetupRequest.getSelectedItem().getItemID().getSupplierPartID();

                // ignore dummy articles
                if (!dummyArticles.contains(supplierPartID)) {
                    setupRequestParams.put(DistConstants.Ariba.SetupRequestParams.PRODUCT_CODE, supplierPartID);
                }
            }

            setupRequestParams.put(DistConstants.Ariba.SetupRequestParams.CART_CODE, "");
            final List<Extrinsic> extrinsics = punchOutSetupRequest.getExtrinsic();
            if (CollectionUtils.isNotEmpty(extrinsics)) {
                for (final Extrinsic extrinsic : extrinsics) {
                    if (DistConstants.Ariba.SetupRequestParams.CART_CODE.equals(extrinsic.getName())) {
                        setupRequestParams.put(DistConstants.Ariba.SetupRequestParams.CART_CODE, getContent(extrinsic.getContent()));
                    }
                    // DISTRELEC-6049 Allow customer to send language setup
                    else if (DistConstants.Ariba.SetupRequestParams.LANG_CODE.equals(extrinsic.getName())) {
                        setupRequestParams.put(DistConstants.Ariba.SetupRequestParams.LANG_CODE, getContent(extrinsic.getContent()));
                    }
                }
            }
        }
        return setupRequestParams;
    }

    private String getContent(final List<Object> contents) {
        if (contents != null) {
            if (CollectionUtils.isNotEmpty(contents)) {
                return contents.get(0).toString();
            }
        }
        return "";
    }

    @Override
    public String setUpAribaCart() {
        final AribaCartModel aribaCartModel = (AribaCartModel) getDistAribaCartFactory().createCart();
        getCartService().setSessionCart(aribaCartModel);
        return aribaCartModel.getCode();
    }

    @Override
    public boolean haveItemOutData(CXML cXmlPunchOutSetupRequest) {
        final PunchOutSetupRequest punchOutSetupRequest = getDataFactory().getPunchOutSetupRequest(cXmlPunchOutSetupRequest);
        if (punchOutSetupRequest != null && CollectionUtils.isNotEmpty(punchOutSetupRequest.getItemOut())) {
            return true;
        }
        return false;
    }

    @Override
    public String setUpAribaCart(final CXML cXmlPunchOutSetupRequest) {
        final PunchOutSetupRequest punchOutSetupRequest = getDataFactory().getPunchOutSetupRequest(cXmlPunchOutSetupRequest);
        if (punchOutSetupRequest != null && CollectionUtils.isNotEmpty(punchOutSetupRequest.getItemOut())) {
            final AribaCartModel aribaCartModel = (AribaCartModel) getDistAribaCartFactory().createCart();
            for (final ItemOut itemOut : punchOutSetupRequest.getItemOut()) {
                final long quantity = (long) NumberUtils.toDouble(itemOut.getQuantity());
                final String code = String.valueOf(itemOut.getItemID().getSupplierPartID());
                try {
                    final ProductModel product = getProductService().getProductForCode(code);
                    getCommerceCartService().addToCart(aribaCartModel, product, quantity, product.getUnit(), false, true, true, null, true);
                } catch (CommerceCartModificationException e) {
                    LOG.error("Ariba Cart could not be updated", e);
                } catch (UnknownIdentifierException uie) {
                    LOG.error("Ariba Cart setup error, product not found", uie);
                }
            }
            writeAddressModel(punchOutSetupRequest, aribaCartModel);
            getCartService().setSessionCart(aribaCartModel);
            return aribaCartModel.getCode();
        }
        // create empty Ariba cart
        return setUpAribaCart();
    }

    protected void writeAddressModel(final PunchOutSetupRequest punchOutSetupRequest, final AribaCartModel aribaCart) {
        if (punchOutSetupRequest.getShipTo() != null) {
            final String name = punchOutSetupRequest.getShipTo().getAddress().getName().getvalue();
            final PostalAddress postalAddress = punchOutSetupRequest.getShipTo().getAddress().getPostalAddress();
            final AddressModel address = getAddressService().createAddressForUser(aribaCart.getUser());
            if (StringUtils.isNotBlank(name)) {
                if (StringUtils.contains(name, " ")) {
                    address.setFirstname(name.split(" ")[0]);
                    address.setLastname(name.split(" ")[1]);
                }
            }
            address.setTown(postalAddress.getCity());
            if (postalAddress.getCountry() != null) {
                address.setCountry(getDeliveryService().getCountryForCode(postalAddress.getCountry().getIsoCountryCode()));
            }
            address.setPostalcode(String.valueOf(postalAddress.getPostalCode()));
            address.setStreetname(postalAddress.getStreet().get(0).getvalue());
            address.setCompany(postalAddress.getDeliverTo().get(0).getvalue());
            aribaCart.setDeliveryAddress(address);
            getModelService().save(aribaCart);
        }
    }

    @Override
    public String parseAribaOrderMessage() {
        final CartModel cart = getCartService().getSessionCart();
        if (cart != null && CollectionUtils.isNotEmpty(cart.getEntries())) {
            final CXML cXmlPunchOutSetupRequest = parseAribaSetupRequest();
            final Map<String, String> setupRequestParams = getAribaSetupRequestParameters(cXmlPunchOutSetupRequest);

            final Header headerPunchOutSetupRequest = getDataFactory().getHeader(cXmlPunchOutSetupRequest);
            final PunchOutSetupRequest punchOutSetupRequest = getDataFactory().getPunchOutSetupRequest(cXmlPunchOutSetupRequest);

            final CXML cXmlPunchOutOrderMessage = getDataFactory().createPunchOutOrderMessage();
            cXmlPunchOutOrderMessage.setPayloadID(cXmlPunchOutSetupRequest.getPayloadID());
            cXmlPunchOutOrderMessage.setTimestamp(DistUtils.getISO8601DateTime());

            final Header headerPunchOutOrderMessage = getDataFactory().getHeader(cXmlPunchOutOrderMessage);
            final PunchOutOrderMessage punchOutOrderMessage = getDataFactory().getPunchOutOrderMessage(cXmlPunchOutOrderMessage);

            // set header
            definePunchOutOrderHeader(headerPunchOutOrderMessage, headerPunchOutSetupRequest);

            // set punchOutOrderMessage
            punchOutOrderMessage.getBuyerCookie().getContent().add(setupRequestParams.get(DistConstants.Ariba.SetupRequestParams.BUYER_COOKIE));

            // set punchOutOrderMessageHeader
            definePunchOutOrderMessageHeader(punchOutOrderMessage.getPunchOutOrderMessageHeader(), punchOutSetupRequest, cart);

            // set ItemIn
            definePunchOutOrderMessageItemIn(punchOutOrderMessage, punchOutSetupRequest, headerPunchOutOrderMessage, cart);

            // set browser form post URL to session
            getSessionService().setAttribute(DistConstants.Ariba.Session.BROWSER_FORM_POST,
                                             setupRequestParams.get(DistConstants.Ariba.SetupRequestParams.BROWSER_FORM_POST));

            // write cXML based on the collected information
            final String punchOutOrderMessageXml = writeOrderMessage(cXmlPunchOutOrderMessage);

            // update Ariba cart
            updateAribaCart(cXmlPunchOutSetupRequest);

            return punchOutOrderMessageXml;
        }
        return "";
    }

    protected void definePunchOutOrderHeader(final Header headerPunchOutOrderMessage, final Header headerPunchOutSetupRequest) {
        // create Header element with stored information from punchOutSetupRequest
        if (headerPunchOutSetupRequest.getFrom() != null) {
            final Credential fromCredential = getDataFactory().createCredential();
            fromCredential.setDomain(headerPunchOutSetupRequest.getFrom().getCredential().get(0).getDomain());
            fromCredential.setIdentity(headerPunchOutSetupRequest.getFrom().getCredential().get(0).getIdentity());
            headerPunchOutOrderMessage.getFrom().getCredential().add(fromCredential);
        }
        if (headerPunchOutSetupRequest.getTo() != null) {
            final Credential toCredential = getDataFactory().createCredential();
            toCredential.setDomain(headerPunchOutSetupRequest.getTo().getCredential().get(0).getDomain());
            toCredential.setIdentity(headerPunchOutSetupRequest.getTo().getCredential().get(0).getIdentity());
            headerPunchOutOrderMessage.getTo().getCredential().add(toCredential);
        }
        if (headerPunchOutSetupRequest.getSender() != null) {
            final Credential senderCredential = getDataFactory().createCredential();
            senderCredential.setDomain(headerPunchOutSetupRequest.getSender().getCredential().get(0).getDomain());
            senderCredential.setIdentity(headerPunchOutSetupRequest.getSender().getCredential().get(0).getIdentity());
            headerPunchOutOrderMessage.getSender().getCredential().add(senderCredential);
            headerPunchOutOrderMessage.getSender().setUserAgent("Distrelec");
        }
    }

    protected void definePunchOutOrderMessageHeader(final PunchOutOrderMessageHeader punchOutOrderMessageHeader,
                                                    final PunchOutSetupRequest punchOutSetupRequest, final CartModel cart) {

        // set allowed operation
        // final String operation = punchOutSetupRequest.getOperation();
        punchOutOrderMessageHeader.setOperationAllowed("edit"); // DISTRELEC-6549

        // create ShipTo element with address information from cart
        if (cart.getDeliveryAddress() != null) {
            punchOutOrderMessageHeader.setShipTo(getDataFactory().createShipTo());
            punchOutOrderMessageHeader.getShipTo().getAddress().setAddressID("001");
            punchOutOrderMessageHeader.getShipTo().getAddress().setName(getDataFactory().createName(getCurrentCartLanguage(),
                                                                                                    cart.getDeliveryAddress().getFirstname() + " "
                                                                                                                              + cart.getDeliveryAddress()
                                                                                                                                    .getLastname()));
            punchOutOrderMessageHeader.getShipTo().getAddress().getPostalAddress().setCity(cart.getDeliveryAddress().getTown());
            punchOutOrderMessageHeader.getShipTo().getAddress().getPostalAddress().getCountry()
                                      .setIsoCountryCode(cart.getDeliveryAddress().getCountry().getIsocode());
            punchOutOrderMessageHeader.getShipTo().getAddress().getPostalAddress().getCountry().setvalue(cart.getDeliveryAddress().getCountry().getName());
            punchOutOrderMessageHeader.getShipTo().getAddress().getPostalAddress().getDeliverTo()
                                      .add(getDataFactory().createDeliverTo(cart.getDeliveryAddress().getCompany()));
            punchOutOrderMessageHeader.getShipTo().getAddress().getPostalAddress().setPostalCode(cart.getDeliveryAddress().getPostalcode());
            punchOutOrderMessageHeader.getShipTo().getAddress().getPostalAddress().setState("");
            punchOutOrderMessageHeader.getShipTo().getAddress().getPostalAddress().getStreet()
                                      .add(getDataFactory().createStreet(cart.getDeliveryAddress().getLine1()));
        }

        // create Tax element if value is greater then 0.0 with tax information from cart
        if (Double.compare(cart.getTotalTax().doubleValue(), 0) > 0) {
            punchOutOrderMessageHeader.setTax(getDataFactory().createTax());
            punchOutOrderMessageHeader.getTax().getMoney().setCurrency(cart.getCurrency().getIsocode());
            punchOutOrderMessageHeader.getTax().getMoney().setvalue(String.valueOf(cart.getTotalTax()));

            // set tax description
            Description taxDescription = getDataFactory().createDescription(getCurrentCartLanguage(), Localization.getLocalizedString("tax"));
            punchOutOrderMessageHeader.getTax().setDescription(taxDescription);
        }

        // create Total element with total information from cart
        punchOutOrderMessageHeader.getTotal().getMoney().setCurrency(cart.getCurrency().getIsocode());
        punchOutOrderMessageHeader.getTotal().getMoney().setvalue(String.valueOf(cart.getTotalPrice()));
    }

    protected void definePunchOutOrderMessageItemIn(final PunchOutOrderMessage punchOutOrderMessage, final PunchOutSetupRequest punchOutSetupRequest,
                                                    final Header headerPunchOutOrderMessage,
                                                    final CartModel cart) {
        // create ItemIn element for all entries in cart
        int i = 0;
        for (final AbstractOrderEntryModel entry : cart.getEntries()) {
            final ItemIn itemIn = getDataFactory().createItemIn();
            itemIn.setQuantity(String.valueOf(entry.getQuantity()));

            // create itemID
            // itemIn.getItemID().setSupplierPartID(entry.getProduct().getCode());
            itemIn.getItemID().setSupplierPartID(entry.getProduct().getCodeErpRelevant());
            itemIn.getItemID().getSupplierPartAuxiliaryID().getContent().add(entry.getProduct().getCodeErpRelevant() + "-" + i);

            // create itemDetail
            itemIn.getItemDetail().getUnitPrice().getMoney().setCurrency(cart.getCurrency().getIsocode());
            itemIn.getItemDetail().getUnitPrice().getMoney().setvalue(String.valueOf(entry.getBasePrice()));
            addItemDetailDescription(itemIn.getItemDetail(), entry.getProduct(), "");

            // set UOM static (from configuration) to AE because J&J has problems with the unit PC, PCE, ...
            String unit = getConfigurationService().getConfiguration().getString("ariba.default.unit", "EA");
            String customerIds = getConfigurationService().getConfiguration().getString("ariba.skip.default.uom.customers", "");
            List<String> uomCustomer = Arrays.asList(customerIds.split(","));
            List<Credential> fromCredentialsList = headerPunchOutOrderMessage.getFrom().getCredential().stream().collect(Collectors.toList());
            boolean customerNeedProductUoM = false;
            if (CollectionUtils.isNotEmpty(uomCustomer) && CollectionUtils.isNotEmpty(fromCredentialsList)) {
                for (Credential customerCred : fromCredentialsList) {
                    for (String customer : uomCustomer) {
                        if (null != customerCred.getIdentity() && null != customer && null != customerCred.getIdentity().getContent()
                                && customerCred.getIdentity().getContent().stream().filter(content -> content.toString().equalsIgnoreCase(customer)).findAny()
                                               .isPresent()) {
                            customerNeedProductUoM = true;
                        }
                    }

                }
            }
            if (customerNeedProductUoM && null != entry.getProduct() && null != entry.getProduct().getUnit()
                    && null != entry.getProduct().getUnit().getUNUoM()) {
                itemIn.getItemDetail().setUnitOfMeasure(entry.getProduct().getUnit().getUNUoM());
            } else {
                itemIn.getItemDetail().setUnitOfMeasure(unit);
            }

            addItemDetailClassification(itemIn.getItemDetail(), entry.getProduct(), "", "");
            addItemDetailExtrinsic(itemIn.getItemDetail(), entry.getProduct());
            addItemDetailManufacturer(itemIn.getItemDetail(), entry.getProduct().getManufacturer());

            if (CollectionUtils.isNotEmpty(punchOutSetupRequest.getItemOut())) {
                // updateAttributesForProductCode(itemIn.getItemDetail(), punchOutSetupRequest, entry.getProduct().getCode());
                updateAttributesForProductCode(itemIn.getItemDetail(), punchOutSetupRequest, entry.getProduct().getCodeErpRelevant());
            }

            punchOutOrderMessage.getItemIn().add(itemIn);
            i++;
        }
    }

    protected void updateAttributesForProductCode(final ItemDetail itemDetail, final PunchOutSetupRequest punchOutSetupRequest, final String productCode) {
        for (ItemOut itemOut : punchOutSetupRequest.getItemOut()) {
            final String supplierPartID = String.valueOf(itemOut.getItemID().getSupplierPartID());
            if (StringUtils.equals(productCode, supplierPartID)) {
                if (itemOut.getItemDetail() != null) {
                    if (CollectionUtils.isNotEmpty(itemOut.getItemDetail().getDescription())) {
                        clearItemDetailDescription(itemDetail);
                        for (final Description description : itemOut.getItemDetail().getDescription()) {
                            addItemDetailDescription(itemDetail, description.getvalue());
                        }
                    }
                    if (CollectionUtils.isNotEmpty(itemOut.getItemDetail().getClassification())) {
                        clearItemDetailClassification(itemDetail);
                        for (final Classification classification : itemOut.getItemDetail().getClassification()) {
                            addItemDetailClassification(itemDetail, classification.getDomain(), classification.getvalue());
                        }
                    }
                    if (CollectionUtils.isNotEmpty(itemOut.getItemDetail().getExtrinsic())) {
                        for (final Extrinsic extrinsic : itemOut.getItemDetail().getExtrinsic()) {
                            addItemDetailExtrinsic(itemDetail, extrinsic.getName(), extrinsic.getContent());
                        }
                    }
                }
            }
        }
    }

    private void addItemDetailDescription(final ItemDetail itemDetail, final ProductModel product, final String value) {
        if (product != null) {
            addItemDetailDescription(itemDetail, product.getName());
            return;
        }
        addItemDetailDescription(itemDetail, value);
    }

    private void addItemDetailDescription(final ItemDetail itemDetail, final String value) {
        final Description description = getDataFactory().createDescription(getCurrentCartLanguage(), value);
        itemDetail.getDescription().add(description);
    }

    private void clearItemDetailDescription(final ItemDetail itemDetail) {
        if (CollectionUtils.isNotEmpty(itemDetail.getDescription())) {
            if (CollectionUtils.size(itemDetail.getDescription()) == 1) {
                if (StringUtils.isBlank(itemDetail.getDescription().get(0).getvalue())) {
                    itemDetail.getDescription().clear();
                }
            }
        }
    }

    private void addItemDetailClassification(final ItemDetail itemDetail, final ProductModel product, final String domain, final String value) {
        final Set<DistFieldConfigModel> fieldConfigs = getCustomerConfigService().getFieldConfigs();
        if (CollectionUtils.isNotEmpty(fieldConfigs)) {
            final Map<String, String> fieldConfigMap = getCustomerConfigService().getFieldConfigsForProduct(product);
            if (MapUtils.isNotEmpty(fieldConfigMap)) {
                for (final DistFieldConfigModel fieldConfig : fieldConfigs) {
                    if (DistFieldLocation.CLASSIFICATION.equals(fieldConfig.getLocation())) {
                        // add classification domain/value from customer configuration
                        addItemDetailClassification(itemDetail, fieldConfig.getDomain(), fieldConfigMap.get(fieldConfig.getDomain()));
                        return;
                    }
                }
            }
        }
        addItemDetailClassification(itemDetail, domain, value);
    }

    // DISTRELEC-32751- Add extrinsic element at item detail level for DangerousGoods element name per customer config
    private void addItemDetailExtrinsic(final ItemDetail itemDetail, final ProductModel product) {
        try {
            final Set<DistFieldConfigModel> fieldConfigs = getCustomerConfigService().getFieldConfigs();
            if (CollectionUtils.isNotEmpty(fieldConfigs)) {
                final Map<String, String> fieldConfigMap = getCustomerConfigService().getFieldConfigsForProduct(product);
                if (MapUtils.isNotEmpty(fieldConfigMap)) {
                    for (final DistFieldConfigModel fieldConfig : fieldConfigs) {
                        if (DistFieldLocation.PRODUCT.equals(fieldConfig.getLocation()) && fieldConfig.getDomain().equalsIgnoreCase(DG_PROFILE)) {
                            if (null != product && null != product.getDangerousGoodsProfile() && product.getDangerousGoodsProfile().isDangerous()) {
                                final Extrinsic orderMessageExtrinsic = getDataFactory().createExtrinsic();
                                orderMessageExtrinsic.setName(fieldConfig.getParameter());
                                orderMessageExtrinsic.getContent().add(Boolean.TRUE.toString());
                                itemDetail.getExtrinsic().add(orderMessageExtrinsic);
                            }
                            return;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            LOG.error("Error while adding extrinsic element, check if customer configuration is set as non dynamic.");
        }
    }

    private void addItemDetailClassification(final ItemDetail itemDetail, final String domain, final String value) {
        final Classification classification = getDataFactory().createClassification(domain, value);
        itemDetail.getClassification().add(classification);
    }

    private void clearItemDetailClassification(final ItemDetail itemDetail) {
        if (CollectionUtils.isNotEmpty(itemDetail.getClassification())) {
            if (CollectionUtils.size(itemDetail.getClassification()) == 1) {
                if (StringUtils.isBlank(itemDetail.getClassification().get(0).getDomain())
                        || StringUtils.isBlank(itemDetail.getClassification().get(0).getvalue())) {
                    itemDetail.getClassification().clear();
                }
            }
        }
    }

    private void addItemDetailManufacturer(final ItemDetail itemDetail, final DistManufacturerModel manufacturer) {
        if (manufacturer != null) {
            itemDetail.setManufacturerName(getDataFactory().createManufacturerName(getCurrentCartLanguage(), manufacturer.getName()));
            itemDetail.setManufacturerPartID(manufacturer.getCode());
        }
    }

    private void addItemDetailExtrinsic(final ItemDetail itemDetail, final String name, final List<Object> content) {
        final Extrinsic orderMessageExtrinsic = getDataFactory().createExtrinsic();
        orderMessageExtrinsic.setName(name);
        orderMessageExtrinsic.getContent().addAll(content);
        itemDetail.getExtrinsic().add(orderMessageExtrinsic);
    }

    protected String writeOrderMessage(final CXML cXmlPunchOutOrderMessage) {
        String message = "";
        try {
            // create JAXB context and initializing Marshaller
            final JAXBContext jaxbContext = JAXBContext.newInstance(CXML.class);
            final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            // marshal DTO and return XML string
            final File xmlFile = new File("PunchOutOrderMessage.xml");
            jaxbMarshaller.marshal(cXmlPunchOutOrderMessage, xmlFile);
            final String orderMessageXml = expandXmlString(xmlFile);
            message = Base64.encodeBase64String(orderMessageXml.getBytes());
        } catch (JAXBException e) {
            LOG.error("An exception occured while writing the XML response", e);
        }
        return message;
    }

    protected String expandXmlString(final File xmlFile) {
        try {
            final FileInputStream fileInputStream = new FileInputStream(xmlFile);
            final StringBuilder expandedXmlString = new StringBuilder();
            expandedXmlString.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
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

    @Override
    public void updateAribaCart(final CXML cXmlPunchOutSetupRequest) {
        // update Ariba cart and set if it should be editable or not.
        final AribaCartModel aribaCart = (AribaCartModel) getCartService().getSessionCart();
        final PunchOutSetupRequest punchOutSetupRequest = getDataFactory().getPunchOutSetupRequest(cXmlPunchOutSetupRequest);
        final boolean allowEditCart = !"inspect".equals(punchOutSetupRequest.getOperation());
        aribaCart.setAllowEditBasket(Boolean.valueOf(allowEditCart));
        getModelService().save(aribaCart);
    }

    @Override
    public void setUpAribaLanguage(final String isocode) {
        if (isAribaCustomer() && StringUtils.isNotBlank(isocode)) {
            try {
                getUserService().getCurrentUser().setSessionLanguage(getCommonI18NService().getLanguage(isocode));
            } catch (Exception exp) {
                LOG.warn("No language defined for ISOCODE: " + isocode);
            }
        }
    }

    private String getCurrentCartLanguage() {
        String language = "";
        final AribaCartModel aribaCart = (AribaCartModel) getCartService().getSessionCart();
        if (aribaCart.getLanguage() != null) {
            language = aribaCart.getLanguage().getIsocode();
        }

        return language;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public SecureTokenService getSecureTokenService() {
        return secureTokenService;
    }

    public void setSecureTokenService(final SecureTokenService secureTokenService) {
        this.secureTokenService = secureTokenService;
    }

    public CartService getCartService() {
        return cartService;
    }

    public void setCartService(final CartService cartService) {
        this.cartService = cartService;
    }

    public DistCommerceCartService getCommerceCartService() {
        return commerceCartService;
    }

    public void setCommerceCartService(final DistCommerceCartService commerceCartService) {
        this.commerceCartService = commerceCartService;
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }

    public DeliveryService getDeliveryService() {
        return deliveryService;
    }

    public void setDeliveryService(final DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    public AddressService getAddressService() {
        return addressService;
    }

    public void setAddressService(final AddressService addressService) {
        this.addressService = addressService;
    }

    public DistEProcurementCustomerConfigService getCustomerConfigService() {
        return customerConfigService;
    }

    public void setCustomerConfigService(final DistEProcurementCustomerConfigService customerConfigService) {
        this.customerConfigService = customerConfigService;
    }

    public CartFactory getDistAribaCartFactory() {
        return distAribaCartFactory;
    }

    public void setDistAribaCartFactory(final CartFactory distAribaCartFactory) {
        this.distAribaCartFactory = distAribaCartFactory;
    }

    public DistAribaDataFactory getDataFactory() {
        if (dataFactory == null) {
            dataFactory = new DistAribaDataFactory();
        }
        return dataFactory;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public long getTokenValidity() {
        return tokenValidity;
    }

    public void setTokenValidity(final long tokenValidity) {
        this.tokenValidity = tokenValidity;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    @Override
    public boolean openInNewWindow() {
        return isAribaCustomer() && getCustomerConfigService().openInNewWindow();
    }

}
