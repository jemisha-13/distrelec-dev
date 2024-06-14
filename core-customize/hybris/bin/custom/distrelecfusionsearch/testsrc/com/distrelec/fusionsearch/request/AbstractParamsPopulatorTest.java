package com.distrelec.fusionsearch.request;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

abstract class AbstractParamsPopulatorTest {

    protected MultiValuedMap<String, String> params;

    @Mock
    protected SearchRequestTuple searchRequestTuple;

    @Before
    public void setUpParams() {
        MockitoAnnotations.openMocks(this);

        params = new HashSetValuedHashMap<>();
    }
}
