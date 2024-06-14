package com.namics.distrelec.b2b.facades.bloomreach.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.namics.distrelec.b2b.core.bloomreach.client.impl.DistBloomreachAPIClientImpl;
import com.namics.distrelec.b2b.core.bloomreach.enums.BloomreachTouchpoint;
import com.namics.distrelec.b2b.core.bloomreach.exception.DistBloomreachBatchException;
import com.namics.distrelec.b2b.core.bloomreach.exception.DistBloomreachExportException;
import com.namics.distrelec.b2b.core.service.url.DistSiteBaseUrlResolutionService;
import com.namics.distrelec.b2b.core.service.user.DistUserService;
import com.namics.distrelec.b2b.facades.bloomreach.data.DistBloomreachConsentData;
import com.namics.distrelec.b2b.facades.user.data.DistConsentData;
import com.namics.distrelec.b2b.facades.user.data.DistMarketingConsentData;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.site.BaseSiteService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistBloomreachFacadeImplTest extends ServicelayerTest {

    private static final String TEST_EMAIL = "testemailselected@testdistrelec123.com";

    private static final String TEST_ERP_CONTACT_ID = "0000000";

    private static final String SWISS_PHONE_NUMBER = "+41212345678";

    private static final String GERMAN_PHONE_NUMBER = "+493012345678";

    private static final String PREFERENCE_CENTER_STRING = "{\"results\": [{\"success\": true, \"value\": true}, {\"success\": true, " +
                                                           "\"value\": false}, {\"success\": true, \"value\": true}, {\"success\": " +
                                                           "true, \"value\": true}, {\"success\": true, \"value\": false}, " +
                                                           "{\"success\": true, \"value\": false}, {\"success\": true, \"value\": " +
                                                           "false}, {\"success\": true, \"value\": true}, {\"success\": true, " +
                                                           "\"value\": false}, {\"success\": true, \"value\": false}, {\"success\": " +
                                                           "true, \"value\": false}], \"success\": true}";

    private static final String PREFERENCE_CENTER_ALL_UNCHECKED = "{\"results\": [{\"success\": true, \"value\": false}, {\"success\": true, " +
                                                                  "\"value\": false}, {\"success\": true, \"value\": false}, {\"success\": " +
                                                                  "true, \"value\": false}, {\"success\": true, \"value\": false}, " +
                                                                  "{\"success\": true, \"value\": false}, {\"success\": true, \"value\": " +
                                                                  "false}, {\"success\": true, \"value\": false}, {\"success\": true, " +
                                                                  "\"value\": false}, {\"success\": true, \"value\": false}, {\"success\": " +
                                                                  "true, \"value\": false}], \"success\": true}";

    private static final String PREFERENCE_CENTER_ALL_SELECTED = "{\"results\": [{\"success\": true, \"value\": true}, {\"success\": true, " +
                                                                 "\"value\": true}, {\"success\": true, \"value\": true}, {\"success\": " +
                                                                 "true, \"value\": true}, {\"success\": true, \"value\": true}, " +
                                                                 "{\"success\": true, \"value\": true}, {\"success\": true, \"value\": " +
                                                                 "true}, {\"success\": true, \"value\": true}, {\"success\": true, " +
                                                                 "\"value\": true}, {\"success\": true, \"value\": true}, {\"success\": " +
                                                                 "true, \"value\": true}], \"success\": true}";

    private static final String SUBSCRIPTION_REQUEST_STRING = "{\"commands\":[{\"name\":\"customers\",\"command_id\":\"customer_change\"," +
                                                              "\"data\":{\"customer_ids\":{\"email_id\":\"testemailselected@testdistrelec123.com\"," +
                                                              "\"erp_contact_id\":null},\"properties\":{\"email\":\"testemailselected@testdistrelec123.com\"," +
                                                              "\"web_store_url\":\"https://distrelec-ch.local:4200/\",\"language\":\"en\"," +
                                                              "\"placement\":\"footer\"},\"event_type\":null}}," +
                                                              "{\"name\":\"customers/events\",\"command_id\":\"consent_change_email\"," +
                                                              "\"data\":{\"customer_ids\":{\"email_id\":\"testemailselected@testdistrelec123.com\"," +
                                                              "\"erp_contact_id\":null},\"properties\":{\"email\":\"testemailselected@testdistrelec123.com\"," +
                                                              "\"web_store_url\":\"https://distrelec-ch.local:4200/\",\"language\":\"en\"," +
                                                              "\"placement\":\"footer\",\"first_name\":null,\"last_name\":null," +
                                                              "\"title\":null,\"phone\":null,\"mobile\":null,\"country\":null," +
                                                              "\"action\":\"accept\",\"category\":\"email\",\"valid_until\":\"unlimited\"," +
                                                              "\"message\":null,\"source\":null},\"event_type\":\"consent\"}}," +
                                                              "{\"name\":\"customers/events\"," +
                                                              "\"command_id\":\"consent_change_content_news\"," +
                                                              "\"data\":{\"customer_ids\":{\"email_id\":\"testemailselected@testdistrelec123.com\"," +
                                                              "\"erp_contact_id\":null},\"properties\":{\"email\":\"testemailselected@testdistrelec123.com\"," +
                                                              "\"web_store_url\":\"https://distrelec-ch.local:4200/\",\"language\":\"en\"," +
                                                              "\"placement\":\"footer\",\"first_name\":null,\"last_name\":null," +
                                                              "\"title\":null,\"phone\":null,\"mobile\":null,\"country\":null," +
                                                              "\"action\":\"accept\",\"category\":\"content_news\"," +
                                                              "\"valid_until\":\"unlimited\",\"message\":null,\"source\":null}," +
                                                              "\"event_type\":\"consent\"}},{\"name\":\"customers/events\"," +
                                                              "\"command_id\":\"consent_change_nps\"," +
                                                              "\"data\":{\"customer_ids\":{\"email_id\":\"testemailselected@testdistrelec123.com\"," +
                                                              "\"erp_contact_id\":null},\"properties\":{\"email\":\"testemailselected@testdistrelec123.com\"," +
                                                              "\"web_store_url\":\"https://distrelec-ch.local:4200/\",\"language\":\"en\"," +
                                                              "\"placement\":\"footer\",\"first_name\":null,\"last_name\":null," +
                                                              "\"title\":null,\"phone\":null,\"mobile\":null,\"country\":null," +
                                                              "\"action\":\"accept\",\"category\":\"nps\",\"valid_until\":\"unlimited\"," +
                                                              "\"message\":null,\"source\":null},\"event_type\":\"consent\"}}," +
                                                              "{\"name\":\"customers/events\"," +
                                                              "\"command_id\":\"consent_change_personalised_recommendations\"," +
                                                              "\"data\":{\"customer_ids\":{\"email_id\":\"testemailselected@testdistrelec123.com\"," +
                                                              "\"erp_contact_id\":null},\"properties\":{\"email\":\"testemailselected@testdistrelec123.com\"," +
                                                              "\"web_store_url\":\"https://distrelec-ch.local:4200/\",\"language\":\"en\"," +
                                                              "\"placement\":\"footer\",\"first_name\":null,\"last_name\":null," +
                                                              "\"title\":null,\"phone\":null,\"mobile\":null,\"country\":null," +
                                                              "\"action\":\"accept\",\"category\":\"personalised_recommendations\"," +
                                                              "\"valid_until\":\"unlimited\",\"message\":null,\"source\":null}," +
                                                              "\"event_type\":\"consent\"}},{\"name\":\"customers/events\"," +
                                                              "\"command_id\":\"consent_change_sales_clearance\"," +
                                                              "\"data\":{\"customer_ids\":{\"email_id\":\"testemailselected@testdistrelec123.com\"," +
                                                              "\"erp_contact_id\":null},\"properties\":{\"email\":\"testemailselected@testdistrelec123.com\"," +
                                                              "\"web_store_url\":\"https://distrelec-ch.local:4200/\",\"language\":\"en\"," +
                                                              "\"placement\":\"footer\",\"first_name\":null,\"last_name\":null," +
                                                              "\"title\":null,\"phone\":null,\"mobile\":null,\"country\":null," +
                                                              "\"action\":\"accept\",\"category\":\"sales_clearance\"," +
                                                              "\"valid_until\":\"unlimited\",\"message\":null,\"source\":null}," +
                                                              "\"event_type\":\"consent\"}},{\"name\":\"customers/events\"," +
                                                              "\"command_id\":\"consent_change_new_brands\"," +
                                                              "\"data\":{\"customer_ids\":{\"email_id\":\"testemailselected@testdistrelec123.com\"," +
                                                              "\"erp_contact_id\":null},\"properties\":{\"email\":\"testemailselected@testdistrelec123.com\"," +
                                                              "\"web_store_url\":\"https://distrelec-ch.local:4200/\",\"language\":\"en\"," +
                                                              "\"placement\":\"footer\",\"first_name\":null,\"last_name\":null," +
                                                              "\"title\":null,\"phone\":null,\"mobile\":null,\"country\":null," +
                                                              "\"action\":\"accept\",\"category\":\"new_brands\"," +
                                                              "\"valid_until\":\"unlimited\",\"message\":null,\"source\":null}," +
                                                              "\"event_type\":\"consent\"}}]}";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private Configuration mockConfiguration;

    @Mock
    private BaseSiteService baseSiteService;

    @Mock
    private DistSiteBaseUrlResolutionService distSiteBaseUrlResolutionService;

    @Mock
    private DistBloomreachAPIClientImpl distBloomreachAPIClient;

    @Mock
    private DistUserService userService;

    @Mock
    private BaseSiteModel swissBaseSite;

    @Mock
    private BaseSiteModel germanBaseSite;

    @Mock
    private B2BCustomerModel swissCustomer;

    @Mock
    private B2BCustomerModel germanCustomer;

    @Mock
    private B2BCustomerModel anonymousCustomer;

    @InjectMocks
    private DistBloomreachFacadeImpl distBloomreachFacadeImpl;

    @Before
    public void setUp() {
        when(configurationService.getConfiguration()).thenReturn(mockConfiguration);
        when(mockConfiguration.getString("ymkt.customer.sevice.consent.confirmation.required.shop")).thenReturn("distrelec_DE");

        when(swissBaseSite.getUid()).thenReturn("distrelec_CH");
        when(distSiteBaseUrlResolutionService.getHeadlessWebsiteUrlForSite(swissBaseSite, true, "/")).
                thenReturn("https://distrelec-ch.local:4200/");
        when(swissCustomer.getCustomersBaseSite()).thenReturn(swissBaseSite);
        when(userService.isAnonymousUser(swissCustomer)).thenReturn(false);

        when(germanBaseSite.getUid()).thenReturn("distrelec_DE");
        when(distSiteBaseUrlResolutionService.getHeadlessWebsiteUrlForSite(germanBaseSite, true, "/")).
                thenReturn("https://distrelec-de.local:4200/");
        when(germanCustomer.getCustomersBaseSite()).thenReturn(germanBaseSite);
        when(userService.isAnonymousUser(germanCustomer)).thenReturn(false);

        when(userService.isAnonymousUser(anonymousCustomer)).thenReturn(true);
    }

    @Test
    public void testCreateBloomreachRegistrationRequest() throws IOException {
        final DistConsentData consentData = createRegistrationConsentData(true, "CH", SWISS_PHONE_NUMBER);
        final String data = getExpectedData("distrelecB2Bfacades/test/Registration/ExpectedJsonAllCheckboxSelected.json");
        when(userService.getCurrentUser()).thenReturn(anonymousCustomer);
        when(baseSiteService.getCurrentBaseSite()).thenReturn(swissBaseSite);

        final String requestString = distBloomreachFacadeImpl.createBloomreachRegistrationRequest(consentData);

        JSONAssert.assertEquals(data, requestString, JSONCompareMode.LENIENT);
    }

    private String getExpectedData(final String path) throws IOException {
        Resource resource = new ClassPathResource(path);
        return new String(resource.getInputStream().readAllBytes());
    }

    @Test
    public void testCreateBloomreachRegistrationRequestRejectsAllConsents() throws IOException {
        final DistConsentData consentData = createRegistrationConsentData(false, "CH", SWISS_PHONE_NUMBER);
        final String data = getExpectedData("distrelecB2Bfacades/test/Registration/ExpectedJsonAllCheckboxUnchecked.json");

        when(baseSiteService.getCurrentBaseSite()).thenReturn(swissBaseSite);

        final String requestString = distBloomreachFacadeImpl.createBloomreachRegistrationRequest(consentData);

        JSONAssert.assertEquals(data, requestString, JSONCompareMode.LENIENT);
    }

    @Test
    public void testCreateBloomreachRegistrationRequestForRsCustomer() throws IOException {
        final DistConsentData consentData = createRegistrationConsentData(true, "CH", SWISS_PHONE_NUMBER);
        consentData.setRsCustomer(true);
        final String data = getExpectedData("distrelecB2Bfacades/test/Registration/ExpectedJsonRsCustomerAllSelected.json");
        when(userService.getCurrentUser()).thenReturn(swissCustomer);
        when(baseSiteService.getCurrentBaseSite()).thenReturn(swissBaseSite);

        final String requestString = distBloomreachFacadeImpl.createBloomreachRegistrationRequest(consentData);

        JSONAssert.assertEquals(data, requestString, JSONCompareMode.LENIENT);
    }

    @Test
    public void testCreateBloomreachRegistrationRequestEnablesDoubleOptIn() throws IOException {
        final DistConsentData consentData = createRegistrationConsentData(true, "DE", GERMAN_PHONE_NUMBER);
        final String data = getExpectedData("distrelecB2Bfacades/test/Registration/ExpectedJsonDoubleOptInAllSelected.json");

        when(userService.getCurrentUser()).thenReturn(anonymousCustomer);
        when(baseSiteService.getCurrentBaseSite()).thenReturn(germanBaseSite);

        final String requestString = distBloomreachFacadeImpl.createBloomreachRegistrationRequest(consentData);

        JSONAssert.assertEquals(data, requestString, JSONCompareMode.LENIENT);
    }

    @Test
    public void testCreateBloomreachRegistrationRequestDisablesDoubleOptIn() throws IOException {
        final DistConsentData consentData = createRegistrationConsentData(false, "DE", GERMAN_PHONE_NUMBER);
        final String data = getExpectedData("distrelecB2Bfacades/test/Registration/ExpectedJsonDoubleOptInAllUnchecked.json");

        when(baseSiteService.getCurrentBaseSite()).thenReturn(germanBaseSite);

        final String requestString = distBloomreachFacadeImpl.createBloomreachRegistrationRequest(consentData);

        JSONAssert.assertEquals(data, requestString, JSONCompareMode.LENIENT);
    }

    private DistConsentData createRegistrationConsentData(boolean selected, final String countryCode, final String phoneNumber) {
        final DistConsentData consentData = new DistConsentData();
        consentData.setUid(TEST_EMAIL);
        consentData.setTitleCode("mr");
        consentData.setFirstName("Firstname");
        consentData.setLastName("Lastname");
        consentData.setPhoneNumber(phoneNumber);
        consentData.setErpContactId(TEST_ERP_CONTACT_ID);
        consentData.setActiveSubscription(selected);
        consentData.setPhonePermission(selected);
        consentData.setSmsPermissions(selected);
        consentData.setPaperPermission(selected);
        consentData.setPersonalisationSubscription(selected);
        consentData.setProfilingSubscription(selected);
        consentData.setCountryCode(countryCode);
        return consentData;
    }

    @Test
    public void testCreateBloomreachSubscriptionRequest() throws IOException {
        final DistConsentData consentData = createSubscriptionConsentData(true, false, "footer");
        final String data = getExpectedData("distrelecB2Bfacades/test/Subscription/ExpectedJsonSubscriptionRequest.json");

        when(userService.getCurrentUser()).thenReturn(anonymousCustomer);
        when(baseSiteService.getCurrentBaseSite()).thenReturn(swissBaseSite);

        final String requestString = distBloomreachFacadeImpl.createBloomreachSubscriptionRequest(consentData);

        JSONAssert.assertEquals(data, requestString, JSONCompareMode.LENIENT);
    }

    @Test
    public void testCreateBloomreachSubscriptionRequestEnablesDoubleOptIn() throws IOException {
        final DistConsentData consentData = createSubscriptionConsentData(true, false, "footer");
        final String data = getExpectedData("distrelecB2Bfacades/test/Subscription/ExpectedJsonDoubleOptInSubscription.json");

        when(userService.getCurrentUser()).thenReturn(anonymousCustomer);
        when(baseSiteService.getCurrentBaseSite()).thenReturn(germanBaseSite);

        final String requestString = distBloomreachFacadeImpl.createBloomreachSubscriptionRequest(consentData);

        JSONAssert.assertEquals(data, requestString, JSONCompareMode.LENIENT);
    }

    @Test
    public void testCreateBloomreachSubscriptionRequestAcceptsPersonalisation() throws IOException {
        final DistConsentData consentData = createSubscriptionConsentData(false, true, "order_confirmation");
        final String data = getExpectedData("distrelecB2Bfacades/test/Subscription/ExpectedJsonPersonalisationChecked.json");

        when(userService.getCurrentUser()).thenReturn(swissCustomer);
        when(baseSiteService.getCurrentBaseSite()).thenReturn(swissBaseSite);

        final String requestString = distBloomreachFacadeImpl.createBloomreachSubscriptionRequest(consentData);

        JSONAssert.assertEquals(data, requestString, JSONCompareMode.LENIENT);
    }

    @Test
    public void testCreateBloomreachSubscriptionRequestRejectsPersonalisation() throws IOException {
        final DistConsentData consentData = createSubscriptionConsentData(false, false, "order_confirmation");
        final String data = getExpectedData("distrelecB2Bfacades/test/Subscription/ExpectedJsonPersonalisationUnchecked.json");

        when(userService.getCurrentUser()).thenReturn(swissCustomer);
        when(baseSiteService.getCurrentBaseSite()).thenReturn(swissBaseSite);

        final String requestString = distBloomreachFacadeImpl.createBloomreachSubscriptionRequest(consentData);

        JSONAssert.assertEquals(data, requestString, JSONCompareMode.LENIENT);
    }

    private DistConsentData createSubscriptionConsentData(boolean anonymous, boolean personalisationChecked, String placement) {
        final DistConsentData consentData = new DistConsentData();
        consentData.setUid(TEST_EMAIL);
        consentData.setIsAnonymousUser(anonymous);
        consentData.setIsRegistration(true);
        consentData.setActiveSubscription(true);
        consentData.setPhonePermission(false);
        consentData.setSmsPermissions(false);
        consentData.setPaperPermission(false);
        consentData.setPersonalisationSubscription(personalisationChecked);
        consentData.setProfilingSubscription(false);
        consentData.setPlacement(placement);
        return consentData;
    }

    @Test
    public void testGetPreferenceCenterUpdatesAcceptsPhoneConsent() throws IOException, DistBloomreachExportException {
        final DistConsentData consentData = createPreferenceConsentData();
        final String data = getExpectedData("distrelecB2Bfacades/test/Update/ExpectedJsonPhoneConsentUpdate.json");

        final DistMarketingConsentData updatedConsents = new DistMarketingConsentData();
        updatedConsents.setActiveSubscription(false);
        updatedConsents.setCustomerSurveysConsent(false);
        updatedConsents.setEmailConsent(true);
        updatedConsents.setIsAnonymousUser(false);
        updatedConsents.setIsRegistration(false);
        updatedConsents.setKnowHowConsent(false);
        updatedConsents.setNewsLetterConsent(true);
        updatedConsents.setNpsSubscription(false);
        updatedConsents.setObsolescenceConsent(true);
        updatedConsents.setPersonalisationConsent(false);
        updatedConsents.setPersonalisedRecommendationConsent(false);
        updatedConsents.setPhoneConsent(true);
        updatedConsents.setPhoneNumber(SWISS_PHONE_NUMBER);
        updatedConsents.setPostConsent(false);
        updatedConsents.setProfilingConsent(true);
        updatedConsents.setSaleAndClearanceConsent(true);
        updatedConsents.setSegmentedTopic(false);
        updatedConsents.setSelectAllemailConsents(true);
        updatedConsents.setSmsConsent(false);
        updatedConsents.setTermsAndConditionsConsent(false);
        updatedConsents.setUid(TEST_EMAIL);

        when(userService.getCurrentUser()).thenReturn(swissCustomer);
        when(baseSiteService.getCurrentBaseSite()).thenReturn(swissBaseSite);
        when(distBloomreachAPIClient.exportCustomerConsents(anyString())).thenReturn(PREFERENCE_CENTER_STRING);

        final String requestString =
                distBloomreachFacadeImpl.getPreferenceCenterUpdates(updatedConsents, consentData, BloomreachTouchpoint.MYACCOUNT);

        JSONAssert.assertEquals(data, requestString, JSONCompareMode.LENIENT);
    }

    @Test
    public void testGetPreferenceCenterUpdates() throws IOException, DistBloomreachExportException {
        final DistConsentData consentData = createPreferenceConsentData();
        final String data = getExpectedData("distrelecB2Bfacades/test/Update/ExpectedJsonConsentsUpdate.json");
        final DistMarketingConsentData updatedConsents = createUpdatedConsents(false, SWISS_PHONE_NUMBER);
        updatedConsents.setProfilingConsent(true);

        when(userService.getCurrentUser()).thenReturn(swissCustomer);
        when(baseSiteService.getCurrentBaseSite()).thenReturn(swissBaseSite);
        when(distBloomreachAPIClient.exportCustomerConsents(anyString())).thenReturn(PREFERENCE_CENTER_STRING);

        final String requestString =
                distBloomreachFacadeImpl.getPreferenceCenterUpdates(updatedConsents, consentData, BloomreachTouchpoint.MYACCOUNT);

        JSONAssert.assertEquals(data, requestString, JSONCompareMode.LENIENT);
    }

    @Test
    public void testGetPreferenceCenterUpdatesAcceptsAllConsents() throws IOException, DistBloomreachExportException {
        final DistConsentData consentData = createPreferenceConsentData();
        final String data = getExpectedData("distrelecB2Bfacades/test/Update/ExpectedJsonAllConsentsAccepted.json");
        final DistMarketingConsentData updatedConsents = createUpdatedConsents(true, SWISS_PHONE_NUMBER);

        when(userService.getCurrentUser()).thenReturn(swissCustomer);
        when(baseSiteService.getCurrentBaseSite()).thenReturn(swissBaseSite);
        when(distBloomreachAPIClient.exportCustomerConsents(anyString())).thenReturn(PREFERENCE_CENTER_ALL_UNCHECKED);

        final String requestString =
                distBloomreachFacadeImpl.getPreferenceCenterUpdates(updatedConsents, consentData, BloomreachTouchpoint.MYACCOUNT);

        JSONAssert.assertEquals(data, requestString, JSONCompareMode.LENIENT);
    }

    @Test
    public void testGetPreferenceCenterUpdatesRejectsAllConsents() throws IOException, DistBloomreachExportException {
        final DistConsentData consentData = createPreferenceConsentData();
        final String data = getExpectedData("distrelecB2Bfacades/test/Update/ExpectedJsonAllConsentsRejected.json");
        final DistMarketingConsentData updatedConsents = createUpdatedConsents(false, SWISS_PHONE_NUMBER);

        when(userService.getCurrentUser()).thenReturn(swissCustomer);
        when(baseSiteService.getCurrentBaseSite()).thenReturn(swissBaseSite);
        when(distBloomreachAPIClient.exportCustomerConsents(anyString())).thenReturn(PREFERENCE_CENTER_ALL_SELECTED);

        final String requestString =
                distBloomreachFacadeImpl.getPreferenceCenterUpdates(updatedConsents, consentData, BloomreachTouchpoint.MYACCOUNT);

        JSONAssert.assertEquals(data, requestString, JSONCompareMode.LENIENT);
    }

    @Test
    public void testGetPreferenceCenterUpdatesEnablesDoubleOptIn() throws IOException, DistBloomreachExportException {
        final DistConsentData consentData = createPreferenceConsentData();
        final String data = getExpectedData("distrelecB2Bfacades/test/Update/ExpectedJsonDoubleOptInUpdate.json");

        final DistMarketingConsentData updatedConsents = new DistMarketingConsentData();
        updatedConsents.setActiveSubscription(false);
        updatedConsents.setCustomerSurveysConsent(true);
        updatedConsents.setEmailConsent(true);
        updatedConsents.setIsAnonymousUser(false);
        updatedConsents.setIsRegistration(false);
        updatedConsents.setKnowHowConsent(true);
        updatedConsents.setNewsLetterConsent(true);
        updatedConsents.setNpsSubscription(false);
        updatedConsents.setObsolescenceConsent(false);
        updatedConsents.setPersonalisationConsent(false);
        updatedConsents.setPersonalisedRecommendationConsent(true);
        updatedConsents.setPhoneConsent(false);
        updatedConsents.setPhoneNumber(GERMAN_PHONE_NUMBER);
        updatedConsents.setPostConsent(false);
        updatedConsents.setProfilingConsent(false);
        updatedConsents.setSaleAndClearanceConsent(true);
        updatedConsents.setSegmentedTopic(false);
        updatedConsents.setSelectAllemailConsents(true);
        updatedConsents.setSmsConsent(false);
        updatedConsents.setTermsAndConditionsConsent(false);
        updatedConsents.setUid(TEST_EMAIL);

        when(userService.getCurrentUser()).thenReturn(germanCustomer);
        when(baseSiteService.getCurrentBaseSite()).thenReturn(germanBaseSite);
        when(distBloomreachAPIClient.exportCustomerConsents(anyString())).thenReturn(PREFERENCE_CENTER_ALL_UNCHECKED);

        final String requestString =
                distBloomreachFacadeImpl.getPreferenceCenterUpdates(updatedConsents, consentData, BloomreachTouchpoint.MYACCOUNT);

        JSONAssert.assertEquals(data, requestString, JSONCompareMode.LENIENT);
    }

    @Test
    public void testGetPreferenceCenterUpdatesReturnsEmptyString() throws IOException, DistBloomreachExportException {
        final DistConsentData consentData = createPreferenceConsentData();
        final DistMarketingConsentData updatedConsents = createUpdatedConsents(false, SWISS_PHONE_NUMBER);

        when(userService.getCurrentUser()).thenReturn(swissCustomer);
        when(baseSiteService.getCurrentBaseSite()).thenReturn(swissBaseSite);
        when(distBloomreachAPIClient.exportCustomerConsents(anyString())).thenReturn(PREFERENCE_CENTER_ALL_UNCHECKED);

        final String requestString =
                distBloomreachFacadeImpl.getPreferenceCenterUpdates(updatedConsents, consentData, BloomreachTouchpoint.MYACCOUNT);

        assertEquals(StringUtils.EMPTY, requestString);
    }

    @Test
    public void testGetPreferenceCenterUpdatesThrowsDistBloomreachExportException() throws IOException, DistBloomreachExportException {
        final DistConsentData consentData = createPreferenceConsentData();

        final DistMarketingConsentData updatedConsents = createUpdatedConsents(true, SWISS_PHONE_NUMBER);

        when(distBloomreachAPIClient.exportCustomerConsents(anyString()))
                .thenThrow(new DistBloomreachExportException("Customer consents could not be exported."));

        expectedException.expect(DistBloomreachExportException.class);
        expectedException.expectMessage("Customer consents could not be exported.");

        distBloomreachFacadeImpl.getPreferenceCenterUpdates(updatedConsents, consentData, BloomreachTouchpoint.MYACCOUNT);
    }

    private DistConsentData createPreferenceConsentData() {
        final DistConsentData consentData = new DistConsentData();
        consentData.setUid(TEST_EMAIL);
        consentData.setErpContactId(TEST_ERP_CONTACT_ID);
        consentData.setActiveSubscription(false);
        consentData.setIsAnonymousUser(false);
        consentData.setIsRegistration(false);
        consentData.setNpsSubscription(false);
        consentData.setPaperPermission(false);
        consentData.setPersonalisationSubscription(false);
        consentData.setPhonePermission(false);
        consentData.setProfilingSubscription(false);
        consentData.setSmsPermissions(false);
        return consentData;
    }

    private DistMarketingConsentData createUpdatedConsents(boolean selected, String phoneNumber) {
        final DistMarketingConsentData updatedConsents = new DistMarketingConsentData();
        updatedConsents.setActiveSubscription(false);
        updatedConsents.setCustomerSurveysConsent(selected);
        updatedConsents.setEmailConsent(selected);
        updatedConsents.setIsAnonymousUser(false);
        updatedConsents.setIsRegistration(false);
        updatedConsents.setKnowHowConsent(selected);
        updatedConsents.setNewsLetterConsent(selected);
        updatedConsents.setNpsSubscription(false);
        updatedConsents.setObsolescenceConsent(selected);
        updatedConsents.setPersonalisationConsent(selected);
        updatedConsents.setPersonalisedRecommendationConsent(selected);
        updatedConsents.setPhoneConsent(selected);
        updatedConsents.setPhoneNumber(phoneNumber);
        updatedConsents.setPostConsent(selected);
        updatedConsents.setProfilingConsent(selected);
        updatedConsents.setSaleAndClearanceConsent(selected);
        updatedConsents.setSegmentedTopic(false);
        updatedConsents.setSelectAllemailConsents(selected);
        updatedConsents.setSmsConsent(selected);
        updatedConsents.setTermsAndConditionsConsent(false);
        updatedConsents.setUid(TEST_EMAIL);
        return updatedConsents;
    }

    @Test
    public void testSendBatchRequestToBloomreach() throws DistBloomreachBatchException {
        distBloomreachFacadeImpl.sendBatchRequestToBloomreach(SUBSCRIPTION_REQUEST_STRING);
        verify(distBloomreachAPIClient, times(1)).callBatchRequest(SUBSCRIPTION_REQUEST_STRING);
    }

    @Test
    public void testSendBatchRequestToBloomreachThrowsDistBloomreachBatchException() throws DistBloomreachBatchException {
        doThrow(new DistBloomreachBatchException("Batch request could not be sent.")).when(distBloomreachAPIClient).callBatchRequest(anyString());

        expectedException.expect(DistBloomreachBatchException.class);
        expectedException.expectMessage("Batch request could not be sent.");

        distBloomreachFacadeImpl.sendBatchRequestToBloomreach(SUBSCRIPTION_REQUEST_STRING);
    }

    @Test
    public void testExportCustomerConsentsFromBloomreach() throws IOException, DistBloomreachExportException {
        when(distBloomreachAPIClient.exportCustomerConsents(anyString())).thenReturn(PREFERENCE_CENTER_STRING);
        DistBloomreachConsentData consentData = distBloomreachFacadeImpl.exportCustomerConsentsFromBloomreach(TEST_EMAIL);

        assertEquals(TEST_EMAIL, consentData.getUid());
        assertTrue(consentData.isEmailConsent());
        assertFalse(consentData.isContentNewsConsent());
        assertTrue(consentData.isSalesAndClearanceConsent());
        assertTrue(consentData.isNewBrandsConsent());
        assertFalse(consentData.isPersonalisedRecommendationsConsent());
        assertFalse(consentData.isNpsConsent());
        assertFalse(consentData.isPersonalisationConsent());
        assertTrue(consentData.isProfilingConsent());
        assertFalse(consentData.isPaperConsent());
        assertFalse(consentData.isPhoneConsent());
        assertFalse(consentData.isSmsMarketingConsent());
    }

    @Test
    public void testExportCustomerConsentsFromBloomreachThrowsDistBloomreachExportException() throws IOException, DistBloomreachExportException {
        when(distBloomreachAPIClient.exportCustomerConsents(anyString()))
                .thenThrow(new DistBloomreachExportException("Customer consents could not be exported."));

        expectedException.expect(DistBloomreachExportException.class);
        expectedException.expectMessage("Customer consents could not be exported.");

        distBloomreachFacadeImpl.exportCustomerConsentsFromBloomreach(TEST_EMAIL);
    }

    @Test
    public void testUpdateCustomerInBloomreach() throws DistBloomreachBatchException, JsonProcessingException {
        final DistConsentData consentData = createRegistrationConsentData(false, "CH", SWISS_PHONE_NUMBER);

        distBloomreachFacadeImpl.updateCustomerInBloomreach(consentData);

        verify(distBloomreachAPIClient, times(1)).callBatchRequest(anyString());
    }

    @Test
    public void testUpdateCustomerInBloomreachThrowsDistBloomreachBatchException() throws DistBloomreachBatchException, JsonProcessingException {
        final DistConsentData consentData = createRegistrationConsentData(false, "CH", SWISS_PHONE_NUMBER);

        doThrow(new DistBloomreachBatchException("Batch request could not be sent.")).when(distBloomreachAPIClient).callBatchRequest(anyString());

        expectedException.expect(DistBloomreachBatchException.class);
        expectedException.expectMessage("Batch request could not be sent.");

        distBloomreachFacadeImpl.updateCustomerInBloomreach(consentData);
    }
}
