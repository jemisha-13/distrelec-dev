package com.namics.distrelec.b2b.storefront.controllers.pages;

import com.namics.distrelec.b2b.facades.manufacturer.DistManufacturerFacade;
import com.namics.distrelec.b2b.facades.manufacturer.data.DistMiniManufacturerData;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@UnitTest
public class ManufacturerStoresPageControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DistManufacturerFacade distManufacturerFacade;

    @InjectMocks
    private ManufacturerStoresPageController manufacturerStoresPageController;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(manufacturerStoresPageController).build();
    }

    @Test
    public void getManufacturers() throws Exception {

        Mockito.when(distManufacturerFacade.getManufactures()).thenReturn(getTestManufacturerData());

        mockMvc.perform(get("/manufacturers").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].code", is("man_abu"))).andExpect(jsonPath("$[0].name", is("Abus"))).andExpect(jsonPath("$[1].code", is("man_buf")))
                .andExpect(jsonPath("$[1].name", is("Buffalo Technology"))).andDo(print());
    }

    private Map<String, List<DistMiniManufacturerData>> getTestManufacturerData() {

        DistMiniManufacturerData distMiniManufacturerData1 = new DistMiniManufacturerData();
        DistMiniManufacturerData distMiniManufacturerData2 = new DistMiniManufacturerData();

        List<DistMiniManufacturerData> distMiniManufacturerDataList1 = new ArrayList<>();
        distMiniManufacturerData1.setCode("man_abu");
        distMiniManufacturerData1.setName("Abus");
        distMiniManufacturerData1.setNameSeo("abus");
        distMiniManufacturerData1.setUrlId("abu");
        distMiniManufacturerDataList1.add(distMiniManufacturerData1);

        List<DistMiniManufacturerData> distMiniManufacturerDataList2 = new ArrayList<>();
        distMiniManufacturerData2.setCode("man_buf");
        distMiniManufacturerData2.setName("Buffalo Technology");
        distMiniManufacturerData2.setNameSeo("buffalo-technology");
        distMiniManufacturerData2.setUrlId("buf");
        distMiniManufacturerDataList2.add(distMiniManufacturerData2);

        return new HashMap<String, List<DistMiniManufacturerData>>() {
            {
                put("A", distMiniManufacturerDataList1);
                put("B", distMiniManufacturerDataList2);
            }
        };
    }

}