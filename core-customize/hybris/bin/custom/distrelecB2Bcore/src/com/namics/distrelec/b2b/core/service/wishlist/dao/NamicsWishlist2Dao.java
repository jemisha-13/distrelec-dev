package com.namics.distrelec.b2b.core.service.wishlist.dao;

import java.util.List;
import java.util.Map;

import com.namics.distrelec.b2b.core.enums.NamicsWishlistType;
import com.namics.distrelec.b2b.core.service.wishlist.TransientMiniWishlistModel;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.wishlist2.impl.daos.Wishlist2Dao;
import de.hybris.platform.wishlist2.model.Wishlist2Model;

/**
 * NamicsWishlist2Dao.
 * 
 * @author michaelwegener
 */
public interface NamicsWishlist2Dao extends Wishlist2Dao {

    /**
     * Count the number of entries in the default list of the specified type
     * 
     * @param user
     *            the owner of the wish list.
     * @param type
     *            the type of the wish list
     * @return the number of entries in the default wish list for the specified type.
     */
    int countDefaultWishlistEntries(final UserModel user, final NamicsWishlistType type);

    /**
     * Count the number of entries of wish list given by its {@code uuid}
     * 
     * @param user
     *            the owner of the wish list.
     * @param uuid
     *            the UUID of the wish list
     * @return the number of entries of wish list given by its {@code uuid}
     */
    int countWishlistEntries(final UserModel user, final String uuid);

    /**
     * Find a Wishlist2Model for a certain unique id.
     * 
     * @param uuid
     *            unique id from the wishlist
     * @return matching Wishlist2Model, or null if no Model could be found.
     * @see #findWishlistByUuid(UserModel, String)
     */
    Wishlist2Model findWishlistByUuid(String uuid);

    /**
     * Find a Wishlist2Model for a certain user and unique id
     * 
     * @param user
     *            the owner of the wishlist
     * @param uuid
     *            unique id from the wishlist
     * @return matching Wishlist2Model, or null if no Model could be found.
     */
    Wishlist2Model findWishlistByUuid(final UserModel user, String uuid);

    /**
     * Find a List of Wishlist2Model for a certain user and type.
     * 
     * @param user
     *            that is attached to the wishlist
     * @param type
     *            NamicsWishlistType of the concerning wishlist
     * @return matching List of Wishlist2Model, or null if no Model could be found.
     */
    List<Wishlist2Model> findWishlistsByType(UserModel user, NamicsWishlistType type);

    /**
     * Find a List of {@link TransientMiniWishlistModel} for a certain user and type.
     * 
     * @param user
     *            that is attached to the wishlist
     * @param type
     *            NamicsWishlistType of the concerning wishlist
     * @return matching List of TransientMiniWishlistDatas, or null if no wishlist could be found.
     */
    List<TransientMiniWishlistModel> findMiniWishlistsByType(UserModel user, NamicsWishlistType type);

    /**
     * Find a default Wishlist2Model for a certain user and type.
     * 
     * @param user
     *            that is attached to the wishlist
     * @param type
     *            NamicsWishlistType of the concerning wishlist
     * @return matching Wishlist2Model, or null if no Model could be found.
     */
    Wishlist2Model findDefaultWishlistByType(UserModel user, NamicsWishlistType type);

    /**
     * Find a default {@link TransientMiniWishlistModel} for a certain user and type.
     * 
     * @param user
     *            that is attached to the wishlist
     * @param type
     *            NamicsWishlistType of the concerning wishlist
     * @return matching TransientMiniWishlistData, or null if no wishlist could be found.
     */
    TransientMiniWishlistModel findDefaultMiniWishlistByType(UserModel user, NamicsWishlistType type);

    /**
     * Check if certain user has a given product in any of the wishlists of a given type.
     * 
     * @param user
     *            that is owner to the wishlist
     * @param productCode
     *            product to search for
     * @param type
     *            type of wishlist to search in
     * @return true if product was found
     */
    boolean isProductInWishlist(final UserModel user, final String productCode, final NamicsWishlistType type);

    /**
     * Check if the given products are in any user wish list
     * 
     * @param user
     *            the owner user
     * @param productCodes
     *            the products, given by their codes, to check.
     * @return a {@link Map} with keys as the product codes and the values as {@link List}s of {@link NamicsWishlistType}'s codes
     */
    Map<String, List<String>> productsInWishlists(final UserModel user, final List<String> productCodes);

    Map<String, List<String>> productsInWishlists(final UserModel user, final List<String> productCodes, final NamicsWishlistType... types);

    /**
     * Counts the number of wish lists with the given type and belonging to the specified user.
     * 
     * @param user
     *            the owner of the wish lists
     * @param type
     *            the type of the wish lists
     * @return the number of the wish lists.
     */
    int countLists(final UserModel user, final NamicsWishlistType type);
}
