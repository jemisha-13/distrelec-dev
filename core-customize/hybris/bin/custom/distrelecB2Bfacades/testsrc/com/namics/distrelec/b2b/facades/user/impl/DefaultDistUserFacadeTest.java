
package com.namics.distrelec.b2b.facades.user.impl;

import com.distrelec.webservice.sap.v1.SIHybrisV1Out;
import com.distrelec.webservice.sap.v1.ShippingMethodCode;
import com.namics.distrelec.b2b.core.inout.erp.impl.SapCustomerService;
import com.namics.distrelec.b2b.core.inout.erp.impl.SapPaymentOptionService;
import com.namics.distrelec.b2b.core.inout.erp.impl.SapShippingOptionService;
import com.namics.distrelec.b2b.core.inout.erp.mock.MockSIHybrisV1Out;
import com.namics.distrelec.b2b.core.mock.sap.AbstractSapWebServiceTest;
import com.namics.distrelec.b2b.core.mock.sap.SIHybrisV1OutMockBuilder;
import com.namics.distrelec.b2b.facades.order.data.DistPaymentModeData;
import com.namics.distrelec.b2b.facades.user.DistUserFacade;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.PaymentModeService;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.i18n.daos.CurrencyDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.AddressService;
import de.hybris.platform.servicelayer.user.UserConstants;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.namics.distrelec.b2b.core.mock.sap.SIHybrisV1OutMockBuilder.readPaymentMethod;
import static com.namics.distrelec.b2b.core.mock.sap.SIHybrisV1OutMockBuilder.readShippingMethod;

@IntegrationTest
public class DefaultDistUserFacadeTest extends AbstractSapWebServiceTest {

    @Resource(mappedName = "sap.customerService")
    private SapCustomerService sapCustomerService;

    @Resource(mappedName = "sap.paymentOptionService")
    private SapPaymentOptionService sapPaymentOptionService;

    @Resource(mappedName = "sap.shippingOptionService")
    private SapShippingOptionService sapShippingOptionService;

    @Resource
    private ModelService modelService;

    @Resource
    private CartService cartService;

    @Resource
    private UserService userService;

    @Resource
    private ProductService productService;

    @Resource
    private PaymentModeService paymentModeService;

    @Resource
    private BaseSiteService baseSiteService;

    @Resource
    private SessionService sessionService;

    @Resource
    private AddressService addressService;

    @Resource
    private CurrencyDao currencyDao;

    @Resource
    private DistUserFacade userFacade;

    @Resource
    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

    private MockSIHybrisV1Out mockSIHybrisV1Out = new MockSIHybrisV1Out();

    @Before
    public void setUp() throws Exception {

        setWebServiceClient(mockSIHybrisV1Out);

        userService.setCurrentUser(userService.getAdminUser());
        importCsv("/distrelecB2Bfacades/test/testDistUserFacade.impex", "utf-8");

        final B2BCustomerModel b2bCustomer = (B2BCustomerModel) userService.getUserForUID("b2buser");
        final AddressModel address = addressService.getAddressesForOwner(b2bCustomer).iterator().next();
        final ProductModel product = productService.getProductForCode("5849020");

        final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID("distrelec_CH");
        baseSiteService.setCurrentBaseSite(baseSite, false);

        final CartModel sampleCart = modelService.create(CartModel.class);
        sampleCart.setUser(b2bCustomer);
        sampleCart.setCurrency(currencyDao.findCurrenciesByCode("CHF").iterator().next());
        sampleCart.setDate(new Date());
        sampleCart.setTotalPrice(Double.valueOf(42));
        sampleCart.setCode("0000001");
        sampleCart.setErpOrderCode("0000002");
        sampleCart.setPaymentMode(paymentModeService.getPaymentModeForCode("CreditCard"));
        sampleCart.setDeliveryAddress(address);
        sampleCart.setPaymentAddress(address);
        sampleCart.setSite(baseSite);
        modelService.save(sampleCart);

        final CartEntryModel cartEntry = modelService.create(CartEntryModel.class);
        cartEntry.setProduct(product);
        cartEntry.setQuantity(Long.valueOf(1));
        cartEntry.setUnit(product.getUnit());
        cartEntry.setBasePrice(Double.valueOf(42));
        cartEntry.setTotalPrice(Double.valueOf(42));
        cartEntry.setOrder(sampleCart);
        modelService.save(cartEntry);

        final PaymentTransactionModel paymentTransaction = modelService.create(PaymentTransactionModel.class);
        paymentTransaction.setCode(b2bCustomer.getUid() + "." + sampleCart.getCode() + "." + UUID.randomUUID().toString().replaceAll("-", ""));
        paymentTransaction.setInfo(sampleCart.getPaymentInfo());
        paymentTransaction.setCurrency(sampleCart.getCurrency());
        paymentTransaction.setOrder(sampleCart);
        modelService.save(paymentTransaction);

        cartService.setSessionCart(sampleCart);
        sessionService.setAttribute(UserConstants.USER_SESSION_ATTR_KEY, b2bCustomer);
    }

