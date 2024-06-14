package com.namics.distrelec.b2b.facades.order.converters;

import com.distrelec.webservice.if19.v1.ReturnReasons;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.DistReturnReasonData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistReturnReasonConverterUnitTest {

    DistReturnReasonConverter distReturnReasonConverter = new DistReturnReasonConverter();

    @Test
    public void testPopulate() {

        ReturnReasons source = mock(ReturnReasons.class);
        DistReturnReasonData target = new DistReturnReasonData();

        when(source.getReturnReasonID()).thenReturn("XYZ123");
        when(source.getReturnReasonDescription()).thenReturn("Defective Product");

        distReturnReasonConverter.populate(source, target);

        assertThat(target.getReturnReasonId(), equalTo("XYZ123"));
        assertThat(target.getReturnReasonDesc(), equalTo("Defective Product"));
    }
}
