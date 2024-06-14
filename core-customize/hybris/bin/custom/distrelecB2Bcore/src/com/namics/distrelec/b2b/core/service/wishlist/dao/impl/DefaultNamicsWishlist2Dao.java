/**
 * 
 */
package com.namics.distrelec.b2b.core.service.wishlist.dao.impl;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import com.namics.distrelec.b2b.core.enums.NamicsWishlistType;
import com.namics.distrelec.b2b.core.service.wishlist.TransientMiniWishlistModel;
import com.namics.distrelec.b2b.core.service.wishlist.dao.NamicsWishlist2Dao;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.wishlist2.enums.Wishlist2EntryPriority;
import de.hybris.platform.wishlist2.impl.daos.impl.DefaultWishlist2Dao;
import de.hybris.platform.wishlist2.model.Wishlist2EntryModel;
import de.hybris.platform.wishlist2.model.Wishlist2Model;

/**
 * DefaultNamicsWishlist2Dao.
 * 
 * @author michaelwegener
 * @since Namics Extensions 1.0
 */
public class DefaultNamicsWishlist2Dao extends DefaultWishlist2Dao implements NamicsWishlist2Dao {

    private static final Logger LOG = Logger.getLogger(DefaultNamicsWishlist2Dao.class.getName());

    private static final List<Class<Long>> R_CLASS_LIST = Arrays.asList(Long.class);

    /**
     * 
     */
    private static final String COUNT_ENTRIES_QUERY = "SELECT count({we:" + Wishlist2EntryModel.PK + "}) FROM {" + Wishlist2EntryModel._TYPECODE
                                                      + " AS we JOIN " + Wishlist2Model._TYPECODE + " AS w ON {w:" + Wishlist2Model.PK + "} = {we:"
                                                      + Wishlist2EntryModel.WISHLIST + "}} WHERE {w:"
                                                      + Wishlist2Model.LISTTYPE + "}=?" + Wishlist2Model.LISTTYPE + " AND {w:" + Wishlist2Model.USER + "}=?"
                                                      + Wishlist2Model.USER + " AND {w:"
                                                      + Wishlist2Model.DEFAULT + "}=?" + Wishlist2Model.DEFAULT;

    /**
     * 
     */
    private static final String COUNT_WISHLIST_ENTRIES_QUERY = "SELECT count({we:" + Wishlist2EntryModel.PK + "}) FROM {" + Wishlist2EntryModel._TYPECODE
                                                               + " AS we JOIN " + Wishlist2Model._TYPECODE + " AS w ON {w:" + Wishlist2Model.PK + "} = {we:"
                                                               + Wishlist2EntryModel.WISHLIST + "}} WHERE {w:"
                                                               + Wishlist2Model.USER + "}=?" + Wishlist2Model.USER + " AND {w:" + Wishlist2Model.UNIQUEID
                                                               + "}=?" + Wishlist2Model.UNIQUEID;

    /**
     * 
     */
    private static final String COUNT_WISHLIST_FOR_USER_BY_TYPE = "SELECT COUNT({" + Wishlist2Model.PK + "}) FROM {" + Wishlist2Model._TYPECODE + "} WHERE {"
                                                                  + Wishlist2Model.USER + "}=?" + Wishlist2Model.USER + " AND {" + Wishlist2Model.LISTTYPE
                                                                  + "}=?" + Wishlist2Model.LISTTYPE;

    /**
     * 
     */
    private static final String CHECK_TOGGLES_QUERY = "SELECT DISTINCT {p." + ProductModel.CODE + "}, {tp.code} FROM {" + ProductModel._TYPECODE + " AS p JOIN "
                                                      + Wishlist2EntryModel._TYPECODE + " AS we ON {we." + Wishlist2EntryModel.PRODUCT + "}={p.pk} JOIN "
                                                      + Wishlist2Model._TYPECODE + " AS w ON {we."
                                                      + Wishlist2EntryModel.WISHLIST + "}={w.pk} JOIN " + NamicsWishlistType._TYPECODE + " AS tp ON {w."
                                                      + Wishlist2Model.LISTTYPE
                                                      + "}={tp.pk}} WHERE {w." + Wishlist2Model.USER + "}=?" + Wishlist2Model.USER + " AND {p."
                                                      + ProductModel.CODE + "} IN (?productCodes) ORDER BY {p."
                                                      + ProductModel.CODE + "}";

