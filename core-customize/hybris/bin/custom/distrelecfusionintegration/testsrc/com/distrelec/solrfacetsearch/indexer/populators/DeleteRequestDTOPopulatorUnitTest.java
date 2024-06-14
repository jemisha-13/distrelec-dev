package com.distrelec.solrfacetsearch.indexer.populators;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.distrelec.fusion.integration.dto.DeleteRequestDTO;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DeleteRequestDTOPopulatorUnitTest {

    private static final String QUERY = "(-(indexOperationId:[111125495540089144 TO *]) AND country_s:SE)";

    @InjectMocks
    private DeleteRequestDTOPopulator deleteRequestDTOPopulator;

    private DeleteRequestDTO deleteRequestDTO;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);

        deleteRequestDTO = new DeleteRequestDTO();
    }

    @Test
    public void testPopulate() {
        deleteRequestDTOPopulator.populate(QUERY, deleteRequestDTO);

        assertThat(deleteRequestDTO.getQuery(), notNullValue());
        assertThat(deleteRequestDTO.getQuery(), is(QUERY));
    }

    @Test
    public void testPopulateNull() {
        deleteRequestDTOPopulator.populate(null, deleteRequestDTO);

        assertThat(deleteRequestDTO.getQuery(), nullValue());
    }
}
