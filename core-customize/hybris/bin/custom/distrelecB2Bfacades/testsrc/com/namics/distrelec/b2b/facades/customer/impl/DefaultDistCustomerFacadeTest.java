package com.namics.distrelec.b2b.facades.customer.impl;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Test;

import com.distrelec.webservice.sap.v1.ContactStatus;
import com.distrelec.webservice.sap.v1.CurrencyCode;
import com.distrelec.webservice.sap.v1.CustomerStatus;
import com.distrelec.webservice.sap.v1.SIHybrisV1Out;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.enums.DistErpSystem;
import com.namics.distrelec.b2b.core.inout.erp.mock.MockSIHybrisIF11V1Out;
import com.namics.distrelec.b2b.core.mock.sap.AbstractSapWebServiceTest;
import com.namics.distrelec.b2b.core.mock.sap.SIHybrisV1OutMockBuilder;
import com.namics.distrelec.b2b.core.model.DistBrandModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.service.customer.DistCustomerAccountService;
import com.namics.distrelec.b2b.core.service.site.DistrelecCMSSiteService;
import com.namics.distrelec.b2b.facades.customer.exceptions.ExistingCustomerRegistrationException;
import com.namics.distrelec.b2b.facades.user.data.DistExistingCustomerRegisterData;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.europe1.enums.UserTaxGroup;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;

@IntegrationTest
public class DefaultDistCustomerFacadeTest extends AbstractSapWebServiceTest {

    private static final String PRODUCT_CODE = "301122432";

    private static final String CUSTOMER_ID = "erpCustomerId2";

    private static final String FIRST_NAME = "Fred";

    private static final String LAST_NAME = "Bloggs";

    private static final String VAT_ID = "DE999999999";

    private static final String ORGANIZATIONAL_NUMBER = "12345678";

    private static final String EMAIL = "b2bcustomer@test.com";

    private static final String GERMANY_SALES_ORG_CODE = "7350";

    private static final String TITLE_CODE = "mr";

    private static final String LOGIN = "login";

    private DistExistingCustomerRegisterData registerData;

    @Resource
    private DistrelecCMSSiteService cmsSiteService;

    @Resource
    private CommonI18NService commonI18NService;

    @Resource
    private DistCustomerAccountService customerAccountService;

    @Resource
    private ModelService modelService;

    @Resource
    private SessionService sessionService;

    @Resource
    private CartService cartService;

    @Resource
    private DefaultDistCustomerFacade defaultDistCustomerFacade;

    @Test
    public void testOnlyASingleCustomerHasBeenUpdated() throws DuplicateUidException, ExistingCustomerRegistrationException, ImpExException {
        // given
        importCsv("/distrelecB2Bfacades/test/testExistingCustomerRegistration.impex", "utf-8");
        SIHybrisV1Out siHybrisV1OutMock = new SIHybrisV1OutMockBuilder()
                                                                        .expectIf08FindContact(CUSTOMER_ID, CustomerStatus.CUSTOMER_FOUND,
                                                                                               ContactStatus.CONTACT_FOUND, "12345678")
                                                                        .expectIf08UpdateContact(CUSTOMER_ID, true)
                                                                        .expectIf08ReadCustomer(CUSTOMER_ID, GERMANY_SALES_ORG_CODE,
                                                                                                CustomerType.GUEST.getCode(), CurrencyCode.CHF, "20", true)
                                                                        .build();
        setWebServiceClient(siHybrisV1OutMock);

        registerData = createDistExistingCustomerRegisterData();
        cmsSiteService.setCurrentSite(getCurrentSite());
        boolean updateCurrentCustomerOnly = true;

        // when
        defaultDistCustomerFacade.registerExistingCustomer(registerData, updateCurrentCustomerOnly, CustomerType.B2B);

        // then
        List<B2BCustomerModel> actuals = customerAccountService.getCustomersByEmail(EMAIL);
        B2BCustomerModel actual = actuals.get(0);
        assertNotNull(actual);
        assertEquals(FIRST_NAME + ' ' + LAST_NAME, actual.getName());
        assertEquals(EMAIL, actual.getEmail());
        assertEquals(TITLE_CODE, actual.getTitle().getCode());
    }

    @Test
    public void testUpdateCartWithGuestForAnonymousCheckoutUpdateSessionCartCurrencyAndLanguage() {
        // given
        setWebServiceClientIF11(new MockSIHybrisIF11V1Out());
        UserGroupModel userGroup = getUserGroupModel();
        B2BCustomerModel user = getUserModel(userGroup);
        prepareData(user);
        CustomerData guestCustomerData = getCustomerData();

        // when
        defaultDistCustomerFacade.updateCartWithGuestForAnonymousCheckout(guestCustomerData);

        // then
        assertNull(cartService.getSessionCart().getDeliveryAddress());
        assertNull(cartService.getSessionCart().getDeliveryMode());
        assertNull(cartService.getSessionCart().getPaymentInfo());
        assertEquals(user, cartService.getSessionCart().getUser());
        assertEquals(guestCustomerData.getCurrency().getIsocode(),
                     cartService.getSessionCart().getCurrency().getIsocode());
        assertEquals(guestCustomerData.getLanguage().getIsocode(),
                     cartService.getSessionCart().getLanguage().getIsocode());
    }

    private CustomerData getCustomerData() {
        CustomerData guestCustomerData = new CustomerData();
        guestCustomerData.setCustomerId("guest_customer_id");
        guestCustomerData.setEmail("guest@distrelec.com");
        guestCustomerData.setUid("guest_user");
        guestCustomerData.setCustomerType(CustomerType.GUEST);
        CurrencyData currency = new CurrencyData();
        currency.setIsocode("EUR");
        guestCustomerData.setCurrency(currency);
        LanguageData language = new LanguageData();
        language.setIsocode("en");
        guestCustomerData.setLanguage(language);
        return guestCustomerData;
    }