    @Test
    public void testGetSupportedDeliveryModesForUserB2C() {
        final String defaultDeliveryMode = "Emergency";
        setCurrentUser("b2cuser");

        final Collection<DeliveryModeData> deliveryModes = userFacade.getSupportedDeliveryModesForUser();
        // Only 2 delivery methods, pickup should not be visible
        Assert.assertEquals(7, deliveryModes.size());

        int nbDefautlt = 0;
        // Check the default delivery mode
        for (final DeliveryModeData deliveryMode : deliveryModes) {
            Assert.assertTrue(deliveryMode.getDefaultDeliveryMode() == (deliveryMode.getCode().compareTo(defaultDeliveryMode) == 0));
            if (deliveryMode.getDefaultDeliveryMode()) {
                nbDefautlt++;
            }
        }

        Assert.assertEquals(1, nbDefautlt);
    }

    @Test
    public void testGetSupportedDeliveryModesForUserB2B() {
        final String defaultDeliveryMode = "Emergency";
        setCurrentUser("b2buser");

        final Collection<DeliveryModeData> deliveryModes = userFacade.getSupportedDeliveryModesForUser();
        // Only 2 delivery methods, pickup should not be visible
        Assert.assertEquals(7, deliveryModes.size());

        int nbDefautlt = 0;
        // Check the default delivery mode
        for (final DeliveryModeData deliveryMode : deliveryModes) {
            Assert.assertTrue(deliveryMode.getDefaultDeliveryMode() == (deliveryMode.getCode().compareTo(defaultDeliveryMode) == 0));
            if (deliveryMode.getDefaultDeliveryMode()) {
                nbDefautlt++;
            }
        }
        Assert.assertEquals(1, nbDefautlt);
    }

    @Test
    public void testGetSupportedDeliveryModeForUserB2BWithBudget() {
        final String defaultDeliveryMode = "Emergency";
        setCurrentUser("budgetuser");

        final Collection<DeliveryModeData> deliveryModes = userFacade.getSupportedDeliveryModesForUser();
        // Only 2 delivery methods, pickup should not be visible
        Assert.assertEquals(7, deliveryModes.size());

        int nbDefautlt = 0;
        // Check the default delivery mode
        for (final DeliveryModeData deliveryMode : deliveryModes) {
            Assert.assertTrue(deliveryMode.getDefaultDeliveryMode() == (deliveryMode.getCode().compareTo(defaultDeliveryMode) == 0));
            if (deliveryMode.getDefaultDeliveryMode()) {
                nbDefautlt++;
            }
        }
        Assert.assertEquals(1, nbDefautlt);
    }

    @Test
    public void testGetSupportedPaymentModesForUserB2C() {
        final String defaultPaymentMode = "CreditCard";
        setCurrentUser("b2cuser");

        final Collection<DistPaymentModeData> paymentModes = userFacade.getSupportedPaymentModesForUser();
        Assert.assertEquals(3, paymentModes.size());

        int nbDefautlt = 0;
        // Check the default payment mode
        for (final DistPaymentModeData paymentMode : paymentModes) {
            Assert.assertTrue(paymentMode.getDefaultPaymentMode() == (paymentMode.getCode().compareTo(defaultPaymentMode) == 0));
            if (paymentMode.getDefaultPaymentMode()) {
                nbDefautlt++;
            }
        }
        Assert.assertEquals(1, nbDefautlt);
    }

    @Test
    public void testGetSupportedPaymentModesForUserB2B() {
        final String defaultPaymentMode = "CreditCard";
        setCurrentUser("b2buser");

        final Collection<DistPaymentModeData> paymentModes = userFacade.getSupportedPaymentModesForUser();
        Assert.assertEquals(3, paymentModes.size());

        int nbDefautlt = 0;
        // Check the default payment mode
        for (final DistPaymentModeData paymentMode : paymentModes) {
            Assert.assertTrue(paymentMode.getDefaultPaymentMode() == (paymentMode.getCode().compareTo(defaultPaymentMode) == 0));
            if (paymentMode.getDefaultPaymentMode()) {
                nbDefautlt++;
            }
        }
        Assert.assertEquals(1, nbDefautlt);
    }

    @Test
    public void testGetSupportedPaymentModesForUserB2BWithBudget() {

        final String defaultPaymentMode = "Z001_Invoice";
        final String customerId = "budgetuser";

        SIHybrisV1Out siHybrisV1OutMock = new SIHybrisV1OutMockBuilder()
                                                                        .expectIf10ReadPaymentMethods(customerId, readPaymentMethod("WB01", false),
                                                                                                      readPaymentMethod("Z001", true))
                                                                        .build();
        setWebServiceClient(siHybrisV1OutMock);

        setCurrentUser(customerId);

        final Collection<DistPaymentModeData> paymentModes = userFacade.getSupportedPaymentModesForUser();
        // The user can only use Invoice
        Assert.assertEquals(1, paymentModes.size());
        Assert.assertEquals(defaultPaymentMode, paymentModes.iterator().next().getCode());

        // Check the default payment mode
        Assert.assertTrue(paymentModes.iterator().next().getDefaultPaymentMode());
    }

