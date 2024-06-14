package com.distrelec.solrfacetsearch.indexer.populators;

import static java.lang.Boolean.FALSE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.distrelec.fusion.integration.dto.FusionDocumentRequestDTO;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class FusionDocumentRequestDTOPopulatorUnitTest {

    private static final String FIELD_1_NAME = "Field1";

    private static final String FIELD_2_NAME = "Field2";

    private static final String FIELD_3_NAME = "Field3";

    private static final String FIELD_1_VALUE = "Value1";

    private static final String FIELD_2_VALUE = "Value2";

    private static final String FIELD_3_VALUE = "Value3";

    @InjectMocks
    private FusionDocumentRequestDTOPopulator fusionDocumentRequestDTOPopulator;

    @Mock
    private SolrInputDocument solrInputDocument;

    @Mock
    private SolrInputField fieldOne;

    @Mock
    private SolrInputField fieldTwo;

    @Mock
    private SolrInputField fieldThree;

    private FusionDocumentRequestDTO fusionDocumentRequestDTO;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);

        fusionDocumentRequestDTO = new FusionDocumentRequestDTO();

        when(fieldOne.getValue()).thenReturn(FIELD_1_VALUE);
        when(fieldTwo.getValue()).thenReturn(FIELD_2_VALUE);
        when(fieldThree.getValue()).thenReturn(FIELD_3_VALUE);
        when(solrInputDocument.getFieldNames()).thenReturn(List.of(FIELD_1_NAME, FIELD_2_NAME, FIELD_3_NAME));
        when(solrInputDocument.getField(FIELD_1_NAME)).thenReturn(fieldOne);
        when(solrInputDocument.getField(FIELD_2_NAME)).thenReturn(fieldTwo);
        when(solrInputDocument.getField(FIELD_3_NAME)).thenReturn(fieldThree);
    }

    @Test
    public void testPopulate() {
        fusionDocumentRequestDTOPopulator.populate(solrInputDocument, fusionDocumentRequestDTO);

        assertThat(fusionDocumentRequestDTO.getFields(), notNullValue());
        assertThat(fusionDocumentRequestDTO.getFields().size(), is(3));
        assertThat(fusionDocumentRequestDTO.getFields().get(FIELD_1_NAME), is(FIELD_1_VALUE));
        assertThat(fusionDocumentRequestDTO.getFields().get(FIELD_2_NAME), is(FIELD_2_VALUE));
        assertThat(fusionDocumentRequestDTO.getFields().get(FIELD_3_NAME), is(FIELD_3_VALUE));
    }

    @Test
    public void testPopulateEmptyValue() {
        when(fieldThree.getValue()).thenReturn(null);

        fusionDocumentRequestDTOPopulator.populate(solrInputDocument, fusionDocumentRequestDTO);

        assertThat(fusionDocumentRequestDTO.getFields(), notNullValue());
        assertThat(fusionDocumentRequestDTO.getFields().size(), is(2));
        assertThat(fusionDocumentRequestDTO.getFields().get(FIELD_1_NAME), is(FIELD_1_VALUE));
        assertThat(fusionDocumentRequestDTO.getFields().get(FIELD_2_NAME), is(FIELD_2_VALUE));
        assertThat(fusionDocumentRequestDTO.getFields().containsKey(FIELD_3_NAME), is(FALSE));
    }

}
