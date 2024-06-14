package com.namics.distrelec.b2b.facades.wishlist.impl;

import com.namics.distrelec.b2b.core.enums.NamicsWishlistType;
import com.namics.distrelec.b2b.core.service.wishlist.DistWishlistService;
import com.namics.distrelec.b2b.core.service.wishlist.TransientMiniWishlistModel;
import com.namics.distrelec.b2b.facades.wishlist.NamicsGenericWishlistFacade;
import com.namics.distrelec.b2b.facades.wishlist.converter.NamicsWishlistConverter;
import com.namics.distrelec.b2b.facades.wishlist.data.MiniWishlistData;
import com.namics.distrelec.b2b.facades.wishlist.data.NamicsWishlistData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.wishlist2.model.Wishlist2EntryModel;
import de.hybris.platform.wishlist2.model.Wishlist2Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DefaultNamicsGenericWishlistFacade.
 *
 * @author mwegener
 *
 */
public class DefaultNamicsGenericWishlistFacade implements NamicsGenericWishlistFacade {

    @Autowired
    protected DistWishlistService wishlistService;

    @Autowired
    private ModelService modelService;

    @Autowired
    @Qualifier("namicsWishlistConverter")
    protected Converter<Wishlist2Model, NamicsWishlistData> wishlistConverter;

    @Autowired
    @Qualifier("namicsMiniWishlistConverter")
    protected Converter<TransientMiniWishlistModel, MiniWishlistData> miniWishlistConverter;

    @Autowired
    protected ProductService productService;

    @Override
    public NamicsWishlistData getWishlist(final NamicsWishlistType type) {
        final Wishlist2Model wishlist = getWishlistService().getOrCreateWishlist(type);
        return convertWishlist(wishlist);
    }

    @Override
    public NamicsWishlistData getWishlist(final NamicsWishlistType type, final int maxWishlistEntries) {
        final Wishlist2Model wishlist = getWishlistService().getOrCreateWishlist(type);
        return convertWishlistMaxEntries(wishlist, maxWishlistEntries);
    }

    @Override
    public MiniWishlistData getMiniWishlist(final NamicsWishlistType type) {
        final TransientMiniWishlistModel wishlist = getWishlistService().getOrCreateMiniWishlist(type);
        return convertMiniWishlist(wishlist);
    }

    @Override
    public List<MiniWishlistData> getMiniWishlists(final NamicsWishlistType type) {
        final List<TransientMiniWishlistModel> wishlists = getWishlistService().getOrCreateMiniWishlists(type);
        if (wishlists != null) {
            final List<MiniWishlistData> miniWishlists = new ArrayList<MiniWishlistData>();
            for (final TransientMiniWishlistModel wishlist : wishlists) {
                miniWishlists.add(convertMiniWishlist(wishlist));
            }
            return miniWishlists;
        }
        return Collections.<MiniWishlistData> emptyList();
    }

    @Override
    public NamicsWishlistData getWishlist(final String uuid) {
        final Wishlist2Model wishlist = getWishlistService().getWishlist(uuid);
        return convertWishlist(wishlist);
    }

    @Override
    public List<NamicsWishlistData> getWishlists(final NamicsWishlistType type) {
        final List<NamicsWishlistData> wishlistDatas = new ArrayList<NamicsWishlistData>();
        final List<Wishlist2Model> wishlists = getWishlistService().getOrCreateWishlists(type);

        if (wishlists != null) {
            for (final Wishlist2Model wishlist : wishlists) {
                wishlistDatas.add(convertWishlist(wishlist));
            }
        }

        return wishlistDatas;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.facades.wishlist.NamicsGenericWishlistFacade#getWishlistSize(com.namics.distrelec.b2b.core.enums.
     * NamicsWishlistType)
     */
    @Override
    public int getWishlistSize(final NamicsWishlistType type) {
        return getWishlistService().getWishlistSize(type);
    }

    protected boolean removeFromWishlist(final String productCode, final Wishlist2Model wishlist) {
        final List<Wishlist2EntryModel> entries = wishlist.getEntries().stream()
                                                          .filter(wishlist2EntryModel -> wishlist2EntryModel.getProduct() != null)
                                                          .filter(wishlist2EntryModel -> productCode.equals(wishlist2EntryModel.getProduct().getCode()))
                                                          .collect(Collectors.toList());
        boolean exists = entries.isEmpty();
        modelService.removeAll(entries);
        wishlist.setCalculated(Boolean.FALSE);
        modelService.save(wishlist);
        modelService.refresh(wishlist);
        return exists;
    }

    protected boolean removeAllFromWishlist(final Wishlist2Model wishlist) {
        final Iterator iterator = wishlist.getEntries().iterator();
        while (iterator.hasNext()) {
            final Wishlist2EntryModel entry = (Wishlist2EntryModel) iterator.next();
            getWishlistService().removeWishlistEntry(wishlist, entry);
        }
        return true;

    }

    protected NamicsWishlistData convertWishlist(final Wishlist2Model wishlistModel) {
        return getWishlistConverter().convert(wishlistModel);
    }

    protected NamicsWishlistData convertWishlistMaxEntries(final Wishlist2Model wishlistModel, final int maxEntries) {
        return ((NamicsWishlistConverter) getWishlistConverter()).convertMaxEntries(wishlistModel, maxEntries);
    }

    protected MiniWishlistData convertMiniWishlist(final TransientMiniWishlistModel source) {
        return getMiniWishlistConverter().convert(source);
    }

    protected boolean isEntryInWishlist(final String code, final Wishlist2Model wishlist) {
        final List<ProductModel> products = getWishlistService().getProductsFromWishlist(wishlist);
        for (final ProductModel product : products) {
            if (product.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isEntryInWishlist(final String code, final NamicsWishlistType wishlistType) {
        return getWishlistService().isProductInWishlist(code, wishlistType);
    }

    protected MiniWishlistData createEmptyMiniWishlist() {
        return getMiniWishlistConverter().convert(null);
    }

    protected boolean isValidWishlist(final Wishlist2Model wishlist) {
        if (wishlist != null) {
            return true;
        }

        return false;
    }

    protected boolean isValidMiniWishlist(final TransientMiniWishlistModel wishlist) {
        if (wishlist != null) {
            return true;
        }

        return false;
    }

    public DistWishlistService getWishlistService() {
        return wishlistService;
    }

    public void setWishlistService(final DistWishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    public Converter<Wishlist2Model, NamicsWishlistData> getWishlistConverter() {
        return wishlistConverter;
    }

    public void setWishlistConverter(final Converter<Wishlist2Model, NamicsWishlistData> wishlistConverter) {
        this.wishlistConverter = wishlistConverter;
    }

    public Converter<TransientMiniWishlistModel, MiniWishlistData> getMiniWishlistConverter() {
        return miniWishlistConverter;
    }

    public void setMiniWishlistConverter(final Converter<TransientMiniWishlistModel, MiniWishlistData> miniWishlistConverter) {
        this.miniWishlistConverter = miniWishlistConverter;
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }
}
