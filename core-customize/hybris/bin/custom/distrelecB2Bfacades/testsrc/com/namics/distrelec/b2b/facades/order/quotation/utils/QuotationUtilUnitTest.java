package com.namics.distrelec.b2b.facades.order.quotation.utils;

import com.namics.distrelec.b2b.core.enums.QuoteModificationType;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class QuotationUtilUnitTest {

    @Test
    public void testGetFromCodeWithNull() {
        assertThat(QuotationUtil.getFromCode(null), equalTo(QuoteModificationType.ALL));
    }

    @Test
    public void testGetFromCodeWithIncrease() {
        assertThat(QuotationUtil.getFromCode("01"), equalTo(QuoteModificationType.INCREASE));
    }

    @Test
    public void testGetFromCodeWithDecrease() {
        assertThat(QuotationUtil.getFromCode("02"), equalTo(QuoteModificationType.DECREASE));
    }

    @Test
    public void testGetFromCodeWithOff() {
        assertThat(QuotationUtil.getFromCode("03"), equalTo(QuoteModificationType.OFF));
    }

    @Test
    public void testGetFromCodeWithUnknownCode() {
        assertThat(QuotationUtil.getFromCode("unknown"), equalTo(QuoteModificationType.ALL));
    }
}
