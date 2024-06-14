/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.newsletter.job;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.event.DistNewProductsNewsLetterEvent;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.model.jobs.DistNewProductsNewsLetterCronJobModel;

import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.task.RetryLaterException;

/**
 * {@code DistNewProductsNewsLetterJob}
 * 
 *
 * @since Distrelec 5.10
 */
public class DistNewProductsNewsLetterJob extends AbstractJobPerformable<DistNewProductsNewsLetterCronJobModel> {

    private static final Logger LOG = Logger.getLogger(DistNewProductsNewsLetterJob.class);

    private BaseSiteService baseSiteService;

    private BaseStoreService baseStoreService;

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Autowired
    private EventService eventService;

    private static final String NEW_PRODUCTS_NEWS_LETTER_QUERY = "SELECT DISTINCT {prod." + ProductModel.PK + "}, {prod." + ProductModel.PIMFAMILYCATEGORYCODE
            + "} FROM {" + ProductModel._TYPECODE + " AS prod JOIN  " + DistSalesOrgProductModel._TYPECODE + " AS salesOrgP ON " + " {salesOrgP."
            + DistSalesOrgProductModel.PRODUCT + "}={prod." + ProductModel.PK + " } JOIN " + DistSalesStatusModel._TYPECODE + " AS salesSts ON {salesOrgP."
            + DistSalesOrgProductModel.SALESSTATUS + "}={salesSts." + DistSalesStatusModel.PK + "}} WHERE {salesSts." + DistSalesStatusModel.VISIBLEINSHOP
            + " }=1 AND {salesSts." + DistSalesStatusModel.ENDOFLIFEINSHOP + "} = 0 AND {salesSts." + DistSalesStatusModel.BUYABLEINSHOP + "} = 1 AND {prod."
            + ProductModel.FIRSTAPPEARANCEDATE + "} >= (?" + ProductModel.FIRSTAPPEARANCEDATE + ") ORDER BY {prod." + ProductModel.PIMFAMILYCATEGORYCODE
            + "} ASC";

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#perform(de.hybris.platform.cronjob.model.CronJobModel)
     */
    @Override
    public PerformResult perform(final DistNewProductsNewsLetterCronJobModel cronJob) {
        final long startTime = System.nanoTime();
        LOG.info("Starting DistNewProductsNewsLetterJob at " + new Date());
        boolean success = true;
        try {
            performJob(cronJob);
        } catch (final Exception exp) {
            LOG.error(exp.getMessage(), exp);
        }
        LOG.info("Finished DistNewProductsNewsLetterJob job in " + (int) ((System.nanoTime() - startTime) / 1e9) + " seconds.");
        return new PerformResult(success ? CronJobResult.SUCCESS : CronJobResult.ERROR, CronJobStatus.FINISHED);
    }

    /**
     * 
     * @param cronJob
     * @throws InterruptedException
     * @throws RetryLaterException
     */
    protected void performJob(final DistNewProductsNewsLetterCronJobModel cronJob) throws InterruptedException, RetryLaterException {

        LOG.info("Starting Fetching Data from database at " + new Date());

        try {
            final List<ProductModel> products = searchNewProductsOnTimesStamp();

            final Date fromDate = DateUtils.addDays(new Date(), -7);
            final Date toDate = DateUtils.addDays(new Date(), 0);

            if (products == null || products.isEmpty()) {
                LOG.info("No new product found for the date range [FROM: " + fromDate + ", TO: " + toDate + "] ");
                return;
            }

            // Calling Business process through Event for Email
            LOG.info("Try to send new products newsletter for " + products.size() + " products at " + new Date());
            final DistNewProductsNewsLetterEvent newPorductsNewsLetterEvent = new DistNewProductsNewsLetterEvent(products, fromDate, toDate);
            setUpAdditionalEventData(newPorductsNewsLetterEvent);
            getEventService().publishEvent(newPorductsNewsLetterEvent);
        } catch (final Exception exp) {
            LOG.error("DistNewProductsNewsLetterJob: Exception send new products info to Business Process: " + exp.getMessage(), exp);
        }
    }

    /**
     * Fetch all the new Products
     * 
     * @return a list of new products.
     */
    private List<ProductModel> searchNewProductsOnTimesStamp() {
        final Date ydate = DateUtils.addDays(new Date(), -7);
        final FlexibleSearchQuery query = new FlexibleSearchQuery(NEW_PRODUCTS_NEWS_LETTER_QUERY);
        query.addQueryParameter(ProductModel.FIRSTAPPEARANCEDATE, ydate);
        final SearchResult<ProductModel> result = flexibleSearchService.<ProductModel> search(query);

        return result.getCount() > 0 ? result.getResult() : Collections.<ProductModel> emptyList();
    }

    /**
     * Populate the user event with store and site data.
     * 
     * @param userEvent
     */
    protected void setUpAdditionalEventData(final AbstractCommerceUserEvent userEvent) {
        userEvent.setBaseStore(baseStoreService.getBaseStoreForUid("distrelec_CH_b2b"));
        userEvent.setSite(baseSiteService.getBaseSiteForUID("distrelec_CH"));
    }

    // Converting the images

    public Map<String, NewLetterImageData> convert(final MediaContainerModel source) throws ConversionException {
        return convertToMediaContainer(source, createTarget());
    }

    public Map<String, NewLetterImageData> convertToMediaContainer(final MediaContainerModel source, final Map<String, NewLetterImageData> prototype)
            throws ConversionException {
        populateMediaContainer(source, prototype);
        return prototype;
    }

    public void populateMediaContainer(final MediaContainerModel source, final Map<String, NewLetterImageData> target) {

        for (final MediaModel media : source.getMedias()) {
            final NewLetterImageData image = convertToMedia(media);
            if (StringUtils.isNotEmpty(image.getFormat())) {
                target.put(image.getFormat(), image);
            }
        }
    }

    protected Map<String, NewLetterImageData> createTarget() {
        return new HashMap<String, NewLetterImageData>();
    }

    public NewLetterImageData convertToMedia(final MediaModel source) throws ConversionException {
        return convertToMediaImages(source, createTarget1());
    }

    public NewLetterImageData convertToMediaImages(final MediaModel source, final NewLetterImageData prototype) throws ConversionException {
        populateProductImages(source, prototype);
        return prototype;
    }

    protected NewLetterImageData createTarget1() {
        return new NewLetterImageData();
    }

    public void populateProductImages(final MediaModel source, final NewLetterImageData target) {

        target.setUrl(source.getURL());
        target.setAltText(source.getAltText());
        if (source.getMediaFormat() != null) {
            target.setFormat(source.getMediaFormat().getQualifier());
        }
    }

    // Get All the associated Images based on Illustrative and Primary Image
    protected void getImageDetails(final ProductModel productModel) {
        if (productModel.getIllustrativeImage() != null || productModel.getPrimaryImage() != null) {
            final List<Map<String, NewLetterImageData>> images = new ArrayList<Map<String, NewLetterImageData>>();

            if (productModel.getPrimaryImage() != null) {
                images.add(convert(productModel.getPrimaryImage()));
            } else if (productModel.getIllustrativeImage() != null) {
                images.add(convert(productModel.getIllustrativeImage()));
            }
        }
    }

    // Setter and Getter

    public EventService getEventService() {
        return eventService;
    }

    public void setEventService(final EventService eventService) {
        this.eventService = eventService;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public void setBaseSiteService(BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    public void setBaseStoreService(BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

}