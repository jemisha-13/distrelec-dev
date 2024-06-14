/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.namics.distrelec.b2b.core.event;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.namics.distrelec.b2b.core.enums.DistErpSystem;
import com.namics.distrelec.b2b.core.model.DistOrderProcessModel;
import com.namics.distrelec.b2b.core.service.customer.b2b.budget.DistB2BCommerceBudgetService;
import com.namics.distrelec.b2b.core.service.process.pdfgeneration.PDFGenerationService;
import com.namics.hybris.toolbox.spring.SpringUtil;

import de.hybris.platform.acceleratorservices.model.email.EmailAttachmentModel;
import de.hybris.platform.acceleratorservices.process.strategies.ProcessContextResolutionStrategy;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.events.OrderPlacedEvent;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.media.MediaIOException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

/**
 * Event listener for order confirmation functionality.
 */
public class OrderConfirmationEventListener extends AbstractOrderEventListener<OrderPlacedEvent> {

    protected static final Logger LOG = LogManager.getLogger(OrderConfirmationEventListener.class);

    private static final String XSL_MEDIA_ID = "PDF_ORDER_CONFIRMATION_XSL";
    private static final String RENDERER_TEMPLATE_CODE = "PDF_Order_Confirmation";

    @Override
    protected void onEvent(final OrderPlacedEvent orderPlacedEvent) {
        final OrderModel orderModel = orderPlacedEvent.getProcess().getOrder();

        // Update yearly budget of the user if there is one
        getDistB2BCommerceBudgetService().updateBudget(orderModel);

        // DISTRELEC-4138 just generate an order confirmation mail for Movex, otherwise SAP is generating
        if (!DistErpSystem.SAP.equals(((CMSSiteModel) orderModel.getSite()).getSalesOrg().getErpSystem())) {
            setSessionLocaleForOrder(orderModel);

            // Update yearly budget of the user if there is one
            getDistB2BCommerceBudgetService().updateBudget(orderModel);

            final DistOrderProcessModel orderProcessModel = (DistOrderProcessModel) getBusinessProcessService()
                    .createProcess("orderConfirmationEmailProcess" + System.currentTimeMillis(), "orderConfirmationEmailProcess");
            orderProcessModel.setOrder(orderModel);
            final BaseSiteModel site = getProcessContextResolutionStrategy().getCmsSite(orderProcessModel);
            final File pdfFile = getPDFGenerationService().transformModelToPDF(orderProcessModel, XSL_MEDIA_ID, RENDERER_TEMPLATE_CODE, site);
            if (pdfFile != null) {
                orderProcessModel.setAttachment(createEmailAttachmentModel(pdfFile, orderModel.getCode().concat(".pdf")));
                pdfFile.delete();
            }
            getModelServiceViaLookup().save(orderProcessModel);
            getBusinessProcessService().startProcess(orderProcessModel);
        } else {
            LOG.info("SAP is going to generate the order confirmation mail.");
        }
    }

    protected EmailAttachmentModel createEmailAttachmentModel(final File file, final String attachmentFileName) {
        final EmailAttachmentModel attachmentModel = getModelServiceViaLookup().create(EmailAttachmentModel.class);
        attachmentModel.setCode(file.getName());

        // store attachment model to default catalog
        final CatalogModel catalogModel = new CatalogModel();
        catalogModel.setId("Default");
        final CatalogModel defaultCatalogModel = getFlexibleSearchService().getModelByExample(catalogModel);
        attachmentModel.setCatalogVersion(defaultCatalogModel.getActiveCatalogVersion());

        getModelServiceViaLookup().save(attachmentModel);
        try {
            final byte[] data = FileUtils.readFileToByteArray(file);
            getMediaService().setDataForMedia(attachmentModel, data);
        } catch (final MediaIOException e) {
            LOG.error("Could not create MediaModel from file: " + file.getName(), e);
        } catch (final IllegalArgumentException e) {
            LOG.error("Could not create MediaModel from file: " + file.getName(), e);
        } catch (final IOException e) {
            LOG.error("Error while reading file: " + file.getName(), e);
        }
        attachmentModel.setRealFileName(attachmentFileName);
        attachmentModel.setAltText(file.getName());
        getModelServiceViaLookup().save(attachmentModel);
        return attachmentModel;
    }

    public BusinessProcessService getBusinessProcessService() {
        return SpringUtil.getBean("businessProcessService", BusinessProcessService.class);
    }

    @Override
    public ModelService getModelServiceViaLookup() {
        throw new UnsupportedOperationException("Please define in the spring configuration a <lookup-method> for getModelServiceViaLookup().");
    }

    public PDFGenerationService getPDFGenerationService() {
        return SpringUtil.getBean("pdfGenerationService", PDFGenerationService.class);
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return SpringUtil.getBean("flexibleSearchService", FlexibleSearchService.class);
    }

    public MediaService getMediaService() {
        return SpringUtil.getBean("mediaService", MediaService.class);
    }

    public ProcessContextResolutionStrategy getProcessContextResolutionStrategy() {
        return SpringUtil.getBean("processContextResolutionStrategy", ProcessContextResolutionStrategy.class);
    }

    public DistB2BCommerceBudgetService getDistB2BCommerceBudgetService() {
        return SpringUtil.getBean("distB2BCommerceBudgetService", DistB2BCommerceBudgetService.class);
    }
}
