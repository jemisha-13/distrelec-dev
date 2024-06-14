/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.eprocurement.data.ariba;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.namics.distrelec.b2b.cxml.generated.CXML;
import com.namics.distrelec.b2b.cxml.generated.Classification;
import com.namics.distrelec.b2b.cxml.generated.Credential;
import com.namics.distrelec.b2b.cxml.generated.DeliverTo;
import com.namics.distrelec.b2b.cxml.generated.Description;
import com.namics.distrelec.b2b.cxml.generated.Extrinsic;
import com.namics.distrelec.b2b.cxml.generated.Header;
import com.namics.distrelec.b2b.cxml.generated.Identity;
import com.namics.distrelec.b2b.cxml.generated.ItemIn;
import com.namics.distrelec.b2b.cxml.generated.ManufacturerName;
import com.namics.distrelec.b2b.cxml.generated.Message;
import com.namics.distrelec.b2b.cxml.generated.Name;
import com.namics.distrelec.b2b.cxml.generated.ObjectFactory;
import com.namics.distrelec.b2b.cxml.generated.PunchOutOrderMessage;
import com.namics.distrelec.b2b.cxml.generated.PunchOutOrderMessageHeader;
import com.namics.distrelec.b2b.cxml.generated.PunchOutSetupRequest;
import com.namics.distrelec.b2b.cxml.generated.PunchOutSetupResponse;
import com.namics.distrelec.b2b.cxml.generated.Request;
import com.namics.distrelec.b2b.cxml.generated.Response;
import com.namics.distrelec.b2b.cxml.generated.SharedSecret;
import com.namics.distrelec.b2b.cxml.generated.ShipTo;
import com.namics.distrelec.b2b.cxml.generated.Shipping;
import com.namics.distrelec.b2b.cxml.generated.Street;
import com.namics.distrelec.b2b.cxml.generated.Tax;

public class DistAribaDataFactory {

    private final ObjectFactory objectFactory = new ObjectFactory();

    public CXML createPunchOutSetupResponse() {
        final PunchOutSetupResponse punchOutSetupResponse = objectFactory.createPunchOutSetupResponse();
        punchOutSetupResponse.setStartPage(objectFactory.createStartPage());
        punchOutSetupResponse.getStartPage().setURL(objectFactory.createURL());

        final Response response = objectFactory.createResponse();
        response.getProfileResponseOrPunchOutSetupResponseOrGetPendingResponseOrSubscriptionListResponseOrSubscriptionContentResponseOrSupplierListResponseOrSupplierDataResponse()
                .add(punchOutSetupResponse);
        response.setStatus(objectFactory.createStatus());

        final CXML cXML = objectFactory.createCXML();
        cXML.getHeaderOrMessageOrRequestOrResponse().add(response);

        return cXML;
    }

