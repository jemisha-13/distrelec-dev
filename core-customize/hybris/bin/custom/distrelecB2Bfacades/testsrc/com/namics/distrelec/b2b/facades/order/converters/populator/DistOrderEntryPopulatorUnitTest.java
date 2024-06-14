package com.namics.distrelec.b2b.facades.order.converters.populator;

import com.namics.distrelec.b2b.core.enums.QuoteModificationType;
import com.namics.distrelec.b2b.core.model.availability.DistErpAvailabilityInfoModel;
import com.namics.distrelec.b2b.core.model.order.SubOrderEntryModel;
import com.namics.distrelec.b2b.core.service.environment.RuntimeEnvironmentService;
import com.namics.distrelec.b2b.facades.order.data.SubOrderEntryData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.commercefacades.comment.data.CommentData;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.strategies.ModifiableChecker;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.I18NService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistOrderEntryPopulatorUnitTest {

    @Mock
    private ModifiableChecker<AbstractOrderEntryModel> entryOrderChecker;

    @Mock
    private Converter<ProductModel, ProductData> productConverter;

    @Mock
    private PriceDataFactory priceDataFactory;

    @Mock
    private Converter<AbstractOrderEntryProductInfoModel, List<ConfigurationInfoData>> productConfigurationConverter;

    @Mock
    private Converter<CommentModel, CommentData> orderCommentConverter;

    @Mock
    private I18NService i18NService;

    @Mock
    private Converter<SubOrderEntryModel, SubOrderEntryData> subOrderEntryDataConverter;

    @Mock
    private RuntimeEnvironmentService runtimeEnvironmentService;

    @InjectMocks
    private DistOrderEntryPopulator distOrderEntryPopulator;

    @Test
    public void testPopulate() {
        // given
        AbstractOrderEntryModel source = mock(AbstractOrderEntryModel.class);
        OrderEntryData target = new OrderEntryData();
        AbstractOrderModel order = mock(AbstractOrderModel.class);
        PriceData expectedPriceData = mock(PriceData.class);
        CurrencyModel currencyModel = mock(CurrencyModel.class);

        // when
        when(priceDataFactory.create(eq(PriceDataType.BUY), any(BigDecimal.class), any(CurrencyModel.class))).thenReturn(expectedPriceData);
        when(source.getCustomerReference()).thenReturn("Customer123");
        when(source.getBaseListPrice()).thenReturn(100.0);
        when(source.getTotalListPrice()).thenReturn(150.0);
        when(source.getBaseNetPrice()).thenReturn(90.0);
        when(source.getTotalNetPrice()).thenReturn(135.0);
        when(source.getIsBackOrder()).thenReturn(Boolean.TRUE);
        when(source.getOrder()).thenReturn(order);
        when(order.getCurrency()).thenReturn(currencyModel);

        distOrderEntryPopulator.populate(source, target);

        // then
        assertThat(target.getCustomerReference(), equalTo("Customer123"));
        assertThat(target.getBaseListPrice(), is(notNullValue()));
        assertThat(target.getTotalListPrice(), is(notNullValue()));
        assertThat(target.getBasePrice(), is(notNullValue()));
        assertThat(target.getTotalPrice(), is(notNullValue()));
        assertThat(target.isIsBackOrder(), is(true));
    }

    @Test
    public void testPopulateWithQuotation() {
        // given
        AbstractOrderEntryModel source = mock(AbstractOrderEntryModel.class);
        OrderEntryData target = new OrderEntryData();
        AbstractOrderModel order = mock(AbstractOrderModel.class);

        // when
        when(source.getQuotation()).thenReturn(Boolean.TRUE);
        when(source.getQuotationId()).thenReturn("Q123");
        when(source.getLineNumber()).thenReturn("42");
        when(source.getQuotationReference()).thenReturn("QRef123");
        when(source.getDummyItem()).thenReturn(Boolean.TRUE);
        when(source.getArticleDescription()).thenReturn("Sample Article");
        when(source.getMandatoryItem()).thenReturn(Boolean.TRUE);
        when(source.getQuoteModificationType()).thenReturn(QuoteModificationType.ALL);
        when(source.getOrder()).thenReturn(order);

        distOrderEntryPopulator.populate(source, target);

        // then
        assertThat(target.isIsQuotation(), is(true));
        assertThat(target.getQuotationId(),equalTo("Q123"));
        assertThat(target.getLineNumber(),equalTo("42"));
        assertThat(target.getQuotationReference(),equalTo("QRef123"));
        assertThat(target.isDummyItem(), is(true));
        assertThat(target.getArticleDescription(),equalTo("Sample Article"));
        assertThat(target.isMandatoryItem(), is(true));
        assertThat(target.getQuoteModificationType(),equalTo(QuoteModificationType.ALL));
    }


    @Test
    public void testPopulateWithAvailabilityInfos() {
        // given
        AbstractOrderEntryModel source = mock(AbstractOrderEntryModel.class);
        List<DistErpAvailabilityInfoModel> erpAvailabilityInfos = new ArrayList<>();
        DistErpAvailabilityInfoModel availabilityInfo1 = mock(DistErpAvailabilityInfoModel.class);
        erpAvailabilityInfos.add(availabilityInfo1);
        DistErpAvailabilityInfoModel availabilityInfo2 = mock(DistErpAvailabilityInfoModel.class);
        erpAvailabilityInfos.add(availabilityInfo2);
        AbstractOrderModel order = mock(AbstractOrderModel.class);
        OrderEntryData target = new OrderEntryData();

        // when
        when(availabilityInfo1.getQuantity()).thenReturn(10L);
        when(availabilityInfo1.getEstimatedDeliveryDate()).thenReturn(new Date());
        when(availabilityInfo2.getQuantity()).thenReturn(20L);
        when(availabilityInfo2.getEstimatedDeliveryDate()).thenReturn(new Date());
        when(source.getErpAvailabilityInfos()).thenReturn(erpAvailabilityInfos);
        when(source.getOrder()).thenReturn(order);
        when(i18NService.getCurrentLocale()).thenReturn(Locale.GERMAN);

        distOrderEntryPopulator.populate(source, target);

        // then
        assertThat(target.getAvailabilities(), hasSize(2));
        assertThat(target.getAvailabilities().get(0).getQuantity(), equalTo(10L));
        assertThat(target.getAvailabilities().get(1).getQuantity(), equalTo(20L));
    }

    @Test
    public void testPopulateWithNullAvailabilityInfos() {
        // given
        AbstractOrderEntryModel source = mock(AbstractOrderEntryModel.class);
        OrderEntryData target = new OrderEntryData();
        AbstractOrderModel order = mock(AbstractOrderModel.class);

        // when
        when(source.getErpAvailabilityInfos()).thenReturn(null);
        when(source.getOrder()).thenReturn(order);

        distOrderEntryPopulator.populate(source, target);

        // then
        assertThat(target.getAvailabilities(), is(nullValue()));
    }


    @Test
    public void testPopulateWithBOM() {
        // given
        AbstractOrderEntryModel source = mock(AbstractOrderEntryModel.class);
        SubOrderEntryModel subOrderEntry1 = mock(SubOrderEntryModel.class);
        SubOrderEntryModel subOrderEntry2 = mock(SubOrderEntryModel.class);
        List<SubOrderEntryModel> subOrderEntries = Arrays.asList(subOrderEntry1, subOrderEntry2);
        OrderEntryData target = new OrderEntryData();
        AbstractOrderModel order = mock(AbstractOrderModel.class);

        // when
        when(source.getIsBOM()).thenReturn(Boolean.TRUE);
        when(source.getSubOrderEntries()).thenReturn(subOrderEntries);
        when(source.getOrder()).thenReturn(order);

        distOrderEntryPopulator.populate(source, target);

        // then
        assertThat(target.getSubOrderEntryData(), hasSize(2));
    }
}
