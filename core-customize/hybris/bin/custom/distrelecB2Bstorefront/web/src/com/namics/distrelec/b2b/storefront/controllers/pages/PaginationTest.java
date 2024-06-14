/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages;

import com.namics.distrelec.b2b.storefront.seo.DistLink;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import de.hybris.bootstrap.annotations.UnitTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@UnitTest
@RunWith(DataProviderRunner.class)
public class PaginationTest {
    
    private static final Logger LOG = LogManager.getLogger(PaginationTest.class);

    @InjectMocks
    private final AbstractPageController abstractPageController = Mockito.mock(AbstractPageController.class, Mockito.CALLS_REAL_METHODS);
    
    @Test
    // @formatter:off
    @DataProvider(value = {
        //Expected previous                                         | Expected Next                                             | RequestURL                                            | currentPage   | totalNumberOfPages
        "https://www.distrelec.ch/en/power/c/cat-L1D_379521?page=2  | https://www.distrelec.ch/en/power/c/cat-L1D_379521?page=4 | https://www.distrelec.ch/en/power/c/cat-L1D_379521    | 3             | 5",
        "https://www.distrelec.ch/en/power/c/cat-L1D_379521?page=2  | null                                                      | https://www.distrelec.ch/en/power/c/cat-L1D_379521    | 3             | 3",
        "null                                                       | https://www.distrelec.ch/en/power/c/cat-L1D_379521?page=2 | https://www.distrelec.ch/en/power/c/cat-L1D_379521    | 1             | 5",
        "null                                                       | null                                                      | https://www.distrelec.ch/en/power/c/cat-L1D_379521    | 1             | 1",
        "https://www.distrelec.ch/en/power/c/cat-L1D_379521?page=1  | https://www.distrelec.ch/en/power/c/cat-L1D_379521?page=3 | https://www.distrelec.ch/en/power/c/cat-L1D_379521    | 2             | 5"
    }, splitBy = "\\|", trimValues = true)
    // @formatter:on
    public void setPaginationHrefLangTest(final String expectedPrevious, final String expectedNext, final String requestUrl, final int currentPage,
            final int totalNumberOfPages) {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer(
                requestUrl));
        final List<DistLink> links = abstractPageController.setPaginationHrefLang(request, currentPage, totalNumberOfPages);
        LOG.info(links.stream().map(l -> l.toString()).collect(Collectors.joining(", ")));
        final List<DistLink> nextLinks = links.stream().filter(l -> AbstractPageController.LINK_NEXT.equals(l.getRel())).collect(Collectors.toList());
        final List<DistLink> previousLinks = links.stream().filter(l -> AbstractPageController.LINK_PREVIOUS.equals(l.getRel())).collect(Collectors.toList());
        assertTrue(nextLinks.size() <= 1);
        assertTrue(previousLinks.size() <= 1);

        if (expectedPrevious == null) {
            assertTrue(previousLinks.size() == 0);
        } else {
            assertEquals(expectedPrevious, previousLinks.get(0).getHref());
        }

        if (expectedNext == null) {
            assertTrue(nextLinks.size() == 0);
        } else {
            assertEquals(expectedNext, nextLinks.get(0).getHref());
        }
    }

}