    public CXML createPunchOutSetupRequest() {
        final PunchOutSetupRequest punchOutSetupRequest = objectFactory.createPunchOutSetupRequest();
        punchOutSetupRequest.setBrowserFormPost(objectFactory.createBrowserFormPost());
        punchOutSetupRequest.getBrowserFormPost().setURL(objectFactory.createURL());
        punchOutSetupRequest.setBuyerCookie(objectFactory.createBuyerCookie());
        punchOutSetupRequest.setSelectedItem(objectFactory.createSelectedItem());
        punchOutSetupRequest.getSelectedItem().setItemID(objectFactory.createItemID());
        punchOutSetupRequest.getSelectedItem().getItemID().setSupplierPartAuxiliaryID(objectFactory.createSupplierPartAuxiliaryID());
        punchOutSetupRequest.setShipTo(objectFactory.createShipTo());
        punchOutSetupRequest.getShipTo().setAddress(objectFactory.createAddress());
        punchOutSetupRequest.getShipTo().getAddress().setPostalAddress(objectFactory.createPostalAddress());
        punchOutSetupRequest.getShipTo().getAddress().getPostalAddress().setCountry(objectFactory.createCountry());
        punchOutSetupRequest.setSupplierSetup(objectFactory.createSupplierSetup());
        punchOutSetupRequest.getSupplierSetup().setURL(objectFactory.createURL());

        final Credential credential = objectFactory.createCredential();
        credential.setIdentity(objectFactory.createIdentity());

        final Header header = objectFactory.createHeader();
        header.setFrom(objectFactory.createFrom());
        header.getFrom().getCredential().add(credential);
        header.setSender(objectFactory.createSender());
        header.getSender().getCredential().add(credential);
        header.setTo(objectFactory.createTo());
        header.getTo().getCredential().add(credential);

        final Request request = objectFactory.createRequest();
        request.getProfileRequestOrOrderRequestOrPunchOutSetupRequestOrStatusUpdateRequestOrGetPendingRequestOrSubscriptionListRequestOrSubscriptionContentRequestOrSupplierListRequestOrSupplierDataRequestOrCopyRequest()
                .add(punchOutSetupRequest);

        final CXML cXML = objectFactory.createCXML();
        cXML.getHeaderOrMessageOrRequestOrResponse().add(header);
        cXML.getHeaderOrMessageOrRequestOrResponse().add(request);

        return cXML;
    }

    public CXML createPunchOutOrderMessage() {
        final PunchOutOrderMessageHeader punchOutOrderMessageHeader = objectFactory.createPunchOutOrderMessageHeader();
        punchOutOrderMessageHeader.setTotal(objectFactory.createTotal());
        punchOutOrderMessageHeader.getTotal().setMoney(objectFactory.createMoney());

        final PunchOutOrderMessage punchOutOrderMessage = objectFactory.createPunchOutOrderMessage();
        punchOutOrderMessage.setBuyerCookie(objectFactory.createBuyerCookie());
        punchOutOrderMessage.setPunchOutOrderMessageHeader(punchOutOrderMessageHeader);

        final Header header = objectFactory.createHeader();
        header.setFrom(objectFactory.createFrom());
        header.setSender(objectFactory.createSender());
        header.setTo(objectFactory.createTo());

        final Message message = objectFactory.createMessage();
        message.getPunchOutOrderMessageOrSubscriptionChangeMessageOrSupplierChangeMessage().add(punchOutOrderMessage);

        final CXML cXML = objectFactory.createCXML();
        cXML.getHeaderOrMessageOrRequestOrResponse().add(header);
        cXML.getHeaderOrMessageOrRequestOrResponse().add(message);

        return cXML;
    }

    public ItemIn createItemIn() {
        final ItemIn itemIn = objectFactory.createItemIn();
        itemIn.setItemID(objectFactory.createItemID());
        itemIn.getItemID().setSupplierPartAuxiliaryID(objectFactory.createSupplierPartAuxiliaryID());
        itemIn.setItemDetail(objectFactory.createItemDetail());
        itemIn.getItemDetail().setUnitPrice(objectFactory.createUnitPrice());
        itemIn.getItemDetail().getUnitPrice().setMoney(objectFactory.createMoney());
        // itemIn.getItemDetail().setURL(messageFactory.createCXMLMessagePunchOutOrderMessageItemInItemDetailURL());
        return itemIn;
    }

    public Classification createClassification(final String domain, final String value) {
        final Classification classification = objectFactory.createClassification();
        classification.setDomain(domain);
        classification.setvalue(value);
        return classification;
    }

    public Description createDescription(final String language, final String value) {
        final Description description = objectFactory.createDescription();
        description.setXmlLang(language);
        description.setvalue(value);
        return description;
    }

    public Extrinsic createExtrinsic() {
        return objectFactory.createExtrinsic();
    }

    public Name createName(final String language, final String value) {
        final Name name = objectFactory.createName();
        name.setXmlLang(language);
        name.setvalue(value);
        return name;
    }

    public DeliverTo createDeliverTo(final String value) {
        final DeliverTo deliverTo = objectFactory.createDeliverTo();
        deliverTo.setvalue(value);
        return deliverTo;
    }

