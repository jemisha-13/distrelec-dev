package com.namics.distrelec.b2b.storefront.controllers.pages;

import com.namics.distrelec.b2b.core.enums.NPSReason;
import com.namics.distrelec.b2b.core.enums.NPSType;
import com.namics.distrelec.b2b.core.service.nps.data.DistNetPromoterScoreData;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.nps.DistNetPromoterScoreFacade;
import com.namics.distrelec.b2b.facades.storesession.data.SalesOrgData;
import com.namics.distrelec.b2b.storefront.constants.MessageConstants;
import com.namics.distrelec.b2b.storefront.forms.FeedbackNPSForm;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.store.BaseStoreModel;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NetPromoterScoreControllerTest extends AbstractPageControllerTest<NetPromoterScoreController> {

    private static final String NPS_URL = FeedbackPageController.FEEDBACK_REQUEST_MAPPING_URL + "/nps";
    private static final String DD_MM_YYYY = "dd/MM/yyyy";
    private static final String HAS_ERRORS = "hasErrors";
    private static final String NPS_FORM = "npsForm";
    private static final String TESTING_DISTRELEC_COM = "testing@distrelec.com";
    private static final String EMAIL = "email";
    private static final String VALUE = "value";
    private static final String CODE = "code";
    private static final String ID = "id";

    @Mock
    private DistNetPromoterScoreFacade distNetPromoterScoreFacade;
    @Mock
    private EnumerationService enumService;
    @InjectMocks
    private NetPromoterScoreController controller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = createMockMvc();
        super.setUp();
        prepareMocks();
    }

    @Override
    protected NetPromoterScoreController getController() {
        return controller;
    }

    private void prepareMocks() throws CMSItemNotFoundException {
        final String currentSalesOrg = "currentSalesOrg";
        final String englishAsISO = "en";
        final String wtCode = "WtCode";
        final String breadcrumbMaxLength = "breadcrumb.maxlength";
        final String twoHundredFiftyFive = "255";


        final BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(Boolean.FALSE);
        when(messageSource.getMessage(eq(MessageConstants.TEXT_STORE_FORMAT), eq(null), anyObject())).thenReturn(DD_MM_YYYY);
        when(sessionService.getOrLoadAttribute(eq(currentSalesOrg), anyObject())).thenReturn(createSalesOrgData());

        final Locale locale = new Locale(englishAsISO);
        when(i18nService.getCurrentLocale()).thenReturn(locale);
        when(messageSource.getMessage(anyString(), eq(null), eq(DD_MM_YYYY), eq(locale))).thenReturn(DD_MM_YYYY);

        final FeedbackNPSForm form = createNPSForm();
        when(distNetPromoterScoreFacade.getLastSubmittedNPSDate(form.getEmail())).thenReturn(null);

        doNothing().when(distDigitalDatalayerFacade).populatePageType(anyObject(), eq(DigitalDatalayerConstants.PageType.NPSPAGE));

        final ContentPageModel contentPageModel = mock(ContentPageModel.class);
        when(distCmsPageService.getPageForLabelOrId(anyString(), any())).thenReturn(contentPageModel);

        final CMSSiteModel siteModel = mock(CMSSiteModel.class);
        when(cmsSiteService.getCurrentSite()).thenReturn(siteModel);
        when(siteModel.getWtDefaultAreaCode()).thenReturn(wtCode);

        final Configuration configuration = mock(BaseConfiguration.class);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getString(breadcrumbMaxLength)).thenReturn(twoHundredFiftyFive);

        final BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);
        when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
        when(baseStoreModel.getLanguages()).thenReturn(Collections.emptySet());

        final BaseSiteModel baseSiteModel = mock(BaseSiteModel.class);
        when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSiteModel);
        when(baseStoreService.getCurrentChannel(any(BaseSiteModel.class))).thenReturn(SiteChannel.B2B);
        when(distCategoryFacade.getCategoryIndex()).thenReturn(Collections.emptyList());

        doNothing().when(distNetPromoterScoreFacade).createNPS(any(DistNetPromoterScoreData.class));
    }

    @Test
    public void testGetPageWithEmptyScore() throws Exception {
         mockMvc.perform(get(NPS_URL)
                .param(EMAIL, TESTING_DISTRELEC_COM))
                .andExpect(status().isOk())
                .andExpect(model().attribute(HAS_ERRORS, Boolean.FALSE))
                .andExpect(model().attributeExists(WebConstants.NPS_FORM))
                .andExpect(model().attribute(NPS_FORM, hasProperty(EMAIL, is(TESTING_DISTRELEC_COM))))
                .andExpect(model().attribute(NPS_FORM, hasProperty(VALUE, is(-1))))
                .andReturn();
    }

    @Test
    public void testGetPageWithAScoreSupplied() throws Exception {
        final Integer score = 7;
        mockMvc.perform(get(NPS_URL)
                .param(EMAIL, TESTING_DISTRELEC_COM)
                .param(VALUE, String.valueOf(score)))
                .andExpect(status().isOk())
                .andExpect(model().attribute(HAS_ERRORS, Boolean.FALSE))
                .andExpect(model().attributeExists(WebConstants.NPS_FORM))
                .andExpect(model().attribute(NPS_FORM, hasProperty(EMAIL, is(TESTING_DISTRELEC_COM))))
                .andExpect(model().attribute(NPS_FORM, hasProperty(VALUE, is(score))))
                .andReturn();
    }

    @Test
    public void testGetPageWithAZeroScoreSupplied() throws Exception {
        final Integer score = 0;
        mockMvc.perform(get(NPS_URL)
                .param(EMAIL, TESTING_DISTRELEC_COM)
                .param(VALUE, String.valueOf(score)))
                .andExpect(status().isOk())
                .andExpect(model().attribute(HAS_ERRORS, Boolean.FALSE))
                .andExpect(model().attributeExists(WebConstants.NPS_FORM))
                .andExpect(model().attribute(NPS_FORM, hasProperty(EMAIL, is(TESTING_DISTRELEC_COM))))
                .andExpect(model().attribute(NPS_FORM, hasProperty(VALUE, is(score))))
                .andReturn();
    }

    @Test
    public void testDefaultGetPage() throws Exception {
        final Integer score = 7;
        mockMvc.perform(get(NPS_URL)
                .param(EMAIL, TESTING_DISTRELEC_COM)
                .param(CODE, String.valueOf(score)))
                .andExpect(status().isOk())
                .andExpect(model().attribute(HAS_ERRORS, Boolean.FALSE))
                .andReturn();
    }

    @Test
    public void testPostFormWithNoId() throws Exception {
        mockMvc.perform(post(NPS_URL))
                .andExpect(status().isOk())
                .andExpect(model().attribute(WebConstants.STATUS, WebConstants.NOK))
                .andExpect(model().attribute(WebConstants.MESSAGE_KEY, MessageConstants.FEEDBACK_NPS_ERROR))
                .andReturn();
    }

    @Test
    public void testPostFormWithScore() throws Exception {
        final String testId = "0123456";
        when(distNetPromoterScoreFacade.getNPSByCode(anyString())).thenReturn(null);
        doNothing().when(distNetPromoterScoreFacade).updateNPS(anyObject());

        mockMvc.perform(post(NPS_URL)
                .param(EMAIL, TESTING_DISTRELEC_COM)
                .param(ID, testId))
                .andExpect(status().isOk())
                .andExpect(model().attribute(WebConstants.ALLOWED_TO_SUBMIT_NPS, Boolean.TRUE))
                .andExpect(model().attribute(WebConstants.LAST_NPS_DATE, nullValue()))
                .andExpect(model().attribute(WebConstants.STATUS, WebConstants.OK))
                .andExpect(model().attribute(WebConstants.MESSAGE_KEY, MessageConstants.FEEDBACK_NPS_SUCCESS))
                .andReturn();
    }

    private FeedbackNPSForm createNPSForm(){
        final FeedbackNPSForm form = new FeedbackNPSForm();
        final String firstName = "Test";
        final String lastName = "User";
        final String companyName = "Distrelec";
        final String contactNumber = "00099999";
        final String orderNumber = "999999";
        final Date deliveryDate = new Date();
        final Integer score = 7;
        final String orderId = "120000";


        form.setEmail(TESTING_DISTRELEC_COM);
        form.setType(NPSType.ORDERCONFIRMATION.getType());
        form.setFname(firstName);
        form.setNamn(lastName);
        form.setCompany(companyName);
        form.setContactnum(contactNumber);
        form.setOrder(orderNumber);
        form.setDelivery(deliveryDate);
        form.setValue(score);
        form.setFeedback(NPSReason.DELIVERYOFGOODS.getType());
        form.setId(orderId);
        return form;
    }

    private SalesOrgData createSalesOrgData() {
        final String swissSalesOrganisation = "7310";
        SalesOrgData data = new SalesOrgData();
        data.setCode(swissSalesOrganisation);
        return data;
    }
}
