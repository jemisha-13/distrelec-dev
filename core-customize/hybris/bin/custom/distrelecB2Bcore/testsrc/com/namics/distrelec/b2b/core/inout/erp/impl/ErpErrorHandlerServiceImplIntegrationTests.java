package com.namics.distrelec.b2b.core.inout.erp.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.*;

import javax.annotation.Resource;

import org.junit.Test;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.erp.ErpErrorHandlerService;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

@IntegrationTest
public class ErpErrorHandlerServiceImplIntegrationTests extends ServicelayerTransactionalTest {

    private static final String PRODUCT_CODE_1 = "112233";

    private static final String PRODUCT_CODE_2 = "556677";

    @Resource
    private ModelService modelService;

    @Resource
    private ErpErrorHandlerService erpErrorHandlerServiceImpl;

    @Resource
    private CartService cartService;

    @Test
    public void testRemoveProductFromCart() {
        prepareData();

        List<CommerceCartModification> result = erpErrorHandlerServiceImpl.updateProductQuantity(PRODUCT_CODE_1, 0);

        assertEquals(1, cartService.getSessionCart().getEntries().size());
        assertEquals(1, result.size());
        assertTrue(allProductCodesMatched(result, PRODUCT_CODE_1));
        assertEquals(0, getTotalQuantity(result));
    }

    @Test
    public void testRemoveMultipleProductsFromCartWithMultipleEntries() {
        prepareDataWithMultipleEntries();

        List<CommerceCartModification> result = erpErrorHandlerServiceImpl.updateProductQuantity(PRODUCT_CODE_2, 0);

        assertEquals(1, cartService.getSessionCart().getEntries().size());
        assertEquals(3, result.size());
        assertTrue(allProductCodesMatched(result, PRODUCT_CODE_2));
        assertEquals(0, getTotalQuantity(result));
    }

    @Test
    public void testUpdateProductQuantity() {
        prepareData();

        List<CommerceCartModification> result = erpErrorHandlerServiceImpl.updateProductQuantity(PRODUCT_CODE_1, 10);

        assertEquals(2, cartService.getSessionCart().getEntries().size());
        assertEquals(1, result.size());
        assertTrue(allProductCodesMatched(result, PRODUCT_CODE_1));
        assertEquals(10, getTotalQuantity(result));
    }

    private boolean allProductCodesMatched(List<CommerceCartModification> result, String productCode) {
        return result.stream()
                     .map(cartModification -> cartModification.getEntry())
                     .filter(Objects::nonNull)
                     .map(AbstractOrderEntryModel::getProduct)
                     .filter(Objects::nonNull)
                     .map(ProductModel::getCode)
                     .allMatch(code -> productCode.equals(code));
    }

    private long getTotalQuantity(List<CommerceCartModification> result) {
        return result.stream()
                     .mapToLong(CommerceCartModification::getQuantity)
                     .sum();
    }

    private void prepareData() {
        CurrencyModel currencyDef = getCurrencyModel();
        UserGroupModel userGroup = getUserGroupModel();
        UserModel user = getUserModel(userGroup);
        CatalogModel catalog = getCatalogModel();
        CatalogVersionModel catalogVersion = getCatalogVersionModel(catalog);
        UnitModel unitModel = getUnitModel();

        CartModel cartModel = modelService.create(CartModel.class);
        cartModel.setDate(new Date());
        cartModel.setUser(user);
        cartModel.setCurrency(currencyDef);

        ProductModel productModel1 = getProductModel(catalogVersion, PRODUCT_CODE_1);
        ProductModel productModel2 = getProductModel(catalogVersion, PRODUCT_CODE_2);

        final CartEntryModel entry1 = getCartEntryModel(unitModel, cartModel, productModel1, 0, 5L);
        final CartEntryModel entry2 = getCartEntryModel(unitModel, cartModel, productModel2, 1, 6L);
        cartModel.setEntries(List.of(entry1, entry2));
        modelService.save(cartModel);
        cartService.setSessionCart(cartModel);
    }