    @Test
    public void testChangeDefaultPaymentModesForUserB2B() {
        final String newPaymentMode = "PayPal";
        final String customerId = "b2buser";
        setCurrentUser(customerId);

        Assert.assertTrue(userFacade.setDefaultPaymentMode(newPaymentMode));

        final Collection<DistPaymentModeData> paymentModes = userFacade.getSupportedPaymentModesForUser();
        Assert.assertEquals(3, paymentModes.size());

        int nbDefautlt = 0;
        for (final DistPaymentModeData paymentMode : paymentModes) {
            Assert.assertTrue(paymentMode.getDefaultPaymentMode() == (paymentMode.getCode().compareTo(newPaymentMode) == 0));
            if (paymentMode.getDefaultPaymentMode()) {
                nbDefautlt++;
            }
        }
        Assert.assertEquals(1, nbDefautlt);

        final List<CCPaymentInfoData> creditsCardsOptions = userFacade.getCCPaymentInfos(true);
        for (final CCPaymentInfoData ccPayment : creditsCardsOptions) {
            Assert.assertFalse(ccPayment.isDefaultPaymentInfo());
        }
    }

    @Test
    public void testChangeDefaultPaymentModesWithWrongValuePaymentModeForUserB2B() {

        final String defaultPaymentMode = "CreditCard";
        final String wrongPaymentMode = "wrongMode";

        setCurrentUser("b2buser");

        // try to set the new paymentMode. The paymentMode doesn't change
        Assert.assertFalse(userFacade.setDefaultPaymentMode(wrongPaymentMode));

        final Collection<DistPaymentModeData> paymentModes = userFacade.getSupportedPaymentModesForUser();
        Assert.assertEquals(3, paymentModes.size());

        int nbDefautlt = 0;
        // Check the default payment mode
        for (final DistPaymentModeData paymentMode : paymentModes) {
            Assert.assertTrue(paymentMode.getDefaultPaymentMode() == (paymentMode.getCode().compareTo(defaultPaymentMode) == 0));
            if (paymentMode.getDefaultPaymentMode()) {
                nbDefautlt++;
            }
        }
        Assert.assertEquals(1, nbDefautlt);
    }

    @Test
    public void testChangeDefaultDeliveryModesForUserB2B() {
        final String newDeliveryMode = "Express";

        final String customerId = "b2buser";

        SIHybrisV1Out siHybrisV1OutMock = new SIHybrisV1OutMockBuilder()
                                                                        .expectIf09ReadShippingMethods(customerId,
                                                                                                       readShippingMethod(ShippingMethodCode.E_1, false),
                                                                                                       readShippingMethod(ShippingMethodCode.E_2, true))
                                                                        .expectIf09UpdateDefaultShippingMethod(customerId, ShippingMethodCode.E_1, true)
                                                                        .expectIf09ReadShippingMethods(customerId,
                                                                                                       readShippingMethod(ShippingMethodCode.E_1, true),
                                                                                                       readShippingMethod(ShippingMethodCode.E_2, false),
                                                                                                       readShippingMethod(ShippingMethodCode.N_1, false),
                                                                                                       readShippingMethod(ShippingMethodCode.N_2, false),
                                                                                                       readShippingMethod(ShippingMethodCode.O_1, false),
                                                                                                       readShippingMethod(ShippingMethodCode.S_1, false),
                                                                                                       readShippingMethod(ShippingMethodCode.X_1, false))
                                                                        .build();
        setWebServiceClient(siHybrisV1OutMock);

        setCurrentUser(customerId);

        // Set the new deliveryMode
        Assert.assertTrue(userFacade.setDefaultDeliveryMode(newDeliveryMode));

        final Collection<DeliveryModeData> deliveryModes = userFacade.getSupportedDeliveryModesForUser();
        // Only 2 delivery methods, pickup should not be visible
        Assert.assertEquals(7, deliveryModes.size());

        int nbDefautlt = 0;
        // Check the default delivery mode
        for (final DeliveryModeData deliveryMode : deliveryModes) {
            boolean b1 = deliveryMode.getDefaultDeliveryMode();
            int i1 = deliveryMode.getCode().compareTo(newDeliveryMode);
            Assert.assertTrue(deliveryMode.getDefaultDeliveryMode() == (deliveryMode.getCode().compareTo(newDeliveryMode) == 0));
            if (deliveryMode.getDefaultDeliveryMode()) {
                nbDefautlt++;
            }
        }
        Assert.assertEquals(1, nbDefautlt);
    }