    public Street createStreet(final String value) {
        final Street street = objectFactory.createStreet();
        street.setvalue(value);
        return street;
    }

    public Shipping createShipping() {
        final Shipping shipping = objectFactory.createShipping();
        // shipping.setDescription(createDescription());
        shipping.setMoney(objectFactory.createMoney());
        return shipping;
    }

    public ShipTo createShipTo() {
        final ShipTo shipTo = objectFactory.createShipTo();
        shipTo.setAddress(objectFactory.createAddress());
        shipTo.getAddress().setPostalAddress(objectFactory.createPostalAddress());
        shipTo.getAddress().getPostalAddress().setCountry(objectFactory.createCountry());
        return shipTo;
    }

    public Tax createTax() {
        final Tax tax = objectFactory.createTax();
        // tax.setDescription(createDescription());
        tax.setMoney(objectFactory.createMoney());
        return tax;
    }

    public ManufacturerName createManufacturerName(final String language, final String value) {
        final ManufacturerName manufacturerName = objectFactory.createManufacturerName();
        manufacturerName.setXmlLang(language);
        manufacturerName.setvalue(value);
        return manufacturerName;
    }

    public Credential createCredential() {
        return objectFactory.createCredential();
    }

    public PunchOutSetupRequest getPunchOutSetupRequest(final CXML cXML) {
        if (cXML != null) {
            for (final Object object : cXML.getHeaderOrMessageOrRequestOrResponse()) {
                if (object instanceof Request) {
                    for (final Object object2 : ((Request) object)
                            .getProfileRequestOrOrderRequestOrPunchOutSetupRequestOrStatusUpdateRequestOrGetPendingRequestOrSubscriptionListRequestOrSubscriptionContentRequestOrSupplierListRequestOrSupplierDataRequestOrCopyRequest()) {
                        if (object2 instanceof PunchOutSetupRequest) {
                            return (PunchOutSetupRequest) object2;
                        }
                    }
                }
            }
        }
        return null;
    }

    public PunchOutOrderMessage getPunchOutOrderMessage(final CXML cXML) {
        if (cXML != null) {
            for (final Object object : cXML.getHeaderOrMessageOrRequestOrResponse()) {
                if (object instanceof Message) {
                    for (final Object object2 : ((Message) object).getPunchOutOrderMessageOrSubscriptionChangeMessageOrSupplierChangeMessage()) {
                        if (object2 instanceof PunchOutOrderMessage) {
                            return (PunchOutOrderMessage) object2;
                        }
                    }
                }
            }
        }
        return null;
    }

    public Header getHeader(final CXML cXML) {
        if (cXML != null) {
            for (final Object object : cXML.getHeaderOrMessageOrRequestOrResponse()) {
                if (object instanceof Header) {
                    return (Header) object;
                }
            }
        }
        return null;
    }
    public SharedSecret getSharedSecret(final Credential senderCredential) {
    	  for (final Object object : senderCredential.getSharedSecretOrDigitalSignature()) {
              if (object instanceof SharedSecret) {
                  return (SharedSecret) object;
              }
          }
        return null;
    }
    public SharedSecret getSharedSecret(final List<Credential> credentials) {
        if (CollectionUtils.isNotEmpty(credentials)) {
            for (final Credential credential : credentials) {
                for (final Object object : credential.getSharedSecretOrDigitalSignature()) {
                    if (object instanceof SharedSecret) {
                        return (SharedSecret) object;
                    }
                }
            }
        }
        return null;
    }

    public Identity createIdentity(String prosuppliername) {
        Identity identity = objectFactory.createIdentity();
        identity.getContent().add(prosuppliername);
        return identity;
    }

    public Credential createCredential(String domain, String identityName) {
        Credential credential = createCredential();
        Identity identity = createIdentity(identityName);

        credential.setDomain(domain);
        credential.setIdentity(identity);

        return credential;
    }

}
