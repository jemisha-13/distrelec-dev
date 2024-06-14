package com.distrelec.solrfacetsearch.indexer.populators;

import static com.distrelec.enums.FusionMigrationAuditStatus.FAILURE;
import static com.distrelec.enums.FusionMigrationAuditStatus.SUCCESS;
import static com.distrelec.enums.FusionMigrationAuditType.ATOMIC;
import static com.distrelec.enums.FusionMigrationAuditType.BULK;
import static com.distrelec.enums.FusionMigrationAuditType.EOL;
import static com.distrelec.enums.FusionMigrationAuditType.INCREMENTAL;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.distrelec.fusion.integration.dto.MigrationStatusRequestDTO;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.solrfacetsearch.config.IndexOperation;
import de.hybris.platform.solrfacetsearch.indexer.IndexerContext;

public class MigrationStatusRequestDTOPopulator implements Populator<IndexerContext, MigrationStatusRequestDTO> {

    private static final String EOL_INDEX_NAME = "product_eol";

    private static final String COUNTRY_ALL = "ALL";

    private static final String INDEX_SUCCESS_MESSAGE = "Indexing finished successfully!";

    @Override
    public void populate(IndexerContext indexerContext, MigrationStatusRequestDTO migrationStatus) throws ConversionException {
        migrationStatus.setId(System.currentTimeMillis() / 1000);
        migrationStatus.setIndexOperationId(indexerContext.getIndexOperationId());
        migrationStatus.setName(indexerContext.getIndexedType().getCode().toLowerCase());
        migrationStatus.setType(getType(indexerContext));
        migrationStatus.setCount(indexerContext.getPks().size());
        migrationStatus.setCountry(getCountry(indexerContext));
        migrationStatus.setStart(formatDate(getStartDate(indexerContext)));
        migrationStatus.setEnd(formatDate(getEndDate(indexerContext)));
        migrationStatus.setStatus(getStatus(indexerContext));
        migrationStatus.setMessage(getMessage(indexerContext));
    }

    private String getType(IndexerContext indexerContext) {
        if (indexerContext.getIndexOperation() == IndexOperation.UPDATE
                && indexerContext.getFacetSearchConfig().getIndexConfig().isAtomicUpdate()) {
            return ATOMIC.getCode();
        }
        if (indexerContext.getIndexOperation() == IndexOperation.UPDATE) {
            return INCREMENTAL.getCode();
        }
        if (indexerContext.getIndexOperation() == IndexOperation.FULL
                && EOL_INDEX_NAME.equals(indexerContext.getIndexedType().getIndexName())) {
            return EOL.getCode();
        }
        if (indexerContext.getIndexOperation() == IndexOperation.FULL) {
            return BULK.getCode();
        }
        return null;
    }

    private String getCountry(IndexerContext indexerContext) {
        CMSSiteModel cmsSite = indexerContext.getFacetSearchConfig().getIndexConfig().getCmsSite();
        if (cmsSite == null) {
            return COUNTRY_ALL;
        }
        return cmsSite.getCountry().getIsocode();
    }

    private LocalDateTime getStartDate(IndexerContext indexerContext) {
        return indexerContext.getFacetSearchConfig().getIndexConfig().getStartTime();
    }

    private LocalDateTime getEndDate(IndexerContext indexerContext) {
        return indexerContext.getFacetSearchConfig().getIndexConfig().getEndTime();
    }

    private String formatDate(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return DateTimeFormatter.ISO_DATE_TIME.format(date);
    }

    private String getStatus(IndexerContext indexerContext) {
        return indexerContext.getStatus() == IndexerContext.Status.FAILED ? FAILURE.getCode() : SUCCESS.getCode();
    }

    private String getMessage(IndexerContext indexerContext) {
        return indexerContext.getStatus() == IndexerContext.Status.FAILED
                                                                          ? getRootCauseMessage(indexerContext.getFailureExceptions().get(0))
                                                                          : INDEX_SUCCESS_MESSAGE;
    }
}
