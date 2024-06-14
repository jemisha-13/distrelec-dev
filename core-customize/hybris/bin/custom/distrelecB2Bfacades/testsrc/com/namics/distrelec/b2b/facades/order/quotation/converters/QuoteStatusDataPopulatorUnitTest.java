package com.namics.distrelec.b2b.facades.order.quotation.converters;

import com.namics.distrelec.b2b.core.model.DistQuotationStatusModel;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuoteStatusData;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class QuoteStatusDataPopulatorUnitTest {

    @Test
    public void testPopulate() {
        // given
        DistQuotationStatusModel source = mock(DistQuotationStatusModel.class);
        QuoteStatusData target = new QuoteStatusData();
        QuoteStatusDataPopulator populator = new QuoteStatusDataPopulator();

        // when
        when(source.getCode()).thenReturn("007");
        when(source.getName()).thenReturn("James Bond");

        populator.populate(source, target);

        // then
        assertThat(target.getCode(), equalTo("007"));
        assertThat(target.getName(), equalTo("James Bond"));
    }
}
