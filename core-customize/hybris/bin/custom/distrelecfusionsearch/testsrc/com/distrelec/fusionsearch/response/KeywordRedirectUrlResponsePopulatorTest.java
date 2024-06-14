package com.distrelec.fusionsearch.response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.distrelec.fusion.search.dto.FusionDTO;
import com.distrelec.fusion.search.dto.SearchResponseDTO;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
public class KeywordRedirectUrlResponsePopulatorTest extends AbstractSearchResponsePopulatorTest {

    @InjectMocks
    KeywordRedirectUrlResponsePopulator responsePopulator;

    @Mock
    SearchResponseDTO searchResponseDTO;

    @Before
    public void setUp() {
        when(searchResponseTuple.getSearchResponseDTO()).thenReturn(searchResponseDTO);
    }

    @Test
    public void testNotRedirectIfMoreThanOneRedirects() {
        String redirect1 = "redirect1"; // first should be used
        String redirect2 = "redirect2";

        FusionDTO fusionDTO = mock(FusionDTO.class);

        when(searchResponseDTO.getFusion()).thenReturn(fusionDTO);
        when(fusionDTO.getRedirect()).thenReturn(List.of(redirect1, redirect2));

        responsePopulator.populate(searchResponseTuple, searchPageData);

        assertNull(searchPageData.getKeywordRedirectUrl());
    }

    @Test
    public void testNotPopulateIfFusionDtoIsNull() {
        responsePopulator.populate(searchResponseTuple, searchPageData);

        assertNull(searchPageData.getKeywordRedirectUrl());
    }

    @Test
    public void testNotPopulateIfRedirectListIsNull() {
        FusionDTO fusionDTO = mock(FusionDTO.class);

        when(searchResponseDTO.getFusion()).thenReturn(fusionDTO);
        when(fusionDTO.getRedirect()).thenReturn(null);

        responsePopulator.populate(searchResponseTuple, searchPageData);

        assertNull(searchPageData.getKeywordRedirectUrl());
    }
}