    private void prepareData(B2BCustomerModel user) {
        CurrencyModel currency = getCurrencyModel();
        CatalogModel catalog = getCatalogModel();
        CatalogVersionModel catalogVersion = getCatalogVersionModel(catalog);
        UnitModel unitModel = getUnitModel();

        CMSSiteModel cmsSiteModel = modelService.create(CMSSiteModel.class);
        cmsSiteModel.setDefaultCurrency(currency);
        LanguageModel deLanguage = modelService.create(LanguageModel.class);
        deLanguage.setIsocode("de");
        cmsSiteModel.setDefaultLanguage(deLanguage);
        cmsSiteModel.setUid("uid");

        UserPriceGroup userPriceGroup = UserPriceGroup.valueOf("testUserPriceGroup");
        cmsSiteModel.setUserPriceGroup(userPriceGroup);
        UserTaxGroup userTaxGroup = UserTaxGroup.valueOf("testUserTaxGroup");
        cmsSiteModel.setUserTaxGroup(userTaxGroup);

        LanguageModel frLanguage = modelService.create(LanguageModel.class);
        frLanguage.setIsocode("fr");
        modelService.save(frLanguage);

        DistSalesOrgModel salesOrg = modelService.create(DistSalesOrgModel.class);
        salesOrg.setCode("7310");
        DistBrandModel distBrand = new DistBrandModel();
        distBrand.setCode("brand_model");
        salesOrg.setBrand(distBrand);
        CountryModel country = new CountryModel();
        country.setIsocode("CH");
        salesOrg.setCountry(country);
        salesOrg.setErpSystem(DistErpSystem.SAP);
        modelService.save(salesOrg);
        cmsSiteModel.setSalesOrg(salesOrg);
        cmsSiteModel.setCountry(country);
        modelService.save(cmsSiteModel);

        CartModel cartModel = modelService.create(CartModel.class);
        cartModel.setDate(new Date());
        cartModel.setUser(user);
        cartModel.setCurrency(currency);

        ProductModel productModel = getProductModel(catalogVersion, PRODUCT_CODE);

        final CartEntryModel entry = getCartEntryModel(unitModel, cartModel, productModel, 0, 5L);
        cartModel.setEntries(List.of(entry));
        cartModel.setSite(cmsSiteModel);
        modelService.save(cartModel);
        cartService.setSessionCart(cartModel);
        sessionService.setAttribute("currentSite", cmsSiteModel);
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

    private UserGroupModel getUserGroupModel() {
        UserGroupModel b2cUserGroup = modelService.create(UserGroupModel.class);
        b2cUserGroup.setUid(DistConstants.User.B2CCUSTOMERGROUP_UID);
        modelService.save(b2cUserGroup);

        UserGroupModel ociUserGroup = modelService.create(UserGroupModel.class);
        ociUserGroup.setUid(DistConstants.User.OCICUSTOMERGROUP_UID);
        modelService.save(ociUserGroup);

        UserGroupModel b2bUserGroup = modelService.create(UserGroupModel.class);
        b2bUserGroup.setUid(DistConstants.User.B2BGROUP_UID);
        modelService.save(b2bUserGroup);

        UserGroupModel b2beeshopgroup = modelService.create(UserGroupModel.class);
        b2beeshopgroup.setUid(DistConstants.User.B2BEESHOPGROUP_UID);
        modelService.save(b2beeshopgroup);

        return b2cUserGroup;
    }

    private B2BCustomerModel getUserModel(UserGroupModel userGroup) {
        B2BCustomerModel user = modelService.create(B2BCustomerModel.class);
        user.setUid("guest_user");
        user.setEmail("guest.user@distrelec.com");
        user.setGroups(Set.of(userGroup));
        B2BUnitModel unitModel = modelService.create(B2BUnitModel.class);
        unitModel.setCustomerType(CustomerType.GUEST);
        unitModel.setUid("unit_uid");
        modelService.save(unitModel);
        user.setDefaultB2BUnit(unitModel);
        modelService.save(user);
        return user;
    }

    private CurrencyModel getCurrencyModel() {
        CurrencyModel currencyChf = modelService.create(CurrencyModel.class);
        currencyChf.setIsocode("CHF");
        modelService.save(currencyChf);

        CurrencyModel currencyEur = modelService.create(CurrencyModel.class);
        currencyEur.setIsocode("EUR");
        modelService.save(currencyEur);
        return currencyChf;
    }

    private CMSSiteModel getCurrentSite() {
        return cmsSiteService.getSiteForCountry(commonI18NService.getCountry("DE"));
    }

    private DistExistingCustomerRegisterData createDistExistingCustomerRegisterData() {
        DistExistingCustomerRegisterData registerData = new DistExistingCustomerRegisterData();
        registerData.setCustomerId(CUSTOMER_ID);
        registerData.setFirstName(FIRST_NAME);
        registerData.setLastName(LAST_NAME);
        registerData.setEmail(EMAIL);
        registerData.setOrganizationalNumber(ORGANIZATIONAL_NUMBER);
        registerData.setVatId(VAT_ID);
        registerData.setLogin(LOGIN);
        registerData.setTitleCode(TITLE_CODE);
        registerData.setContactId(CUSTOMER_ID);
        registerData.setCustomerType(CustomerType.GUEST);
        return registerData;
    }
}
