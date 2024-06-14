/**
 * 
 */
package com.namics.distrelec.b2b.core.service.product.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

import com.namics.distrelec.b2b.core.price.DistPriceFactory;
import com.namics.distrelec.b2b.core.service.product.DistPriceService;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.order.OrderManager;
import de.hybris.platform.jalo.order.price.DiscountInformation;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.order.price.PriceFactory;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.product.impl.DefaultPriceService;
import de.hybris.platform.servicelayer.exceptions.SystemException;

/**
 * Default implementation of the {@link DistPriceService}.
 * 
 * @author daehusir, Distrelec
 * 
 */
@SuppressWarnings("deprecation")
public class DefaultDistPriceService extends DefaultPriceService implements DistPriceService {

    @Override
    public List<PriceInformation> getListPriceInformationsForProduct(final ProductModel product) {
        final Product productItem = (Product) getModelService().getSource(product);
        try {
            final DistPriceFactory priceFactory = (DistPriceFactory) OrderManager.getInstance().getPriceFactory();
            return priceFactory.getProductListPriceInformations(JaloSession.getCurrentSession().getSessionContext(), productItem, new Date(),
                    priceFactory.isNetUser(JaloSession.getCurrentSession().getUser()));
        } catch (final JaloPriceFactoryException e) {
            throw new SystemException(e.getMessage(), e);
        }
    }

    @Override
    public List<DiscountInformation> getDiscountInformationsForProduct(final ProductModel product) {
        final Product productItem = (Product) getModelService().getSource(product);
        try {
            final PriceFactory priceFactory = OrderManager.getInstance().getPriceFactory();
            return productItem.getDiscountInformations(priceFactory.isNetUser(JaloSession.getCurrentSession().getUser()));
        } catch (final JaloPriceFactoryException e) {
            throw new SystemException(e.getMessage(), e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.service.product.DistPriceService#getPriceInformationsForProduct(de.hybris.platform.core.model.product
     * .ProductModel, boolean)
     */
    @Override
    public List<PriceInformation> getPriceInformationsForProduct(final ProductModel model, final boolean onlinePrice) {
        final Product productItem = (Product) getModelService().getSource(model);
        try {
            final DistPriceFactory priceFactory = (DistPriceFactory) OrderManager.getInstance().getPriceFactory();
            return priceFactory.getProductPriceInformations(JaloSession.getCurrentSession().getSessionContext(), productItem, new Date(),
                    priceFactory.isNetUser(JaloSession.getCurrentSession().getUser()), onlinePrice);
        } catch (final JaloPriceFactoryException e) {
            throw new SystemException(e.getMessage(), e);
        }
    }
    
    @Override
    public List<PriceInformation> getPriceInformationNet(final ProductModel model) {
        final Product productItem = getModelService().getSource(model);
        try {
            final DistPriceFactory priceFactory = (DistPriceFactory) OrderManager.getInstance().getPriceFactory();
            return priceFactory.getProductPriceInformations(JaloSession.getCurrentSession().getSessionContext(), productItem, new Date(),
                    true, true);
        } catch (final JaloPriceFactoryException e) {
            throw new SystemException(e.getMessage(), e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.product.DistPriceService#getListPriceInformationsForProduct(de.hybris.platform.core.model.
     * product .ProductModel, boolean)
     */
    @Override
    public List<PriceInformation> getListPriceInformationsForProduct(final ProductModel product, final boolean onlinePrice) {
        final Product productItem = (Product) getModelService().getSource(product);
        try {
            final DistPriceFactory priceFactory = (DistPriceFactory) OrderManager.getInstance().getPriceFactory();
            return priceFactory.getProductListPriceInformations(JaloSession.getCurrentSession().getSessionContext(), productItem, new Date(),
                    priceFactory.isNetUser(JaloSession.getCurrentSession().getUser()), onlinePrice);
        } catch (final JaloPriceFactoryException e) {
            throw new SystemException(e.getMessage(), e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.service.product.DistPriceService#getPriceInformationsForProduct(de.hybris.platform.core.model.product.
     * ProductModel, boolean, boolean)
     */
    @Override
    public List<PriceInformation> getPriceInformationsForProduct(final ProductModel model, final boolean onlinePrice, final boolean force) {
        try {
            final DistPriceFactory priceFactory = (DistPriceFactory) OrderManager.getInstance().getPriceFactory();
            return priceFactory.getProductPriceInformations(JaloSession.getCurrentSession().getSessionContext(), model, new Date(),
                    priceFactory.isNetUser(JaloSession.getCurrentSession().getUser()), onlinePrice, force);
        } catch (final JaloPriceFactoryException e) {
            throw new SystemException(e.getMessage(), e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.product.DistPriceService#getPriceInformationsForProducts(java.util.List, boolean, boolean)
     */
    @Override
    public Map<String, List<PriceInformation>> getPriceInformationsForProducts(final List<ProductModel> models, final boolean onlinePrice,
            final boolean force) {
        if (models == null || models.isEmpty()) {
            return MapUtils.EMPTY_MAP;
        }

        try {
            final DistPriceFactory priceFactory = (DistPriceFactory) OrderManager.getInstance().getPriceFactory();
            return priceFactory.getProductPriceInformations(JaloSession.getCurrentSession().getSessionContext(), models, new Date(),
                    priceFactory.isNetUser(JaloSession.getCurrentSession().getUser()), onlinePrice, force);
        } catch (final JaloPriceFactoryException e) {
            throw new SystemException(e.getMessage(), e);
        }
    }
}
