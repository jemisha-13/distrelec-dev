/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.eprocurement.service.oci;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.namics.distrelec.distrelecoci.data.DistSapProductList;
import com.namics.distrelec.distrelecoci.exception.OciException;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.JaloConnectException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.order.exceptions.CalculationException;

/**
 * DistOciService.
 * 
 * @author pbueschi, Namics AG
 * @since Distrelec 1.0
 */
public interface DistOciService {

    boolean isOciSession(JaloSession jaloSession);

    /**
     * Checks if current customer is member of OCICUSTOMERGROUP and valid ociSession
     * 
     * @return is OCI customer
     */
    boolean isOciCustomer();

    /**
     * Checks whether the mega fly out should be hidden or not for the OCI customer
     * 
     * @return {@code true} is the mega flyout should be hidden, {@code false} otherwise.
     */
    boolean hasMegaFlyOutDisabled();

    /**
     * Checks whether the custom footer should be enabled for the OCI customer
     * 
     * @return {@code true} if and only if custom footer is enabled.
     */
    boolean isCustomFooterEnabled();

    /**
     * Check whether we need to open the OCI window page in a new window or not.
     * 
     * @return {@code true} if the OCI page should be open in a new window. Default value is {@code false}
     */
    boolean openInNewWindow();

    /**
     * Generates OCI form based on current session cart.
     * 
     * @return OCI form as string
     * @throws OciException
     * @throws CalculationException
     */
    String generateOciForm() throws OciException, CalculationException;

    /**
     * Gets redirect URL if additional OCI functionalities are called.
     * 
     * @return redirect URL
     */
    String getOciRedirectUrl();

    /**
     * Gets OCI form for additional OCI functionalities.
     * 
     * @return OCI form
     */
    String getOciFunctionForm(List<ProductModel> backgroundSearchPlist) throws OciException;

    /**
     * Do additional OCI login (spring security login already done) for set up all necessary attributes.
     * 
     * @param request
     * @param response
     * @throws OciException
     */
    void doOciLogin(final HttpServletRequest request, final HttpServletResponse response) throws OciException;

    /**
     * Do additional OCI login (spring security login already done) for set up all necessary attributes.
     * 
     * @param request
     * @param response
     * @param catalogLoginPerformer
     * @param disableRedirect
     * @throws OciException
     */
    void ociLogin(final HttpServletRequest request, final HttpServletResponse response, final DistCatalogLoginPerformer catalogLoginPerformer,
            final boolean disableRedirect, final boolean useHtml) throws OciException, JaloConnectException;

    String createOciBuyerButton(final DistSapProductList sapproductlist) throws OciException;

    String createOciBuyerButton(final DistSapProductList sapproductlist, final boolean useHtml) throws OciException;

    String createOciBuyerButton(final DistSapProductList sapproductlist, final String ociButton) throws OciException;

    String createOciBuyerButton(final DistSapProductList sapproductlist, final String ociButton, final boolean useHtml) throws OciException;
}
