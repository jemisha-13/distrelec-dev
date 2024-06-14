/*
 * Copyright 2013-2018 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.storefront.controllers.pages;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.mockito.Mock;
import org.springframework.context.MessageSource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.MockMvcConfigurerAdapter;
import org.springframework.validation.Validator;
import org.springframework.web.context.WebApplicationContext;

import com.namics.distrelec.b2b.core.eprocurement.service.ariba.DistAribaService;
import com.namics.distrelec.b2b.core.eprocurement.service.oci.DistOciService;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.service.DistCmsPageService;
import com.namics.distrelec.b2b.core.service.cart.dao.DistCartDao;
import com.namics.distrelec.b2b.core.service.order.impl.DefaultDistCartService;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.core.service.site.DistrelecBaseStoreService;
import com.namics.distrelec.b2b.core.service.site.DistrelecCMSSiteService;
import com.namics.distrelec.b2b.core.service.url.DistSiteBaseUrlResolutionService;
import com.namics.distrelec.b2b.facades.adobe.datalayer.DistDigitalDatalayerFacade;
import com.namics.distrelec.b2b.facades.bomtool.DistBomToolFacade;
import com.namics.distrelec.b2b.facades.caching.DistCachingFacade;
import com.namics.distrelec.b2b.facades.category.DistCategoryFacade;
import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.newsletter.DistNewsletterFacade;
import com.namics.distrelec.b2b.facades.order.DistB2BOrderFacade;
import com.namics.distrelec.b2b.facades.order.cart.DistB2BCartFacade;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.seo.DistSeoFacade;
import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import com.namics.distrelec.b2b.facades.storesession.data.SalesOrgData;
import com.namics.distrelec.b2b.facades.user.DistUserFacade;
import com.namics.distrelec.b2b.facades.webtrekk.DistWebtrekkFacade;
import com.namics.distrelec.b2b.facades.wishlist.DistCompareListFacade;
import com.namics.distrelec.b2b.facades.wishlist.DistShoppingListFacade;
import com.namics.distrelec.b2b.storefront.support.PageTitleResolver;
import com.namics.distrelec.b2b.storefront.util.CaptchaUtil;
import com.namics.distrelec.b2b.storefront.util.SearchRobotDetector;
import com.namics.distrelec.b2b.core.version.GitVersionService;
import com.namics.hybris.ffsearch.service.FactFinderChannelService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.cms2.data.PagePreviewCriteriaData;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.ProductPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPreviewService;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.security.dynamic.PrincipalAllGroupsAttributeHandler;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;

/**
 * {@code AbstractPageControllerTest}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 7.3
 */
@UnitTest
public abstract class AbstractPageControllerTest<T extends AbstractPageController> {
    @Mock
    protected DistProductService productService;

    @Mock
    protected DistSalesStatusModel productSalesStatusModel;

    @Mock
    protected ProductModel productModel;

    @Mock
    protected ProductData productData;

    @Mock
    protected CartModel cartModel;

    @Mock
    protected ProductPageModel pageModel;

    @Mock
    protected CMSSiteModel cmsSiteModel;

    @Mock
    protected B2BCustomerModel currentUser;

    @Mock
    protected DistCategoryFacade distCategoryFacade;

    @Mock
    protected SalesOrgData salesOrgData;

    @Mock
    protected CustomerData distB2BCustomerData;

    @Mock
    protected BaseStoreModel baseStoreModel;

    @Mock
    protected DistrelecProductFacade distrelecProductFacade;

    @Mock
    protected ConfigurationService configurationService;

    @Mock
    protected Configuration configuration;

    @Mock
    protected DistB2BCartFacade cartFacade;

    @Mock
    protected DefaultDistCartService cartService;

    @Mock
    protected MessageSource messageSource;

    @Mock
    protected I18NService i18nService;

    @Mock
    protected DistSeoFacade distSeoFacade;

    @Mock
    protected PageTitleResolver pageTitleResolver;

    @Mock
    protected DistCmsPageService distCmsPageService;

    @Mock
    protected DistWebtrekkFacade distWebtrekkFacade;

    @Mock
    protected DistrelecCMSSiteService cmsSiteService;

    @Mock
    protected DistDigitalDatalayerFacade distDigitalDatalayerFacade;

    @Mock
    protected SessionService sessionService;

    @Mock
    protected DistrelecStoreSessionFacade storeSessionFacade;

