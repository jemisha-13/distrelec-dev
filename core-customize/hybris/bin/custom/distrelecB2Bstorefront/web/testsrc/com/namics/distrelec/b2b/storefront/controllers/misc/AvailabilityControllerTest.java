package com.namics.distrelec.b2b.storefront.controllers.misc;

import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
public class AvailabilityControllerTest {

    @InjectMocks
    private AvailabilityController availabilityController;
    
    @Mock
    private DistrelecProductFacade distrelecProductFacade;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        availabilityController = new AvailabilityController();
        availabilityController.setProductFacade(distrelecProductFacade);

    }

//    @Test
//    public void testSaveStockNotificationData_Success() {
//
//        Model model = new ExtendedModelMap();
//        String articleNumber = "11001155";
//        List<String> articleNumbers = new ArrayList<>();
//        articleNumbers.add(articleNumber);
//        String customerEmail = "testuser@test.com";
//
//        given(getDistrelecProductFacade().saveStockNotificationDetails(anyString(), anyListOf(String.class))).willReturn(true);
//
//        final boolean status = availabilityController.saveStockNotificationData(customerEmail, articleNumbers, model);
//
//        assertEquals(true, status);
//    }
//
//    @Test
//    public void testSaveStockNotificationData_Failure() {
//
//        Model model = new ExtendedModelMap();
//        String customerEmail = "testuser@test.com";
//        String articleNumber = "11001155";
//        List<String> articleNumbers = new ArrayList<>();
//        articleNumbers.add(articleNumber);
//
//        given(getDistrelecProductFacade().saveStockNotificationDetails(anyString(), anyListOf(String.class))).willReturn(true);
//
//        final boolean status = availabilityController.saveStockNotificationData(customerEmail, null, model);
//
//        assertEquals(false, status);
//    }

    public DistrelecProductFacade getDistrelecProductFacade() {
        return distrelecProductFacade;
    }

    public void setDistrelecProductFacade(DistrelecProductFacade distrelecProductFacade) {
        this.distrelecProductFacade = distrelecProductFacade;
    }
    
}
