package com.namics.distrelec.b2b.core.inout.erp.impl;

import com.distrelec.webservice.sap.v1.*;
import com.namics.distrelec.b2b.core.inout.erp.dao.SapCustomerDao;
import com.namics.distrelec.b2b.core.model.DistDepartmentModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import com.namics.distrelec.b2b.core.service.user.daos.DistTitleDao;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.daos.CountryDao;
import de.hybris.platform.servicelayer.i18n.daos.CurrencyDao;
import de.hybris.platform.servicelayer.i18n.daos.RegionDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.AddressService;
import de.hybris.platform.servicelayer.user.UserService;
import org.fest.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigInteger;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
public class SapCustomerServiceTest {

    @Mock
    private ModelService modelService;
    @Mock
    private B2BCustomerService b2bCustomerService;
    @Mock
    private SIHybrisV1Out webServiceClient;
    @Mock
    private SapCustomerDao sapCustomerDao;
    @Mock
    private DistTitleDao titleDao;
    @Mock
    private CountryDao countryDao;
    @Mock
    private RegionDao regionDao;
    @Mock
    private CurrencyDao currencyDao;
    @Mock
    private DistrelecCodelistService distCodelistService;
    @Mock
    private AddressService addressService;
    @Mock
    private UserService userService;
    @Mock
    private CommonI18NService commonI18NService;
    @Mock
    private ObjectFactory sapObjectFactory;
    @InjectMocks
    private SapCustomerService sapCustomerService;

    private B2BUnitModel unitModel;

    private static final String ERP_CUSTOMER_ID = "01234567";
    private static final String SALES_ORGANISATION_CODE = "7640";
    private static final String COMPANY_CUSTOMER_TYPE = "COMPANY";


    @Before
    public void setUp() throws P1FaultMessage {
        MockitoAnnotations.initMocks(this);
        prepareMocks();
    }

    private void prepareMocks() throws P1FaultMessage {
        final DistSalesOrgModel salesOrgModel = mock(DistSalesOrgModel.class);
        when(salesOrgModel.getCode()).thenReturn(SALES_ORGANISATION_CODE);

        unitModel = mock(B2BUnitModel.class);
        when(unitModel.getErpCustomerID()).thenReturn(ERP_CUSTOMER_ID);
        when(unitModel.getSalesOrg()).thenReturn(salesOrgModel);

        when(sapObjectFactory.createReadCustomerRequest()).thenReturn(new ReadCustomerRequest());

        final ReadCustomerResponse response = mock(ReadCustomerResponse.class);
        when(response.isActive()).thenReturn(Boolean.valueOf(true));
        when(response.getCustomerType()).thenReturn(COMPANY_CUSTOMER_TYPE);
        when(response.isKeyAccountBusinessCustomer()).thenReturn(Boolean.valueOf(false));
        when(response.isOnlinePriceCalculation()).thenReturn(Boolean.valueOf(false));
        when(response.getOpenOrderMaxKeepOpenDays()).thenReturn(BigInteger.ZERO);
        when(response.getAddresses()).thenReturn(null);
        final List<ContactWithIdResponse> contacts = createAListOfContacts();
        when(response.getContacts()).thenReturn(contacts);

        when(webServiceClient.if08ReadCustomer(Mockito.any(ReadCustomerRequest.class))).thenReturn(response);

        final B2BCustomerModel customerModel = mock(B2BCustomerModel.class);
        when(b2bCustomerService.getCurrentB2BCustomer()).thenReturn(customerModel);
        when(addressService.createAddressForUser(customerModel)).thenReturn(new AddressModel());
        when(distCodelistService.getDistDepartment(Mockito.anyString())).thenReturn(new DistDepartmentModel());

        when(customerModel.getAddresses()).thenReturn(null);

        when(userService.getCurrentUser()).thenReturn(customerModel);

        doNothing().when(modelService).save(Mockito.any(AddressModel.class));
        doNothing().when(modelService).saveAll(Mockito.anyListOf(ItemModel.class));
    }


    @Test
    public void testNormalReadCustomerExecution() {
        final B2BUnitModel result = sapCustomerService.readCustomer(unitModel, true);

        assertEquals(unitModel.getErpCustomerID(), result.getErpCustomerID());
        assertNotNull(result.getSalesOrg());
        assertEquals(unitModel.getSalesOrg().getCode(), result.getSalesOrg().getCode());
        assertEquals(unitModel.getVatID(), result.getVatID());
    }

    private List<ContactWithIdResponse> createAListOfContacts() {
        final ContactWithIdResponse contact = mock(ContactWithIdResponse.class);
        when(contact.getEmail()).thenReturn("testing@distrelec.com");
        when(contact.getFaxNumber()).thenReturn("01908000000");
        when(contact.getFirstName()).thenReturn("Test");
        when(contact.getLastName()).thenReturn("User");
        when(contact.getPhoneNumber()).thenReturn("01442000000");
        when(contact.getMobileNumber()).thenReturn("07788000000");
        when(contact.getTitle()).thenReturn(Title.MR);

        return Collections.list(contact);
    }

}
