package com.namics.distrelec.b2b.storefront.controllers.pages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.facades.category.DistCategoryIndexFacade;
import com.namics.distrelec.b2b.facades.product.data.DistCategoryIndexData;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSNavigationService;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.session.SessionService;

@UnitTest
public class CategoryIndexPageControllerTest extends AbstractPageControllerTest<CategoryIndexPageController> {

    private static final String EN = "EN";

    private static final String CATEGORIES_URL = "/en/categories";

    private static final String CATEGORIES = "categories";

    private static final String AUTOMATION = "Automation";

    private static final String CAT_L1D_541300 = "cat-l1d_541300";

    private static final String CAT_L1D_5414 = "cat-l1d_5414";

    @Mock
    private DistUrlResolver<CategoryModel> categoryModelUrlResolver;

    @Mock
    private DefaultCMSNavigationService defaultCMSNavigationService;

    @Mock
    private CMSNavigationNodeModel cmsRootNavigationNode;

    @Mock
    private DistCategoryIndexFacade distCategoryIndexFacade;

    @InjectMocks
    private CategoryIndexPageController controller;

    private static final String ENGLISH_ISO_CODE = "en";

    private static final String AUTOMATION_CATEGORY_URL = "/en/automation/c/cat-L1D_379516";

    @Override
    protected CategoryIndexPageController getController() {
        return controller;
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = createMockMvc();
        super.setUp();
        setUpMocks();
    }

    @Test
    public void specialShopsAreFilteredOut() throws Exception {
        when(sessionService.getOrLoadAttribute(eq("categoryIndex"),
                                               Matchers.<SessionService.SessionAttributeLoader<List<DistCategoryIndexData>>> any())).thenReturn(createListOfCategoryIndexData());

        final MockHttpSession session = new MockHttpSession();
        final MvcResult result = mockMvc.perform(get(CATEGORIES_URL)
                                                                    .session(session))
                                        .andExpect(status().isOk()).andReturn();
        final Map<String, Object> model = result.getModelAndView().getModel();
        assertNotNull(model);

        when(distCategoryIndexFacade.getCategoryIndexData())
                                                            .thenReturn(Collections.emptyList());

        final List<String> categories = (List<String>) model.get(CATEGORIES);
        assertNotNull(categories);
    }

    @Test
    public void testTrytoReturnNullFromCategoryIndexSpecifically() throws Exception {
        when(sessionService.getOrLoadAttribute(eq("categoryIndex"),
                                               Matchers.<SessionService.SessionAttributeLoader<List<DistCategoryIndexData>>> any())).thenReturn(null);

        final MockHttpSession session = new MockHttpSession();
        final MvcResult result = mockMvc.perform(get(CATEGORIES_URL)
                                                                    .session(session))
                                        .andExpect(status().isOk()).andReturn();
        final Map<String, Object> model = result.getModelAndView().getModel();
        assertNotNull(model);

        final List<String> categories = (List<String>) model.get(CATEGORIES);
        assertNotNull(categories);
        assertEquals(0, categories.size());
    }

    private void setUpMocks() throws CMSItemNotFoundException {
        final List<CMSNavigationNodeModel> childrenOfRootNavigation = createChildrenOfRootNavigation();
        when(commonI18NService.getCurrentLanguage()).thenReturn(createLanguageModel());
        when(storeSessionFacade.getCurrentLanguage()).thenReturn(createLanguageData());
        when(defaultCMSNavigationService.getNavigationNodeForId("MainCategoryNavNode")).thenReturn(cmsRootNavigationNode);
        when(cmsRootNavigationNode.getChildren()).thenReturn(childrenOfRootNavigation);
        when(categoryModelUrlResolver.resolve(Mockito.any(CategoryModel.class))).thenReturn(AUTOMATION_CATEGORY_URL);
    }

    private LanguageData createLanguageData() {
        final LanguageData languageData = new LanguageData();
        languageData.setIsocode(ENGLISH_ISO_CODE);
        return languageData;
    }

    private LanguageModel createLanguageModel() {
        final LanguageModel languageModel = new LanguageModel();
        languageModel.setIsocode(ENGLISH_ISO_CODE);
        return languageModel;
    }

    private List<CMSNavigationNodeModel> createChildrenOfRootNavigation() {
        final CMSNavigationNodeModel childNode = mock(CMSNavigationNodeModel.class);
        when(childNode.getName()).thenReturn(AUTOMATION);
        return Arrays.asList(childNode);
    }

    private List<DistCategoryIndexData> createListOfCategoryIndexData() {
        final DistCategoryIndexData dataOne = new DistCategoryIndexData();
        dataOne.setUrl(CAT_L1D_541300);
        final DistCategoryIndexData dataTwo = new DistCategoryIndexData();
        dataTwo.setUrl(CAT_L1D_5414);
        return Arrays.asList(dataOne, dataTwo);
    }
}