    @Mock
    protected UserService userService;

    @Mock
    protected DistCustomerFacade distCustomerFacade;

    @Mock
    protected DistCustomerFacade b2bCustomerFacade;

    @Mock
    protected DistB2BOrderFacade orderFacade;

    @Mock
    protected CommonI18NService commonI18NService;

    @Mock
    protected SiteConfigService siteConfigService;

    @Mock
    protected DistSiteBaseUrlResolutionService distSiteBaseUrlResolutionService;

    @Mock
    protected BaseSiteService baseSiteService;

    @Mock
    protected DistrelecBaseStoreService baseStoreService;

    @Mock
    protected DistOciService distOciService;

    @Mock
    protected DistAribaService distAribaService;

    @Mock
    protected SearchRobotDetector searchRobotDetector;

    @Mock
    protected FactFinderChannelService channelService;

    @Mock
    protected DistShoppingListFacade distShoppingListFacade;

    @Mock
    protected DistCompareListFacade distCompareListFacade;

    @Mock
    protected DistCachingFacade distCachingFacade;

    @Mock
    protected ContentPageModel contentPageModel;

    @Mock
    protected DistCartDao distCartDao;

    @Mock
    protected CMSPreviewService cmsPreviewService;

    @Mock
    protected DistrelecCMSSiteService distrelecCMSSiteService;

    @Mock
    protected PagePreviewCriteriaData pagePreviewCriteriaData;

    @Mock
    protected DistUserFacade userFacade;

    @Mock
    protected DistBomToolFacade distBomToolFacade;

    @Mock
    protected GitVersionService gitVersionService;

    @Mock
    protected DistNewsletterFacade newsletterFacade;

    @Mock
    protected CartData cartData;

    @Mock
    protected CaptchaUtil captchaUtil;

    protected MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {

        // Call to setup the controller
        setUp(getController());

        when(cmsSiteModel.isHttpsOnly()).thenReturn(Boolean.TRUE);

        when(configuration.getString(anyString(), anyString())).thenReturn("");
        when(configuration.getBoolean(anyString(), anyBoolean())).thenReturn(false);
        when(configuration.getString("captcha.public.key")).thenReturn("testKey");
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(cartService.getSessionCart()).thenReturn(cartModel);
        when(messageSource.getMessage(anyString(), any(), anyString(), any(Locale.class))).thenReturn(null);
        when(pageTitleResolver.resolveProductPageTitle(productModel)).thenReturn("");
        when(distCmsPageService.getPageForProduct(any(ProductModel.class))).thenReturn(pageModel);
        when(cmsSiteService.getCurrentSite()).thenReturn(cmsSiteModel);
        when(configuration.getString(anyString())).thenReturn("");
        when(configuration.getString("newsletter.popup.ignored.pages.regex")).thenReturn("\\/cart(.*)|\\\\/checkout\\\\/(.*)|\\\\/registration(.*)|\\\\/login/checkout(.*)");
        when(configuration.getString(anyString(), anyString())).thenReturn("");
        when(configuration.getBoolean(anyString(), anyBoolean())).thenReturn(false);
        when(distrelecProductFacade.getProductForCodeAndOptions(any(ProductModel.class), anyList())).thenReturn(productData);
        when(b2bCustomerFacade.getCurrentCustomer()).thenReturn(distB2BCustomerData);
        when(i18nService.getCurrentLocale()).thenReturn(new Locale("en"));
        when(distOciService.hasMegaFlyOutDisabled()).thenReturn(false);
        when(distAribaService.isCustomFooterEnabled()).thenReturn(true);
        when(distAribaService.isAribaCustomer()).thenReturn(false);
        when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
        when(channelService.getCurrentFactFinderChannel()).thenReturn("7310_ch_en");
        when(baseSiteService.getCurrentBaseSite()).thenReturn(cmsSiteModel);
        when(baseStoreService.getCurrentChannel(any())).thenReturn(SiteChannel.B2B);
        when(storeSessionFacade.getCurrentSalesOrg()).thenReturn(salesOrgData);
        when(storeSessionFacade.getCurrentCurrency()).thenReturn(new CurrencyData());
        when(distCmsPageService.getPageForLabelOrId(anyString())).thenReturn(contentPageModel);
        when(productService.getProductForCode(anyString())).thenReturn(productModel);
        when(storeSessionFacade.getCurrentLocale()).thenReturn(Locale.ENGLISH);
        when(gitVersionService.getRevision()).thenReturn("12345678");
        when(gitVersionService.getGitVersion()).thenReturn("12345678");
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(userService.isAnonymousUser(currentUser)).thenReturn(Boolean.FALSE);
        when(cartData.getB2bCustomerData()).thenReturn(distB2BCustomerData);
        when(cartFacade.getCurrentCart()).thenReturn(cartData);
    }

