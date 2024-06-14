package com.namics.distrelec.b2b.facades.nps.converter;

import com.namics.distrelec.b2b.core.enums.NPSReason;
import com.namics.distrelec.b2b.core.enums.NPSSubReason;
import com.namics.distrelec.b2b.core.model.feedback.DistNetPromoterScoreModel;
import com.namics.distrelec.b2b.core.service.nps.data.DistNetPromoterScoreData;
import de.hybris.bootstrap.annotations.UnitTest;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

@UnitTest
public class DistNetPromoterScoreConverterTest {

    private DistNetPromoterScoreConverter converter;
    private DistNetPromoterScoreModel source;
    private DistNetPromoterScoreData target;

    @Before
    public void setUp(){
        converter = new DistNetPromoterScoreConverter();
        target = new DistNetPromoterScoreData();
        setUpDefaultTestFixtures();
    }

    @After
    public void tearDown(){
        source = null;
        target = null;
        converter = null;
    }

    private void setUpDefaultTestFixtures() {
        source = new DistNetPromoterScoreModel();
        source.setCode("456789");
        source.setReason(NPSReason.DELIVERYOFGOODS);
        source.setSubReason(NPSSubReason.DELIVERYOFGOODS_DAMAGEDGOODS);
        source.setErpCustomerID("000123456");
        source.setErpContactID("000123457");
        source.setOrderNumber("100123");
        source.setEmail("testing@distrelec.com");
        source.setFirstname("Test");
        source.setLastname("User");
        source.setSalesOrg("7310");
        source.setDomain("http://dev.distrelec.ch");
        source.setCreationtime(new Date());
        source.setDeliveryDate(new Date());
        source.setCompanyName("Datwyler AG");
    }

    @Test
    public void testNormalWithNormalScore(){
        source.setValue(7);
        source.setText("Reasonable");
        converter.populate(source, target);

        assertEquals(source.getValue(), target.getValue());
        assertEquals(source.getText(), target.getText());
        checkCommonAttributeEquality(source, target);
    }

    @Test
    public void testWithNullScore() {
        source.setValue(null);
        source.setText(StringUtils.EMPTY);
        converter.populate(source, target);

        assertEquals(Integer.valueOf(0), target.getValue()); //Should be NA
        assertEquals(source.getText(), target.getText());
        checkCommonAttributeEquality(source, target);
    }

    @Test
    public void testWithZeroScore() {
        source.setValue(0);
        source.setText("Broken during transit.");
        converter.populate(source, target);

        assertEquals(Integer.valueOf(0), target.getValue());
        assertEquals(source.getText(), target.getText());
        checkCommonAttributeEquality(source, target);
    }

    private void checkCommonAttributeEquality(DistNetPromoterScoreModel source, DistNetPromoterScoreData target) {
        assertEquals(source.getCompanyName(), target.getCompanyName());
        assertEquals(source.getCode(), target.getCode());
        assertEquals(source.getReason(), target.getReason());
        assertEquals(source.getSubReason(), target.getSubreason());
        assertEquals(source.getErpCustomerID(), target.getErpCustomerID());
        assertEquals(source.getErpContactID(), target.getErpContactID());
        assertEquals(source.getOrderNumber(), target.getOrderNumber());
        assertEquals(source.getEmail(), target.getEmail());
        assertEquals(source.getFirstname(), target.getFirstname());
        assertEquals(source.getLastname(), target.getLastname());
        assertEquals(source.getDomain(), target.getDomain());
        assertEquals(source.getCreationtime(), target.getCreationDate());
        assertEquals(source.getDeliveryDate(), target.getDeliveryDate());
    }
}