    private static final String CHECK_TYPED_TOGGLES_QUERY = "SELECT DISTINCT {p." + ProductModel.CODE + "}, {tp.code} FROM {" + ProductModel._TYPECODE
                                                            + " AS p JOIN " + Wishlist2EntryModel._TYPECODE + " AS we ON {we." + Wishlist2EntryModel.PRODUCT
                                                            + "}={p.pk} JOIN " + Wishlist2Model._TYPECODE
                                                            + " AS w ON {we." + Wishlist2EntryModel.WISHLIST + "}={w.pk} JOIN " + NamicsWishlistType._TYPECODE
                                                            + " AS tp ON {w." + Wishlist2Model.LISTTYPE
                                                            + "}={tp.pk}} WHERE {w." + Wishlist2Model.USER + "}=?" + Wishlist2Model.USER + " AND {p."
                                                            + ProductModel.CODE + "} IN (?productCodes) AND {w."
                                                            + Wishlist2Model.LISTTYPE + "} IN (?" + Wishlist2Model.LISTTYPE + ")" + " ORDER BY {p."
                                                            + ProductModel.CODE + "}";

    @Override
    public Wishlist2Model findWishlistByUuid(final UserModel user, final String uuid) {
        final Map<String, Object> params = new HashMap<>();
        params.put(Wishlist2Model.UNIQUEID, uuid);

        final StringBuilder builder = new StringBuilder();
        builder.append("SELECT {").append(Wishlist2Model.PK).append("} ");
        builder.append("FROM {").append(Wishlist2Model._TYPECODE).append("} ");
        builder.append("WHERE {").append(Wishlist2Model.UNIQUEID).append("}=?").append(Wishlist2Model.UNIQUEID);

        if (user != null) {
            params.put(Wishlist2Model.USER, user);
            builder.append(" AND {").append(Wishlist2Model.USER).append("}=?").append(Wishlist2Model.USER);
        }

        final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
        query.addQueryParameters(params);

        try {
            return getFlexibleSearchService().searchUnique(query);
        } catch (final ModelNotFoundException e) {
            return null;
        } catch (final AmbiguousIdentifierException e) {
            LOG.error("More than one wishlist found for uniqueId " + uuid);
            return null;
        }
    }

    @Override
    public Wishlist2Model findWishlistByUuid(final String uuid) {
        return findWishlistByUuid(null, uuid);
    }

