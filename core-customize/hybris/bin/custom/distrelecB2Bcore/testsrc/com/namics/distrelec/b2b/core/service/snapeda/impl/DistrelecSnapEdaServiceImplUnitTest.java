package com.namics.distrelec.b2b.core.service.snapeda.impl;

import java.lang.reflect.Field;
import java.util.Date;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.service.snapeda.response.SnapEdaResponse;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistrelecSnapEdaServiceImplUnitTest {

    @InjectMocks
    private DistrelecSnapEdaServiceImpl distrelecSnapEdaService;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private ModelService modelService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Logger logger;

    private ProductModel mockProduct;

    private SnapEdaResponse mockSnapEdaResponse;

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        Configuration mockConfiguration = mock(Configuration.class);
        DistManufacturerModel mockManufacturer = mock(DistManufacturerModel.class);
        mockProduct = mock(ProductModel.class);
        mockSnapEdaResponse = mock(SnapEdaResponse.class);
        // Modify the final field using reflection
        Field restTemplateField = DistrelecSnapEdaServiceImpl.class.getDeclaredField("restTemplate");
        restTemplateField.setAccessible(true);
        restTemplateField.set(distrelecSnapEdaService, restTemplate);

        when(mockProduct.getTypeName()).thenReturn("Potato");
        when(mockProduct.getManufacturer()).thenReturn(mockManufacturer);
        when(mockProduct.getSnapEdaAvailabilityFlaggingDate()).thenReturn(null);
        when(mockManufacturer.getName()).thenReturn("PotatOS");
        when(configurationService.getConfiguration()).thenReturn(mockConfiguration);
        when(mockConfiguration.getString("snapeda.api.url")).thenReturn("https://potato.potato");
        when(mockConfiguration.getString("snapeda.api.token")).thenReturn("potatoken");
        when(mockConfiguration.getInt("snapeda.product.flagging.timeout.days.not_found", 30)).thenReturn(30);
        when(mockConfiguration.getInt("snapeda.product.flagging.timeout.days.found", 180)).thenReturn(180);
        when(mockSnapEdaResponse.getStatus()).thenReturn(true);
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(SnapEdaResponse.class))).thenReturn(
          new ResponseEntity<>(mockSnapEdaResponse,
                               HttpStatus.OK));
    }

    @Test
    public void testFlagProduct() {
        distrelecSnapEdaService.flagProduct(mockProduct);

        verify(configurationService, times(2)).getConfiguration();
        verify(modelService, times(1)).save(mockProduct);
    }

    @Test
    public void testFlagProductWhenFlaggingNotNeededForTrueProduct() {
        when(mockProduct.getSnapEdaAvailabilityFlaggingDate()).thenReturn(new Date());
        when(mockProduct.isAvailableInSnapEda()).thenReturn(true);

        distrelecSnapEdaService.flagProduct(mockProduct);

        verify(configurationService, times(1)).getConfiguration();
        verify(modelService, times(0)).save(mockProduct);
    }

    @Test
    public void testFlagProductWhenFlaggingNotNeededForFalseProduct() {
        when(mockProduct.getSnapEdaAvailabilityFlaggingDate()).thenReturn(new Date());
        when(mockProduct.isAvailableInSnapEda()).thenReturn(false);

        distrelecSnapEdaService.flagProduct(mockProduct);

        verify(configurationService, times(1)).getConfiguration();
        verify(modelService, times(0)).save(mockProduct);
    }

    @Test
    public void testFlagProductWhenProductIsNull() {
        distrelecSnapEdaService.flagProduct(null);
        verify(modelService, times(0)).save(any());
        verify(configurationService, times(0)).getConfiguration();
    }

    @Test
    public void testFlagProductWhenTypeNameBlank() {
        when(mockProduct.getTypeName()).thenReturn("");

        distrelecSnapEdaService.flagProduct(mockProduct);

        verify(configurationService, times(0)).getConfiguration();
    }

}
