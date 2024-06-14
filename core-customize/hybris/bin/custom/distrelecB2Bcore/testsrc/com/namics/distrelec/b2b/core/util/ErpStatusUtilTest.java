package com.namics.distrelec.b2b.core.util;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;

@UnitTest
public class ErpStatusUtilTest {

    @Mock
    private ConfigurationService configurationService;
    @Mock
    private Configuration configuration;
    @InjectMocks
    private ErpStatusUtil erpStatusUtil;

    private static final String NEW_ERP_STATUSES = "20,40,52,60,90,99";
    private static final String NEW_KEY = "erp.new.status";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(configurationService.getConfiguration()).thenReturn(configuration);
    }

    private List<String> convert(final String statusCodes) {
        return Stream.of(statusCodes.split(ErpStatusUtil.COMMA)).map(String::trim).collect(Collectors.toList());
    }
}
