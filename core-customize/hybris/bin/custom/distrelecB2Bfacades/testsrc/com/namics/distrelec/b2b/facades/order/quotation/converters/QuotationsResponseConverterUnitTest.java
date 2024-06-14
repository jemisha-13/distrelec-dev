package com.namics.distrelec.b2b.facades.order.quotation.converters;

import com.distrelec.webservice.if18.v1.CurrencyCode;
import com.distrelec.webservice.if18.v1.QuotationsResponse;
import com.namics.distrelec.b2b.core.model.DistQuotationStatusModel;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuotationHistoryData;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuoteStatusData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.site.BaseSiteService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class QuotationsResponseConverterUnitTest {

    @Mock
    private Converter<CurrencyModel, CurrencyData> currencyConverter;

    @Mock
    private PriceDataFactory priceDataFactory;

    @Mock
    private CommonI18NService commonI18NService;

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

    @InjectMocks
    private QuotationsResponseConverter converter;

    @Test
    public void testPopulate() {
        // given
        QuotationsResponse source = mock(QuotationsResponse.class);
        QuotationHistoryData target = new QuotationHistoryData();
        CMSSiteModel cmsSiteModel = mock(CMSSiteModel.class);

        // when
        when(source.getQuotationId()).thenReturn("Q123");
        when(source.getPOnumber()).thenReturn("PO123");
        when(source.getCustomerName()).thenReturn("Distrelec Customer");
        when(source.getQuotationDocURL()).thenReturn("http://www.distrelec.com/doc");
        when(source.getQuotationStatus()).thenReturn("Active");
        when(source.getTotal()).thenReturn(1000.0);
        when(source.getCurrencyCode()).thenReturn(CurrencyCode.CHF);
        when(cmsSiteService.getCurrentSite()).thenReturn(cmsSiteModel);
        when(cmsSiteModel.getCountry()).thenReturn(mock(CountryModel.class));

        converter.populate(source, target);

        // then
        assertThat(target.getQuotationId(),equalTo("Q123"));
        assertThat(target.getPoNumber(),equalTo("PO123"));
        assertThat(target.getCustomerName(), equalTo("Distrelec Customer"));
        assertThat(target.getQuotationDocURL(), equalTo("http://www.distrelec.com/doc"));
    }
}