    @Test
    public void testChangeDefaultDeliveryModesWithWrongValueForUserB2B() {
        final String wrongDeliveryMode = "wrongMode";
        final String defaultDeliveryMode = "Emergency";

        setCurrentUser("b2buser");

        // try to set the new deliveryMode. The deliveryMode doesn't change
        Assert.assertFalse(userFacade.setDefaultDeliveryMode(wrongDeliveryMode));

        final Collection<DeliveryModeData> deliveryModes = userFacade.getSupportedDeliveryModesForUser();
        // Only 2 delivery methods, pickup should not be visible
        Assert.assertEquals(7, deliveryModes.size());

        int nbDefautlt = 0;
        // Check the default delivery mode
        for (final DeliveryModeData deliveryMode : deliveryModes) {
            Assert.assertTrue(deliveryMode.getDefaultDeliveryMode() == (deliveryMode.getCode().compareTo(defaultDeliveryMode) == 0));
            if (deliveryMode.getDefaultDeliveryMode()) {
                nbDefautlt++;
            }
        }
        Assert.assertEquals(1, nbDefautlt);
    }

    @Test
    public void testDefaultCCModesForUserB2B() {
        final String defaultCCType = "visa";

        setCurrentUser("b2buser");

        final List<CCPaymentInfoData> creditsCardsOptions = userFacade.getCCPaymentInfos(true);
        Assert.assertEquals(2, creditsCardsOptions.size());

        int nbDefautlt = 0;
        // Check the default delivery mode
        for (final CCPaymentInfoData ccPayment : creditsCardsOptions) {
            Assert.assertTrue(ccPayment.isDefaultPaymentInfo() == (ccPayment.getCardTypeData().getCode().compareTo(defaultCCType) == 0));
            if (ccPayment.isDefaultPaymentInfo()) {
                nbDefautlt++;
            }
        }
        Assert.assertEquals(1, nbDefautlt);
    }

    @Test
    public void testChangeDefaultCCModesWithWrongValueForUserB2B() {
        final String defaultCCNumber = "*************881";
        final String wrongPk = "000001";

        setCurrentUser("b2buser");

        // try to set the new CC. The deliveryMode doesn't change
        Assert.assertFalse(userFacade.setDefaultPaymentInfo(wrongPk));

        final List<CCPaymentInfoData> creditsCardsOptions = userFacade.getCCPaymentInfos(true);
        Assert.assertEquals(2, creditsCardsOptions.size());

        int nbDefautlt = 0;
        // Check the default delivery mode
        for (final CCPaymentInfoData ccPayment : creditsCardsOptions) {
            Assert.assertTrue(ccPayment.isDefaultPaymentInfo() == (ccPayment.getCardNumber().compareTo(defaultCCNumber) == 0));
            if (ccPayment.isDefaultPaymentInfo()) {
                nbDefautlt++;
            }
        }
        Assert.assertEquals(1, nbDefautlt);
    }

    @Test
    public void testChangeDefaultDeliveryModesForUserB2BPickUp() {
        final String newDeliveryMode = "PickUp";
        final String defaultDeliveryMode = "Emergency";

        setCurrentUser("b2buser");

        // Set the new deliveryMode
        Assert.assertFalse(userFacade.setDefaultDeliveryMode(newDeliveryMode));

        final Collection<DeliveryModeData> deliveryModes = userFacade.getSupportedDeliveryModesForUser();
        // Only 2 delivery methods, pickup should not be visible
        Assert.assertEquals(7, deliveryModes.size());

        int nbDefautlt = 0;
        // Check the default delivery mode
        for (final DeliveryModeData deliveryMode : deliveryModes) {
            Assert.assertTrue(deliveryMode.getDefaultDeliveryMode() == (deliveryMode.getCode().compareTo(defaultDeliveryMode) == 0));
            if (deliveryMode.getDefaultDeliveryMode()) {
                nbDefautlt++;
            }
        }
        Assert.assertEquals(1, nbDefautlt);
    }

    @Test
    public void testEditB2CShippingAddress() {
        // Test init
        final B2BCustomerModel user = setCurrentUser("b2cuser");

        // Search the user shippingaddress
        final AddressData futurAddress = createB2CAddress(false, true);
        // Only one shippingAddress for the customer
        futurAddress.setId(((List<AddressModel>) b2bUnitService.getParent(user).getShippingAddresses()).get(0).getPk().toString());

        // try to call the method
        userFacade.editAddress(futurAddress);

        // test the result (new address)
        final AddressModel newAddress = ((List<AddressModel>) b2bUnitService.getParent(user).getShippingAddresses()).get(0);

        checkB2CAddress(newAddress, Boolean.FALSE, Boolean.TRUE);
    }

    @Test
    public void testEditB2CBillingAddress() {
        // Test init
        final B2BCustomerModel user = setCurrentUser("b2cuser");

        // Search the user shippingaddress
        final AddressData futurAddress = createB2CAddress(true, false);
        // Only one shippingAddress for the customer
        futurAddress.setId(((List<AddressModel>) b2bUnitService.getParent(user).getBillingAddresses()).get(0).getPk().toString());

        // try to call the method
        userFacade.editAddress(futurAddress);

        // test the result (new address)
        final AddressModel newAddress = ((List<AddressModel>) b2bUnitService.getParent(user).getBillingAddresses()).get(0);

        checkB2CAddress(newAddress, Boolean.TRUE, Boolean.FALSE);
    }

