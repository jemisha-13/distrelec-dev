/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.process.email.context;

import java.util.*;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.process.DistrelecNewProductsNewsLetterEmailProcessModel;
import com.namics.distrelec.b2b.core.model.process.ProductsNewsLetterProcessEntryModel;
import com.namics.distrelec.b2b.core.service.process.email.context.AbstractDistEmailContext;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.model.process.StoreFrontProcessModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.site.BaseSiteService;

/**
 * {@code DistNewProductsNewsLetterEmailContext}
 *
 *
 * @since Distrelec 5.8
 */
public class DistNewProductsNewsLetterEmailContext extends AbstractDistEmailContext {

    private static final String DEFAULT_IMG_URL = "/_ui/all/media/img/missing_landscape_small.png";

    private static final String PRODUCT_IMG_LANDSCAPE = "landscape_small";

    @Autowired
    @Qualifier("productFacade")
    private DistrelecProductFacade productFacade;

    @Autowired
    @Qualifier("productModelUrlResolver")
    private DistUrlResolver<ProductModel> productUrlResolver;

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    private BaseSiteService baseSiteService;

    @Autowired
    private CatalogVersionService catalogVersionService;

    private List<ProductData> products;

    private Date fromDate;

    private Date toDate;

    /*
     * (non-Javadoc)
     * @see
     * com.namics.distrelec.b2b.core.service.process.email.context.AbstractDistEmailContext#init(de.hybris.platform.processengine.model.
     * BusinessProcessModel, de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel)
     */
    @Override
    public void init(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
        // super.init(businessProcessModel, emailPageModel);

        put(THEME, null);
        put(FROM_EMAIL, emailPageModel.getFromEmail());
        put(FROM_DISPLAY_NAME, emailPageModel.getFromName());
        put(DISPLAY_NAME, emailPageModel.getFromName());
        put(EMAIL, getNewProductsNewsLetterEmail());
        put(EMAIL_LANGUAGE, getEmailLanguage(businessProcessModel));
        // DISTRELEC-2427: add string escape utils to email context
        put("StringEscapeUtils", StringEscapeUtils.class);

        if (businessProcessModel instanceof DistrelecNewProductsNewsLetterEmailProcessModel) {
            // Populate data
            setFromDate(((DistrelecNewProductsNewsLetterEmailProcessModel) businessProcessModel).getFromdate());
            setToDate(((DistrelecNewProductsNewsLetterEmailProcessModel) businessProcessModel).getTodate());
            convert(((DistrelecNewProductsNewsLetterEmailProcessModel) businessProcessModel).getProductItems());
        }
    }

    /**
     * Converts the list of product models to a list of product data.
     *
     * @param sources
     *            the list of product models.
     */
    private void convert(final Set<ProductsNewsLetterProcessEntryModel> sources) {
        if (sources == null || sources.isEmpty()) {
            this.products = Collections.<ProductData> emptyList();
        }

        this.products = new ArrayList<>();

        final Collection<ProductOption> options = Arrays.asList(ProductOption.MIN_BASIC, ProductOption.DIST_MANUFACTURER, ProductOption.COUNTRY_OF_ORIGIN);

        final String language = getEmailLanguage().getIsocode();
        final BaseSiteModel baseSite = getBaseSiteService().getBaseSiteForUID("distrelec_CH");
        getCommonI18NService().setCurrentLanguage(getEmailLanguage());
        getCmsSiteService().setCurrentSite((CMSSiteModel) baseSite);
        final String productCatalogId = getConfigurationService().getConfiguration().getString("import.pim.productCatalog.id", "distrelecProductCatalog");
        final String productCatalogVersion = getConfigurationService().getConfiguration().getString("import.pim.productCatalogVersion.version", "Online");
        getCatalogVersionService().setSessionCatalogVersion(productCatalogId, productCatalogVersion);

        final String domain = getConfigurationService().getConfiguration().getString("website.distrelec_CH.http", "http://www.distrelec.ch");

        for (final ProductsNewsLetterProcessEntryModel entry : sources) {
            final ProductModel source = entry.getProduct();
            final ProductData productData = getProductFacade().getProductForCodeAndOptions(source.getCode(), options);
            // We should set the full URL not the relative one
            productData.setUrl(domain + getProductUrlResolver().resolve(source, baseSite, language));

            // For the product image, if no image found, then we use the default one.
            // /_ui/all/media/img/missing_landscape_medium.png

            if (productData.getProductImages() == null) {
                productData.setProductImages(new ArrayList<>());
            }
            if (productData.getProductImages().isEmpty()) {
                productData.getProductImages().add(new HashMap<>());
            }
            if (!productData.getProductImages().get(0).containsKey(PRODUCT_IMG_LANDSCAPE)) {
                productData.getProductImages().get(0).put(PRODUCT_IMG_LANDSCAPE, new ImageData());
            }

            final String imageURL = productData.getProductImages().get(0).get(PRODUCT_IMG_LANDSCAPE).getUrl();
            productData.getProductImages().get(0).get(PRODUCT_IMG_LANDSCAPE).setUrl(domain + (StringUtils.isNotBlank(imageURL) ? imageURL : DEFAULT_IMG_URL));

            this.products.add(productData);
        }
    }

    /**
     * @return the destination email address.
     */
    private String getNewProductsNewsLetterEmail() {
        final String email = getConfigurationService().getConfiguration().getString(DistConstants.PropKey.Email.NEW_PRODUCT_NEWS_LETTER_EMAIL);
        return StringUtils.isNotBlank(email) ? email
                                             : getConfigurationService().getConfiguration()
                                                                        .getString(DistConstants.PropKey.Email.NEW_PRODUCT_NEWS_LETTER_EMAIL_DEFAULT);
    }

    /*
     * (non-Javadoc)
     * @see
     * de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext#getSite(de.hybris.platform.processengine.model.
     * BusinessProcessModel)
     */
    @Override
    protected BaseSiteModel getSite(final BusinessProcessModel businessProcessModel) {
        if (businessProcessModel instanceof StoreFrontProcessModel) {
            return ((StoreFrontProcessModel) businessProcessModel).getSite();
        }
        return getBaseSiteService().getBaseSiteForUID("distrelec_CH");
    }

    /*
     * (non-Javadoc)
     * @see
     * de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext#getCustomer(de.hybris.platform.processengine.model.
     * BusinessProcessModel)
     */
    @Override
    protected CustomerModel getCustomer(final BusinessProcessModel businessProcessModel) {
        return null;
    }

    public List<ProductData> getProducts() {
        return products;
    }

    public void setProducts(final List<ProductData> products) {
        this.products = products;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(final Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(final Date toDate) {
        this.toDate = toDate;
    }

    public DistrelecProductFacade getProductFacade() {
        return productFacade;
    }

    public void setProductFacade(final DistrelecProductFacade productFacade) {
        this.productFacade = productFacade;
    }

    public DistUrlResolver<ProductModel> getProductUrlResolver() {
        return productUrlResolver;
    }

    public void setProductUrlResolver(final DistUrlResolver<ProductModel> productUrlResolver) {
        this.productUrlResolver = productUrlResolver;
    }

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(final CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public void setBaseSiteService(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public CatalogVersionService getCatalogVersionService() {
        return catalogVersionService;
    }

    public void setCatalogVersionService(final CatalogVersionService catalogVersionService) {
        this.catalogVersionService = catalogVersionService;
    }
}
