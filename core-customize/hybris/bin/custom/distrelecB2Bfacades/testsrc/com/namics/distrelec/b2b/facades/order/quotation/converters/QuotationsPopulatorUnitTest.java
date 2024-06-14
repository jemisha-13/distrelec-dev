package com.namics.distrelec.b2b.facades.order.quotation.converters;

import com.distrelec.webservice.if18.v1.CurrencyCode;
import com.distrelec.webservice.if18.v1.QuotationReadEntryResponse;
import com.distrelec.webservice.if18.v1.ReadQuotationsResponse;
import com.namics.distrelec.b2b.core.model.DistQuotationStatusModel;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuotationData;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuoteStatusData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.converters.ConfigurablePopulator;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.site.BaseSiteService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigInteger;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class QuotationsPopulatorUnitTest {

    @Mock
    private CommonI18NService commonI18NService;

    @Mock
    private Converter<CurrencyModel, CurrencyData> currencyConverter;

    @Mock
    private PriceDataFactory priceDataFactory;

    @Mock
    private Converter<ProductModel, ProductData> productConverter;

    @Mock
    private ProductService productService;

    @Mock
    private DistrelecCodelistService distCodelistService;

    @Mock
    private BaseSiteService baseSiteService;

    @Mock
    private CMSSiteService cmsSiteService;

    @Mock
    private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

    @Mock
    private Converter<DistQuotationStatusModel, QuoteStatusData> quoteStatusDataConverter;

    @Mock
    private ConfigurablePopulator<ProductModel, ProductData, ProductOption> productConfiguredPopulator;

    @InjectMocks
    private QuotationsPopulator populator;

    @Test
    public void testPopulate() {
        // given
        ReadQuotationsResponse source = mock(ReadQuotationsResponse.class);
        CurrencyModel currencyModel = mock(CurrencyModel.class);
        DistQuotationStatusModel statusModel = mock(DistQuotationStatusModel.class);
        BaseSiteModel baseSiteModel = mock(BaseSiteModel.class);
        QuotationData target = new QuotationData();
        CMSSiteModel cmsSiteModel = mock(CMSSiteModel.class);

        // when
        prepareBasicData(currencyModel, statusModel, cmsSiteModel, baseSiteModel, source);

        populator.populate(source, target);

        // then
        assertThat(target.getCustomerId(), equalTo("12345"));
        assertThat(target.getQuotationId(), equalTo("Q123"));
        assertThat(target.getQuotationNote(), equalTo("Note"));
        assertThat(target.getQuotationChannel(), equalTo("Online"));
    }

    @Test
    public void testPopulateWithEntryResponse() {
        // given
        ReadQuotationsResponse source = mock(ReadQuotationsResponse.class);
        CurrencyModel currencyModel = mock(CurrencyModel.class);
        DistQuotationStatusModel statusModel = mock(DistQuotationStatusModel.class);
        BaseSiteModel baseSiteModel = mock(BaseSiteModel.class);
        QuotationData target = new QuotationData();
        CMSSiteModel cmsSiteModel = mock(CMSSiteModel.class);
        QuotationReadEntryResponse readEntryResponse = mock(QuotationReadEntryResponse.class);

        // when
        prepareBasicData(currencyModel, statusModel, cmsSiteModel, baseSiteModel, source);
        prepareEntryResponse(readEntryResponse, source);

        populator.populate(source, target);

        // then
        assertThat(target.getQuotationEntries().get(0).getItemNumber(), equalTo("Item1"));
        assertThat(target.getQuotationEntries().get(0).isDummyItem(), is(false));
        assertThat(target.getQuotationEntries().get(0).getArticleDescription(),equalTo("Description"));
        assertThat(target.getQuotationEntries().get(0).isMandatory(), is(true));
    }

    private void prepareEntryResponse(QuotationReadEntryResponse readEntryResponse, ReadQuotationsResponse source) {
        when(readEntryResponse.getItemNumber()).thenReturn("Item1");
        when(readEntryResponse.isDummyItem()).thenReturn(false);
        when(readEntryResponse.getArticleDescription()).thenReturn("Description");
        when(readEntryResponse.isMandatoryItem()).thenReturn(true);
        when(readEntryResponse.getQuantityModification()).thenReturn("01");
        when(readEntryResponse.getItemNote()).thenReturn("Note");
        when(readEntryResponse.getManufacturerPartNumber()).thenReturn("MPN123");
        when(readEntryResponse.getManufacturerType()).thenReturn("TypeA");
        when(readEntryResponse.getCustomerReferenceItemLevel()).thenReturn("Ref123");
        when(readEntryResponse.getYourItemRef()).thenReturn("YourRef123");
        when(readEntryResponse.getArticleNumber()).thenReturn("A123");
        when(readEntryResponse.getQuantity()).thenReturn(BigInteger.valueOf(10));
        when(readEntryResponse.getPrice()).thenReturn(100.0);
        when(readEntryResponse.getSubtotal1()).thenReturn(1000.0);
        when(source.getQuotationEntries()).thenReturn(Arrays.asList(readEntryResponse));
    }

    private void prepareBasicData(CurrencyModel currencyModel, DistQuotationStatusModel statusModel, CMSSiteModel cmsSiteModel, BaseSiteModel baseSiteModel,
                           ReadQuotationsResponse source) {
        when(commonI18NService.getCurrency(anyString())).thenReturn(currencyModel);
        when(currencyConverter.convert(any(CurrencyModel.class))).thenReturn(new CurrencyData());
        when(distCodelistService.getDistQuotationStatus(anyString())).thenReturn(statusModel);
        when(quoteStatusDataConverter.convert(any(DistQuotationStatusModel.class))).thenReturn(new QuoteStatusData());
        when(cmsSiteService.getCurrentSite()).thenReturn(cmsSiteModel);
        when(cmsSiteModel.getCountry()).thenReturn(mock(CountryModel.class));
        when(baseSiteService.getBaseSiteForUID(anyString())).thenReturn(baseSiteModel);
        when(siteBaseUrlResolutionService.getWebsiteUrlForSite(any(BaseSiteModel.class), anyBoolean(), anyString())).thenReturn("test-url");
        when(source.getCustomerId()).thenReturn("12345");
        when(source.getQuotationId()).thenReturn("Q123");
        when(source.getContactName()).thenReturn("Distrelec Customer");
        when(source.getPOnumber()).thenReturn("PO12345");
        when(source.getQuotationChannel()).thenReturn("Online");
        when(source.isFullQuote()).thenReturn(true);
        when(source.getQuotationNote()).thenReturn("Note");
        when(source.getQuotationStatus()).thenReturn("Active");
        when(source.getCurrencyCode()).thenReturn(CurrencyCode.CHF);
    }
}