    @Test
    public void testEditB2BShippingAddress() {
        // Test init
        final B2BCustomerModel user = setCurrentUser("b2buser");

        // Search the user shippingaddress
        final AddressData futurAddress = createB2BAddress(false, true);
        // Only one shippingAddress for the customer
        futurAddress.setId(((List<AddressModel>) b2bUnitService.getParent(user).getShippingAddresses()).get(0).getPk().toString());

        // try to call the method
        userFacade.editAddress(futurAddress);

        // test the result (new address)
        final AddressModel newAddress = ((List<AddressModel>) b2bUnitService.getParent(user).getShippingAddresses()).get(0);

        checkB2BAddress(newAddress, Boolean.FALSE, Boolean.TRUE);
    }

    @Test
    public void testB2BBillingEditAddress() {
        // Test init
        final B2BCustomerModel user = setCurrentUser("b2buser");

        // Search the user shippingaddress
        final AddressData futurAddress = createB2BAddress(true, false);
        // Only one shippingAddress for the customer
        futurAddress.setId(((List<AddressModel>) b2bUnitService.getParent(user).getBillingAddresses()).get(0).getPk().toString());

        // try to call the method
        userFacade.editAddress(futurAddress);

        // test the result (new address)
        final AddressModel newAddress = ((List<AddressModel>) b2bUnitService.getParent(user).getBillingAddresses()).get(0);

        checkB2BAddress(newAddress, Boolean.TRUE, Boolean.FALSE);
    }

