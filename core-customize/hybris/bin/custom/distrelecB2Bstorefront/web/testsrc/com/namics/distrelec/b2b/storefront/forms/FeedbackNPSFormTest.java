package com.namics.distrelec.b2b.storefront.forms;

import com.namics.distrelec.b2b.core.enums.NPSReason;
import com.namics.distrelec.b2b.core.enums.NPSSubReason;
import com.namics.distrelec.b2b.core.enums.NPSType;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

@UnitTest
public class FeedbackNPSFormTest {

    private FeedbackNPSForm form;
    private static final String REASON = NPSReason.DELIVERYOFGOODS.getCode();
    private static final String SUBREASON = NPSSubReason.DELIVERYOFGOODS_DAMAGEDGOODS.getCode();
    private static final String TYPE = NPSType.DELIVERYCONFIRMATION.getCode();
    private static final Integer VALUE = Integer.valueOf(WebConstants.DEFAULT_STARTING_NPS_SCORE);
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
        form = new FeedbackNPSForm();
        setupTestObject();
    }

    private void setupTestObject() {
        form.setReason(REASON);
        form.setSubreason(SUBREASON);
        form.setType(TYPE);
        form.setValue(VALUE);
        form.setFeedback(FEEDBACK);
        form.setOrder(ORDER_NUMBER);
        form.setEmail(EMAIL);
        form.setFname(FIRST_NAME);
        form.setNamn(LAST_NAME);
        form.setCompany(COMPANY);
        form.setContactnum(CONTACT_ID);
        form.setCnumber(CUSTOMER_ID);
        form.setDelivery(DELIVERY_DATE);
        form.setId(NPS_CODE);
    }

    @After
    public void tearDown(){
        form = null;
    }

    @Test
    public void testGettersAndSetters(){
        assertEquals(REASON, form.getReason());
        assertEquals(SUBREASON, form.getSubreason());
        assertEquals(TYPE, form.getType());
        assertEquals(VALUE, form.getValue());
        assertEquals(FEEDBACK, form.getFeedback());
        assertEquals(ORDER_NUMBER, form.getOrder());
        assertEquals(EMAIL, form.getEmail());
        assertEquals(FIRST_NAME, form.getFname());
        assertEquals(LAST_NAME, form.getNamn());
        assertEquals(COMPANY, form.getCompany());
        assertEquals(CUSTOMER_ID, form.getCnumber());
        assertEquals(CONTACT_ID, form.getContactnum());
        assertEquals(DELIVERY_DATE, form.getDelivery());
        assertEquals(NPS_CODE, form.getId());
    }

    @Test
    public void testDefaultValueIsMinusOne(){
        final FeedbackNPSForm cleanForm = new FeedbackNPSForm();
        assertEquals(VALUE, cleanForm.getValue());
    }
}
