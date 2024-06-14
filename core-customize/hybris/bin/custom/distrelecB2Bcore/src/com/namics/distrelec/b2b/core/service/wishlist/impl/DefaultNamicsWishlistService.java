package com.namics.distrelec.b2b.core.service.wishlist.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.enums.NamicsWishlistType;
import com.namics.distrelec.b2b.core.service.wishlist.NamicsWishlistService;
import com.namics.distrelec.b2b.core.service.wishlist.TransientMiniWishlistModel;
import com.namics.distrelec.b2b.core.service.wishlist.dao.NamicsWishlist2Dao;

import de.hybris.platform.basecommerce.strategies.BaseStoreSelectorStrategy;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.wishlist2.enums.Wishlist2EntryPriority;
import de.hybris.platform.wishlist2.impl.DefaultWishlist2Service;
import de.hybris.platform.wishlist2.model.Wishlist2EntryModel;
import de.hybris.platform.wishlist2.model.Wishlist2Model;

/**
 * DefaultNamicsWishlist2Service. Attention: To allow this implementation to work as described, the spring bean configuration must allow
 * anonymous wishlist to be saved! All wishlists will be loaded from the database. The only information in the session concerning the
 * wishlists is the uniqueId from anonymous users (and those of the configured user groups in the spring bean). The wishlists are identified
 * either by their uniqueId or their type and user. If the identification is by type and user, the concerning wishlist is the default
 * wishlist from this user and type. The first time an entry is added to the wishlist, the wishlist itself gets saved to the database. If an
 * anonymous user want to login, all entries from the anonymous wishlist will be merged to the users wishlist. Every product can occur only
 * once in a wishlist.
 * 
 * @author michaelwegener
 */
public class DefaultNamicsWishlistService extends DefaultWishlist2Service implements NamicsWishlistService {

    protected static final String DEFAULT_LIST = "Shopping List";

    @Autowired
    private NamicsWishlist2Dao namicsWishlistDao;

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @Autowired
    private BaseStoreSelectorStrategy baseStoreSelectorStrategy;

    @Autowired
    private CMSSiteService cmsSiteService;

    private List<String> guestUsergroups = new ArrayList<String>();

    @Override
    public Wishlist2Model getOrCreateWishlist(final NamicsWishlistType type) {
        Wishlist2Model wishlist = null;

        if (!isGuestUsergroup()) {
            wishlist = getDefaultWishlist(type);
            if (wishlist == null) {
                wishlist = createEmptyDefaultWishlist(getDefaultListName(), StringUtils.EMPTY, type);
            }
        }

        return wishlist;
    }

    @Override
    public List<Wishlist2Model> getOrCreateWishlists(final NamicsWishlistType type) {
        List<Wishlist2Model> wishlists = null;
        final Wishlist2Model emptyWishlist = getOrCreateWishlist(type);
        if (emptyWishlist != null) {
            wishlists = new ArrayList<Wishlist2Model>();
            wishlists.add(emptyWishlist);
        }

        return wishlists;
    }

    @Override
    public TransientMiniWishlistModel getOrCreateMiniWishlist(final NamicsWishlistType type) {
        TransientMiniWishlistModel wishlist = null;

        if (!isGuestUsergroup()) {
            wishlist = getDefaultMiniWishlist(type);
            if (wishlist == null) {
                // this will be done for stability reasons, despite it takes too long for a get-mini-method
                wishlist = createEmptyDefaultMiniWishlist(getDefaultListName(), StringUtils.EMPTY, type);
            }
        }

        return wishlist;
    }

    @Override
    public List<TransientMiniWishlistModel> getOrCreateMiniWishlists(final NamicsWishlistType type) {
        if (!isGuestUsergroup()) {
            List<TransientMiniWishlistModel> wishlists = getNamicsWishlistDao().findMiniWishlistsByType(getCurrentUser(), type);
            if (wishlists == null) {
                // this will be done for stability reasons, despite it takes too long for a get-mini-method
                TransientMiniWishlistModel miniWishlist = createEmptyDefaultMiniWishlist(getDefaultListName(), StringUtils.EMPTY, type);
                wishlists = new ArrayList<TransientMiniWishlistModel>();
                wishlists.add(miniWishlist);
            }

            return wishlists;
        }

        return null;
    }

    @Override
    public Wishlist2Model getWishlist(final String uuid) {
        final Wishlist2Model wishlist = getNamicsWishlistDao().findWishlistByUuid(getUserService().getCurrentUser(), uuid);

        if (wishlist == null) {
            throw new UnknownIdentifierException("Cannot find wishlist with id '" + uuid + "'");
        }
        return wishlist;
    }

    @Override
    public List<ProductModel> getProductsFromWishlist(final Wishlist2Model wishlist) {
        final List<ProductModel> products = new ArrayList<ProductModel>();

        if (wishlist != null) {
            for (final Wishlist2EntryModel entry : wishlist.getEntries()) {
                if (entry.getProduct() != null) {
                    products.add(entry.getProduct());
                }
            }
        }

        return products;
    }

