package com.namics.distrelec.b2b.core.service.wishlist;

import java.util.List;

import com.namics.distrelec.b2b.core.enums.NamicsWishlistType;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.wishlist2.Wishlist2Service;
import de.hybris.platform.wishlist2.enums.Wishlist2EntryPriority;
import de.hybris.platform.wishlist2.model.Wishlist2Model;

/**
 * NamicsWishlistService.
 * 
 * 
 * <p>
 * Gibt die Default-Wishlist des jeweiligen Types zurück, wobei folgende Typen unterstützt werden:
 * <ul>
 * <li>NamicsWishlistType#
 * </ul>
 * 
 * <p>
 * Wenn der current-User Anonymous/Guest-User in der aktuellen Session ist, wird die Liste in der DB gespeichert, jedoch mit Anonymous als
 * User. Um die Verknüpfung zur Session zu haben, wird die UUID der Wishlist in die Session geschrieben.
 * </p>
 * 
 * @author michaelwegener
 * 
 */
public interface NamicsWishlistService extends Wishlist2Service {
    /**
     * Gets the default Wishlist2Model from the concerning NamicsWishlistType and the current user. As every user should at least have one
     * wishlist per type, an empty wishlist will be created.
     * 
     * @param type
     *            Type of the Wishlist
     * @return Wishlist2Model of the current user
     */

    public Wishlist2Model getOrCreateWishlist(final NamicsWishlistType type);

    /**
     * Gets the Wishlist2Model from the uniqueId.
     * 
     * @param uuid
     *            unique identification of the Wishlist2Model
     * @return Wishlist2Model of the concerning uniqueId
     * 
     * @throws UnknownIdentifierException
     *             if no wishlist with the given uuid was found
     */
    public Wishlist2Model getWishlist(final String uuid) throws UnknownIdentifierException;

    /**
     * Gets all Wishlist2Model from the concerning NamicsWishlistType and the current user. As every user should at least have one wishlist
     * per type, an empty wishlist will be created.
     * 
     * @param type
     *            Type of the Wishlist
     * @return List of Wishlist2Model of the current user
     */
    public List<Wishlist2Model> getOrCreateWishlists(final NamicsWishlistType type);

    /**
     * Gets the default {@link TransientMiniWishlistModel} from the concerning {@link NamicsWishlistType} and the current user. As every
     * user should at least have one wishlist per type, an empty wishlist will be created. <br/>
     * <br/>
     * The creating of a default wishlist normally takes way too long for a get mini method but for stability reasons it will be done
     * nonetheless.
     * 
     * @param type
     *            Type of the Wishlist
     * @return TransientMiniWishlistData of the current user
     */

    public TransientMiniWishlistModel getOrCreateMiniWishlist(final NamicsWishlistType type);

    /**
     * Gets all {@link TransientMiniWishlistModel} from the concerning {@link NamicsWishlistType} and the current user. As every user should
     * at least have one wishlist per type, an empty wishlist will be created. <br/>
     * <br/>
     * The creating of a default wishlist normally takes way too long for a get mini method but for stability reasons it will be done
     * nonetheless.
     * 
     * @param type
     *            Type of the Wishlist
     * @return List of TransientMiniWishlistData of the current user
     */
    public List<TransientMiniWishlistModel> getOrCreateMiniWishlists(final NamicsWishlistType type);

    /**
     * Gets a List of ProductModel from the concerning Wishlist2Model. If the wishlist has no products, an empty list will be returned.
     * 
     * @param wishlist
     *            Wishlist2Model
     * @return List of ProductModel from the concerning wishlist
     */
    public List<ProductModel> getProductsFromWishlist(final Wishlist2Model wishlist);

    /**
     * Determines if a certain product is attached to the concerning wishlist
     * 
     * @param productCode
     *            code of the product
     * @param wishlist
     *            Wishlist2Model which will be search for the product code
     * @return true if wishlist contains the productCode
     */
    public boolean isProductInWishlist(final String productCode, final Wishlist2Model wishlist);

    /**
     * Determines if a certain product is attached to any wishlist of a given type
     * 
     * @param productCode
     *            code of the product
     * @param type
     *            type of withlist which will be search for the product code
     * @return true if any wishlist of a given type contains the productCode
     */
    public boolean isProductInWishlist(final String productCode, final NamicsWishlistType type);

    /**
     * Add a wishlist entry to a certain wishlist for a certain product.
     * 
     * @param wishlist
     *            the concerning wishlist
     * @param type
     *            the wishlist Type
     * @param product
     *            that should be added
     * @param desiredQuantity
     *            quantity to add
     * @param priority
     *            of the wishlist entry
     * @param comment
     *            for the wishlist entry
     * @return true if the wishlist entry was added
     */
    public boolean addWishlistEntryToWishlist(final Wishlist2Model wishlist, final NamicsWishlistType type, final ProductModel product,
            final Integer desiredQuantity, final Wishlist2EntryPriority priority, final String comment);

    /**
     * Create an empty default Wishlist2Model with a random generated UUID
     * 
     * @param wishlistName
     *            name of the wishlist
     * @param type
     *            the wishlist Type
     * @return new created Wishlist2Model
     */
    public Wishlist2Model createEmptyDefaultWishlist(final String wishlistName, final String wishlistDescription, final NamicsWishlistType type);

    /**
     * Changes the name and description of a certain wishlist.
     * 
     * @param uuid
     *            uniqueId to identify the wishlist
     * @param wishlistName
     *            the new wishlist name
     * @param wishlistDescription
     *            the new wishlist description
     */
    public void updateWishlistMetadata(final String uuid, final String wishlistName, final String wishlistDescription);

    /**
     * Return the number of products in the default wish list for the specified type.
     *
     * @param type
     *            the list type
     * @return the number of products in the default wish list for the specified type.
     */
    public int getWishlistSize(final NamicsWishlistType type);

    /**
     * Return the number of products in the wish list given by its UID.
     *
     * @param uniqueId
     *            the list UID
     * @return the number of products in the wish list given by its UID.
     */
    public int getWishlistSize(final String uniqueId);
}
