import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import de.hybris.platform.b2b.model.B2BCustomerModel
import de.hybris.platform.servicelayer.interceptor.impl.InterceptorExecutionPolicy
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import de.hybris.platform.servicelayer.session.SessionExecutionBody
import de.hybris.platform.servicelayer.session.SessionService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.Duration
import java.time.Instant

Logger LOG = LoggerFactory.getLogger("BIZ-GB B2C DEACTIVATION")

StringBuilder rollbackScriptBuilder = new StringBuilder();

Instant startTime = Instant.now()

FlexibleSearchService flexibleSearchService = spring.getBean(FlexibleSearchService.class)
ModelService modelService = spring.getBean(ModelService.class)

String customersByTypeAndSalesOrgAndCountryAndActiveFlagQueryText = "SELECT {cust.pk} FROM {B2BCustomer AS cust\n" +
        "\tJOIN CustomerType AS ct ON {cust.customerType}={ct.pk}\n" +
        "    JOIN Company AS com ON {cust.defaultB2BUnit}={com.pk}\n" +
        "    JOIN DistSalesOrg AS so ON {com.salesOrg}={so.pk}\n" +
        "    JOIN Country AS coun ON {com.country}={coun.pk}\n" +
        "    }\n" +
        "    \n" +
        "WHERE {ct.code}=?type\n" +
        "AND {cust.active}=?active\n " +
        "AND {cust.loginDisabled}=?loginDisabled\n " +
        "AND {so.code}=?salesOrg\n" +
        "AND {coun.isocode}=?country"

FlexibleSearchQuery b2cCustomersFromBizOrgInGbQuery = new FlexibleSearchQuery(customersByTypeAndSalesOrgAndCountryAndActiveFlagQueryText)
b2cCustomersFromBizOrgInGbQuery.addQueryParameter("type", "B2C")
b2cCustomersFromBizOrgInGbQuery.addQueryParameter("active", true)
b2cCustomersFromBizOrgInGbQuery.addQueryParameter("loginDisabled", false)
b2cCustomersFromBizOrgInGbQuery.addQueryParameter("salesOrg", "7801")
b2cCustomersFromBizOrgInGbQuery.addQueryParameter("country", "GB")

SearchResult<B2BCustomerModel> b2cCustomersFromBizOrgInFrance = flexibleSearchService.search(b2cCustomersFromBizOrgInGbQuery)

rollbackScriptBuilder.append("UPDATE B2BCustomer;uid[unique=true];active;loginDisabled;deactivationReason\n")

b2cCustomersFromBizOrgInFrance.result.stream().forEach {
    rollbackScriptBuilder.append(";${it.uid};${it.active};${it.loginDisabled};;\n")
}

int b2cCustomerCount = b2cCustomersFromBizOrgInFrance.count

SessionService sessionService = spring.getBean(SessionService.class)

final Map<String, Object> params = ImmutableMap.of(InterceptorExecutionPolicy.DISABLED_INTERCEPTOR_TYPES, ImmutableSet.of(InterceptorExecutionPolicy.InterceptorType.VALIDATE))

sessionService.executeInLocalViewWithParams(params, new SessionExecutionBody() {
    void executeWithoutResult() {
        // Deactivate all B2C users
        b2cCustomersFromBizOrgInFrance.result.stream()
                .forEach { customer ->
                    customer.setActive(false)
                    customer.setLoginDisabled(true)
                    customer.setDeactivationReason(null)
                    modelService.save(customer)
                    String logMessage = "Deactivated B2C user ${customer.uid}"
                    println(logMessage)
                    LOG.info(logMessage)
                }
    }
})

LOG.info("Rollback script:\n${rollbackScriptBuilder.toString()}")
println("\nRollback script:\n${rollbackScriptBuilder.toString()}")

Instant endTime = Instant.now()

Duration executionDuration = Duration.between(startTime, endTime)

String durationText = "${executionDuration.getSeconds()}s"

String logMessage = "Found $b2cCustomerCount B2C customers. Execution duration: ${durationText}"
LOG.info(logMessage)
return logMessage