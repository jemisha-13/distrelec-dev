package com.namics.distrelec.b2b.core.service.stock.impl;

import com.namics.distrelec.b2b.core.model.stock.DistStockNotificationModel;
import com.namics.distrelec.b2b.core.service.product.DistCommercePriceService;
import com.namics.distrelec.b2b.core.service.product.dao.DistProductDao;
import com.namics.distrelec.b2b.core.service.stock.DistStockNotificationService;
import com.namics.distrelec.b2b.core.service.stock.dao.DistStockNotificationDao;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.price.CommercePriceService;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@code StockNotificationEmailJob}
 * 
 * @author <a href="sudarshan.tembhurnikar@datwyler.com">Sudarshan Tembhurnikar@datwyler</a>, Distrelec
 * @since Distrelec 7.1
 */
public class DefaultDistStockNotificationService implements DistStockNotificationService {

    private static final Logger LOG = Logger.getLogger(DefaultDistStockNotificationService.class);

    private static final String DEFAULT_IMG_URL = "/_ui/all/media/img/missing_landscape_small.png";
    private static final String PRODUCT_IMG_LANDSCAPE = "landscape_small";

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    private CommonI18NService commonI18NService;

    @Autowired
    @Qualifier("productModelUrlResolver")
    private DistUrlResolver<ProductModel> productModelUrlResolver;

    @Autowired
    @Qualifier("catalogPlusProductModelUrlResolver")
    private DistUrlResolver<ProductModel> catalogPlusProductModelUrlResolver;

    @Autowired
    private CommercePriceService commercePriceService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private DistStockNotificationDao distStockNotificationDao;

    @Autowired
    private DistProductDao productDao;

