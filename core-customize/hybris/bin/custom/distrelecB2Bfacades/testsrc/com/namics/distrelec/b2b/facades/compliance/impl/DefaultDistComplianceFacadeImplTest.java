package com.namics.distrelec.b2b.facades.compliance.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.pdf.data.DistPdfData;
import com.namics.distrelec.b2b.core.pdf.service.DistPDFService;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.facades.helper.DistLogoUrlHelper;
import com.namics.distrelec.b2b.facades.manufacturer.data.DistManufacturerData;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.user.UserService;

import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.COMMA_COUNTRIES;
import static java.util.Locale.ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.anyObject;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.argThat;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Matchers.any;

@UnitTest
public class DefaultDistComplianceFacadeImplTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @InjectMocks
    private DefaultDistComplianceFacade complianceFacade;

    @Mock
    private ProductService mockProductService;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private Configuration mockConfiguration;

    @Mock
    private CMSSiteService mockCmsSiteService;

    @Mock
    private DistrelecProductFacade mockProductFacade;

    @Mock
    private CommerceCommonI18NService mockCommerceCommonI18NService;

    @Mock
    private DistPDFService mockDistPDFService;

    @Mock
    private MediaService mockMediaService;

    @Mock
    private CMSComponentService mockCmsComponentService;

    @Mock
    private UserService mockUserService;

    @Mock
    private CatalogVersionService mockCatalogVersionService;

    @Mock
    private DistSalesOrgService mockDistSalesOrgService;

    @Mock
    private DistLogoUrlHelper mockDistLogoUrlHelper;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        given(configurationService.getConfiguration())
          .willReturn(mockConfiguration);
        LanguageModel mockLanguage = mock(LanguageModel.class);
        given(mockLanguage.getIsocode())
          .willReturn("en");
        given(mockCommerceCommonI18NService.getCurrentLanguage())
          .willReturn(mockLanguage);
        CMSSiteModel mockSite = mock(CMSSiteModel.class);
        given(mockSite.getUid())
          .willReturn("testSite");
        ContentCatalogModel mockCountryContentCatalog = mock(ContentCatalogModel.class);
        given(mockSite.getCountryContentCatalog())
          .willReturn(mockCountryContentCatalog);
        given(mockCmsSiteService.getCurrentSite())
          .willReturn(mockSite);
        given(mockConfiguration.getString(eq(COMMA_COUNTRIES), anyString()))
          .willReturn("AT,BE,BG,CY,CZ,DE,DK,EE,ES,FI,FR,HR,HU,IT,LT,LU,LV,NL,NO,PL,PT,RO,SI,SK,SE,UA");
        given(mockCommerceCommonI18NService.getCurrentLocale())
          .willReturn(ENGLISH);
        CatalogVersionModel mockCatalogVersion = mock(CatalogVersionModel.class);
        given(mockCatalogVersionService.getCatalogVersion(anyString(), anyString()))
          .willReturn(mockCatalogVersion);
        MediaModel mockMedia = mock(MediaModel.class);
        given(mockMediaService.getMedia(anyObject(), anyString()))
          .willReturn(mockMedia);
        given(mockMedia.getURL())
          .willReturn("media");
        DistSalesOrgModel mockSalesOrg = mock(DistSalesOrgModel.class);
        given(mockSalesOrg.getFullAddress())
          .willReturn("address");
        given(mockDistSalesOrgService.getCurrentSalesOrg())
          .willReturn(mockSalesOrg);

        ByteArrayInputStream pdfInputStream = mock(ByteArrayInputStream.class);
        given(mockDistPDFService.generatePdfFromData(any(DistPdfData.class)))
          .willReturn(pdfInputStream);

    }

    @Test
    public void testIsRohsCompliant() {
        given(mockConfiguration.getString("rohs.allowed.code"))
          .willReturn("100,200");
        ProductData mockProductData = mock(ProductData.class);

        given(mockProductData.getRohsCode())
          .willReturn("100");
        assertThat(complianceFacade.isROHSCompliant(mockProductData))
          .isTrue();

        given(mockProductData.getRohsCode())
          .willReturn("200");
        assertThat(complianceFacade.isROHSCompliant(mockProductData))
          .isTrue();

        given(mockProductData.getRohsCode())
          .willReturn("300");
        assertThat(complianceFacade.isROHSCompliant(mockProductData))
          .isFalse();
    }

    @Test
    public void testIsRohsConform() {
        ProductData mockProductData = mock(ProductData.class);

        given(mockProductData.getRohsCode())
          .willReturn("15");
        assertThat(complianceFacade.isROHSConform(mockProductData))
          .isTrue();

        given(mockProductData.getRohsCode())
          .willReturn("12");
        assertThat(complianceFacade.isROHSConform(mockProductData))
          .isFalse();
    }

    @Test
    public void testIsRndProduct() {
        DistManufacturerData manufacturerData = mock(DistManufacturerData.class);
        ProductData mockProductData = mock(ProductData.class);

        given(manufacturerData.getCode()).willReturn("man_rnd");
        given(mockProductData.getDistManufacturer()).willReturn(manufacturerData);
        given(mockConfiguration.getString("rohs.rnd.manufacturers.code")).willReturn("man_rnd, man_rrr");

        assertThat(complianceFacade.isRNDProduct(mockProductData))
          .isTrue();

        given(manufacturerData.getCode()).willReturn("man_r");

        assertThat(complianceFacade.isRNDProduct(mockProductData))
          .isFalse();
    }

    @Test
    public void testIsRndProduct_manufacturerIsNull() {
        ProductData mockProductData = mock(ProductData.class);
        given(mockProductData.getDistManufacturer()).willReturn(null);

        assertThat(complianceFacade.isRNDProduct(mockProductData))
          .isFalse();
    }

    @Test
    public void testIsROHSAllowedForProduct() {
        given(mockConfiguration.getString(eq("rohs.allowed.code")))
          .willReturn("100,200");
        given(mockConfiguration.getString(eq("rohs.ignore.sites")))
          .willReturn("rohs_forbidden_site");
        CMSSiteModel mockRohsAllowedSite = mock(CMSSiteModel.class);
        given(mockRohsAllowedSite.getUid())
          .willReturn("rohs_allowed_site");
        CMSSiteModel mockRohsForbiddenSite = mock(CMSSiteModel.class);
        given(mockRohsForbiddenSite.getUid())
          .willReturn("rohs_forbidden_site");

        ProductData mockProductData = mock(ProductData.class);
        given(mockCmsSiteService.getCurrentSite())
          .willReturn(mockRohsAllowedSite);

        given(mockProductData.getRohsCode())
          .willReturn("100");
        assertThat(complianceFacade.isROHSAllowedForProduct(mockProductData))
          .isTrue();

        given(mockProductData.getRohsCode())
          .willReturn("200");
        assertThat(complianceFacade.isROHSAllowedForProduct(mockProductData))
          .isTrue();

        given(mockProductData.getRohsCode())
          .willReturn("300");
        assertThat(complianceFacade.isROHSAllowedForProduct(mockProductData))
          .isFalse();

        given(mockCmsSiteService.getCurrentSite())
          .willReturn(mockRohsForbiddenSite);

        given(mockProductData.getRohsCode())
          .willReturn("100");
        assertThat(complianceFacade.isROHSAllowedForProduct(mockProductData))
          .isFalse();

        given(mockProductData.getRohsCode())
          .willReturn("200");
        assertThat(complianceFacade.isROHSAllowedForProduct(mockProductData))
          .isFalse();

        given(mockProductData.getRohsCode())
          .willReturn("300");
        assertThat(complianceFacade.isROHSAllowedForProduct(mockProductData))
          .isFalse();
    }

    @Test
    public void testProductHasSvhc() {
        ProductData noSvhcProduct = mock(ProductData.class);

        given(noSvhcProduct.getHasSvhc())
          .willReturn(false);
        assertThat(complianceFacade.productHasSvhc(noSvhcProduct))
          .isFalse();

        ProductData yesSvhcProduct = mock(ProductData.class);
        given(yesSvhcProduct.getHasSvhc()).willReturn(true);

        assertThat(complianceFacade.productHasSvhc(yesSvhcProduct))
          .isTrue();

        ProductData emptySvhcProduct = mock(ProductData.class);
        given(emptySvhcProduct.getCode()).willReturn("123");

        given(emptySvhcProduct.getHasSvhc())
          .willReturn(null);

        assertThatThrownBy(() -> complianceFacade.productHasSvhc(emptySvhcProduct))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Product 123 doesn't have hasSVHC flag defined");
    }

    @Test
    public void testGetPDFStreamForSvhc() {
        ProductData mockProductData = mock(ProductData.class);

        complianceFacade.getPDFStreamForSvhc(mockProductData, false);

        DistPdfData distPdfData = mock(DistPdfData.class);
        given(distPdfData.getXslName())
          .willReturn("product-no-svhc.xsl");
        given(distPdfData.getTemplateName())
          .willReturn("product-no-svhc.vm");

        verify(mockDistPDFService, times(1))
          .generatePdfFromData(argThat(new DistPDFDataMatcher(distPdfData)));
    }

    @Test
    public void testGetPDFStreamForSvhc_RND() {
        ProductData mockProductData = mock(ProductData.class);

        complianceFacade.getPDFStreamForSvhc_RND(mockProductData, false);

        DistPdfData distPdfData = mock(DistPdfData.class);
        given(distPdfData.getXslName())
          .willReturn("product-no-svhc-rnd.xsl");
        given(distPdfData.getTemplateName())
          .willReturn("product-no-svhc-rnd.vm");

        verify(mockDistPDFService, times(1))
          .generatePdfFromData(argThat(new DistPDFDataMatcher(distPdfData)));
    }

    @Test
    public void testGetPDFStreamForROHS() {
        ProductData mockProductData = mock(ProductData.class);
        DistPdfData distPdfData = mock(DistPdfData.class);

        given(distPdfData.getXslName())
          .willReturn("product-rohs.xsl");
        given(distPdfData.getTemplateName())
          .willReturn("product-rohs.vm");

        InputStream result = complianceFacade.getPDFStreamForROHS(mockProductData);

        verify(mockDistPDFService, times(1))
          .generatePdfFromData(argThat(new DistPDFDataMatcher(distPdfData)));
        assertNotNull(result);
    }

    @Test
    public void testGetPDFStreamForROHS_RND() {
        ProductData mockProductData = mock(ProductData.class);
        DistPdfData distPdfData = mock(DistPdfData.class);

        given(distPdfData.getXslName())
          .willReturn("product-rohs-rnd.xsl");
        given(distPdfData.getTemplateName())
          .willReturn("product-rohs-rnd.vm");

        InputStream result = complianceFacade.getPDFStreamForROHS_RND(mockProductData);

        verify(mockDistPDFService, times(1))
          .generatePdfFromData(argThat(new DistPDFDataMatcher(distPdfData)));
        assertNotNull(result);
    }

    @Test
    public void testGetPDFStreamForBatteryCompliance() {
        DistPdfData distPdfData = mock(DistPdfData.class);

        given(distPdfData.getXslName())
          .willReturn("battery-compliance.xsl");
        given(distPdfData.getTemplateName())
          .willReturn("battery-compliance.vm");

        InputStream result = complianceFacade.getPDFStreamForBatteryCompliance();

        assertNotNull(result);
        verify(mockDistPDFService, times(1))
          .generatePdfFromData(argThat(new DistPDFDataMatcher(distPdfData)));
    }

    @Test
    public void testGetPDFStreamForBatteryCompliance_siteIsNull() {
        given(mockCmsSiteService.getCurrentSite())
          .willReturn(null);

        InputStream result = complianceFacade.getPDFStreamForBatteryCompliance();

        assertNull(result);
    }

    @Test
    public void testGetPDFStreamForBatteryCompliance_languageIsNull() {
        given(mockCommerceCommonI18NService.getCurrentLanguage())
          .willReturn(null);

        InputStream result = complianceFacade.getPDFStreamForBatteryCompliance();

        assertNull(result);
    }

    @Test
    public void testGetPDFStreamForDisposalOfPackagingWaste() {
        DistPdfData distPdfData = mock(DistPdfData.class);

        given(distPdfData.getXslName())
          .willReturn("disposal-of-packaging.xsl");
        given(distPdfData.getTemplateName())
          .willReturn("disposal-of-packaging.vm");

        InputStream result = complianceFacade.getPDFStreamForDisposalOfPackagingWaste();

        assertNotNull(result);
        verify(mockDistPDFService, times(1))
          .generatePdfFromData(argThat(new DistPDFDataMatcher(distPdfData)));
    }

    @Test
    public void testGetPDFStreamForDisposalOfPackagingWaste_siteIsNull() {
        given(mockCmsSiteService.getCurrentSite())
          .willReturn(null);

        InputStream result = complianceFacade.getPDFStreamForDisposalOfPackagingWaste();

        assertNull(result);
    }

    @Test
    public void testGetPDFStreamForDisposalOfPackagingWaste_languageIsNull() {
        given(mockCommerceCommonI18NService.getCurrentLanguage())
          .willReturn(null);

        InputStream result = complianceFacade.getPDFStreamForDisposalOfPackagingWaste();

        assertNull(result);
    }

    @Test
    public void testGetPDFStreamForConflictMineral() {
        DistPdfData distPdfData = mock(DistPdfData.class);

        given(distPdfData.getXslName())
          .willReturn("conflict-mineral.xsl");
        given(distPdfData.getTemplateName())
          .willReturn("conflict-mineral.vm");

        InputStream result = complianceFacade.getPDFStreamForConflictMineral();

        assertNotNull(result);
        verify(mockDistPDFService, times(1))
          .generatePdfFromData(argThat(new DistPDFDataMatcher(distPdfData)));
    }

    @Test
    public void testGetPDFStreamForConflictMineral_siteIsNull() {
        given(mockCmsSiteService.getCurrentSite())
          .willReturn(null);

        InputStream result = complianceFacade.getPDFStreamForConflictMineral();

        assertNull(result);
    }

    @Test
    public void testGetPDFStreamForConflictMineral_languageIsNull() {
        given(mockCommerceCommonI18NService.getCurrentLanguage())
          .willReturn(null);

        InputStream result = complianceFacade.getPDFStreamForConflictMineral();

        assertNull(result);
    }

    @Test
    public void testGetPDFStreamForWEEE() {
        DistPdfData distPdfData = mock(DistPdfData.class);

        given(distPdfData.getXslName())
          .willReturn("weee.xsl");
        given(distPdfData.getTemplateName())
          .willReturn("weee.vm");

        InputStream result = complianceFacade.getPDFStreamForWEEE();

        assertNotNull(result);
        verify(mockDistPDFService, times(1))
          .generatePdfFromData(argThat(new DistPDFDataMatcher(distPdfData)));
    }

    @Test
    public void testGetPDFStreamForWEEE_siteIsNull() {
        given(mockCmsSiteService.getCurrentSite())
          .willReturn(null);

        InputStream result = complianceFacade.getPDFStreamForWEEE();

        assertNull(result);
    }

    @Test
    public void testGetPDFStreamForWEEE_languageIsNull() {
        given(mockCommerceCommonI18NService.getCurrentLanguage())
          .willReturn(null);

        InputStream result = complianceFacade.getPDFStreamForWEEE();

        assertNull(result);
    }

    @Test
    public void testIsProductBatteryCompliant() {
        ProductData mockProductData = mock(ProductData.class);

        given(mockConfiguration.getString("battery.compliances.code"))
          .willReturn("100,200,300");
        given(mockProductData.getBatteryComplianceCode())
          .willReturn("200");

        boolean result = complianceFacade.isProductBatteryCompliant(mockProductData);

        verify(mockConfiguration, times(1)).getString("battery.compliances.code");
        assertTrue(result);
    }

    @Test(expected = NullPointerException.class)
    public void testIsProductBatteryCompliant_productDataIsNull() {
        given(mockConfiguration.getString("battery.compliances.code"))
          .willReturn("100,200,300");

        complianceFacade.isProductBatteryCompliant(null);
    }

    @Test
    public void testIsProductBatteryCompliant_batteryComplianceCodeIsBlank() {
        ProductData mockProductData = mock(ProductData.class);

        given(mockConfiguration.getString("battery.compliances.code"))
          .willReturn("100,200,300");
        given(mockProductData.getBatteryComplianceCode())
          .willReturn("");

        boolean result = complianceFacade.isProductBatteryCompliant(mockProductData);

        verify(mockConfiguration, times(1)).getString("battery.compliances.code");
        assertFalse(result);
    }

    @Test
    public void testIsProductBatteryCompliant_batteryComplianceListDoesNotContainBatteryComplianceCode() {
        ProductData mockProductData = mock(ProductData.class);

        given(mockConfiguration.getString("battery.compliances.code"))
          .willReturn("100,200,300");
        given(mockProductData.getBatteryComplianceCode())
          .willReturn("400");

        boolean result = complianceFacade.isProductBatteryCompliant(mockProductData);

        verify(mockConfiguration, times(1)).getString("battery.compliances.code");
        assertFalse(result);
    }

    @Test
    public void testIsNotRndProductAndIsNotAllowedForSiteAndNotCompliant() {
        ProductData mockProductData = mock(ProductData.class);
        CMSSiteModel mockRohsAllowedSite = mock(CMSSiteModel.class);
        CMSSiteModel mockRohsForbiddenSite = mock(CMSSiteModel.class);
        boolean isRNDProduct = false;

        given(mockConfiguration.getString(eq("rohs.allowed.code")))
          .willReturn("100,200,300");
        given(mockConfiguration.getString(eq("rohs.ignore.sites")))
          .willReturn("rohs_forbidden_site");
        given(mockRohsAllowedSite.getUid())
          .willReturn("rohs_allowed_site");
        given(mockRohsForbiddenSite.getUid())
          .willReturn("rohs_forbidden_site");
        given(mockCmsSiteService.getCurrentSite())
          .willReturn(mockRohsAllowedSite);
        given(mockProductData.getRohsCode())
          .willReturn("400");

        boolean result = complianceFacade.isNotRndProductAndIsNotAllowedForSiteAndNotCompliant(mockProductData, isRNDProduct);

        assertTrue(result);

        isRNDProduct = true;

        result = complianceFacade.isNotRndProductAndIsNotAllowedForSiteAndNotCompliant(mockProductData, isRNDProduct);

        assertFalse(result);
    }

    @Test
    public void testIsRndProductAndIsNotROHSConform() {
        ProductData mockProductData = mock(ProductData.class);
        boolean isRNDProduct = true;

        given(mockProductData.getRohsCode())
          .willReturn("16");

        boolean result = complianceFacade.isRndProductAndIsNotROHSConform(mockProductData, isRNDProduct);

        assertTrue(result);

        isRNDProduct = false;

        result = complianceFacade.isRndProductAndIsNotROHSConform(mockProductData, isRNDProduct);

        assertFalse(result);
    }

    private class DistPDFDataMatcher implements ArgumentMatcher<DistPdfData> {

        private final DistPdfData original;

        public DistPDFDataMatcher(DistPdfData original) {
            this.original = original;
        }

        @Override
        public boolean matches(DistPdfData other) {
            if (!(other instanceof DistPdfData)) {
                return false;
            }

            DistPdfData otherData = (DistPdfData) other;
            return original.getXslName().equals(otherData.getXslName())
                   && original.getTemplateName().equals(otherData.getTemplateName());
        }
    }

}