    @Override
    public boolean isProductInWishlist(final String productCode, final Wishlist2Model wishlist) {
        if (wishlist != null) {
            for (final Wishlist2EntryModel entry : wishlist.getEntries()) {
                if (entry.getProduct() != null) {
                    if (entry.getProduct().getCode().equals(productCode)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean isProductInWishlist(final String productCode, final NamicsWishlistType type) {
        return getNamicsWishlistDao().isProductInWishlist(getCurrentUser(), productCode, type);
    }

    @Override
    public Wishlist2Model createEmptyDefaultWishlist(final String name, final String description, final NamicsWishlistType type) {
        final Wishlist2Model wishlist = getModelService().create(Wishlist2Model.class);
        wishlist.setUniqueId(UUID.randomUUID().toString());
        wishlist.setListType(type);
        wishlist.setName(name);
        wishlist.setDescription(description);
        wishlist.setUser(getCurrentUser());
        wishlist.setDefault(Boolean.TRUE);
        wishlist.setTotalPrice(Double.valueOf(0));

        if (wishlist.getEntries() == null) {
            wishlist.setEntries(new ArrayList<Wishlist2EntryModel>());
        }

        getModelService().save(wishlist);
        return wishlist;
    }

    @Override
    public void updateWishlistMetadata(final String uuid, final String wishlistName, final String wishlistDescription) {
        final Wishlist2Model wishlist = getWishlist(uuid);
        if (wishlist != null) {
            wishlist.setName(wishlistName);
            wishlist.setDescription(wishlistDescription);
            getModelService().save(wishlist);
        }

    }

    @Override
    public synchronized boolean addWishlistEntryToWishlist(final Wishlist2Model wishlist, final NamicsWishlistType type, final ProductModel product,
            final Integer desiredQuantity, final Wishlist2EntryPriority priority, final String comment) {

        boolean added = false;
        Wishlist2Model concerningWishlist = wishlist;

        if (concerningWishlist == null) {
            concerningWishlist = createEmptyDefaultWishlist(getDefaultListName(), StringUtils.EMPTY, type);
        }

        if (!isProductInWishlist(product.getCode(), concerningWishlist)) {
            // this call saves the wishlist
            addWishlistEntry(concerningWishlist, product, desiredQuantity, priority, comment);
            added = true;
        }
        return added;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.wishlist.NamicsWishlistService#getWishlistSize(com.namics.distrelec.b2b.core.enums.
     * NamicsWishlistType)
     */
    @Override
    public int getWishlistSize(final NamicsWishlistType type) {
        return getNamicsWishlistDao().countDefaultWishlistEntries(getCurrentUser(), type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.wishlist.NamicsWishlistService#getWishlistSize(java.lang.String)
     */
    @Override
    public int getWishlistSize(final String uniqueId) {
        return getNamicsWishlistDao().countWishlistEntries(getCurrentUser(), uniqueId);
    }

    protected TransientMiniWishlistModel createEmptyDefaultMiniWishlist(final String name, final String description, final NamicsWishlistType type) {
        Wishlist2Model wishlist = createEmptyDefaultWishlist(name, description, type);
        // convert
        TransientMiniWishlistModel miniWishlist = new TransientMiniWishlistModel();
        miniWishlist.setName(wishlist.getName());
        miniWishlist.setUniqueId(wishlist.getUniqueId());
        miniWishlist.setTotalUnitCount(0);
        return miniWishlist;
    }

    protected Wishlist2Model getDefaultWishlist(final NamicsWishlistType type) {
        return getNamicsWishlistDao().findDefaultWishlistByType(getCurrentUser(), type);
    }

    protected String getDefaultListName() {
        return DEFAULT_LIST; // getCurrentUser().getUid();
    }

    protected TransientMiniWishlistModel getDefaultMiniWishlist(final NamicsWishlistType type) {
        return getNamicsWishlistDao().findDefaultMiniWishlistByType(getCurrentUser(), type);
    }

    protected boolean isGuestUsergroup() {
        // anonymous users are per definition guest users
        if (isAnonymousUser()) {
            return true;
        }

        // is the user in a guest user group?
        for (final PrincipalGroupModel group : getCurrentUser().getGroups()) {
            for (final String usergroupName : getGuestUsergroups()) {
                if (usergroupName.equals(group.getUid())) {
                    return true;
                }
            }
        }

        return false;
    }

    private UserModel getAnonymousUser() {
        return getUserService().getAnonymousUser();
    }

    private boolean isAnonymousUser() {
        return getAnonymousUser() == getCurrentUser();
    }

    public void setGuestUsergroups(final List<String> guestUsergroups) {
        this.guestUsergroups = guestUsergroups;

    }

    public NamicsWishlist2Dao getNamicsWishlistDao() {
        return namicsWishlistDao;
    }

    public void setNamicsWishlistDao(final NamicsWishlist2Dao namicsWishlistDao) {
        this.namicsWishlistDao = namicsWishlistDao;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public EventService getEventService() {
        return eventService;
    }

    public void setEventService(final EventService eventService) {
        this.eventService = eventService;
    }

    public BaseStoreSelectorStrategy getBaseStoreSelectorStrategy() {
        return baseStoreSelectorStrategy;
    }

    public void setBaseStoreSelectorStrategy(final BaseStoreSelectorStrategy baseStoreSelectorStrategy) {
        this.baseStoreSelectorStrategy = baseStoreSelectorStrategy;
    }

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(final CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    public List<String> getGuestUsergroups() {
        return guestUsergroups;
    }
}
