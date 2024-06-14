package com.namics.distrelec.b2b.facades.newsletter.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.namics.distrelec.b2b.core.bloomreach.exception.DistBloomreachBatchException;
import com.namics.distrelec.b2b.core.obsolescence.service.DistObsolescenceService;
import com.namics.distrelec.b2b.facades.bloomreach.DistBloomreachFacade;
import com.namics.distrelec.b2b.facades.user.data.DistConsentData;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDistNewsletterFacadeUnitTest {

    private static final String TEST_EMAIL = "testemailselected@testdistrelec123.com";

    private static final String DUMMY_EMAIL = "testemaildeactivated@testdistrelec123.com";

    @Mock
    private UserService userService;

    @Mock
    private B2BCustomerModel b2bCustomer;

    @Mock
    private DistObsolescenceService distObsolescenceService;

    @Mock
    private ModelService modelService;

    @Mock
    private DistBloomreachFacade distBloomreachFacade;

    @InjectMocks
    private DefaultDistNewsletterFacade defaultDistNewsletterFacade;

    @Test
    public void testOptInForAllObsolescenceEmailsForExistingUser() {
        when(userService.getUserForUID(TEST_EMAIL)).thenReturn(b2bCustomer);

        defaultDistNewsletterFacade.optInForAllObsolescenceEmailsForExistingUser(TEST_EMAIL);

        verify(distObsolescenceService, times(1)).saveObsolescenceCategoriesForCustomer(any(CustomerModel.class), eq(true));
    }

    @Test
    public void testOptInForAllObsolescenceEmailsForNonExistingUser() {
        when(userService.getUserForUID(DUMMY_EMAIL)).thenThrow(UnknownIdentifierException.class);

        defaultDistNewsletterFacade.optInForAllObsolescenceEmailsForExistingUser(DUMMY_EMAIL);

        verify(distObsolescenceService, times(0)).saveObsolescenceCategoriesForCustomer(any(CustomerModel.class), eq(true));
    }

    @Test
    public void testOptInForAllObsolescenceEmailsForCurrentUser() {
        when(userService.getCurrentUser()).thenReturn(b2bCustomer);

        defaultDistNewsletterFacade.optInForAllObsolescenceEmailsForCurrentUser();

        verify(modelService, times(1)).save(b2bCustomer);
    }

    @Test
    public void testHandleBloomreachNewsletterSubscription() throws IOException, DistBloomreachBatchException {
        final DistConsentData consentData = createConsentData(TEST_EMAIL);
        when(distBloomreachFacade.createBloomreachSubscriptionRequest(consentData)).thenReturn(anyString());

        boolean result = defaultDistNewsletterFacade.handleBloomreachNewsletterSubscription(consentData);

        assertTrue(result);
        verify(distBloomreachFacade, times(1)).sendBatchRequestToBloomreach(anyString());
    }

    @Test
    public void testHandleBloomreachNewsletterSubscriptionThrowsIOException() throws IOException, DistBloomreachBatchException {
        final DistConsentData consentData = createConsentData(TEST_EMAIL);
        when(distBloomreachFacade.createBloomreachSubscriptionRequest(consentData)).thenThrow(IOException.class);

        boolean result = defaultDistNewsletterFacade.handleBloomreachNewsletterSubscription(consentData);

        assertFalse(result);
        verify(distBloomreachFacade, times(0)).sendBatchRequestToBloomreach(anyString());
    }

    private DistConsentData createConsentData(final String email) {
        final DistConsentData consentData = new DistConsentData();
        consentData.setUid(email);
        consentData.setIsAnonymousUser(true);
        consentData.setIsRegistration(true);
        consentData.setActiveSubscription(true);
        consentData.setPhonePermission(false);
        consentData.setSmsPermissions(false);
        consentData.setPaperPermission(false);
        consentData.setPersonalisationSubscription(false);
        consentData.setProfilingSubscription(false);
        consentData.setPlacement("footer");
        return consentData;
    }
}