    @Override
    public Wishlist2Model findDefaultWishlistByType(final UserModel user, final NamicsWishlistType type) {
        final Map<String, Object> params = new HashMap<>();
        params.put(Wishlist2Model.USER, user);
        params.put(Wishlist2Model.LISTTYPE, type);
        params.put(Wishlist2Model.DEFAULT, Boolean.TRUE);

        final StringBuilder builder = new StringBuilder();
        builder.append("SELECT {").append(Wishlist2Model.PK).append("} ");
        builder.append("FROM {").append(Wishlist2Model._TYPECODE).append("} ");
        builder.append("WHERE {").append(Wishlist2Model.USER).append("}=?").append(Wishlist2Model.USER + " ");
        builder.append("AND {").append(Wishlist2Model.LISTTYPE).append("}=?").append(Wishlist2Model.LISTTYPE + " ");
        builder.append("AND {").append(Wishlist2Model.DEFAULT).append("}=?").append(Wishlist2Model.DEFAULT);

        final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
        query.addQueryParameters(params);

        try {
            final List<Wishlist2Model> wishlists = getFlexibleSearchService().<Wishlist2Model> search(query).getResult();

            switch (wishlists.size()) {
                case 0: // No wishlist found
                    return null;
                case 1: // Only one wishlist
                    return wishlists.get(0);
                default: // More than one wishlist. We merge all wishlists in one.
                    break;
            }

            // //////////////////////////////////////////////////////////////////////////////////////////////////
            // Begin cleanup code
            // FIXME distrelec: (re)move cleanup code

            // Set informations on the wishlist
            final Wishlist2Model newWishlist = getModelService().create(Wishlist2Model.class);
            newWishlist.setUser(wishlists.get(0).getUser());
            newWishlist.setComments(wishlists.get(0).getComments());
            newWishlist.setDescription(wishlists.get(0).getDescription());
            newWishlist.setDefault(Boolean.TRUE);
            newWishlist.setListType(wishlists.get(0).getListType());
            newWishlist.setName(wishlists.get(0).getName());
            newWishlist.setOwner(wishlists.get(0).getOwner());
            newWishlist.setUniqueId(UUID.randomUUID().toString());

            // We merge all products in one list
            final Set<ProductModel> products = new HashSet<>();
            for (final Wishlist2Model wishlist2 : wishlists) {
                for (final Wishlist2EntryModel entry : wishlist2.getEntries()) {
                    if (entry.getProduct() != null) {
                        products.add(entry.getProduct());
                    }
                }
            }
            final List<Wishlist2EntryModel> newEntries = new ArrayList<>();

            // Add all products to the new wishlist
            for (final ProductModel product : products) {
                final Wishlist2EntryModel newEntry = getModelService().create(Wishlist2EntryModel.class);
                newEntry.setProduct(product);
                newEntry.setAddedDate(new Date());
                newEntry.setPriority(Wishlist2EntryPriority.LOW);
                newEntry.setWishlist(newWishlist);
                newEntries.add(newEntry);
            }
            newWishlist.setEntries(newEntries);
            getModelService().removeAll(wishlists); // it removed also the wishlist entries
            getModelService().save(newWishlist);
            getModelService().saveAll(newWishlist.getEntries());

            return newWishlist;
            // end cleanup code
            // /////////////////////////////////////////////////////////////////////////////////////////

        } catch (final ModelNotFoundException e) {
            return null;
        }
    }

    @Override
    public TransientMiniWishlistModel findDefaultMiniWishlistByType(final UserModel user, final NamicsWishlistType type) {

        List<TransientMiniWishlistModel> wishlists = findMiniWishlistsByType(user, type, true);
        if (wishlists == null) {
            return null;
        }

        return wishlists.get(0);
    }

    @Override
    public List<Wishlist2Model> findWishlistsByType(final UserModel user, final NamicsWishlistType type) {
        final Map<String, Object> params = new HashMap<>();
        params.put(Wishlist2Model.USER, user);
        params.put(Wishlist2Model.LISTTYPE, type);

        final StringBuilder builder = new StringBuilder();
        builder.append("SELECT {").append(Wishlist2Model.PK).append("} ");
        builder.append("FROM {").append(Wishlist2Model._TYPECODE).append("} ");
        builder.append("WHERE {").append(Wishlist2Model.USER).append("}=?").append(Wishlist2Model.USER + " ");
        builder.append("AND {").append(Wishlist2Model.LISTTYPE).append("}=?").append(Wishlist2Model.LISTTYPE + " ");
        builder.append("ORDER BY {").append(Wishlist2Model._TYPECODE).append(".").append(Wishlist2Model.NAME).append("}");

        final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
        query.addQueryParameters(params);

        final List<Wishlist2Model> wishlists = getFlexibleSearchService().<Wishlist2Model> search(query).getResult();

        if (CollectionUtils.isEmpty(wishlists)) {
            return null;
        }

        return wishlists;
    }

    @Override
    public List<TransientMiniWishlistModel> findMiniWishlistsByType(final UserModel user, final NamicsWishlistType type) {
        return findMiniWishlistsByType(user, type, false);
    }

