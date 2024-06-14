/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.cms.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;

@UnitTest
@RunWith(DataProviderRunner.class)
public class DefaultDistCMSNavigationServiceTest {

    @InjectMocks
    private DefaultDistCMSNavigationService defaultDistCMSNavigationService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

 // @formatter:off
    @Test
    @DataProvider(value = {
        "Root",
        "Root    | Leaf",
        "Root    | Intermediate1    | Leaf",
        "Root    | Intermediate1    | Intermediate2  | Leaf"
    }, splitBy = "\\|", trimValues = true)
    // @formatter:on
    public void getNavigationNodeHierarchyTest(final String... expectedNodeUids) {

        final List<CMSNavigationNodeModel> nodes = new ArrayList<>();
        for (int i = 0; i < expectedNodeUids.length; i++) {
            final CMSNavigationNodeModel node = Mockito.mock(CMSNavigationNodeModel.class);
            BDDMockito.given(node.getUid()).willReturn(expectedNodeUids[i]);
            CMSNavigationNodeModel parentNode;
            if (i == 0) {
                parentNode = null;
            } else {
                parentNode = nodes.get(i - 1);
            }
            BDDMockito.given(node.getParent()).willReturn(parentNode);
            nodes.add(node);
        }

        final ArrayList<CMSNavigationNodeModel> actual = defaultDistCMSNavigationService.getNavigationNodeHierarchy(nodes.get(nodes.size() - 1));
        final List<String> actualNodeUids = actual.stream().map(CMSItemModel::getUid).collect(Collectors.toList());
        assertEquals(Arrays.asList(expectedNodeUids), actualNodeUids);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNavigationNodeHierarchyTestNull() {
        defaultDistCMSNavigationService.getNavigationNodeHierarchy(null);
    }

}
