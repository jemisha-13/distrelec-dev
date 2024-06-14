package distrelecpatches.releases.releaser3_2.patch_patch_32448.global

import de.hybris.platform.core.Registry
import groovy.transform.Field
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Field Logger LOG = LoggerFactory.getLogger("EOL PRODUCTS CLEANUP")

@Field Connection connection = null

@Field Instant startTime = Instant.now();

@Field batchSize = 1000
// If set to -1 all batches will be executed
@Field final int maxBatches = -1

try {
    connection = Registry.getCurrentTenant().getDataSource().getConnection()

    long productCount = findProductCount()

    LOG.info("Found {} products for removal", productCount)

    def count = 0;
    def processedProducts = 0;
    List<Long> pks = null;

    do {
        pks = getProductPkBatch();
        if(!pks.isEmpty()) {
            long deletedProductFeatures = deleteProductFeatures(pks)
            long deletedPrices = deletePrices(pks)
            long deletedStocks = deleteStocks(pks)
            long deletedSalesOrgProducts = deleteEOLSalesOrgProducts(pks)
            long deletedProductLocalizations = deleteEOLProductsLocalizations(pks)
            long deletedProducts = deleteEOLProducts(pks)

            LOG.info("Deleted {} product features", deletedProductFeatures)
            LOG.info("Deleted {} prices", deletedPrices)
            LOG.info("Deleted {} stocks", deletedStocks)
            LOG.info("Deleted {} salesOrgProducts", deletedSalesOrgProducts)
            LOG.info("Deleted {} product localizations", deletedProductLocalizations)
            LOG.info("Deleted {} products", deletedProducts)

            processedProducts += deletedProducts
            printStatistics(productCount, processedProducts, startTime)
            connection.commit()
            count++
        }
    } while (!pks.isEmpty() && (count < maxBatches || maxBatches < 0))

    Duration duration = Duration.between(startTime, Instant.now())
    LOG.info("Finished in {}", formatDuration(duration))
} catch (Exception e) {
    LOG.error("EOL cleanup failed. Error {}, {}", e.class.name, e.message, e);
} finally {
    if (connection != null) {
        connection.close()
    }
}

def printStatistics(long totalCount, long processedCount, Instant startTime) {
    double percentage = ((double) processedCount / totalCount) * 100.00
    Duration totalDuration = Duration.between(startTime, Instant.now())
    double msPerProduct = (double) totalDuration.toMillis() / processedCount
    long etaMs = (long) (msPerProduct * (totalCount - processedCount))
    Duration etaDuration = Duration.ofMillis(etaMs)
    LOG.info("Processed {}%, deleted {} out of {} products, Total duration {} ETA: {}",
            String.format("%.2f", percentage),
            processedCount,
            totalCount,
            formatDuration(totalDuration),
            formatDuration(etaDuration))
}

static String formatDuration(Duration duration) {
    return String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart())
}

long findProductCount() {
    LOG.info("Calculating total product count")
    String query = """
    select count(distinct P_PRODUCT) from DISTSALESORGPRODUCT where pk in (
        select dsp.pk from DISTSALESORGPRODUCT dsp
    where (exists(select 1
                  from DISTCODELIST ss
                  where ss.PK = dsp.P_SALESSTATUS
                    and ss.P_ENDOFLIFEINSHOP = 1)
               and ((exists(select 1
                            from PRODUCTREFERENCES pr
                            where pr.P_SOURCE = dsp.P_PRODUCT)
            and dsp.P_ENDOFLIFEDATE < ?))
        or ((not exists(select 1
                        from PRODUCTREFERENCES pr
                        where pr.P_SOURCE = dsp.P_PRODUCT))
            and dsp.P_ENDOFLIFEDATE < ?))
    )
    """
    PreparedStatement statement = connection.prepareStatement(query)
    statement.setTimestamp(1, Timestamp.from(getPastDate(18)))
    statement.setTimestamp(2, Timestamp.from(getPastDate(12)))

    ResultSet rs = statement.executeQuery();
    rs.next()
    Long count = rs.getLong(1)
    rs.close()
    statement.close()
    connection.commit()
    return count
}

