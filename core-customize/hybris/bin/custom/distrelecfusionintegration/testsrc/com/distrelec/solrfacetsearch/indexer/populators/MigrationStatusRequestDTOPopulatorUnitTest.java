package com.distrelec.solrfacetsearch.indexer.populators;

import static com.distrelec.enums.FusionMigrationAuditStatus.FAILURE;
import static com.distrelec.enums.FusionMigrationAuditStatus.SUCCESS;
import static com.distrelec.enums.FusionMigrationAuditType.ATOMIC;
import static com.distrelec.enums.FusionMigrationAuditType.BULK;
import static com.distrelec.enums.FusionMigrationAuditType.EOL;
import static com.distrelec.enums.FusionMigrationAuditType.INCREMENTAL;
import static de.hybris.platform.solrfacetsearch.config.IndexOperation.FULL;
import static de.hybris.platform.solrfacetsearch.config.IndexOperation.UPDATE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.distrelec.fusion.integration.dto.MigrationStatusRequestDTO;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.indexer.IndexerContext;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MigrationStatusRequestDTOPopulatorUnitTest {

    private static final String PRODUCT_INDEX_TYPE_CODE = "Product";

    private static final long INDEX_OPERATION_ID = 111005724545785054L;

    private static final String EOL_INDEX_NAME = "product_eol";

    private static final String COUNTRY_ALL = "ALL";

    private static final String COUNTRY_SE = "SE";

    private static final String INDEX_SUCCESS_MESSAGE = "Indexing finished successfully!";

    private static final String ROOT_EXCEPTION_MESSAGE = "Root exception message";

    private static final String EXPECTED_EXCEPTION_MESSAGE = "NullPointerException: " + ROOT_EXCEPTION_MESSAGE;

    @InjectMocks
    private MigrationStatusRequestDTOPopulator migrationStatusRequestDTOPopulator;

    @Mock
    private IndexerContext indexerContext;

    @Mock
    private FacetSearchConfig facetSearchConfig;

    @Mock
    private IndexConfig indexConfig;

    @Mock
    private CMSSiteModel cmsSite;

    @Mock
    private CountryModel country;

    @Mock
    private IndexedType productIndexedType;

    @Mock
    private IndexedType productEOLIndexedType;

    private MigrationStatusRequestDTO migrationStatusDTO;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Exception exception;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);

        migrationStatusDTO = new MigrationStatusRequestDTO();

        startTime = LocalDateTime.now().minusHours(2);
        endTime = LocalDateTime.now();
        exception = new NullPointerException(ROOT_EXCEPTION_MESSAGE);

        when(facetSearchConfig.getIndexConfig()).thenReturn(indexConfig);
        when(indexerContext.getFacetSearchConfig()).thenReturn(facetSearchConfig);

        when(indexerContext.getIndexOperationId()).thenReturn(INDEX_OPERATION_ID);
        when(productIndexedType.getCode()).thenReturn(PRODUCT_INDEX_TYPE_CODE);
        when(productEOLIndexedType.getCode()).thenReturn(PRODUCT_INDEX_TYPE_CODE);
        when(productEOLIndexedType.getIndexName()).thenReturn(EOL_INDEX_NAME);
        when(indexerContext.getIndexedType()).thenReturn(productIndexedType);
        when(indexerContext.getIndexOperation()).thenReturn(FULL);
        when(indexerContext.getPks()).thenReturn(List.of());
        when(cmsSite.getCountry()).thenReturn(country);
        when(country.getIsocode()).thenReturn(COUNTRY_SE);
        when(indexConfig.getCmsSite()).thenReturn(cmsSite);
        when(indexConfig.getStartTime()).thenReturn(startTime);
        when(indexConfig.getEndTime()).thenReturn(endTime);
        when(indexerContext.getStatus()).thenReturn(IndexerContext.Status.COMPLETED);
    }

    @Test
    public void testPopulate() {
        migrationStatusRequestDTOPopulator.populate(indexerContext, migrationStatusDTO);

        assertThat(migrationStatusDTO.getIndexOperationId(), is(INDEX_OPERATION_ID));
        assertThat(migrationStatusDTO.getName(), is(PRODUCT_INDEX_TYPE_CODE.toLowerCase()));
        assertThat(migrationStatusDTO.getType(), is(BULK.getCode()));
        assertThat(migrationStatusDTO.getCount(), is(0));
        assertThat(migrationStatusDTO.getCountry(), is(COUNTRY_SE));
        assertThat(migrationStatusDTO.getStart(), is(formatDate(startTime)));
        assertThat(migrationStatusDTO.getEnd(), is(formatDate(endTime)));
        assertThat(migrationStatusDTO.getStatus(), is(SUCCESS.getCode()));
        assertThat(migrationStatusDTO.getMessage(), is(INDEX_SUCCESS_MESSAGE));
    }

    @Test
    public void testPopulateAtomic() {
        when(indexerContext.getIndexOperation()).thenReturn(UPDATE);
        when(indexConfig.isAtomicUpdate()).thenReturn(Boolean.TRUE);

        migrationStatusRequestDTOPopulator.populate(indexerContext, migrationStatusDTO);

        assertThat(migrationStatusDTO.getType(), is(ATOMIC.getCode()));
    }

    @Test
    public void testPopulateEOL() {
        when(indexerContext.getIndexOperation()).thenReturn(FULL);
        when(indexerContext.getIndexedType()).thenReturn(productEOLIndexedType);

        migrationStatusRequestDTOPopulator.populate(indexerContext, migrationStatusDTO);

        assertThat(migrationStatusDTO.getType(), is(EOL.getCode()));
    }

    @Test
    public void testPopulateUpdate() {
        when(indexerContext.getIndexOperation()).thenReturn(UPDATE);

        migrationStatusRequestDTOPopulator.populate(indexerContext, migrationStatusDTO);

        assertThat(migrationStatusDTO.getType(), is(INCREMENTAL.getCode()));
    }

    @Test
    public void testPopulateUpdateNotSiteSpecific() {
        when(indexConfig.getCmsSite()).thenReturn(null);

        migrationStatusRequestDTOPopulator.populate(indexerContext, migrationStatusDTO);

        assertThat(migrationStatusDTO.getCountry(), is(COUNTRY_ALL));
    }

    @Test
    public void testPopulateFailed() {
        when(indexConfig.getEndTime()).thenReturn(null);
        when(indexerContext.getStatus()).thenReturn(IndexerContext.Status.FAILED);
        when(indexerContext.getFailureExceptions()).thenReturn(List.of(exception));

        migrationStatusRequestDTOPopulator.populate(indexerContext, migrationStatusDTO);

        assertThat(migrationStatusDTO.getStart(), is(formatDate(startTime)));
        assertThat(migrationStatusDTO.getEnd(), nullValue());
        assertThat(migrationStatusDTO.getStatus(), is(FAILURE.getCode()));
        assertThat(migrationStatusDTO.getMessage(), is(EXPECTED_EXCEPTION_MESSAGE));
    }

    private String formatDate(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return DateTimeFormatter.ISO_DATE_TIME.format(date);
    }
}
