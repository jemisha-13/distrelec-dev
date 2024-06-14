package com.namics.distrelec.b2b.core.service.nps.data;

import com.namics.distrelec.b2b.core.enums.NPSReason;
import com.namics.distrelec.b2b.core.enums.NPSSubReason;
import com.namics.distrelec.b2b.core.enums.NPSType;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

@UnitTest
public class DistNetPromoterScoreDataTest {

    private DistNetPromoterScoreData data;
    private static final NPSReason REASON = NPSReason.DELIVERYOFGOODS;
    private static final NPSSubReason SUBREASON = NPSSubReason.DELIVERYOFGOODS_DAMAGEDGOODS;
    private static final NPSType TYPE = NPSType.DELIVERYCONFIRMATION;
    private static final Integer VALUE = Integer.valueOf(-1);
    private static final String FEEDBACK = "This is some feedback";
    private static final String ORDER_NUMBER = "100123";
    private static final String EMAIL = "customer@distrelec.com";
    private static final String FIRST_NAME = "Distrelec";
    private static final String LAST_NAME = "Limited";
    private static final String COMPANY = "Distrelec";
    private static final String CUSTOMER_ID = "000123456";
    private static final String CONTACT_ID = "000112233";
    private static final Date DELIVERY_DATE = new Date();
    private static final String NPS_CODE = "0001";


    @Before
    public void setUp(){
        data = new DistNetPromoterScoreData();
        setupTestObject();
    }

    private void setupTestObject() {
        data.setReason(REASON);
        data.setSubreason(SUBREASON);
        data.setType(TYPE);
        data.setValue(VALUE);
        data.setText(FEEDBACK);
        data.setOrderNumber(ORDER_NUMBER);
        data.setEmail(EMAIL);
        data.setFirstname(FIRST_NAME);
        data.setLastname(LAST_NAME);
        data.setCompanyName(COMPANY);
        data.setErpContactID(CONTACT_ID);
        data.setErpCustomerID(CUSTOMER_ID);
        data.setDeliveryDate(DELIVERY_DATE);
        data.setCode(NPS_CODE);
    }

    @After
    public void tearDown(){
        data = null;
    }

    @Test
    public void testGettersAndSetters(){
        assertEquals(REASON, data.getReason());
        assertEquals(SUBREASON, data.getSubreason());
        assertEquals(TYPE, data.getType());
        assertEquals(VALUE, data.getValue());
        assertEquals(FEEDBACK, data.getText());
        assertEquals(ORDER_NUMBER, data.getOrderNumber());
        assertEquals(EMAIL, data.getEmail());
        assertEquals(FIRST_NAME, data.getFirstname());
        assertEquals(LAST_NAME, data.getLastname());
        assertEquals(COMPANY, data.getCompanyName());
        assertEquals(CUSTOMER_ID, data.getErpCustomerID());
        assertEquals(CONTACT_ID, data.getErpContactID());
        assertEquals(DELIVERY_DATE, data.getDeliveryDate());
        assertEquals(NPS_CODE, data.getCode());
    }
}
