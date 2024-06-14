package com.namics.distrelec.b2b.storefront.controllers.pages;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.namics.distrelec.b2b.facades.compliance.DistComplianceFacade;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

@UnitTest
public class ComplianceDocumentControllerTest {

    @InjectMocks
    private ComplianceDocumentController controller;

    @Mock
    private DistrelecProductFacade mockProductFacade;

    @Mock
    private DistComplianceFacade mockComplianceFacade;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                                 .build();
    }

    @Test
    public void testGetROHS_RNDDocumentOk() throws Exception {
        ProductData mockProductData = mock(ProductData.class);

        given(mockProductFacade.getProductForOptions(eq("123456"), anyList()))
                                                                              .willReturn(mockProductData);

        given(mockComplianceFacade.isRNDProduct(mockProductData))
                                                                 .willReturn(true);

        given(mockComplianceFacade.isROHSConform(mockProductData))
                                                                  .willReturn(true);

        InputStream emptyPdfStream = new ByteArrayInputStream(new byte[0]);
        given(mockComplianceFacade.getPDFStreamForROHS_RND(eq(mockProductData)))
                                                                                .willReturn(emptyPdfStream);

        mockMvc.perform(get("/compliance-document/pdf/ROHS_123456"))
               .andExpect(status().isOk());
    }

    @Test
    public void testGetROHSDocumentOk() throws Exception {
        ProductData mockProductData = mock(ProductData.class);

        given(mockProductFacade.getProductForOptions(eq("123456"), anyList()))
                                                                              .willReturn(mockProductData);

        given(mockComplianceFacade.isROHSAllowedForProduct(mockProductData))
                                                                            .willReturn(true);

        given(mockComplianceFacade.isROHSCompliant(mockProductData))
                                                                    .willReturn(true);

        InputStream emptyPdfStream = new ByteArrayInputStream(new byte[0]);
        given(mockComplianceFacade.getPDFStreamForROHS(mockProductData))
                                                                        .willReturn(emptyPdfStream);

        mockMvc.perform(get("/compliance-document/pdf/ROHS_123456"))
               .andExpect(status().isOk());
    }

    @Test
    public void testGetROHSDocumentForbiddenSite() throws Exception {
        ProductData mockProductData = mock(ProductData.class);

        given(mockProductFacade.getProductForOptions(eq("123456"), anyList()))
                                                                              .willReturn(mockProductData);

        given(mockComplianceFacade.isROHSAllowedForProduct(mockProductData))
                                                                            .willReturn(false);

        given(mockComplianceFacade.isROHSCompliant(mockProductData))
                                                                    .willReturn(true);

        given(mockComplianceFacade.isRndProductAndIsNotROHSConform(mockProductData, false))
                                                                                           .willReturn(false);

        given(mockComplianceFacade.isNotRndProductAndIsNotAllowedForSiteAndNotCompliant(mockProductData, false))
                                                                                                                .willReturn(true);

        InputStream emptyPdfStream = new ByteArrayInputStream(new byte[0]);
        given(mockComplianceFacade.getPDFStreamForROHS(mockProductData))
                                                                        .willReturn(emptyPdfStream);

        mockMvc.perform(get("/compliance-document/pdf/ROHS_123456"))
               .andExpect(status().isNotFound());
    }

    @Test
    public void testGetROHSDocumentNotROHSCompliant() throws Exception {
        ProductData mockProductData = mock(ProductData.class);

        given(mockProductFacade.getProductForOptions(eq("123456"), anyList()))
                                                                              .willReturn(mockProductData);

        given(mockComplianceFacade.isRNDProduct(mockProductData))
                                                                 .willReturn(false);

        given(mockComplianceFacade.isROHSAllowedForProduct(mockProductData))
                                                                            .willReturn(false);

        given(mockComplianceFacade.isRndProductAndIsNotROHSConform(mockProductData, false))
                                                                                           .willReturn(true);

        given(mockComplianceFacade.isNotRndProductAndIsNotAllowedForSiteAndNotCompliant(mockProductData, false))
                                                                                                                .willReturn(true);

        InputStream emptyPdfStream = new ByteArrayInputStream(new byte[0]);
        given(mockComplianceFacade.getPDFStreamForROHS(mockProductData))
                                                                        .willReturn(emptyPdfStream);

        mockMvc.perform(get("/compliance-document/pdf/ROHS_123456"))
               .andExpect(status().isNotFound());
    }

    @Test
    public void testGetROHSDocumentMissingProduct() throws Exception {
        ProductData mockProductData = mock(ProductData.class);

        given(mockProductFacade.getProductForOptions(eq("123456"), anyList()))
                                                                              .willThrow(UnknownIdentifierException.class);

        mockMvc.perform(get("/compliance-document/pdf/ROHS_123456"))
               .andExpect(status().isNotFound());
    }

    @Test
    public void testGetSvhcPdfOk() throws Exception {
        ProductData mockProductData = mock(ProductData.class);

        given(mockProductFacade.getProductForOptions(eq("123456"), anyList())).willReturn(mockProductData);
        given(mockComplianceFacade.productHasSvhc(mockProductData))
                                                                   .willReturn(false);

        InputStream emptyPdfStream = new ByteArrayInputStream(new byte[0]);
        given(mockComplianceFacade.getPDFStreamForSvhc(mockProductData, false))
                                                                               .willReturn(emptyPdfStream);

        mockMvc.perform(get("/compliance-document/pdf/SVHC_123456"))
               .andExpect(status().isOk());
    }

    @Test
    public void testGetSvhc_RND_PdfOk() throws Exception {
        ProductData mockProductData = mock(ProductData.class);

        given(mockProductFacade.getProductForOptions(eq("123456"), anyList())).willReturn(mockProductData);
        given(mockComplianceFacade.isRNDProduct(mockProductData))
                                                                 .willReturn(true);
        given(mockComplianceFacade.productHasSvhc(mockProductData))
                                                                   .willReturn(false);

        InputStream emptyPdfStream = new ByteArrayInputStream(new byte[0]);
        given(mockComplianceFacade.getPDFStreamForSvhc_RND(mockProductData, false))
                                                                                   .willReturn(emptyPdfStream);

        mockMvc.perform(get("/compliance-document/pdf/SVHC_123456"))
               .andExpect(status().isOk());
    }

    @Test
    public void testGetSvhcPdfHasSvhc() throws Exception {
        ProductData mockProductData = mock(ProductData.class);

        given(mockComplianceFacade.productHasSvhc(mockProductData))
                                                                   .willReturn(true);

        mockMvc.perform(get("/compliance-document/pdf/SVHC_123456"))
               .andExpect(status().isNotFound());
    }

    @Test
    public void testGetSvhcPdfMissingProduct() throws Exception {
        ProductData mockProductData = mock(ProductData.class);

        given(mockComplianceFacade.productHasSvhc(mockProductData))
                                                                   .willThrow(UnknownIdentifierException.class);

        mockMvc.perform(get("/compliance-document/pdf/SVHC_123456"))
               .andExpect(status().isNotFound());
    }

}
