import de.hybris.platform.core.Registry
import de.hybris.platform.util.Config
import groovy.transform.Field
import org.apache.commons.lang3.time.DurationFormatUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

@Field Logger LOG = LoggerFactory.getLogger("PRODUCT_ERP_TIME_RESET_SCRIPT")

@Field int batchSize = 100000

def getSelectQuery() {
    if (Config.isSQLServerUsed()) {
        return "SELECT TOP $batchSize pk FROM products WHERE p_lastmodifiederp IS NOT NULL"
    } else {
        return "SELECT pk FROM products WHERE p_lastmodifiederp IS NOT NULL ${getLimitQuery()}"
    }
}

def getLimitQuery() {
    if (Config.isHanaUsed()) {
        return "LIMIT $batchSize"
    } else {
        return "AND rownum <= $batchSize"
    }
}

@Field Connection connection = Registry.getCurrentTenant().getDataSource().getConnection()

def getTotalCount() {
    Statement statement = connection.prepareStatement("SELECT COUNT(*) FROM products WHERE p_lastmodifiederp IS NOT NULL")
    try {
        ResultSet rs = statement.executeQuery()
        rs.next()
        return rs.getInt(1)
    } finally {
        statement.close()
    }
}

@Field int updateRows = 1

def updateNextBatch() {
    String query = """UPDATE products SET p_lastmodifiederp = NULL WHERE pk IN (${getSelectQuery()})"""
    Statement statement = connection.prepareStatement(query)
    try {
        updateRows = statement.executeUpdate()
        connection.commit()
    } finally {
        statement.close()
    }
}

Instant start = Instant.now()

LOG.info("Total items found {}", getTotalCount())
@Field int totalCount = getTotalCount()
@Field int remainingCount = totalCount

if (totalCount > 0) {
    while (updateRows > 0) {
        updateNextBatch()

        remainingCount = getTotalCount()

        LOG.info("Updated rows $updateRows")

        Duration elapsedDuration = Duration.between(start, Instant.now())
        double processedPercentage = (double) (totalCount - remainingCount) / totalCount
        if (processedPercentage < 1) {
            Duration estimatedTotalDuration = Duration.of((long) ((double) elapsedDuration.toMillis() / processedPercentage), ChronoUnit.MILLIS)
            Duration remainingTime = estimatedTotalDuration.minusMillis(elapsedDuration.toMillis())

            LOG.info("Processed {}%,\telapsed: {},\tremaining: {},\testimated total: {},\tremaining rows: {}",
                    String.format("%.2f", processedPercentage * 100.0),
                    DurationFormatUtils.formatDurationHMS(elapsedDuration.toMillis()),
                    DurationFormatUtils.formatDurationHMS(remainingTime.toMillis()),
                    DurationFormatUtils.formatDurationHMS(estimatedTotalDuration.toMillis()),
                    remainingCount)
        }
    }
}

Duration elapsed = Duration.between(start, Instant.now())
LOG.info("Finished in ${DurationFormatUtils.formatDurationHMS(elapsed.toMillis())}")

return "Finished"