    @Autowired
    private CartService cartService;

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.product.DistProductService#getStockNotificationDetails(java.lang.String, java.lang.String)
     */
    @Override
    public List<DistStockNotificationModel> getStockNotificationDetails(final String customerEmail, final List<String> articleNumbers, final CMSSiteModel site) {
        List<DistStockNotificationModel> models = new ArrayList<>();
        for(String articleNumber: articleNumbers) {
            final DistStockNotificationModel stockNotificationDetails = getDistStockNotificationDao().getStockNotificationDetails(customerEmail, articleNumber, site);
            if(stockNotificationDetails != null){
                models.add(stockNotificationDetails);
            }
        }
        return models;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.product.DistProductService#addStockNotificationDetails(java.lang.String, java.lang.String)
     */
    @Override
    public boolean addStockNotificationDetails(final String customerEmail, final List<String> articleNumbers, final CMSSiteModel site) {
        Map<String, OrderEntryData> dataMap = new HashMap<>();
        for(String articleNumber: articleNumbers){
            OrderEntryData pdpEntry = new OrderEntryData();
            pdpEntry.setQuantity(1L);
            dataMap.put(articleNumber, pdpEntry);
        }
        return processOutOfStockNotification(customerEmail, dataMap, site);
    }

    @Override
    public boolean processOutOfStockNotification(final String customerEmail, final Map<String, OrderEntryData> productAndQuantityMap, final CMSSiteModel site){
        boolean hasOutOfStockBeenSaved = false;
        List<DistStockNotificationModel> stockNotificationModels = new ArrayList<>();

        for(Map.Entry<String, OrderEntryData> entry: productAndQuantityMap.entrySet()){
            final String articleNumber = entry.getKey();
            final ProductModel productModel = getProductDao().findProductByTypeOrCode(articleNumber);
            final PriceInformation priceData = getCommercePriceService().getWebPriceForProduct(productModel, true);

            if(priceData != null) {
                // Set the Product Image URL
                final String productImageUrl = getProductImageUrl(productModel);
                final DistStockNotificationModel stockNotification = getModelService().create(DistStockNotificationModel.class);

                stockNotification.setArticleNumber(articleNumber);
                stockNotification.setCustomerEmailId(customerEmail);
                stockNotification.setCurrentSite(site);
                stockNotification.setLanguage(getCommonI18NService().getCurrentLanguage());
                stockNotification.setCurrency(priceData.getPriceValue().getCurrencyIso());
                stockNotification.setProductPrice(String.valueOf(priceData.getPriceValue().getValue()));

                final OrderEntryData entryValue = entry.getValue();
                stockNotification.setQuantityRequiredToSendEmail(entryValue != null ? entryValue.getQuantity().intValue() : 0);
                stockNotification.setProductImageUrl((StringUtils.isNotBlank(productImageUrl) ? productImageUrl : DEFAULT_IMG_URL));
                stockNotification.setProductPageUrl(getProductUrlResolver(productModel).resolve(productModel));
                stockNotification.setManufacturer(productModel.getTypeName());
                stockNotificationModels.add(stockNotification);
            }
        }
        if(CollectionUtils.isNotEmpty(stockNotificationModels)){
            hasOutOfStockBeenSaved = saveToDatabase(stockNotificationModels);
        }
        return hasOutOfStockBeenSaved;
    }

    private boolean saveToDatabase(final List<DistStockNotificationModel> stockNotificationModels) {
        boolean hasSaved = false;
        try {
            // Save in Database
            if(CollectionUtils.isNotEmpty(stockNotificationModels)) {
                getModelService().saveAll(stockNotificationModels);
                hasSaved = true;
            }
        } catch (ModelSavingException exception) {
            LOG.warn("Exception in adding Stock Notification Details in Database : " + exception.toString());
        }
        return hasSaved;
    }

    private String getProductImageUrl(final ProductModel productModel) {
        final List<MediaModel> productImageList = new ArrayList<MediaModel>(productModel.getPrimaryImage().getMedias());
        for (MediaModel mediaModel : productImageList) {
            if (mediaModel.getMediaFormat().getQualifier().equals(PRODUCT_IMG_LANDSCAPE)) {
                return mediaModel.getURL();
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.stock.DistStockNotificationService#getAllStockNotifications()
     */
    @Override
    public List<DistStockNotificationModel> getAllStockNotifications() {
        return getDistStockNotificationDao().getAllStockNotifications();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.stock.DistStockNotificationService#getStockNotificationsForAvailableStocks()
     */
    @Override
    public List<DistStockNotificationModel> getStockNotificationsForAvailableStocks() {
        final List<DistStockNotificationModel> stockNotificationsForAvailableStocks = new ArrayList<>(getDistStockNotificationDao().getStockNotificationsForAvailableStocks());
        final List<DistStockNotificationModel> duplicates = getDistStockNotificationDao().getDuplicateStockNotificationRecords();

        if(!CollectionUtils.isEmpty(duplicates)) {
            for(DistStockNotificationModel duplicate: duplicates){
                stockNotificationsForAvailableStocks.remove(duplicate);
            }
            removeStockNotificationDetails(duplicates);
        }
        return stockNotificationsForAvailableStocks;
    }

    @Override
    public void removeStockNotificationDetails(List<DistStockNotificationModel> distStockNotificationModelList) {
        modelService.removeAll(distStockNotificationModelList);
    }

    /**
     * Return the product URL resolver for the specified product
     *
     * @param productModel
     * @return the product URL resolver.
     */
    private DistUrlResolver<ProductModel> getProductUrlResolver(final ProductModel productModel) {
        return productModel.getCatPlusSupplierAID() == null ? getProductModelUrlResolver() : getCatalogPlusProductModelUrlResolver();
    }

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public DistUrlResolver<ProductModel> getProductModelUrlResolver() {
        return productModelUrlResolver;
    }

    public void setProductModelUrlResolver(DistUrlResolver<ProductModel> productModelUrlResolver) {
        this.productModelUrlResolver = productModelUrlResolver;
    }

    public DistUrlResolver<ProductModel> getCatalogPlusProductModelUrlResolver() {
        return catalogPlusProductModelUrlResolver;
    }

    public void setCatalogPlusProductModelUrlResolver(DistUrlResolver<ProductModel> catalogPlusProductModelUrlResolver) {
        this.catalogPlusProductModelUrlResolver = catalogPlusProductModelUrlResolver;
    }

    public DistCommercePriceService getCommercePriceService() {
        return (DistCommercePriceService) commercePriceService;
    }

    public void setCommercePriceService(CommercePriceService commercePriceService) {
        this.commercePriceService = commercePriceService;
    }

    public TypeService getTypeService() {
        return typeService;
    }

    public void setTypeService(TypeService typeService) {
        this.typeService = typeService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public DistStockNotificationDao getDistStockNotificationDao() {
        return distStockNotificationDao;
    }

    public void setDistStockNotificationDao(DistStockNotificationDao distStockNotificationDao) {
        this.distStockNotificationDao = distStockNotificationDao;
    }

    public DistProductDao getProductDao() {
        return productDao;
    }

    public void setProductDao(DistProductDao productDao) {
        this.productDao = productDao;
    }

    public CartService getCartService() {
        return cartService;
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }
}
