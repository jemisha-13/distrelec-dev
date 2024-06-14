package distrelecpatches.releases.releaser3_2.patch_patch_32448.global

import de.hybris.platform.core.Registry
import de.hybris.platform.core.TenantAwareThreadFactory
import de.hybris.platform.persistence.property.JDBCValueMappings
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import de.hybris.platform.servicelayer.search.TranslationResult
import groovy.transform.Field
import org.apache.commons.collections4.ListUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.Duration
import java.time.Instant
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ThreadFactory
import java.util.stream.Collectors

@Field Logger LOG = LoggerFactory.getLogger("EOL Product cleanup")

@Field ApplicationContext applicationContext = (ApplicationContext) spring
@Field FlexibleSearchService flexibleSearchService = applicationContext.getBean(FlexibleSearchService.class)

@Field int batchSize = 1000
@Field Connection connection = Registry.currentTenant.getDataSource().getConnection()
int threadCount = 4

long getProductWithMissingTimestampCount() {
    String queryText = """
        select count({p.pk})
        from {DistSalesOrgProduct as p}
        where {p.endOfLifeDate} is null
              and {p.salesStatus} in (
                   {{
                     select {s.pk}
                     from {DistSalesStatus as s}
                     where {s.endOfLifeInShop} = 1
                   }}
                  )
    """

    FlexibleSearchQuery query = new FlexibleSearchQuery(queryText)
    query.setResultClassList(List.of(Long.class))

    SearchResult<Long> searchResult = flexibleSearchService.search(query)
    return searchResult.result.get(0)
}

List<Long> getProductBatchPks() {
    String queryText = """
        select {p.pk}
        from {DistSalesOrgProduct as p}
        where {p.endOfLifeDate} is null
              and {p.salesStatus} in (
                   {{
                     select {s.pk}
                     from {DistSalesStatus as s}
                     where {s.endOfLifeInShop} = 1
                   }}
                  )
    """

    FlexibleSearchQuery query = new FlexibleSearchQuery(queryText)
    query.setDisableCaching(true)
    TranslationResult translationResult = flexibleSearchService.translate(query)

    PreparedStatement preparedStatement
    ResultSet resultSet
    try {
        preparedStatement = connection.prepareStatement(translationResult.SQLQuery)
        JDBCValueMappings.getInstance().fillStatement(preparedStatement, translationResult.SQLQueryParameters)
        preparedStatement.setMaxRows(batchSize)
        resultSet = preparedStatement.executeQuery()

        List<Long> pkList = new ArrayList<>(batchSize)
        while (resultSet.next()) {
            Long pk = resultSet.getLong(1)
            pkList.add(pk)
        }

        connection.commit()
        return pkList
    } finally {
        if (resultSet != null) {
            resultSet.close()
        }
        if (preparedStatement != null) {
            preparedStatement.close()
        }
    }
}

static String formatDuration(Duration duration) {
    return "${String.format("%02d", duration.toMinutes())}:${String.format("%02d", duration.toSecondsPart())}"
}

long totalProducts = getProductWithMissingTimestampCount()
LOG.info("Found ${totalProducts} products that need to be updated")
long processedItems = 0L
Instant startTime = Instant.now()
boolean running = true
ThreadFactory tenantAwareThreadFactory = new TenantAwareThreadFactory(Registry.getCurrentTenant())
Executor dbUpdateExecutor = Executors.newFixedThreadPool(threadCount, tenantAwareThreadFactory)
Executor executor = Executors.newSingleThreadExecutor()

executor.submit {
    while (running) {
        Thread.sleep(1000)
        double percentage = (double) processedItems / totalProducts
        Duration duration = Duration.between(startTime, Instant.now())

        String eta = "N/A"
        if (processedItems != 0) {
            double timePerItem = (double) duration.toMillis() / processedItems
            long etaMillis = (long) (timePerItem * (totalProducts - processedItems))
            Duration etaDuration = Duration.ofMillis(etaMillis)
            eta = formatDuration(etaDuration)
        }

        LOG.info("Processed ${String.format("%.2f", percentage * 100)}%. $processedItems out of $totalProducts. Elapsed: ${formatDuration(duration)}. ETA: $eta")
    }
}

try {
    List<Long> productBatchPks = getProductBatchPks()
    while (!productBatchPks.isEmpty()) {
        int subListSize = (int) (batchSize / threadCount)
        List<List<Long>> batchSubLists = ListUtils.partition(productBatchPks, subListSize)

        List<Future> futures = batchSubLists.stream().map { pkList ->
            return dbUpdateExecutor.submit {
                StringBuilder updateQueryText = new StringBuilder("""
                    update distsalesorgproduct
                    set p_endoflifedate = case
                            when p_lastmodifiederp is not null then p_lastmodifiederp
                            when modifiedts is not null then modifiedts
                            else null
                        end
                    where pk in (
                 """)

                PreparedStatement preparedStatement
                try {
                    updateQueryText.append(StringUtils.repeat("?", ",", pkList.size()))
                    updateQueryText.append(")")
                    preparedStatement = connection.prepareStatement(updateQueryText.toString())
                    JDBCValueMappings.getInstance().fillStatement(preparedStatement, pkList)
                    int updatedItems = preparedStatement.executeUpdate()

                    if (updatedItems != pkList.size()) {
                        throw new RuntimeException("Only $updatedItems were updated in batch of ${pkList.size()}")
                    }

                    connection.commit()
                    processedItems += pkList.size()
                } catch (Exception e) {
                    LOG.warn("Failed to update products with PKs ${pkList}. Error: ${e.getMessage()}. ${e.class}")
                    e.printStackTrace()
                    throw e
                } finally {
                    if (preparedStatement != null) {
                        preparedStatement.close()
                    }
                }
            }
        }.collect(Collectors.toList())
        futures.stream().forEach { it.get() }

        productBatchPks = getProductBatchPks()
    }
} catch (Exception e) {
    LOG.error("Script failed with error: ${e.message}")
    throw e
} finally {
    running = false
    connection.close()
    dbUpdateExecutor.shutdown()
    executor.shutdown()

    Duration totalDuration = Duration.between(startTime, Instant.now())
    LOG.info("DONE in ${formatDuration(totalDuration)}")
}

return totalProducts