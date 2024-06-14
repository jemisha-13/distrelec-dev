package com.namics.distrelec.b2b.facades.customer;

import static org.mockito.Mockito.when;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.namics.distrelec.b2b.core.inout.erp.data.FindContactRequestData;
import com.namics.distrelec.b2b.core.inout.erp.data.FindContactResponseData;
import com.namics.distrelec.b2b.core.inout.erp.impl.SapCustomerService;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.service.company.impl.DefaultDistB2BCommerceUnitService;
import com.namics.distrelec.b2b.core.service.customer.impl.DefaultDistCustomerAccountService;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.service.site.DistrelecCMSSiteService;
import com.namics.distrelec.b2b.facades.customer.exceptions.ExistingCustomerRegistrationException;
import com.namics.distrelec.b2b.facades.customer.impl.DefaultDistCustomerFacade;
import com.namics.distrelec.b2b.facades.user.data.DistExistingCustomerRegisterData;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

@IntegrationTest
public class OfflineCustomerUnitTest extends ServicelayerTransactionalTest {

    private static final String GERMANY_SALES_ORGANIZATION = "7350";

    @Resource
    private DefaultDistCustomerFacade customerFacade;

    @Resource
    private DefaultDistCustomerAccountService customerAccountService;

    @Resource
    private UserService userService;

    @Mock
    private SapCustomerService sapCustomerService;

    @Resource
    private DistSalesOrgService distSalesOrgService;

    @Resource
    private DistrelecCMSSiteService cmsSiteService;

    @Resource
    private CommonI18NService commonI18NService;

    @Resource
    private ModelService modelService;

    @Resource
    private DefaultDistB2BCommerceUnitService defaultDistB2BCommerceUnitService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final String CUSTOMER2__CONTACT_ERP_ID = "erpContactId2";

    private static final String CUSTOMER2__CUSTOMER_ERP_ID = "erpCustomerId2";

    private static final String CUSTOMER2__FIRST_NAME = "testing";

    private static final String CUSTOMER2__LAST_NAME = "testing";

    private static final String CUSTOMER2__EMAIL = "distrelec.testing+b2b02@gmail.com";

    private static final String CUSTOMER2__ORGANIZATIONAL_NUMBER = "testingOrganization2";

    private static final String CUSTOMER2__VAT_ID = "222222222222";

    private static final String CUSTOMER2__SALES_ORGANIZATION = "7350";

    @Before
    public void setUp() throws ImpExException {
        MockitoAnnotations.initMocks(this);

        importCsv("/distrelecB2Bfacades/test/testExistingCustomerRegistration_reset.impex", "utf-8");
        importCsv("/distrelecB2Bfacades/test/testExistingCustomerRegistration.impex", "utf-8");

        this.customerFacade.setCustomerService(this.sapCustomerService);
        this.customerAccountService.setErpCustomerService(this.sapCustomerService);
        this.cmsSiteService.setCurrentSite(getCurrentSite());
    }

    private CMSSiteModel getCurrentSite() {
        return this.cmsSiteService.getSiteForCountry(this.commonI18NService.getCountry("DE"));
    }

    /**
     * User Management = ACTIVE<br>
     * HYBRIS Customer = FOUND<br>
     * HYBRIS Contact = NOT FOUND<br>
     * SAP Customer = FOUND<br>
     * SAP Contact = FOUND<br>
     * 
     * Expected Result = normal contact creation in hybris
     * 
     * @throws DuplicateUidException
     * @throws ExistingCustomerRegistrationException
     */
    @Test
    public void testExistingB2BCase() throws DuplicateUidException, ExistingCustomerRegistrationException {
        activateUserManagement(true);

        try {
            final B2BCustomerModel existingContact = (B2BCustomerModel) this.userService.getUserForUID(CUSTOMER2__EMAIL);
            Assert.assertNull(existingContact);
        } catch (final Exception ex) {
            Assert.assertEquals(ex.getClass(), de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException.class);
            Assert.assertEquals(ex.getMessage(), "Cannot find user with uid '" + CUSTOMER2__EMAIL + "'");
        }

        final FindContactResponseData findContactResponse = prepareStubFindCustomerResponse(true, true, true, CUSTOMER2__CONTACT_ERP_ID,
                                                                                            CUSTOMER2__CUSTOMER_ERP_ID);

        when(sapCustomerService.findContact((FindContactRequestData) Mockito.any())).thenReturn(findContactResponse);
        when(sapCustomerService.updateContact((B2BCustomerModel) Mockito.any())).thenReturn(true);

        when(sapCustomerService.readCustomer((B2BUnitModel) Mockito.any())).thenReturn(this.defaultDistB2BCommerceUnitService.getB2BUnitByErpCustomerId(distSalesOrgService.getSalesOrgForCode(CUSTOMER2__SALES_ORGANIZATION),
                                                                                                                                                        CUSTOMER2__CUSTOMER_ERP_ID));

        final DistExistingCustomerRegisterData registerForm = prepareRegistrationForm(CUSTOMER2__CUSTOMER_ERP_ID, CUSTOMER2__FIRST_NAME, CUSTOMER2__LAST_NAME,
                                                                                      CUSTOMER2__ORGANIZATIONAL_NUMBER, CUSTOMER2__VAT_ID, CUSTOMER2__EMAIL);

        this.customerFacade.registerExistingB2B(registerForm);

        final B2BCustomerModel createdContact = (B2BCustomerModel) this.userService.getUserForUID(CUSTOMER2__EMAIL);
        Assert.assertNotNull("The B2BCustomer must not be null", createdContact);
        Assert.assertNotNull("The B2BCustomer erpContactId must not be null", createdContact.getErpContactID());
        Assert.assertEquals("The erpContactId is not correct", CUSTOMER2__CONTACT_ERP_ID, createdContact.getErpContactID());
    }

    private void activateUserManagement(final boolean active) {
        final DistSalesOrgModel salesOrg = this.distSalesOrgService.getSalesOrgForCode(GERMANY_SALES_ORGANIZATION);
        salesOrg.setAdminManagingSubUsers(active);
        this.modelService.save(salesOrg);
    }

    private FindContactResponseData prepareStubFindCustomerResponse(final boolean contactFound, final boolean contactUnique, final boolean customerFound,
                                                                    final String erpContactId, final String erpCustomerId) {
        final FindContactResponseData findContactResponse = new FindContactResponseData();
        findContactResponse.setContactFound(contactFound);
        findContactResponse.setContactUnique(contactUnique);
        findContactResponse.setCustomerFound(customerFound);
        findContactResponse.setErpContactId(erpContactId);
        findContactResponse.setErpCustomerId(erpCustomerId);
        return findContactResponse;
    }

    private DistExistingCustomerRegisterData prepareRegistrationForm(final String customerId, final String firstName, final String lastName,
                                                                     final String organizationalNumber, final String vatId, final String email) {
        final DistExistingCustomerRegisterData registerForm = new DistExistingCustomerRegisterData();
        registerForm.setLogin(email);
        registerForm.setCustomerId(customerId);
        registerForm.setFirstName(firstName);
        registerForm.setLastName(lastName);
        registerForm.setOrganizationalNumber(organizationalNumber);
        registerForm.setVatId(vatId);
        registerForm.setEmail(email);
        registerForm.setTitleCode("mr");
        return registerForm;
    }
}
