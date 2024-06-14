/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.misc.impl;

import com.namics.distrelec.b2b.core.event.DistSendToFriendEvent;
import com.namics.distrelec.b2b.facades.misc.DistShareWithFriendsFacade;
import de.hybris.platform.acceleratorservices.model.email.EmailAttachmentModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.media.MediaIOException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Default implementation of {@link DistShareWithFriendsFacade}
 * 
 * @author pbueschi, Namics AG
 * 
 */
public class DefaultDistShareWithFriendsFacade implements DistShareWithFriendsFacade {

    protected static final Logger LOG = Logger.getLogger(DefaultDistShareWithFriendsFacade.class);

    @Autowired
    private EventService eventService;

    @Autowired
    private BaseStoreService baseStoreService;

    @Autowired
    private BaseSiteService baseSiteService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private UserService userService;

    @Override
    public void shareProductWithFriends(final DistSendToFriendEvent distSendToFriendEvent, final ProductModel productModel, final String productPageUrl) {
        setUpAdditionalEventData(distSendToFriendEvent);
        distSendToFriendEvent.setProduct(productModel);
        distSendToFriendEvent.setUrl(productPageUrl);
        eventService.publishEvent(distSendToFriendEvent);
    }

    @Override
    public void shareCartWithFriends(final DistSendToFriendEvent distSendToFriendEvent, final CartModel cartModel, final File csvFile) {
        setUpAdditionalEventData(distSendToFriendEvent);
        distSendToFriendEvent.setCart(cartModel);
        distSendToFriendEvent.setAttachment(createEmailAttachmentModel(csvFile));
        eventService.publishEvent(distSendToFriendEvent);
    }

    @Override
    public void shareCartPdfWithFriends(DistSendToFriendEvent distSendToFriendEvent, CartModel cartModel, InputStream pdfStream) {
        setUpAdditionalEventData(distSendToFriendEvent);
        distSendToFriendEvent.setCart(cartModel);
        distSendToFriendEvent.setAttachment(createEmailPdfAttachmentModel(pdfStream));
        eventService.publishEvent(distSendToFriendEvent);
    }

    @Override
    public void shareSearchResultsWithFriends(final DistSendToFriendEvent distSendToFriendEvent, final String searchPageUrl) {
        setUpAdditionalEventData(distSendToFriendEvent);
        distSendToFriendEvent.setUrl(searchPageUrl);
        eventService.publishEvent(distSendToFriendEvent);
    }

    @Override
    public void shareProductComparisonWithFriends(final DistSendToFriendEvent distSendToFriendEvent, final List<ProductData> productList) {
        setUpAdditionalEventData(distSendToFriendEvent);
        distSendToFriendEvent.setComparisonList(getProductModelsFromProductData(productList));
        eventService.publishEvent(distSendToFriendEvent);
    }

    protected void setUpAdditionalEventData(final DistSendToFriendEvent distSendToFriendEvent) {
        distSendToFriendEvent.setBaseStore(baseStoreService.getCurrentBaseStore());
        distSendToFriendEvent.setSite(baseSiteService.getCurrentBaseSite());
        distSendToFriendEvent.setCustomer((CustomerModel) userService.getCurrentUser());
    }

    private List<ProductModel> getProductModelsFromProductData(final List<ProductData> productDataList) {
        final List<ProductModel> productModelList = new ArrayList<ProductModel>();
        for (final ProductData dataItem : productDataList) {
            final ProductModel productModel = productService.getProductForCode(dataItem.getCode());
            if (productModel != null) {
                productModelList.add(productModel);
            }
        }
        return productModelList;
    }

    private EmailAttachmentModel createEmailAttachmentModel(final File csvFile) {
        final EmailAttachmentModel attachmentModel = modelService.create(EmailAttachmentModel.class);
        attachmentModel.setCode(csvFile.getName());
        attachmentModel.setCatalogVersion(cmsSiteService.getCurrentCatalogVersion());
        modelService.save(attachmentModel);
        try {
            final byte[] data = FileUtils.readFileToByteArray(csvFile);
            mediaService.setDataForMedia(attachmentModel, data);
        } catch (final MediaIOException e) {
            LOG.error("Could not create MediaModel from file: " + csvFile.getName(), e);
        } catch (final IllegalArgumentException e) {
            LOG.error("Could not create MediaModel from file: " + csvFile.getName(), e);
        } catch (final IOException e) {
            LOG.error("Error while reading file: " + csvFile.getName(), e);
        }
        attachmentModel.setRealFileName(csvFile.getName());
        attachmentModel.setAltText(csvFile.getName());
        modelService.save(attachmentModel);
        return attachmentModel;
    }

    private EmailAttachmentModel createEmailPdfAttachmentModel(InputStream pdfStream) {
        if (pdfStream == null) {
            return null;
        }

        final EmailAttachmentModel attachmentModel = modelService.create(EmailAttachmentModel.class);
        Date now = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String uid = userService.getCurrentUser().getUid();
        String code = String.format("cart_%s_%s", uid, df.format(now));
        attachmentModel.setCode(code);
        attachmentModel.setCatalogVersion(cmsSiteService.getCurrentCatalogVersion());
        modelService.save(attachmentModel);

        mediaService.setStreamForMedia(attachmentModel, pdfStream);

        String name = String.format("cart_%s%s", df.format(now), ".pdf");
        attachmentModel.setRealFileName(name);
        attachmentModel.setAltText(name);
        modelService.save(attachmentModel);

        return attachmentModel;
    }

}
