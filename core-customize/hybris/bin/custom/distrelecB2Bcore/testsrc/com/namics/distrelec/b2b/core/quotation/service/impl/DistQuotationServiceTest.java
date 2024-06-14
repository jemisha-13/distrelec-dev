package com.namics.distrelec.b2b.core.quotation.service.impl;

import com.namics.distrelec.b2b.core.model.DistB2BQuoteLimitModel;
import com.namics.distrelec.b2b.core.quotation.dao.DistQuotationDao;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.SelfServiceQuotation.ATTRIBUTE_DEFAULT_CUSTOMER_QUOTE_SUBMIT_LIMIT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
public class DistQuotationServiceTest {
    @Mock
    private DistQuotationDao quotationDao;
    @Mock
    private UserService userService;
    @Mock
    private ModelService modelService;
    @Mock
    private ConfigurationService configurationService;
    @InjectMocks
    private DistQuotationService quotationService;
    @Mock
    private UserModel user;
    @Mock
    private DistB2BQuoteLimitModel quoteModel;
    @Mock
    private Configuration configuration;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        when(userService.getUserForUID(Mockito.anyString())).thenReturn(user);
        when(quotationDao.getQuotationCount(user)).thenReturn(quoteModel);
        when(configurationService.getConfiguration()).thenReturn(configuration);
    }

    @Test
    public void testCustomerIsUnderLimit(){
        when(quoteModel.getCurrentQuotationCount()).thenReturn(0);
        when(configuration.getInteger(ATTRIBUTE_DEFAULT_CUSTOMER_QUOTE_SUBMIT_LIMIT, 100)).thenReturn(5);
        boolean answer = quotationService.isCustomerOverQuotationLimit("0123456789");
        assertFalse(answer);
    }

    @Test
    public void testCustomerIsOverLimit(){
        when(quoteModel.getCurrentQuotationCount()).thenReturn(6);
        when(configuration.getInteger(ATTRIBUTE_DEFAULT_CUSTOMER_QUOTE_SUBMIT_LIMIT, 100)).thenReturn(5);
        boolean answer = quotationService.isCustomerOverQuotationLimit("0123456789");
        assertTrue(answer);
    }

    @Test
    public void testCustomerIsAtLimit(){
        when(quoteModel.getCurrentQuotationCount()).thenReturn(5);
        when(configuration.getInteger(ATTRIBUTE_DEFAULT_CUSTOMER_QUOTE_SUBMIT_LIMIT, 100)).thenReturn(5);
        boolean answer = quotationService.isCustomerOverQuotationLimit("0123456789");
        assertFalse(answer);
    }

    @Test
    public void testIncrementCounterWithNonExistingRecord(){
        when(quoteModel.getCurrentQuotationCount()).thenReturn(0);
        when(quotationDao.getQuotationCount(user)).thenReturn(null);
        doReturn(quoteModel).when(modelService).create(DistB2BQuoteLimitModel.class);
        doNothing().when(modelService).save(DistB2BQuoteLimitModel.class);

        boolean answer = quotationService.incrementQuotationCounter("UID");
        assertTrue(answer);

        verify(quotationDao, times(1)).getQuotationCount(user);
        verify(modelService, times(1)).create(DistB2BQuoteLimitModel.class);
        verify(modelService, times(1)).save(quoteModel);
    }


    @Test
    public void testIncrementCounterWithExistingRecord(){
        when(quoteModel.getCurrentQuotationCount()).thenReturn(0);
        when(quotationDao.getQuotationCount(user)).thenReturn(quoteModel);

        boolean answer = quotationService.incrementQuotationCounter("UID");
        assertTrue(answer);

        verify(quotationDao, times(1)).getQuotationCount(user);
        verify(modelService, times(0)).create(DistB2BQuoteLimitModel.class);
        verify(modelService, times(1)).save(quoteModel);
    }

    @Test
    public void testIncrementCounterWithNullUser(){
        when(userService.getUserForUID(Mockito.anyString())).thenReturn(null);
        when(quoteModel.getCurrentQuotationCount()).thenReturn(0);

        boolean answer = quotationService.incrementQuotationCounter("UID");
        assertFalse(answer);

        verify(quotationDao, times(0)).getQuotationCount(user);
        verify(modelService, times(0)).create(DistB2BQuoteLimitModel.class);
        verify(modelService, times(0)).save(quoteModel);
    }

    @Test
    public void testIncrementCounterWithUserOverTheLimit(){
        when(userService.getUserForUID(Mockito.anyString())).thenReturn(user);
        when(quoteModel.getCurrentQuotationCount()).thenReturn(6);
        when(configuration.getInteger(ATTRIBUTE_DEFAULT_CUSTOMER_QUOTE_SUBMIT_LIMIT, 100)).thenReturn(5);
        doReturn(quoteModel).when(modelService).create(DistB2BQuoteLimitModel.class);

        boolean answer = quotationService.incrementQuotationCounter("UID");
        assertFalse(answer);

        verify(quotationDao, times(1)).getQuotationCount(user);
        verify(modelService, times(0)).create(DistB2BQuoteLimitModel.class);
        verify(modelService, times(0)).save(quoteModel);
    }

    private List<DistB2BQuoteLimitModel> createDaoResponse() {
        DistB2BQuoteLimitModel quoteModel2 = mock(DistB2BQuoteLimitModel.class);
        DistB2BQuoteLimitModel quoteModel3 = mock(DistB2BQuoteLimitModel.class);
        return Stream.of(quoteModel, quoteModel2, quoteModel3).collect(Collectors.toList());
    }


    private Date convertToDate(final LocalDateTime dateToConvert) {
        return Date.from(dateToConvert.atZone(ZoneId.systemDefault()).toInstant());
    }
}