    /**
     * 
     * @param controller
     * @throws Exception
     */
    public void setUp(final T controller) throws Exception {
        controller.setConfigurationService(configurationService);
        controller.setMessageSource(messageSource);
        controller.setI18nService(i18nService);
        controller.setPageTitleResolver(pageTitleResolver);
        controller.setCmsPageService(distCmsPageService);
        controller.setDistWebtrekkFacade(distWebtrekkFacade);
        controller.setDistDigitalDatalayerFacade(distDigitalDatalayerFacade);
        controller.setDistUserFacade(userFacade);
        controller.setSessionService(sessionService);
        controller.setStoreSessionFacade(storeSessionFacade);
        controller.setUserService(userService);
        controller.setOrderFacade(orderFacade);
        controller.setDistSeoFacade(distSeoFacade);
        controller.setSiteConfigService(siteConfigService);
        controller.setCommonI18NService(commonI18NService);
        controller.setDistSiteBaseUrlResolutionService(distSiteBaseUrlResolutionService);
        controller.setDistOciService(distOciService);
        controller.setDistAribaService(distAribaService);
        controller.setSearchRobotDetector(searchRobotDetector);
        controller.setChannelService(channelService);
        controller.setDistCompareListFacade(distCompareListFacade);
        controller.setDistShoppingListFacade(distShoppingListFacade);
        controller.setDistCachingFacade(distCachingFacade);
        controller.setBaseSiteService(baseSiteService);
        controller.setBaseStoreService(baseStoreService);
        controller.setProductService(productService);
        controller.setB2bCartFacade(cartFacade);
        controller.setCmsPageService(distCmsPageService);
        controller.setCmsSiteService(cmsSiteService);
        controller.setCustomerFacade(b2bCustomerFacade);
        controller.setGitVersionService(gitVersionService);
        controller.setCaptchaUtil(captchaUtil);
        controller.setNewsletterFacade(newsletterFacade);
    }

    protected MockMvc createMockMvc() {
        return MockMvcBuilders.standaloneSetup(getController()).apply(new MockMvcConfigurerAdapter() {
            @Override
            public void afterConfigurerAdded(ConfigurableMockMvcBuilder<?> builder) {
                super.afterConfigurerAdded(builder);
            }

            @Override
            public RequestPostProcessor beforeMockMvcCreated(ConfigurableMockMvcBuilder<?> builder, WebApplicationContext cxt) {
                ReflectionTestUtils.invokeMethod(cxt.getAutowireCapableBeanFactory(), "addBean", "configurationService", configurationService);
                ReflectionTestUtils.invokeMethod(cxt.getAutowireCapableBeanFactory(), "addBean", "Principal_allGroupsAttributeHandler",
                                                 new PrincipalAllGroupsAttributeHandler());
                return super.beforeMockMvcCreated(builder, cxt);
            }
        }).build();
    }

    protected MockMvc createMockMvcWithValidator(Validator validator) {
        return MockMvcBuilders.standaloneSetup(getController()).apply(new MockMvcConfigurerAdapter() {
            @Override
            public void afterConfigurerAdded(ConfigurableMockMvcBuilder<?> builder) {
                super.afterConfigurerAdded(builder);
            }

            @Override
            public RequestPostProcessor beforeMockMvcCreated(ConfigurableMockMvcBuilder<?> builder, WebApplicationContext cxt) {
                ReflectionTestUtils.invokeMethod(cxt.getAutowireCapableBeanFactory(), "addBean", "configurationService", configurationService);
                ReflectionTestUtils.invokeMethod(cxt.getAutowireCapableBeanFactory(), "addBean", "Principal_allGroupsAttributeHandler",
                                                 new PrincipalAllGroupsAttributeHandler());
                return super.beforeMockMvcCreated(builder, cxt);
            }
        }).setValidator(validator).build();
    }

    protected abstract T getController();

}