    @Test
    public void testEditB2BKeyShippingAddress() {
        // Test init
        final B2BCustomerModel user = setCurrentUser("b2bkeyuser");

        // Search the user shippingaddress
        final AddressData futurAddress = createB2BAddress(false, true);
        // Only one shippingAddress for the customer
        futurAddress.setId(((List<AddressModel>) b2bUnitService.getParent(user).getShippingAddresses()).get(0).getPk().toString());

        // try to call the method
        userFacade.editAddress(futurAddress);

        // test the result (new address)
        final AddressModel newAddress = ((List<AddressModel>) b2bUnitService.getParent(user).getShippingAddresses()).get(0);

        checkB2BAddress(newAddress, Boolean.FALSE, Boolean.TRUE);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testB2BKeyBillingEditAddress() {
        // Test init
        final B2BCustomerModel user = setCurrentUser("b2bkeyuser");

        // Search the user shippingaddress
        final AddressData futurAddress = createB2BAddress(true, false);

        // Only one shippingAddress for the customer
        futurAddress.setId(((List<AddressModel>) b2bUnitService.getParent(user).getBillingAddresses()).get(0).getPk().toString());

        // try to call the method
        userFacade.editAddress(futurAddress);
    }

    @Test
    public void testAddB2CShippingAddress() {
        // Test init
        final B2BCustomerModel user = setCurrentUser("b2cuser");
        final AddressData futurAddress = createB2CAddress(false, true);

        // try to call the method
        userFacade.addAddress(futurAddress);

        // test the result (new address)
        final AddressModel newAddress = ((List<AddressModel>) b2bUnitService.getParent(user).getShippingAddresses()).get(1);

        checkB2CAddress(newAddress, Boolean.FALSE, Boolean.TRUE);
    }

    @Test
    public void testAddB2BShippingAddress() {
        // Test init
        final B2BCustomerModel user = setCurrentUser("b2buser");

        final AddressData futurAddress = createB2BAddress(false, true);

        // try to call the method
        userFacade.addAddress(futurAddress);

        // test the result (new address)
        final AddressModel newAddress = ((List<AddressModel>) b2bUnitService.getParent(user).getShippingAddresses()).get(1);

        checkB2BAddress(newAddress, Boolean.FALSE, Boolean.TRUE);
    }

    @Test
    public void testAddB2BKeyShippingAddress() {
        // Test init
        final B2BCustomerModel user = setCurrentUser("b2bkeyuser");

        final AddressData futurAddress = createB2BAddress(false, true);

        // try to call the method
        userFacade.addAddress(futurAddress);

        // test the result (new address)
        final AddressModel newAddress = ((List<AddressModel>) b2bUnitService.getParent(user).getShippingAddresses()).get(1);
        checkB2BAddress(newAddress, Boolean.FALSE, Boolean.TRUE);
    }

    @Test
    public void testRemoveB2CShippingAddress() {

        // Test init
        final B2BCustomerModel user = setCurrentUser("b2cuser");

        final AddressData addressToRemove = new AddressData();
        addressToRemove.setId(((List<AddressModel>) b2bUnitService.getParent(user).getShippingAddresses()).get(0).getPk().toString());

        // try to call the method
        userFacade.removeAddress(addressToRemove);

        // refresh
        final B2BUnitModel unit = b2bUnitService.getParent(user);
        modelService.refresh(unit);

        // Test the number of billing addresses for the current customer
        Assert.assertTrue(unit.getShippingAddresses().isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveB2CBillingAddress() {

        final B2BCustomerModel user = setCurrentUser("b2cuser");
        final AddressData addressToRemove = new AddressData();
        addressToRemove.setId(((List<AddressModel>) b2bUnitService.getParent(user).getBillingAddresses()).get(0).getPk().toString());

        // try to call the method
        userFacade.removeAddress(addressToRemove);
        Assert.assertTrue(false);
    }

    @Test
    public void testRemoveB2BShippingAddress() {

        // Test init
        final B2BCustomerModel user = setCurrentUser("b2buser");

        final AddressData addressToRemove = new AddressData();
        addressToRemove.setId(((List<AddressModel>) b2bUnitService.getParent(user).getShippingAddresses()).get(0).getPk().toString());

        // try to call the method
        userFacade.removeAddress(addressToRemove);

        // refresh
        final B2BUnitModel unit = b2bUnitService.getParent(user);
        modelService.refresh(unit);

        // Test the number of billing addresses for the current customer
        Assert.assertTrue(unit.getShippingAddresses().isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveB2BBillingAddress() {

        // Test init
        final B2BCustomerModel user = setCurrentUser("b2buser");

        final AddressData addressToRemove = new AddressData();
        addressToRemove.setId(((List<AddressModel>) b2bUnitService.getParent(user).getBillingAddresses()).get(0).getPk().toString());

        // try to call the method
        userFacade.removeAddress(addressToRemove);
    }

    @Test
    public void testRemoveB2BKeyShippingAddress() {

        // Test init
        final B2BCustomerModel user = setCurrentUser("b2bkeyuser");

        final AddressData addressToRemove = new AddressData();
        addressToRemove.setId(((List<AddressModel>) b2bUnitService.getParent(user).getShippingAddresses()).get(0).getPk().toString());

        // try to call the method
        userFacade.removeAddress(addressToRemove);

        // refresh
        final B2BUnitModel unit = b2bUnitService.getParent(user);
        modelService.refresh(unit);

        // Test the number of billing addresses for the current customer
        Assert.assertTrue(unit.getShippingAddresses().isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveB2BKeyBillingAddress() {

        // Test init
        final B2BCustomerModel user = setCurrentUser("b2bkeyuser");

        final AddressData addressToRemove = new AddressData();
        addressToRemove.setId(((List<AddressModel>) b2bUnitService.getParent(user).getBillingAddresses()).get(0).getPk().toString());

        // try to call the method
        userFacade.removeAddress(addressToRemove);
    }

    @Test(expected = IllegalArgumentException.class)
    // Try to remove an address from an other user
    public void testb2cuserRemoveB2BBillingAddress() {
        // Test init
        setCurrentUser("b2cuser");
        final UserModel userB2B = userService.getUserForUID("b2buser");

        final AddressData addressToRemove = new AddressData();
        addressToRemove.setId(((List<AddressModel>) b2bUnitService.getParent((B2BCustomerModel) userB2B).getBillingAddresses()).get(0).getPk().toString());

        // try to call the method
        userFacade.removeAddress(addressToRemove);
    }

    @Test(expected = IllegalArgumentException.class)
    // Try to remove an address from an other user
    public void testb2cuserEditB2BBillingAddress() {

        // Test init
        final B2BCustomerModel userB2B = (B2BCustomerModel) userService.getUserForUID("b2buser");
        setCurrentUser("b2cuser");

        // Search the user shippingaddress
        final AddressData futurAddress = createB2BAddress(true, false);
        // Only one shippingAddress for the customer
        futurAddress.setId(((List<AddressModel>) b2bUnitService.getParent(userB2B).getShippingAddresses()).get(0).getPk().toString());

        // try to call the method
        userFacade.editAddress(futurAddress);
    }

    @Test
    public void testGetAddressBookDeliveryEntriesB2COrderByCityAsc() {

        // Test init
        setCurrentUser("b2cusermultiaddresses");

        // try to call the method
        final List<AddressData> addresses = userFacade.getAddressBookDeliveryEntries("byCity", "asc");
        Assert.assertEquals(2, addresses.size());

        Assert.assertEquals("City1", addresses.get(0).getTown());
        Assert.assertEquals("City2", addresses.get(1).getTown());
    }

    @Test
    public void testGetAddressBookDeliveryEntriesB2COrderByCityDesc() {
        // Test init
        setCurrentUser("b2cusermultiaddresses");

        // try to call the method
        final List<AddressData> addresses = userFacade.getAddressBookDeliveryEntries("byCity", "desc");
        Assert.assertEquals(2, addresses.size());

        Assert.assertEquals("City2", addresses.get(0).getTown());
        Assert.assertEquals("City1", addresses.get(1).getTown());
    }

    @Test
    public void testGetAddressBookDeliveryEntriesB2COrderByErrorType() {
        // Test init
        setCurrentUser("b2cusermultiaddresses");

        // try to call the method
        final List<AddressData> addresses = userFacade.getAddressBookDeliveryEntries("error", "asc");
        Assert.assertEquals(2, addresses.size());

        // Like byCity (default)
        Assert.assertEquals("City1", addresses.get(0).getTown());
        Assert.assertEquals("City2", addresses.get(1).getTown());
    }

    @Test
    public void testGetAddressBookDeliveryEntriesB2COrderByCityNoType() {
        // Test init
        setCurrentUser("b2cusermultiaddresses");

        // try to call the method
        final List<AddressData> addresses = userFacade.getAddressBookDeliveryEntries("byCity", "");
        Assert.assertEquals(2, addresses.size());

        // without type, it will be asc (default)
        Assert.assertEquals("City1", addresses.get(0).getTown());
        Assert.assertEquals("City2", addresses.get(1).getTown());
    }

    @Test
    public void testGetAddressBookDeliveryEntriesB2COrderByNameAsc() {
        // Test init
        setCurrentUser("b2cusermultiaddresses");

        // try to call the method
        final List<AddressData> addresses = userFacade.getAddressBookDeliveryEntries("byName", "asc");
        Assert.assertEquals(2, addresses.size());

        Assert.assertEquals("Last1", addresses.get(0).getLastName());
        Assert.assertEquals("Last2", addresses.get(1).getLastName());

    }

    @Test
    public void testGetAddressBookDeliveryEntriesB2COrderByNameDesc() {
        // Test init
        setCurrentUser("b2cusermultiaddresses");

        // try to call the method
        final List<AddressData> addresses = userFacade.getAddressBookDeliveryEntries("byName", "desc");
        Assert.assertEquals(2, addresses.size());

        Assert.assertEquals("Last2", addresses.get(0).getLastName());
        Assert.assertEquals("Last1", addresses.get(1).getLastName());
    }

    @Test
    public void testGetAddressBookDeliveryEntriesB2BOrderByCityAsc() {
        // Test init
        setCurrentUser("b2bkeyusermultiaddresses");

        // try to call the method
        final List<AddressData> addresses = userFacade.getAddressBookDeliveryEntries("byCity", "asc");
        Assert.assertEquals(2, addresses.size());

        Assert.assertEquals("City1", addresses.get(0).getTown());
        Assert.assertEquals("City2", addresses.get(1).getTown());
    }

    @Test
    public void testGetAddressBookDeliveryEntriesB2BOrderByCityDesc() {
        // Test init
        setCurrentUser("b2bkeyusermultiaddresses");

        // try to call the method
        final List<AddressData> addresses = userFacade.getAddressBookDeliveryEntries("byCity", "desc");
        Assert.assertEquals(2, addresses.size());

        Assert.assertEquals("City2", addresses.get(0).getTown());
        Assert.assertEquals("City1", addresses.get(1).getTown());
    }

    @Test
    public void testGetAddressBookDeliveryEntriesB2BOrderByErrorType() {
        // Test init
        setCurrentUser("b2bkeyusermultiaddresses");

        // try to call the method
        final List<AddressData> addresses = userFacade.getAddressBookDeliveryEntries("error", "asc");
        Assert.assertEquals(2, addresses.size());

        // Like byCity (default)
        Assert.assertEquals("City1", addresses.get(0).getTown());
        Assert.assertEquals("City2", addresses.get(1).getTown());
    }

    @Test
    public void testGetAddressBookDeliveryEntriesB2BOrderByCityNoType() {
        // Test init
        setCurrentUser("b2bkeyusermultiaddresses");

        // try to call the method
        final List<AddressData> addresses = userFacade.getAddressBookDeliveryEntries("byCity", "");
        Assert.assertEquals(2, addresses.size());

        // without type, it will be asc (default)
        Assert.assertEquals("City1", addresses.get(0).getTown());
        Assert.assertEquals("City2", addresses.get(1).getTown());

    }

    @Test
    public void testGetAddressBookDeliveryEntriesB2BOrderByCompanyAsc() {
        // Test init
        setCurrentUser("b2bkeyusermultiaddresses");

        // try to call the method
        final List<AddressData> addresses = userFacade.getAddressBookDeliveryEntries("byCompany", "asc");
        Assert.assertEquals(2, addresses.size());

        Assert.assertEquals("Company1", addresses.get(0).getCompanyName());
        Assert.assertEquals("Company2", addresses.get(1).getCompanyName());
    }

    @Test
    public void testGetAddressBookDeliveryEntriesB2BOrderByCompanyDesc() {
        // Test init
        setCurrentUser("b2bkeyusermultiaddresses");

        // try to call the method
        final List<AddressData> addresses = userFacade.getAddressBookDeliveryEntries("byCompany", "desc");
        Assert.assertEquals(2, addresses.size());

        Assert.assertEquals("Company2", addresses.get(0).getCompanyName());
        Assert.assertEquals("Company1", addresses.get(1).getCompanyName());
    }

    // helper
    protected AddressData createB2BAddress(final boolean isBilling, final boolean isShipping) {
        // New values
        final String companyName = "CompanyName";
        final String companyName2 = "CompanyName2";
        final String companyName3 = "CompanyName3";

        final String adrLine1 = "adrLine1";
        final String adrLine2 = "adrLine2";

        final String poBox = "poBox";
        final String town = "town";
        final String zipCode = "zipCode";
        final String isoCode = "CH";

        final AddressData address = new AddressData();

        // Set the new values
        address.setCompanyName(companyName);
        address.setCompanyName2(companyName2);
        address.setCompanyName3(companyName3);
        address.setLine1(adrLine1);
        address.setLine2(adrLine2);

        address.setPobox(poBox);
        address.setTown(town);
        address.setPostalCode(zipCode);

        address.setBillingAddress(isBilling);
        address.setShippingAddress(isShipping);

        final CountryData countryData = new CountryData();
        countryData.setIsocode(isoCode);
        address.setCountry(countryData);

        return address;
    }

    // helper
    protected void checkB2BAddress(final AddressModel address, final Boolean isBilling, final Boolean isShipping) {
        // New values
        final String companyName = "CompanyName";
        final String companyName2 = "CompanyName2";
        final String companyName3 = "CompanyName3";

        final String adrLine1 = "adrLine1";
        final String adrLine2 = "adrLine2";

        final String poBox = "poBox";
        final String town = "town";
        final String zipCode = "zipCode";
        final String isoCode = "CH";

        Assert.assertEquals(companyName2, address.getCompanyName2());
        Assert.assertEquals(companyName3, address.getCompanyName3());

        Assert.assertEquals(adrLine1, address.getLine1());
        Assert.assertEquals(adrLine2, address.getLine2());

        Assert.assertEquals(poBox, address.getPobox());
        Assert.assertEquals(town, address.getTown());
        Assert.assertEquals(zipCode, address.getPostalcode());
        Assert.assertEquals(isoCode, address.getCountry().getIsocode());

        Assert.assertEquals(isBilling, address.getBillingAddress());
        Assert.assertEquals(isShipping, address.getShippingAddress());

    }

    // helper
    protected AddressData createB2CAddress(final boolean isBilling, final boolean isShipping) {
        // New values
        final String title = "mr";
        final String firstName = "First";
        final String lastName = "Last";

        final String additionnalAddress = "additional address";
        final String adrLine1 = "adrLine1";
        final String adrLine2 = "adrLine2";

        final String poBox = "poBox";
        final String town = "town";
        final String zipCode = "zipCode";
        final String isoCode = "CH";

        final AddressData address = new AddressData();

        // Set the new values
        address.setTitleCode(title);
        address.setFirstName(firstName);
        address.setLastName(lastName);

        address.setAdditionalAddress(additionnalAddress);
        address.setLine1(adrLine1);
        address.setLine2(adrLine2);

        address.setPobox(poBox);
        address.setTown(town);
        address.setPostalCode(zipCode);

        address.setBillingAddress(isBilling);
        address.setShippingAddress(isShipping);

        final CountryData countryData = new CountryData();
        countryData.setIsocode(isoCode);
        address.setCountry(countryData);

        return address;

    }

    // helper
    protected void checkB2CAddress(final AddressModel address, final Boolean isBilling, final Boolean isShipping) {
        final String title = "mr";
        final String firstName = "First";
        final String lastName = "Last";

        final String additionnalAddress = "additional address";
        final String adrLine1 = "adrLine1";
        final String adrLine2 = "adrLine2";

        final String poBox = "poBox";
        final String town = "town";
        final String zipCode = "zipCode";
        final String isoCode = "CH";

        Assert.assertEquals(title, address.getTitle().getCode());
        Assert.assertEquals(firstName, address.getFirstname());
        Assert.assertEquals(lastName, address.getLastname());

        Assert.assertEquals(additionnalAddress, address.getAdditionalAddressCompany());
        Assert.assertEquals(adrLine1, address.getLine1());
        Assert.assertEquals(adrLine2, address.getLine2());

        Assert.assertEquals(poBox, address.getPobox());
        Assert.assertEquals(town, address.getTown());
        Assert.assertEquals(zipCode, address.getPostalcode());
        Assert.assertEquals(isoCode, address.getCountry().getIsocode());

        Assert.assertEquals(isBilling, address.getBillingAddress());
        Assert.assertEquals(isShipping, address.getShippingAddress());

    }

    private B2BCustomerModel setCurrentUser(final String uid) {
        final B2BCustomerModel user = (B2BCustomerModel) userService.getUserForUID(uid);
        userService.setCurrentUser(user);
        return user;
    }

}
