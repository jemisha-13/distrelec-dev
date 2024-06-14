package com.namics.distrelec.b2b.core.event;

import com.namics.distrelec.b2b.core.enums.NPSReason;
import com.namics.distrelec.b2b.core.enums.NPSSubReason;
import com.namics.distrelec.b2b.core.enums.NPSType;
import com.namics.distrelec.b2b.core.model.process.DistNpsProcessModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.store.BaseStoreModel;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@UnitTest
public class DistNetScorePromoterEventListenerTest {

    private static final BaseSiteModel SITE = new BaseSiteModel();
    private static final BaseStoreModel BASE_STORE_MODEL = new BaseStoreModel();
    private static final String CODE = "0000000";
    private static final String ERP_CONTACT_ID = "000111111";
    private static final String ERP_CUSTOMER_ID = "000333333";
    private static final String TESTING_DISTRELEC_IT = "testing@distrelec.it";
    private static final String SALES_ORG = "7330";
    private static final String DISTRELEC = "Distrelec";
    private static final String LASTNAME = "User";
    private static final int VALUE = 7;
    private static final String TO_EMAIL = "no-reply@distrelec.com";
    private static final String EMAIL_SUBJECT_MSG = "Resolve delivery complaint";

    private DistNetPromoterScoreEventListener listener;

    @Before
    public void setUp(){
        listener = new DistNetPromoterScoreEventListener();
    }

    @Test
    public void testPopulation(){
        final DistNpsProcessModel npsProcessModel = new DistNpsProcessModel();
        final DistNetPromoterScoreEvent event = createDistNetPromoterScoreEvent();

        listener.populate(event, npsProcessModel);

        assertEquals(event.getSite(), npsProcessModel.getSite());
        assertEquals(event.getBaseStore(), npsProcessModel.getStore());
        assertEquals(event.getReason(), npsProcessModel.getReason());
        assertEquals(event.getSubreason().getCode(), npsProcessModel.getSubReason().getCode());
        assertEquals(event.getCode(), npsProcessModel.getNpsCode());
        assertEquals(event.getErpContactID(), npsProcessModel.getErpContactID());
        assertEquals(event.getErpCustomerID(), npsProcessModel.getErpCustomerID());
        assertEquals(event.getEmail(), npsProcessModel.getEmail());
        assertEquals(event.getSalesOrg(), npsProcessModel.getSalesOrg());
        assertEquals(event.getFirstname(), npsProcessModel.getFirstname());
        assertEquals(event.getLastname(), npsProcessModel.getLastname());
        assertEquals(event.getCompanyName(), npsProcessModel.getCompanyName());
        assertEquals(event.getOrderNumber(), npsProcessModel.getOrderNumber());
        assertEquals(event.getDomain(), npsProcessModel.getDomain());
        assertEquals(event.getDeliveryDate(), npsProcessModel.getDeliveryDate());

        assertEquals(event.getValue(), npsProcessModel.getValue().intValue());
        assertEquals(event.getText(), npsProcessModel.getText());
        assertEquals(event.getToEmail(), npsProcessModel.getToEmail());
        assertEquals(event.getEmailSubjectMsg(), npsProcessModel.getEmailSubjectMsg());
    }


    private DistNetPromoterScoreEvent createDistNetPromoterScoreEvent(){
        final DistNetPromoterScoreEvent event = new DistNetPromoterScoreEvent();
        event.setSite(SITE);
        event.setBaseStore(BASE_STORE_MODEL);
        event.setReason(NPSReason.DELIVERYOFGOODS);
        event.setSubreason(NPSSubReason.DELIVERYOFGOODS_DAMAGEDGOODS);
        event.setCode(CODE);
        event.setErpContactID(ERP_CONTACT_ID);
        event.setErpCustomerID(ERP_CUSTOMER_ID);
        event.setType(NPSType.DELIVERYCONFIRMATION);
        event.setEmail(TESTING_DISTRELEC_IT);
        event.setSalesOrg(SALES_ORG);
        event.setFirstname(DISTRELEC);
        event.setLastname(LASTNAME);
        event.setCompanyName(DISTRELEC);
        event.setValue(VALUE);
        event.setToEmail(TO_EMAIL);
        event.setEmailSubjectMsg(EMAIL_SUBJECT_MSG);
        return event;
    }
}
