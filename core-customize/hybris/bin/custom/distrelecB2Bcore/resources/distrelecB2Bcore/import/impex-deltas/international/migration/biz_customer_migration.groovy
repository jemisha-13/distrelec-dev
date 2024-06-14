import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import com.namics.distrelec.b2b.core.enums.DeactivationReason
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel
import com.namics.distrelec.b2b.core.service.wishlist.DistWishlistService
import de.hybris.platform.b2b.model.B2BBudgetModel
import de.hybris.platform.b2b.model.B2BCustomerModel
import de.hybris.platform.core.model.security.PrincipalGroupModel
import de.hybris.platform.core.model.user.UserGroupModel
import de.hybris.platform.servicelayer.interceptor.impl.InterceptorExecutionPolicy
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import de.hybris.platform.servicelayer.session.SessionExecutionBody
import de.hybris.platform.servicelayer.session.SessionService
import de.hybris.platform.servicelayer.user.daos.UserGroupDao
import de.hybris.platform.wishlist2.model.Wishlist2Model
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.lang.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.Duration
import java.time.Instant
import java.util.stream.Collectors

Logger LOG = LoggerFactory.getLogger("BIZ-FR MIGRATION")

StringBuilder rollbackScriptBuilder = new StringBuilder();

Instant startTime = Instant.now()

FlexibleSearchService flexibleSearchService = spring.getBean(FlexibleSearchService.class)
ModelService modelService = spring.getBean(ModelService.class)
SessionService sessionService = spring.getBean(SessionService.class)
DistWishlistService wishlistService = spring.getBean(DistWishlistService.class)

String salesOrgByCodeQueryText = "SELECT {so.pk} FROM {DistSalesOrg AS so} WHERE {so.code}=?code"

FlexibleSearchQuery frSalesOrgQuery = new FlexibleSearchQuery(salesOrgByCodeQueryText)
frSalesOrgQuery.addQueryParameter("code", "7900")

DistSalesOrgModel frSalesOrg = flexibleSearchService.searchUnique(frSalesOrgQuery)

UserGroupDao userGroupDao = spring.getBean(UserGroupDao.class)
UserGroupModel approverGroup = userGroupDao.findUserGroupByUid("b2bapprovergroup")
UserGroupModel adminGroup = userGroupDao.findUserGroupByUid("b2badmingroup")
UserGroupModel customerGroup = userGroupDao.findUserGroupByUid("b2bcustomergroup")

String customersByTypeAndSalesOrgAndCountryQueryText = "SELECT {cust.pk} FROM {B2BCustomer AS cust\n" +
        "\tJOIN CustomerType AS ct ON {cust.customerType}={ct.pk}\n" +
        "    JOIN Company AS com ON {cust.defaultB2BUnit}={com.pk}\n" +
        "    JOIN DistSalesOrg AS so ON {com.salesOrg}={so.pk}\n" +
        "    JOIN Country AS coun ON {com.country}={coun.pk}\n" +
        "    }\n" +
        "    \n" +
        "WHERE {ct.code}=?type\n" +
        "AND {so.code}=?salesOrg\n" +
        "AND {coun.isocode}=?country"

String wishlistByUserQueryText = "SELECT {wl.pk} FROM {Wishlist2 AS wl\n" +
        "JOIN User AS u ON {wl.user}={u.pk}}\n" +
        "WHERE {u.pk} = ?user"

FlexibleSearchQuery b2bCustomersFromBizOrgInFranceQuery = new FlexibleSearchQuery(customersByTypeAndSalesOrgAndCountryQueryText)
b2bCustomersFromBizOrgInFranceQuery.addQueryParameter("type", "B2B")
b2bCustomersFromBizOrgInFranceQuery.addQueryParameter("salesOrg", "7801")
b2bCustomersFromBizOrgInFranceQuery.addQueryParameter("country", "FR")

SearchResult<B2BCustomerModel> b2bCustomersFromBizOrgInFrance = flexibleSearchService.search(b2bCustomersFromBizOrgInFranceQuery)

rollbackScriptBuilder.append("UPDATE B2BUnit;uid[unique=true];salesOrg(code)\n")

b2bCustomersFromBizOrgInFrance.result.stream().map { it.getDefaultB2BUnit() }
        .distinct()
        .forEach {
            rollbackScriptBuilder.append(";${it.uid};${it.salesOrg.code}\n")
        }
rollbackScriptBuilder.append("\n")

int b2bCustomerCount = b2bCustomersFromBizOrgInFrance.count

final Map<String, Object> params = ImmutableMap.of(InterceptorExecutionPolicy.DISABLED_INTERCEPTOR_TYPES, ImmutableSet.of(InterceptorExecutionPolicy.InterceptorType.VALIDATE))

rollbackScriptBuilder.append("UPDATE B2BCustomer;uid[unique=true];groups(uid)\n")

