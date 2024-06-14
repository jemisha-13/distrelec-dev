package com.namics.distrelec.b2b.facades.rma;

import com.namics.distrelec.b2b.core.inout.erp.impl.DefaultReturnOnlineService;
import com.namics.distrelec.b2b.core.inout.erp.mock.MockSIHybrisIF19Out;
import com.namics.distrelec.b2b.core.rma.CreateRMAOrderEntryDataForm;
import com.namics.distrelec.b2b.core.rma.CreateRMARequestForm;
import com.namics.distrelec.b2b.core.service.rma.data.CreateRMAResponseData;
import com.namics.distrelec.b2b.core.service.rma.data.CreateRMAResponseItemData;
import com.namics.distrelec.b2b.facades.rma.impl.DefaultDistReturnRequestFacade;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * {@code DefaultDistReturnRequestFacadeTest}
 *
 *
 * <a href="sudarshan.tembhurnikar@datwyler.com">Sudarshan Tembhurnikar@datwyler</a>, Distrelec
 */
@IntegrationTest
public class DefaultDistReturnRequestFacadeTest extends ServicelayerTransactionalTest {

    @Resource
    private DefaultDistReturnRequestFacade defaultDistReturnRequestFacade;

    @Resource
    private B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService;

    @Resource
    private UserService userService;

    @Resource
    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

    @Resource
    private SessionService sessionService;

    @Resource
    private BaseSiteService baseSiteService;

    @Resource
    private DefaultReturnOnlineService returnOnlineService;

    private final MockSIHybrisIF19Out mockSIHybrisIF19Out = new MockSIHybrisIF19Out();

    @Before
    public void setUp() throws Exception {
        importCsv("/distrelecB2Bfacades/test/orderHistory/site.impex", "utf-8");
        importCsv("/distrelecB2Bfacades/test/orderHistory/b2bcustomer.impex", "utf-8");
    }

    /**
     * This test is performed to successfully submit the Return RMA request raised and receives response once request is submitted
     * successfully.
     */
    @Test
    public void testCreateRMACreateRequestSuccess() {
        final CreateRMARequestForm createRMARequestForm = new CreateRMARequestForm();
        createRMARequestForm.setOrderId("1000402161");
        createRMARequestForm.setOrderItems(Collections.singletonList(getCreateRMAOrderEntryDataForm()));

        // Set User details
        final CustomerModel customerModel = (CustomerModel) userService.getUserForUID("hans.muster@test.ch");
        final B2BUnitModel unitModel = b2bUnitService.getUnitForUid("testUnit");
        b2bCustomerService.addMember(customerModel, unitModel);

        // Set the customer in Session
        sessionService.setAttribute("user", customerModel);

        // Set the base site
        final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID("distrelec_CH");
        baseSiteService.setCurrentBaseSite(baseSite, false);

        // Call the Mock class go generate response
        returnOnlineService.setHybrisIF19Out(mockSIHybrisIF19Out);

        // Test the facade to create return request
        final CreateRMAResponseData createRMAResponseData = defaultDistReturnRequestFacade.createRMACreateRequest(createRMARequestForm);

        // Verify the result
        assertEquals("Switzerland", createRMAResponseData.getOfficeAddress());
        assertEquals("Return Raised", createRMAResponseData.getRmaHeaderStatus());
        assertEquals("11000011", createRMAResponseData.getRmaNumber());

        final List<CreateRMAResponseItemData> outputList = createRMAResponseData.getOrderItems();
        for (final CreateRMAResponseItemData createRMAResponseItemData : outputList) {

            final CreateRMAOrderEntryDataForm createRMAOrderEntry = createRMARequestForm.getOrderItems().stream()
                    .filter(rma -> rma.getItemNumber().equals(createRMAResponseItemData.getRmaItemNumber())).findFirst().get();

            assertEquals(createRMAOrderEntry.getItemNumber(), createRMAResponseItemData.getRmaItemNumber());
            assertEquals("Return Raised", createRMAResponseItemData.getRmaItemStatus());
        }
    }

    /**
     * This test is performed to check if any exception occurs if we receive null response from sap.
     */
    @Test
    public void testCreateRMACreateRequestFailure() {

        final CreateRMARequestForm createRMARequestForm = new CreateRMARequestForm();
        createRMARequestForm.setOrderId("1000402161");
        createRMARequestForm.setOrderItems(Collections.singletonList(getCreateRMAOrderEntryDataForm()));

        // Set User details
        final CustomerModel customerModel = (CustomerModel) userService.getUserForUID("hans.muster@test.ch");
        final B2BUnitModel unitModel = b2bUnitService.getUnitForUid("testUnit");
        b2bCustomerService.addMember(customerModel, unitModel);

        // Set the customer in SessionGuestReturnsRMAControllerTest
        sessionService.setAttribute("user", customerModel);

        // Set the base site
        final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID("distrelec_CH");
        baseSiteService.setCurrentBaseSite(baseSite, false);
        returnOnlineService.setHybrisIF19Out(null);

        // Test the facade to create return request
        assertThrows(NullPointerException.class, () -> defaultDistReturnRequestFacade.createRMACreateRequest(createRMARequestForm));
    }

    private CreateRMAOrderEntryDataForm getCreateRMAOrderEntryDataForm() {
        final CreateRMAOrderEntryDataForm createRMAOrderEntryDataForm = new CreateRMAOrderEntryDataForm();
        createRMAOrderEntryDataForm.setArticleNumber("18055840");
        createRMAOrderEntryDataForm.setCustomerText("Test Data");
        createRMAOrderEntryDataForm.setItemNumber("000010");
        createRMAOrderEntryDataForm.setQuantity(2L);
        createRMAOrderEntryDataForm.setRefundType("1");
        createRMAOrderEntryDataForm.setReturnReasonID("018");
        createRMAOrderEntryDataForm.setRmaRaised(false);
        return createRMAOrderEntryDataForm;
    }
}
