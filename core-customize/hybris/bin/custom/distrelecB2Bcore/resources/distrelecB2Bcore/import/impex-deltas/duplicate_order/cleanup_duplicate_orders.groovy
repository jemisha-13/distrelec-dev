import de.hybris.platform.core.model.order.OrderModel
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import groovy.transform.Field
import org.apache.commons.lang3.time.DurationFormatUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.Duration
import java.time.Instant

Instant start = Instant.now()

@Field Logger LOG = LoggerFactory.getLogger("FIX_DUPLICATE_ORDERS_SCRIPT")

def log(String text) {
    LOG.info(text)
    println(text)
}

String findDuplicateOrderCodesQueryText = """
    SELECT {o.code} FROM {Order AS o}
    GROUP BY {o.code}
    HAVING COUNT({o.code}) > 1
""".stripIndent()

@Field FlexibleSearchService flexibleSearchService = spring.getBean(FlexibleSearchService.class)
FlexibleSearchQuery findDuplicateOrderCodesQuery = new FlexibleSearchQuery(findDuplicateOrderCodesQueryText)
findDuplicateOrderCodesQuery.resultClassList = [String.class]
SearchResult<String> duplicateOrderCodesSearchResults = flexibleSearchService.search(findDuplicateOrderCodesQuery)

log("Found ${duplicateOrderCodesSearchResults.count} duplicated orders")

List<OrderModel> findOrdersWithCode(String code) {
    String findOrderByCodeQueryText = """
        SELECT {o.pk} FROM {Order AS o}
        WHERE {o.code} = ?code
    """.stripIndent()
    FlexibleSearchQuery findOrderByCodeQuery = new FlexibleSearchQuery(findOrderByCodeQueryText)
    findOrderByCodeQuery.addQueryParameter("code", code)
    return flexibleSearchService.search(findOrderByCodeQuery).result
}

@Field ModelService modelService = spring.getBean(ModelService.class)

duplicateOrderCodesSearchResults.result.forEach { duplicatedOrderCode ->
    List<OrderModel> ordersWithCode = findOrdersWithCode(duplicatedOrderCode)
    log("Fixing ${ordersWithCode.size() - 1} duplicated orders with code ${duplicatedOrderCode}")

    for(int i=1; i<ordersWithCode.size(); i++){
        OrderModel duplicatedOrder = ordersWithCode.get(i)
        String newOrderCode = duplicatedOrder.code + i.toString()
        duplicatedOrder.code = newOrderCode
        modelService.save(duplicatedOrder)
    }
}

Duration executionDuration = Duration.between(start, Instant.now())

return "Execution finished in ${DurationFormatUtils.formatDurationHMS(executionDuration.toMillis())}"