List<Long> getProductPkBatch() {
    LOG.info("Getting ${batchSize} product pks")
    String query = """
    select distinct P_PRODUCT from DISTSALESORGPRODUCT where pk in (
        select dsp.pk from DISTSALESORGPRODUCT dsp
    where (exists(select 1
                  from DISTCODELIST ss
                  where ss.PK = dsp.P_SALESSTATUS
                    and ss.P_ENDOFLIFEINSHOP = 1)
               and ((exists(select 1
                            from PRODUCTREFERENCES pr
                            where pr.P_SOURCE = dsp.P_PRODUCT)
            and dsp.P_ENDOFLIFEDATE < ?))
        or ((not exists(select 1
                        from PRODUCTREFERENCES pr
                        where pr.P_SOURCE = dsp.P_PRODUCT))
            and dsp.P_ENDOFLIFEDATE < ?))
    )    
        order by P_PRODUCT
        offset 0 rows
        fetch first $batchSize rows only
    """
    PreparedStatement statement = connection.prepareStatement(query)
    statement.setTimestamp(1, Timestamp.from(getPastDate(18)))
    statement.setTimestamp(2, Timestamp.from(getPastDate(12)))

    ResultSet rs = statement.executeQuery();
    List<Long> pks = new ArrayList<>()
    while (rs.next()) {
        Long pk = rs.getLong(1)
        pks.add(pk)
    }
    rs.close()
    statement.close()
    connection.commit()
    return pks
}

static String buildPksInClause(List<Long> pks) {
    return "(" + StringUtils.join(pks, ",") + ")";
}

static Instant getPastDate(final int months) {
    final LocalDate localDate = LocalDate.now().minusMonths(months).minusDays(1L)
    final ZoneId defaultZoneId = ZoneId.systemDefault()
    return localDate.atStartOfDay(defaultZoneId).toInstant()
}

long deleteEOLSalesOrgProducts(List<Long> pks) {
    LOG.info("Removing sales org products")
    String query = "delete from DISTSALESORGPRODUCT where P_PRODUCT in ${buildPksInClause(pks)}"
    PreparedStatement statement = connection.prepareStatement(query)
    int removedItemsCount = statement.executeUpdate()
    statement.close()
    return removedItemsCount
}

long deleteEOLProducts(List<Long> pks) {
    LOG.info("Removing products")
    String query = "delete from PRODUCTS where pk in ${buildPksInClause(pks)}"
    PreparedStatement statement = connection.prepareStatement(query)
    int removedItemsCount = statement.executeUpdate()
    statement.close()
    return removedItemsCount
}

long deleteEOLProductsLocalizations(List<Long> pks) {
    LOG.info("Removing product localizations")
    String locQuery = "delete from PRODUCTSLP where itempk in ${buildPksInClause(pks)}"

    PreparedStatement statement = connection.prepareStatement(locQuery)
    int removedItemsCount = statement.executeUpdate()
    statement.close()
    return removedItemsCount
}

long deleteStocks(List<Long> pks) {
    LOG.info("Removing stocks")
    String query = """    
    delete from STOCKLEVELS where pk in (
    select sl.pk from STOCKLEVELS sl join PRODUCTS p on sl.P_PRODUCTCODE=p.CODE
                     where p.pk in ${buildPksInClause(pks)}
                     )
    """
    PreparedStatement statement = connection.prepareStatement(query)
    int removedItemsCount = statement.executeUpdate()
    statement.close()
    return removedItemsCount
}

long deletePrices(List<Long> pks) {
    LOG.info("Removing prices")
    String query = "delete from PRICEROWS where P_PRODUCT in ${buildPksInClause(pks)}"
    PreparedStatement statement = connection.prepareStatement(query)
    int removedItemsCount = statement.executeUpdate()
    statement.close()
    return removedItemsCount
}

def deleteProductFeatures(List<Long> pks) {
    LOG.info("Removing product features")
    String query = "delete from PRODUCTFEATURES where P_PRODUCT in ${buildPksInClause(pks)}"
    PreparedStatement statement = connection.prepareStatement(query)
    int removedItemsCount = statement.executeUpdate()
    statement.close()
    return removedItemsCount
}
