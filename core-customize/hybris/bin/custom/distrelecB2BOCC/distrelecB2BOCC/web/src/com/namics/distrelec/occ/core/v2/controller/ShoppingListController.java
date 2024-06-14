package com.namics.distrelec.occ.core.v2.controller;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.service.order.exceptions.ProductNotBuyableException;
import com.namics.distrelec.b2b.facades.wishlist.DistCompareListFacade;
import com.namics.distrelec.b2b.facades.wishlist.DistShoppingListFacade;
import com.namics.distrelec.b2b.facades.wishlist.data.NamicsWishlistData;
import com.namics.distrelec.b2b.facades.wishlist.data.NamicsWishlistEntryData;
import com.namics.distrelec.occ.cms.ws.dto.ShoppingListEntryWsDTO;
import com.namics.distrelec.occ.cms.ws.dto.ShoppingListWsDTO;
import com.namics.distrelec.occ.cms.ws.dto.ShoppingListsWsDTO;
import com.namics.distrelec.occ.cms.ws.dto.UpdateShoppingListWsDTO;
import com.namics.distrelec.occ.core.v2.helper.ProductsHelper;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductWsDTO;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import de.hybris.platform.wishlist2.model.Wishlist2Model;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@Tag(name = "ShoppingLists")
@RequestMapping(value = "/{baseSiteId}/users/{userId}/shoppingList")
public class ShoppingListController extends BaseController {
    private static final Logger LOG = LogManager.getLogger(ShoppingListController.class);

    private static final String LIST_PATH_VARIABLE_PATTERN = "/{listId:.*}";

    private static final String CALCULATE_LIST_PATH_VARIABLE_PATTERN = LIST_PATH_VARIABLE_PATTERN + "/calculate";

    private static final String ADD_LIST_PATH = "/add";

    private static final String COMPARE_LIST_PATH = "/compare";

    private static final String CREATE_LIST_PATH = "/create";

    private static final String GET_ALL_WISHLISTS_PATTERN = "/all";

    private static final String UPDATE_LIST_PATH_VARIABLE_PATTERN = LIST_PATH_VARIABLE_PATTERN + "/update";

    private static final String UPDATE_PRODUCT_COUNT = "/update/entry";

    private static final String DELETE_LIST_PATH_VARIABLE_PATTERN = LIST_PATH_VARIABLE_PATTERN + "/delete";

    private static final String REMOVE_PATH = "/remove";

    private static final String LIST_COUNT_PATH = "/listCount";

    private static final String COMPARE_LIST_COUNT = COMPARE_LIST_PATH + LIST_COUNT_PATH;

    private static final String LIST_PATH_VARIABLE = "listId";

    private static final String LIST_IDS_VARIABLE = "listIds[]";

    private static final String PRODUCT_CODES = "productCodes[]";

    private static final int METAHD_LISTSIZE_DEFAULT_VALUE = 5;

    @Autowired
    private DistShoppingListFacade shoppingListFacade;

    @Autowired
    private DistCompareListFacade distCompareListFacade;

    @Resource(name = "productsHelper")
    private ProductsHelper productsHelper;

