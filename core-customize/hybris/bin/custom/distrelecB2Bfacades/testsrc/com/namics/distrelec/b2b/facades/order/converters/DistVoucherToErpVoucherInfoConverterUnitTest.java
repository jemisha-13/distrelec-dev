package com.namics.distrelec.b2b.facades.order.converters;

import com.distrelec.webservice.if15.v1.VoucherResponse;
import com.namics.distrelec.b2b.facades.order.data.DistErpVoucherInfoData;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistVoucherToErpVoucherInfoConverterUnitTest {

    DistVoucherToErpVoucherInfoConverter distVoucherToErpVoucherInfoConverter = new DistVoucherToErpVoucherInfoConverter();

    @Test
    public void testPopulateValidVoucher() {
        // given
        VoucherResponse source = createMockValidVoucherResponse();
        DistErpVoucherInfoData target = new DistErpVoucherInfoData();

        // when
        distVoucherToErpVoucherInfoConverter.populate(source, target);

        // then
        assertThat(target.getCode(), equalTo("12345"));
        assertThat(target.getValid(), is(true));
        assertThat(target.getReturnERPCode(), equalTo("00"));
    }

    private VoucherResponse createMockValidVoucherResponse() {
        VoucherResponse source = mock(VoucherResponse.class);
        when(source.isValid()).thenReturn(true);
        when(source.getCode()).thenReturn("12345");
        when(source.getErrorCode()).thenReturn(null);
        return source;
    }
}
