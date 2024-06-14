package com.namics.distrelec.b2b.facades.compliance;

import java.io.InputStream;

import de.hybris.platform.commercefacades.product.data.ProductData;

public interface DistComplianceFacade {

    /**
     * Get stream of data of generated PDF for ROHS compliant product
     *
     * @param productCode
     * @return
     */
    InputStream getPDFStreamForROHS(ProductData productData);

    /**
     * Get stream of data of generated PDF for RND ROHS compliant product
     *
     * @param productCode
     * @return
     */
    InputStream getPDFStreamForROHS_RND(ProductData productData);

    /**
     * Check if ROHS link display is allowed for given product
     *
     * @param productData
     * @return
     */
    boolean isROHSAllowedForProduct(ProductData productData);

    /**
     * Check if product is ROHS compliant
     *
     * @param productData
     * @return
     */
    boolean isROHSCompliant(ProductData productData);

    /**
     * Check if product is ROHS conform
     *
     * @param productData
     * @return
     */
    boolean isROHSConform(ProductData productData);

    /**
     * Check if is RND Lab product and is ROHS compliant
     *
     * @param productData
     * @return
     */
    boolean isRNDProduct(ProductData productData);

    /**
     * Get stream of data of generated PDF for product without SVHC materials
     *
     * @param productCode
     * @return
     */
    InputStream getPDFStreamForSvhc(ProductData productData, boolean hasSvhc);

    InputStream getPDFStreamForBatteryCompliance();

    InputStream getPDFStreamForWEEE();

    InputStream getPDFStreamForDisposalOfPackagingWaste();

    /**
     * Get stream of data of generated PDF for product without SVHC materials
     *
     * @param productCode
     * @return
     */
    InputStream getPDFStreamForSvhc_RND(ProductData productData, boolean hasSvhc);

    InputStream getPDFStreamForConflictMineral();

    /**
     * Returns true if product has SVHC materials and false otherwise
     *
     * @param productData
     * @return
     * @throws IllegalArgumentException
     *             If product doesn't have SVHC flag defined
     */
    boolean productHasSvhc(ProductData productData) throws IllegalArgumentException;

    /**
     * Returns true if product is battery compliant
     *
     * @param productData
     * @return {boolean} true if submit is disabled
     */
    boolean isProductBatteryCompliant(ProductData productData);

    boolean isNotRndProductAndIsNotAllowedForSiteAndNotCompliant(ProductData productData, boolean isRNDProduct);

    boolean isRndProductAndIsNotROHSConform(ProductData productData, boolean isRNDProduct);
}
