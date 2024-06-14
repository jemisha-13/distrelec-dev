import de.hybris.platform.core.Registry
import groovy.transform.Field
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.Connection
import java.sql.PreparedStatement

@Field Logger LOG = LoggerFactory.getLogger("Media_Cleanup")

@Field int cleanedRecords = 0

@Field Connection connection = Registry.getCurrentTenant().getDataSource().getConnection()

def replaceLeadingSpacesWithZeroOnDuplicatedMedias() {
    LOG.info("Replace leading spaces")

    String replaceLeadingSpacesQuery = "UPDATE medias m SET code=TRIM(m.code) || '_' " +
            "WHERE m.code<>TRIM(m.code) " +
            "  AND EXISTS (" +
            "    SELECT * " +
            "    FROM medias m2 " +
            "    WHERE m2.code=TRIM(m.code) AND m2.p_catalogversion=m.p_catalogversion" +
            "  )"

    PreparedStatement replaceLeadingSpacesStatement = connection.prepareStatement(replaceLeadingSpacesQuery);
    try {
        cleanedRecords += replaceLeadingSpacesStatement.executeUpdate()
        connection.commit()
    } finally {
        replaceLeadingSpacesStatement.close()
    }
}

def trimAllMediaCodes() {
    LOG.info("Trim all media codes")

    String trimMediaCodesQuery = "UPDATE medias SET code=TRIM(code) WHERE code<>TRIM(code)"

    PreparedStatement trimMediaCodesStatement = connection.prepareStatement(trimMediaCodesQuery)
    try {
        cleanedRecords += trimMediaCodesStatement.executeUpdate()
        connection.commit()
    } finally {
        trimMediaCodesStatement.close()
    }
}

def clearHybrisCache() {
    LOG.info("Clearing cache")
    Registry.getCurrentTenant().getCache().clear();
    LOG.info("Cache was cleared")
}

replaceLeadingSpacesWithZeroOnDuplicatedMedias()
trimAllMediaCodes()
clearHybrisCache()

return "Finished. Total cleaned rows: " + cleanedRecords