    private List<TransientMiniWishlistModel> findMiniWishlistsByType(final UserModel user, final NamicsWishlistType type, boolean isDefault) {

        // set parameters
        FlexibleSearchQuery query = new FlexibleSearchQuery(createQueryForFindMiniWishlistsByType(false));
        query.addQueryParameter(Wishlist2Model.USER, user);
        query.addQueryParameter(Wishlist2Model.LISTTYPE, type);
        if (isDefault) {
            query.addQueryParameter(Wishlist2Model.DEFAULT, Boolean.TRUE);
        }

        // define class list
        final List<Class<?>> resultClassList = new ArrayList<>();
        resultClassList.add(String.class);
        resultClassList.add(String.class);
        resultClassList.add(Integer.class);
        query.setResultClassList(resultClassList);

        // execute search
        final SearchResult<List<Object>> searchResult = getFlexibleSearchService().search(query);

        if (searchResult.getCount() == 0) {
            return null;
        }

        return convertSearchResult(searchResult);
    }

    private String createQueryForFindMiniWishlistsByType(boolean isDefault) {
        final StringBuilder builder = new StringBuilder();
        builder.append("SELECT {l.").append(Wishlist2Model.UNIQUEID).append("}, ");
        builder.append("{l.").append(Wishlist2Model.NAME).append("}, ");
        builder.append("(CASE WHEN {e.").append(Wishlist2EntryModel.WISHLIST).append("} IS NULL THEN 0 ELSE count(*) END)");

        builder.append("FROM {").append(Wishlist2Model._TYPECODE).append(" as l ");
        builder.append("LEFT JOIN ").append(Wishlist2EntryModel._TYPECODE).append(" as e ");
        builder.append("ON {l.").append(Wishlist2Model.PK).append("}= {e.").append(Wishlist2EntryModel.WISHLIST).append("}} ");

        builder.append("WHERE {").append(Wishlist2Model.USER).append("}=?").append(Wishlist2Model.USER + " ");
        builder.append("AND {").append(Wishlist2Model.LISTTYPE).append("}=?").append(Wishlist2Model.LISTTYPE + " ");

        if (isDefault) {
            // return only the lists marked as default
            builder.append("AND {l.").append(Wishlist2Model.DEFAULT).append("}=?").append(Wishlist2Model.DEFAULT);
        }
        //
        builder.append("GROUP BY {l.").append(Wishlist2Model.UNIQUEID).append("}, ");
        builder.append("{e.").append(Wishlist2EntryModel.WISHLIST).append("}, ");
        builder.append("{l.").append(Wishlist2Model.NAME).append("} ");

        if (!isDefault) {
            // sort the result by name, if all lists should be returned
            builder.append("ORDER BY {l.").append(Wishlist2Model.NAME).append("}");
        }

        return builder.toString();
    }

    private List<TransientMiniWishlistModel> convertSearchResult(SearchResult<List<Object>> result) {

        List<TransientMiniWishlistModel> miniWishlists = new ArrayList<>();
        for (final List<Object> row : result.getResult()) {

            TransientMiniWishlistModel miniWishlist = new TransientMiniWishlistModel();

            miniWishlist.setUniqueId((String) row.get(0));
            miniWishlist.setName((String) row.get(1));
            miniWishlist.setTotalUnitCount((Integer) row.get(2));

            miniWishlists.add(miniWishlist);
        }

        return miniWishlists;
    }