    @Secured({ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP" })
    @RequestMapping(value = LIST_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Operation(operationId = "getShoppingListById", summary = "Returns shopping list", description = "Shopping List ID is required")
    @ApiBaseSiteIdAndUserIdParam
    public ShoppingListWsDTO getShoppingListById(@Parameter(description = "listId", required = true) @PathVariable final String listId,
                                                 @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        List<NamicsWishlistEntryData> removedProducts;
        NamicsWishlistData wishlist;
        if (StringUtils.isNotEmpty(listId)) {
            wishlist = getShoppingListFacade().getWishlist(listId);
            wishlist.getEntries().sort(Comparator.comparing(NamicsWishlistEntryData::getAddedDate));
            removedProducts = productsHelper.processPunchOutProductsInWishlist(wishlist);
        } else {
            throw new ModelNotFoundException("Shopping list is not found");
        }
        ShoppingListWsDTO shoppingListWsDTO = getDataMapper().map(wishlist, ShoppingListWsDTO.class, fields);
        productsHelper.populatePunchOutProductCodes(removedProducts, shoppingListWsDTO);
        return shoppingListWsDTO;
    }

    @Secured({ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP" })
    @RequestMapping(value = GET_ALL_WISHLISTS_PATTERN, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Operation(operationId = "getShoppingLists", summary = "Returns shopping lists", description = "Return all shopping lists")
    @ApiBaseSiteIdAndUserIdParam
    public ShoppingListsWsDTO getShoppingLists(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final List<NamicsWishlistData> wishlists = getShoppingListFacade().getWishlists();

        List<ShoppingListWsDTO> list = wishlists.stream()
                                                .map(wishlistData -> getDataMapper().map(wishlistData, ShoppingListWsDTO.class, fields))
                                                .collect(Collectors.toList());

        ShoppingListsWsDTO shoppingListsWsDTO = new ShoppingListsWsDTO();
        shoppingListsWsDTO.setList(list);
        return shoppingListsWsDTO;
    }

    @Secured({ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP" })
    @RequestMapping(value = CREATE_LIST_PATH, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @Operation(operationId = "createShoppinglist", summary = "Creates a shopping list for a user.", description = "Creates a new shopping lists to user.")
    @ApiBaseSiteIdAndUserIdParam
    public ShoppingListWsDTO createShoppinglist(@Parameter(description = "The name and id of the wishlist to create/update a shopping list model.") @RequestParam final String name,
                                                @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final NamicsWishlistData newList = getShoppingListFacade().createEmptyList(name);

        return getDataMapper().map(newList, ShoppingListWsDTO.class, fields);
    }

    @Secured({ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP" })
    @RequestMapping(value = "/new" + ADD_LIST_PATH, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @Operation(operationId = "addToNewShoppinglist", summary = "Add products to a new shopping list for a user.", description = "Add products to a new shopping list for a user.")
    @ApiBaseSiteIdAndUserIdParam
    public ShoppingListWsDTO addToNewShoppingList(@Parameter(description = "New Shopping List Name") @RequestParam final String name,
                                                  @RequestBody final ShoppingListWsDTO products,
                                                  @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        NamicsWishlistData wishlist = getShoppingListFacade().createEmptyList(name);
        wishlist = addProductsToLists(products, Collections.singleton(wishlist.getUniqueId()));
        LOG.debug("Product added to the list: " + wishlist.getName());
        return getDataMapper().map(wishlist, ShoppingListWsDTO.class, fields);
    }

    @Secured({ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP" })
    @RequestMapping(value = ADD_LIST_PATH, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @Operation(operationId = "addToShoppinglist", summary = "Add products to a existing shopping list for a user.", description = "Add products to an existing shopping list for a user.")
    @ApiBaseSiteIdAndUserIdParam
    public ShoppingListWsDTO addToShoppingList(@RequestBody final ShoppingListWsDTO products,
                                               @Parameter(description = "products to a existing shopping list for a user") @RequestParam final String[] existingListUid,
                                               @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        NamicsWishlistData wishlist = addProductsToLists(products, List.of(existingListUid));
        LOG.debug("Product added to the existing list: " + wishlist.getName());
        return getDataMapper().map(wishlist, ShoppingListWsDTO.class, fields);
    }

    @RequestMapping(value = COMPARE_LIST_PATH, method = RequestMethod.GET)
    @ResponseBody
    @Operation(operationId = "getCompareList", summary = "get compare list", description = "returns the list of compared products")
    @ApiBaseSiteIdAndUserIdParam
    public ProductListWsDTO getCompareList(@Parameter(description = "product code on cookie value") @RequestParam final String compareList,
                                           @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final int maxListSize = getConfigurationService().getConfiguration().getInt(DistConstants.PropKey.Shop.COMPARE_METAHD_LIST_SIZE,
                METAHD_LISTSIZE_DEFAULT_VALUE);
        distCompareListFacade.addCookieProductsToCompareList(compareList);
        List<ProductData> list = distCompareListFacade.getCompareList(compareList, maxListSize);
        return getCompareListResponse(list, fields);
    }

    @Secured({ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP" })
    @RequestMapping(value = COMPARE_LIST_PATH, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @Operation(operationId = "compareProducts", summary = "Add products to a new compare list for a user.", description = "Add products to a new compare list for a user.")
    @ApiBaseSiteIdAndUserIdParam
    public ProductListWsDTO addTocompareProductsList(@RequestBody final ShoppingListWsDTO products,
                                                      @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        NamicsWishlistData wishlist = addProductsToCompareLists(products);
        LOG.debug("Product added to the compare list: " + wishlist.getName());
        final int maxListSize = getConfigurationService().getConfiguration().getInt(DistConstants.PropKey.Shop.COMPARE_METAHD_LIST_SIZE,
                METAHD_LISTSIZE_DEFAULT_VALUE);
        List<ProductData> list = distCompareListFacade.getProducts(wishlist, maxListSize);
        return getCompareListResponse(list, fields);
    }

    @Secured({ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP" })
    @RequestMapping(value = CALCULATE_LIST_PATH_VARIABLE_PATTERN, method = RequestMethod.PATCH)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Operation(operationId = "calculateShoppingList", summary = "Calculate an existing shopping list for a user.", description = "Calculate an existing shopping list for a user.")
    @ApiBaseSiteIdAndUserIdParam
    public ResponseEntity calculateShoppingList(@Parameter(description = "Shopping list ID", required = true, name = "listId") @PathVariable final String listId,
                                                @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        NamicsWishlistData wishlist = new NamicsWishlistData();
        if (StringUtils.isNotEmpty(listId)) {
            try {
                wishlist = getShoppingListFacade().calculateShoppingList(listId);
            } catch (final Exception exp) {
                LOG.error("Exception occured in calculating shopping list with ID {} \n{}", listId, exp.getMessage(), exp);
                return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return ResponseEntity.ok(getDataMapper().map(wishlist, ShoppingListWsDTO.class, fields));
    }

    @Secured({ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP" })
    @RequestMapping(value = UPDATE_LIST_PATH_VARIABLE_PATTERN, method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @Operation(operationId = "updateShoppingList", summary = "Update wishlist by shopping name", description = "Update wishlist by shopping name, required param = listId")
    @ApiBaseSiteIdAndUserIdParam
    public UpdateShoppingListWsDTO updateShoppingList(@Parameter(description = "new name of shoppingList") @RequestParam final String name,
                                                      @Parameter(description = "id of shopping list") @PathVariable final String listId) {
        getShoppingListFacade().updateWishlistNameAndDescription(listId, name, StringUtils.EMPTY);
        return getUpdateShoppingListWsDTO();
    }

    @Secured({ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP" })
    @RequestMapping(value = UPDATE_PRODUCT_COUNT, method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @Operation(operationId = "updateProductCount", summary = "Update the count of product in shopping list")
    @ApiBaseSiteIdAndUserIdParam
    public UpdateShoppingListWsDTO updateProductCount(@Parameter(description = "productCode", required = true) @RequestParam final String productCode,
                                                      @Parameter(description = "Shopping list Id", required = true) @RequestParam final String listId,
                                                      @Parameter(description = "Desired quantity to add in shopping list", required = true) @RequestParam final Integer desired) {
        getShoppingListFacade().updateProductCount(listId, normalizeProductCode(productCode), desired);
        return getUpdateShoppingListWsDTO();
    }

    @Secured({ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP" })
    @RequestMapping(value = REMOVE_PATH, method = RequestMethod.POST)
    @ResponseBody
    @Operation(operationId = "removeFromShoppingList", summary = "Remove product from wishlist")
    @ApiBaseSiteIdAndUserIdParam
    public UpdateShoppingListWsDTO removeFromShoppingList(
                                                          @Parameter(description = "Wishlist IDs") @RequestParam(value = LIST_IDS_VARIABLE) final String[] listIds,
                                                          @Parameter(description = "productCodes") @RequestParam(value = PRODUCT_CODES) final String[] productCodes) {
        for (final String list : listIds) {
            for (final String productCode : productCodes) {
                getShoppingListFacade().removeFromWishlist(list, normalizeProductCode(productCode));
            }
        }
        return getUpdateShoppingListWsDTO();
    }

    @Secured({ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP" })
    @RequestMapping(value = DELETE_LIST_PATH_VARIABLE_PATTERN, method = RequestMethod.DELETE)
    @ResponseBody
    @Operation(operationId = "deleteShoppingList", summary = "remove shopping list")
    @ApiBaseSiteIdAndUserIdParam
    public UpdateShoppingListWsDTO deleteShoppingList(@PathVariable(value = LIST_PATH_VARIABLE) final String listId) {
        getShoppingListFacade().deleteList(listId);
        return getUpdateShoppingListWsDTO();
    }

    @Secured({ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP" })
    @RequestMapping(value = LIST_COUNT_PATH, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Operation(operationId = "getShoppingListsCount", summary = "Returns number of shopping list for a user", description = "Returns number of shopping list for a user")
    @ApiBaseSiteIdAndUserIdParam
    public ShoppingListsWsDTO getListsCount() {
        ShoppingListsWsDTO shoppingListsWsDTO = new ShoppingListsWsDTO();
        if (!ObjectUtils.isEmpty(getShoppingListFacade().getListsCount())) {
            shoppingListsWsDTO.setCount(getShoppingListFacade().getListsCount());
        } else {
            shoppingListsWsDTO.setCount(0);
        }
        return shoppingListsWsDTO;
    }

    @Secured({ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP" })
    @RequestMapping(value = COMPARE_LIST_COUNT, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Operation(operationId = "getShoppingListsCount", summary = "Returns number of compare list entries for a user", description = "Returns number of compare list entries for a user")
    @ApiBaseSiteIdAndUserIdParam
    public ShoppingListWsDTO getCompareProducts() {
        Wishlist2Model compareShoppingList = getDistCompareListFacade().getWishlistModel();
        ShoppingListWsDTO shoppingListWsDTO = new ShoppingListWsDTO();
        if (compareShoppingList != null) {
            shoppingListWsDTO.setUniqueId(compareShoppingList.getUniqueId());
            shoppingListWsDTO.setTotalUnitCount(CollectionUtils.isNotEmpty(compareShoppingList.getEntries()) ? compareShoppingList.getEntries().size() : 0);
            shoppingListWsDTO.setListType(compareShoppingList.getListType().getCode());
        }
        return shoppingListWsDTO;
    }

    private UpdateShoppingListWsDTO getUpdateShoppingListWsDTO() {
        UpdateShoppingListWsDTO updateShoppingListWsDTO = new UpdateShoppingListWsDTO();
        updateShoppingListWsDTO.setStatus(true);
        return updateShoppingListWsDTO;
    }

    protected NamicsWishlistData addProductsToLists(final ShoppingListWsDTO listWsDTO, final Collection<String> lists) {
        NamicsWishlistData wishlist = null;
        if (CollectionUtils.isNotEmpty(listWsDTO.getEntries())) {
            for (ShoppingListEntryWsDTO entry : listWsDTO.getEntries()) {
                final String productCode = normalizeProductCode(entry.getProduct().getCode());
                final Integer productQuantity = entry.getDesired() != null ? entry.getDesired() : 1;
                final String comment = entry.getComment();
                for (final String listId : lists) {
                    if (null != productCode && null != productQuantity) {
                        wishlist = getShoppingListFacade().addToWishlistWithCustomerReference(listId, productCode, comment);
                        getShoppingListFacade().updateProductCount(listId, productCode, productQuantity);
                    }
                }
            }
        }

        return wishlist;
    }

    protected NamicsWishlistData addProductsToCompareLists(final ShoppingListWsDTO listWsDTO) {
        NamicsWishlistData wishlist = null;
        if (CollectionUtils.isNotEmpty(listWsDTO.getEntries())) {
            for (ShoppingListEntryWsDTO entry : listWsDTO.getEntries()) {
                final String productCode = normalizeProductCode(entry.getProduct().getCode());
                if (null != productCode) {
                    wishlist = getDistCompareListFacade().addToWishlist(normalizeProductCode(productCode));
                }
            }
        }

        return wishlist;
    }

    protected ProductListWsDTO getCompareListResponse(List<ProductData> list, final String fields) {
        productsHelper.populateAttributes(list);
        List<ProductWsDTO> compareProductsList = productsHelper.getProductListForWsDTOCompare(list, fields);
        ProductListWsDTO response = new ProductListWsDTO();
        response.setProducts(compareProductsList);
        return response;
    }

    public DistCompareListFacade getDistCompareListFacade() {
        return distCompareListFacade;
    }

    public DistShoppingListFacade getShoppingListFacade() {
        return shoppingListFacade;
    }

}