    private void prepareDataWithMultipleEntries() {
        CurrencyModel currencyDef = getCurrencyModel();
        UserGroupModel userGroup = getUserGroupModel();
        UserModel user = getUserModel(userGroup);
        CatalogModel catalog = getCatalogModel();
        CatalogVersionModel catalogVersion = getCatalogVersionModel(catalog);
        UnitModel unitModel = getUnitModel();

        CartModel cartModel = modelService.create(CartModel.class);
        cartModel.setDate(new Date());
        cartModel.setUser(user);
        cartModel.setCurrency(currencyDef);

        ProductModel productModel1 = getProductModel(catalogVersion, PRODUCT_CODE_1);
        ProductModel productModel2 = getProductModel(catalogVersion, PRODUCT_CODE_2);

        final CartEntryModel entry1 = getCartEntryModel(unitModel, cartModel, productModel1, 0, 5L);
        final CartEntryModel entry2 = getCartEntryModel(unitModel, cartModel, productModel2, 1, 6L);
        final CartEntryModel entry3 = getCartEntryModel(unitModel, cartModel, productModel2, 2, 7L);
        final CartEntryModel entry4 = getCartEntryModel(unitModel, cartModel, productModel2, 3, 8L);
        cartModel.setEntries(List.of(entry1, entry2, entry3, entry4));
        modelService.save(cartModel);
        cartService.setSessionCart(cartModel);
    }

    private CartEntryModel getCartEntryModel(UnitModel unitModel, CartModel cartModel, ProductModel productModel1, Integer entryNumber, Long quantity) {
        final CartEntryModel entry = modelService.create(CartEntryModel.class);
        entry.setProduct(productModel1);
        entry.setOrder(cartModel);
        entry.setBasePrice(2.2);
        entry.setTotalPrice(3.9);
        entry.setQuantity(quantity);
        entry.setUnit(unitModel);
        entry.setEntryNumber(entryNumber);
        return entry;
    }

    private ProductModel getProductModel(CatalogVersionModel catalogVersion, String code) {
        ProductModel productModel = modelService.create(ProductModel.class);
        productModel.setCode(code);
        productModel.setCatalogVersion(catalogVersion);
        modelService.save(productModel);
        return productModel;
    }

    private UnitModel getUnitModel() {
        UnitModel unitModel = modelService.create(UnitModel.class);
        unitModel.setUnitType("unit_type");
        unitModel.setCode("unit_code");
        return unitModel;
    }

    private CatalogVersionModel getCatalogVersionModel(CatalogModel catalog) {
        CatalogVersionModel catalogVersion = modelService.create(CatalogVersionModel.class);
        catalogVersion.setVersion("my_catalog_version");
        catalogVersion.setCatalog(catalog);
        modelService.save(catalogVersion);
        return catalogVersion;
    }

    private CatalogModel getCatalogModel() {
        CatalogModel catalog = modelService.create(CatalogModel.class);
        catalog.setId("my_favorite_catalog");
        modelService.save(catalog);
        return catalog;
    }

    private UserModel getUserModel(UserGroupModel userGroup) {
        UserModel user = modelService.create(UserModel.class);
        user.setUid("user");
        user.setGroups(Set.of(userGroup));
        modelService.save(user);
        return user;
    }

    private UserGroupModel getUserGroupModel() {
        UserGroupModel userGroup = modelService.create(UserGroupModel.class);
        userGroup.setUid(DistConstants.User.EPROCUREMENTGROUP_UID);
        modelService.save(userGroup);
        return userGroup;
    }

    private CurrencyModel getCurrencyModel() {
        CurrencyModel currencyDef = modelService.create(CurrencyModel.class);
        currencyDef.setIsocode("EUR");
        modelService.save(currencyDef);
        return currencyDef;
    }
}