sessionService.executeInLocalViewWithParams(params, new SessionExecutionBody() {
    void executeWithoutResult() {
        b2bCustomersFromBizOrgInFrance.result.stream()
                .forEach { customer ->
                    // Check if user is in "User Confirmation Pending"
                    if (!customer.doubleOptInActivated && StringUtils.isNotEmpty(customer.token) && CollectionUtils.isNotEmpty(customer.approvers)) {
                        // If user is in "User Confirmation Pending" remove contact
                        String logMessage = "Removed B2B user ${customer.uid} because sub account was not activated by double opt-in"
                        println(logMessage)
                        LOG.info(logMessage)
                        modelService.remove(customer)
                    } else {
                        // Set new sales org for customer and add all required usergroups
                        customer.defaultB2BUnit.salesOrg = frSalesOrg

                        String allGroups = StringUtils.join(
                                customer.groups.stream().map { it.getUid() }.collect(Collectors.toList())
                                , ",")

                        rollbackScriptBuilder.append(";${customer.uid};${allGroups}\n")

                        Set<PrincipalGroupModel> newGroups = new HashSet<>(customer.groups)
                        newGroups.add(approverGroup)
                        newGroups.add(adminGroup)
                        newGroups.add(customerGroup)
                        customer.groups = newGroups
                        customer.approvers = new HashSet<>()
                        modelService.removeAll(customer.carts)

                        FlexibleSearchQuery wishlistByUserQuery = new FlexibleSearchQuery(wishlistByUserQueryText);
                        wishlistByUserQuery.addQueryParameter("user", customer)

                        SearchResult<Wishlist2Model> wishlistByUserQuerySearchResult = flexibleSearchService.search(wishlistByUserQuery)
                        wishlistByUserQuerySearchResult.result.stream().forEach { wishlistService.deleteWishlist(it) }

                        // Set show order history of all contacts to true
                        customer.setShowAllOrderhistory(true)

                        // Remove default payment and delivery options
                        customer.setDefaultDeliveryMethod(null)
                        customer.setDefaultPaymentMethod(null)

                        // Remove order and yearly limits
                        B2BBudgetModel budget = customer.getBudget()
                        if (budget != null) {
                            modelService.remove(budget)
                            customer.setBudget(null)
                        }

                        modelService.save(customer.defaultB2BUnit)
                        modelService.save(customer)
                        String logMessage = "Migrated B2B user ${customer.uid}"
                        println(logMessage)
                        LOG.info(logMessage)
                    }
                }
    }
})

rollbackScriptBuilder.append("\n")

String customersByTypeAndSalesOrgAndCountryAndActiveFlagQueryText = "SELECT {cust.pk} FROM {B2BCustomer AS cust\n" +
        "\tJOIN CustomerType AS ct ON {cust.customerType}={ct.pk}\n" +
        "    JOIN Company AS com ON {cust.defaultB2BUnit}={com.pk}\n" +
        "    JOIN DistSalesOrg AS so ON {com.salesOrg}={so.pk}\n" +
        "    JOIN Country AS coun ON {com.country}={coun.pk}\n" +
        "    }\n" +
        "    \n" +
        "WHERE {ct.code}=?type\n" +
        "AND {so.code}=?salesOrg\n" +
        "AND {coun.isocode}=?country\n" +
        "AND ({cust.deactivationReason} IS NULL OR {cust.deactivationReason} != ?deactivationReason)"

FlexibleSearchQuery b2cCustomersFromBizOrgInFranceQuery = new FlexibleSearchQuery(customersByTypeAndSalesOrgAndCountryAndActiveFlagQueryText)
b2cCustomersFromBizOrgInFranceQuery.addQueryParameter("type", "B2C")
b2cCustomersFromBizOrgInFranceQuery.addQueryParameter("salesOrg", "7801")
b2cCustomersFromBizOrgInFranceQuery.addQueryParameter("country", "FR")
b2cCustomersFromBizOrgInFranceQuery.addQueryParameter("deactivationReason", DeactivationReason.MIGRATION)

SearchResult<B2BCustomerModel> b2cCustomersFromBizOrgInFrance = flexibleSearchService.search(b2cCustomersFromBizOrgInFranceQuery)

rollbackScriptBuilder.append("UPDATE B2BCustomer;uid[unique=true];active;loginDisabled;deactivationReason\n")

b2cCustomersFromBizOrgInFrance.result.stream().forEach {
    rollbackScriptBuilder.append(";${it.uid};${it.active};${it.loginDisabled};;\n")
}

int b2cCustomerCount = b2cCustomersFromBizOrgInFrance.count

sessionService.executeInLocalViewWithParams(params, new SessionExecutionBody() {
    void executeWithoutResult() {
        // Deactivate all B2C users
        b2cCustomersFromBizOrgInFrance.result.stream()
                .forEach { customer ->
                    customer.setActive(false)
                    customer.setLoginDisabled(true)
                    customer.setDeactivationReason(DeactivationReason.MIGRATION)
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

String logMessage = "Found $b2bCustomerCount B2B customers, $b2cCustomerCount B2C customers. Execution duration: ${durationText}"
LOG.info(logMessage)
return logMessage