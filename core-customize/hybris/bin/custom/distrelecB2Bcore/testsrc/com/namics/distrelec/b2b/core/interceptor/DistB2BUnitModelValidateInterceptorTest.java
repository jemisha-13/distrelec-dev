package com.namics.distrelec.b2b.core.interceptor;

import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.service.company.DistB2BCommerceUnitService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
public class DistB2BUnitModelValidateInterceptorTest {

    @Mock
    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
    @Mock
    private L10NService l10NService;
    @Mock
    private ModelService modelService;
    @Mock
    private DistB2BCommerceUnitService b2bCommerceUnitService;
    @InjectMocks
    private DistB2BUnitModelValidateInterceptor interceptor;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final String ERP_CUSTOMER_ID = "0001234567";
    private static final String UID = "1111";

    private B2BUnitModel businessModel;
    private InterceptorContext ctx;

    @Before
    public void setUp(){
        Registry.activateMasterTenant();
        MockitoAnnotations.initMocks(this);
        prepareMocks();
    }

    private void prepareMocks(){
        businessModel = mock(B2BUnitModel.class);
        when(businessModel.getErpCustomerID()).thenReturn(ERP_CUSTOMER_ID);
        when(businessModel.getSalesOrg()).thenReturn(new DistSalesOrgModel());
        when(businessModel.getPk()).thenReturn(PK.createCounterPK(1));
        when(businessModel.getCustomerType()).thenReturn(CustomerType.B2B);

        final Set<PrincipalModel> members = createListOfMembers();
        when(businessModel.getMembers()).thenReturn(members);

        doNothing().when(modelService).saveAll(anyList());
        doNothing().when(modelService).save(anyList());

        ctx = mock(InterceptorContext.class);
    }

    @Test
    public void saveAllTestWithNoException() throws InterceptorException {
        when(b2bCommerceUnitService.getB2BUnitByErpCustomerId(Mockito.any(DistSalesOrgModel.class), Mockito.anyString())).thenReturn(businessModel);
        final InterceptorContext ctx = mock(InterceptorContext.class);
        interceptor.onValidate(businessModel, ctx);
    }

    @Test
    public void IllegalStateExceptionIsThrownWithNullCustomerType() throws InterceptorException {
        final String exceptionMessage = "Customer type is not set on unit. Please fix that!";
        when(businessModel.getCustomerType()).thenReturn(null);
        when(l10NService.getLocalizedString(anyString())).thenReturn(exceptionMessage);
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(exceptionMessage);
        interceptor.onValidate(businessModel, ctx);
    }

    private Set<PrincipalModel> createListOfMembers() {
        final B2BCustomerModel customer = mock(B2BCustomerModel.class);
        when(customer.getCustomerType()).thenReturn(CustomerType.B2B);
        when(customer.getUid()).thenReturn(UID);
        return new HashSet<>(Arrays.asList(customer));
    }
}
