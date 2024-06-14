package com.namics.distrelec.b2b.core.service.wishlist.impl;

import static java.util.Collections.emptyMap;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import java.util.*;
import java.util.stream.Collectors;

import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;

import com.namics.distrelec.b2b.core.enums.NamicsWishlistType;
import com.namics.distrelec.b2b.core.inout.erp.OrderCalculationService;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.service.order.exceptions.ProductNotBuyableException;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.core.service.product.DistSalesOrgProductService;
import com.namics.distrelec.b2b.core.service.wishlist.DistWishlistService;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.wishlist2.enums.Wishlist2EntryPriority;
import de.hybris.platform.wishlist2.model.Wishlist2EntryModel;
import de.hybris.platform.wishlist2.model.Wishlist2Model;

public class DefaultDistWishlistService extends DefaultNamicsWishlistService implements DistWishlistService {

    private static final String DEFAULT_LIST_KEY = "shoppingList.default";

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private DistProductService productService;

    @Autowired
    private I18NService i18NService;

    @Autowired
    private DistSalesOrgProductService distSalesOrgProductService;

    @Autowired
    @Qualifier("erp.orderCalculationService")
    private OrderCalculationService orderCalculationService;

    @Autowired
    private ModelService modelService;

    @Override
    public void deleteWishlist(final Wishlist2Model wishlist) {
        getModelService().remove(wishlist);
    }

    @Override
    public Wishlist2Model createEmptyWishlist(final String name, final NamicsWishlistType type, final Boolean isDefault) {
        return createEmptyWishlist(name, type, isDefault, getCurrentUser());
    }

    @Override
    public Wishlist2Model createEmptyWishlist(final String name, final NamicsWishlistType type, final Boolean isDefault, final UserModel user) {
        final Wishlist2Model wishlist = getModelService().create(Wishlist2Model.class);
        wishlist.setUniqueId(UUID.randomUUID().toString());
        wishlist.setListType(type);
        wishlist.setName(name);
        wishlist.setUser(user);
        wishlist.setDefault(isDefault);
        wishlist.setTotalPrice(Double.valueOf(0));

        if (wishlist.getEntries() == null) {
            wishlist.setEntries(new ArrayList<>());
        }

        getModelService().save(wishlist);
        return wishlist;
    }

    @Override
    public void setWishlistToDefault(final Wishlist2Model wishlist) {
        wishlist.setDefault(Boolean.TRUE);
        getModelService().save(wishlist);
    }

    @Override
    public Wishlist2Model getOrCreateWishlist(final NamicsWishlistType type) {
        Wishlist2Model wishlist = super.getDefaultWishlist(type);
        if (wishlist == null && !isGuestUsergroup()) {
            wishlist = super.createEmptyDefaultWishlist(getDefaultListName(), StringUtils.EMPTY, type);
        }
        return wishlist;
    }

    @Override
    public List<Wishlist2Model> getOrCreateWishlists(final NamicsWishlistType type) {
        List<Wishlist2Model> wishlists = getNamicsWishlistDao().findWishlistsByType(getCurrentUser(), type);
        if (CollectionUtils.isEmpty(wishlists) && !isGuestUsergroup()) {
            wishlists = new ArrayList<>();
            wishlists.add(super.createEmptyDefaultWishlist(getDefaultListName(), StringUtils.EMPTY, type));
        }
        return wishlists;
    }

    @Override
    public void updateWishlistEntry(final Wishlist2EntryModel entry, final Integer desired) {
        entry.setDesired(desired);
        entry.getWishlist().setCalculated(Boolean.FALSE);
        getModelService().saveAll(entry.getWishlist(), entry);
    }

    @Override
    public void addWishlistEntry(final Wishlist2Model wishlist,
                                 final ProductModel product,
                                 final Integer desired,
                                 final Wishlist2EntryPriority priority,
                                 final String comment) {
        final Wishlist2EntryModel entry = new Wishlist2EntryModel();
        entry.setProduct(product);

        final DistSalesOrgProductModel salesOrgProduct = distSalesOrgProductService.getCurrentSalesOrgProduct(product);
        if (salesOrgProduct != null
                && salesOrgProduct.getOrderQuantityMinimum() != null
                && (desired == null || desired < salesOrgProduct.getOrderQuantityMinimum().intValue())) {
            entry.setDesired(salesOrgProduct.getOrderQuantityMinimum().intValue());
        } else {
            entry.setDesired(desired);
        }
        entry.setPriority(priority);
        entry.setComment(comment);
        entry.setAddedDate(new Date());
        addWishlistEntry(wishlist, entry);
    }

    @Override
    public Wishlist2Model calculateShoppingList(String uuid) throws CalculationException {
        final Wishlist2Model wishlist = getWishlist(uuid);
        if (wishlist != null && isNotEmpty(wishlist.getEntries())) {
            List<Wishlist2EntryModel> orphanedEntries = wishlist.getEntries().stream()
                    .filter(wishlistEntry -> null == wishlistEntry.getProduct())
                    .collect(Collectors.toList());

            if(CollectionUtils.isNotEmpty(orphanedEntries)) {
                modelService.removeAll(orphanedEntries);
                wishlist.setCalculated(false);
                modelService.save(wishlist);
            }

            for (final Wishlist2EntryModel entry : wishlist.getEntries()) {
                if (!productService.isProductBuyable(entry.getProduct()) || productService.isEndOfLife(entry.getProduct())) {
                    throw new ProductNotBuyableException("shoppinglist.product.error.punchout");
                }

            }
            // If all products are buyable, then proceed to calculation
            orderCalculationService.calculateWishList(wishlist);
        }
        return wishlist;
    }

    @Override
    public int countLists(final NamicsWishlistType type) {
        return getNamicsWishlistDao().countLists(getUserService().getCurrentUser(), type);
    }

    @Override
    public Map<String, List<String>> productsInWishlists(final List<String> productCodes) {
        UserModel currentUser = getCurrentUser();
        if (getUserService().isAnonymousUser(currentUser)) {
            return emptyMap();
        }
        return getNamicsWishlistDao().productsInWishlists(currentUser, productCodes);
    }

    @Override
    public Map<String, List<String>> productsInShoppingList(final List<String> productCodes) {
        UserModel currentUser = getCurrentUser();
        if (getUserService().isAnonymousUser(currentUser)) {
            return emptyMap();
        }
        return getNamicsWishlistDao().productsInWishlists(currentUser, productCodes, NamicsWishlistType.SHOPPING);
    }

    @Override
    public Map<String, List<String>> productsInWishlists(final List<String> productCodes, final NamicsWishlistType... types) {
        return getNamicsWishlistDao().productsInWishlists(getCurrentUser(), productCodes, types);
    }

    @Override
    protected String getDefaultListName() {
        final Locale currLocale = i18NService.getCurrentLocale();
        return messageSource.getMessage(DEFAULT_LIST_KEY, null, DEFAULT_LIST, currLocale);
    }
}