    @Override
    public boolean isProductInWishlist(final UserModel user, final String productCode, final NamicsWishlistType type) {
        final Map<String, Object> params = new HashMap<>();
        params.put(Wishlist2Model.USER, user);
        params.put(ProductModel.CODE, productCode);
        params.put(Wishlist2Model.LISTTYPE, type);

        // TODO Query can be optimized if only a int value will be retrieved, not a model
        final StringBuilder builder = new StringBuilder();
        builder.append("SELECT {w.").append(Wishlist2Model.PK).append("} ");
        builder.append("FROM {").append(Wishlist2Model._TYPECODE).append(" AS w ");
        builder.append("JOIN ").append(Wishlist2EntryModel._TYPECODE).append(" AS we ON {we.").append(Wishlist2EntryModel.WISHLIST).append("}={w.")
               .append(Wishlist2Model.PK).append("} ");
        builder.append("JOIN ").append(ProductModel._TYPECODE).append(" AS p ON {p.").append(ProductModel.PK).append("}={we.product}} ");
        builder.append("WHERE {w.").append(Wishlist2Model.USER).append("}=?").append(Wishlist2Model.USER + " AND ");
        builder.append("{p.").append(ProductModel.CODE).append("}=?").append(ProductModel.CODE).append(" AND ");
        builder.append("{w.").append(Wishlist2Model.LISTTYPE).append("}=?").append(Wishlist2Model.LISTTYPE);

        final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
        query.addQueryParameters(params);

        final List<Wishlist2Model> wishlists = getFlexibleSearchService().<Wishlist2Model> search(query).getResult();

        if (CollectionUtils.isEmpty(wishlists)) {
            return false;
        }

        return true;
    }

    @Override
    public Map<String, List<String>> productsInWishlists(final UserModel user, final List<String> productCodes) {
        return productsInWishlists(user, productCodes, null);
    }

    @Override
    public Map<String, List<String>> productsInWishlists(final UserModel user, final List<String> productCodes, final NamicsWishlistType... types) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(ArrayUtils.isNotEmpty(types) ? CHECK_TYPED_TOGGLES_QUERY : CHECK_TOGGLES_QUERY);
        searchQuery.addQueryParameter(Wishlist2Model.USER, user);
        searchQuery.addQueryParameter("productCodes", productCodes);
        if (ArrayUtils.isNotEmpty(types)) {
            searchQuery.addQueryParameter(Wishlist2Model.LISTTYPE, Arrays.asList(types));
        }

        searchQuery.setResultClassList(Arrays.asList(String.class, String.class));
        final SearchResult<List<String>> searchResult = getFlexibleSearchService().<List<String>> search(searchQuery);
        // The map which will contain the result
        final Map<String, List<String>> resultMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(searchResult.getResult())) {
            for (final List<String> row : searchResult.getResult()) {
                final String pCode = String.valueOf(row.get(0));
                final String type = row.get(1);
                if (!resultMap.containsKey(pCode)) {
                    resultMap.put(pCode, new ArrayList<>());
                }
                resultMap.get(pCode).add(type);
            }
        }

        return resultMap;
    }

    @Override
    public int countDefaultWishlistEntries(final UserModel user, final NamicsWishlistType type) {
        final FlexibleSearchQuery searchQuery = createCountQuery(COUNT_ENTRIES_QUERY, user);
        searchQuery.addQueryParameter(Wishlist2Model.LISTTYPE, type);
        searchQuery.addQueryParameter(Wishlist2Model.DEFAULT, Boolean.TRUE);
        return getFlexibleSearchService().<Long> search(searchQuery).getResult().get(0).intValue();
    }

    @Override
    public int countWishlistEntries(final UserModel user, final String uuid) {
        final FlexibleSearchQuery searchQuery = createCountQuery(COUNT_WISHLIST_ENTRIES_QUERY, user);
        searchQuery.addQueryParameter(Wishlist2Model.UNIQUEID, uuid);
        return getFlexibleSearchService().<Long> search(searchQuery).getResult().get(0).intValue();
    }

    @Override
    public int countLists(final UserModel user, final NamicsWishlistType type) {
        final FlexibleSearchQuery searchQuery = createCountQuery(COUNT_WISHLIST_FOR_USER_BY_TYPE, user);
        searchQuery.addQueryParameter(Wishlist2Model.LISTTYPE, type);
        return getFlexibleSearchService().<Long> search(searchQuery).getResult().get(0).intValue();
    }

    private FlexibleSearchQuery createCountQuery(final String queryString, final UserModel user) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(queryString);
        searchQuery.addQueryParameter(Wishlist2Model.USER, user);
        searchQuery.setResultClassList(R_CLASS_LIST);
        return searchQuery;
    }
}
