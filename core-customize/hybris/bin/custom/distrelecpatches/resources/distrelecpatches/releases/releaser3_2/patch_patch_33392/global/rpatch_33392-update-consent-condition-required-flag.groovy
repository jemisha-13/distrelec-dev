package distrelecpatches.releases.releaser3_2.patch_patch_33392.global

import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import de.hybris.platform.b2b.model.B2BCustomerModel
import de.hybris.platform.servicelayer.interceptor.impl.InterceptorExecutionPolicy
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.session.SessionExecutionBody
import de.hybris.platform.servicelayer.session.SessionService
import groovy.transform.Field
import org.apache.commons.collections4.ListUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Field
ModelService modelService = spring.getBean(ModelService.class)

@Field
FlexibleSearchService flexibleSearchService = spring.getBean(FlexibleSearchService.class)

@Field
SessionService sessionService = spring.getBean(SessionService.class)

@Field
static final int CHUNKING_SIZE = 20

@Field
static final Logger LOG = LoggerFactory.getLogger("rpatch_33392-update-consent-condition-required-flag.groovy")

try {
    main()
    return "FINISHED"
} catch (Exception ex) {
    LOG.error("Exception occurred during update of consent condition required flag!", ex)
    return "ERROR"
}

def main() {
    LOG.info("Start update of consent condition required flag")

    final List<B2BCustomerModel> subUsers = getSubUsersForUpdate()
    LOG.info("Prepared " + subUsers.size() + " sub users for update")
    updateCustomers(subUsers)

    final List<B2BCustomerModel> guestUsers = getGuestUsersForUpdate()
    LOG.info("Prepared " + guestUsers.size() + " guest users for update")
    updateCustomers(guestUsers)

    final List<B2BCustomerModel> rsCustomers = getRsCustomersForUpdate()
    LOG.info("Prepared " + rsCustomers.size() + " RS customers for update")
    updateCustomers(rsCustomers)

    LOG.info("End update of consent condition required flag")
}


List<B2BCustomerModel> getSubUsersForUpdate() {
    String subUserQuery = "SELECT {c.pk} FROM {B2BCustomer as c JOIN B2BCustomers2Approvers as rel ON {rel.source} = {c.pk}} WHERE ({c.consentConditionAccepted} = 0 OR {c.consentConditionAccepted} IS NULL) AND {c.consentConditionRequired} IS NULL"
    return flexibleSearchService.search(subUserQuery).result as List<B2BCustomerModel>
}

List<B2BCustomerModel> getGuestUsersForUpdate() {
    String guestQuery = "SELECT {c.pk} FROM {B2BCustomer as c} WHERE ({c.consentConditionAccepted} = 0 OR {c.consentConditionAccepted} IS NULL) AND {c.registeredAsGuest} = 1 AND {c.consentConditionRequired} IS NULL"
    return flexibleSearchService.search(guestQuery).result as List<B2BCustomerModel>
}

List<B2BCustomerModel> getRsCustomersForUpdate() {
    String rsCustomerQuery = "SELECT {c.pk} FROM {B2BCustomer as c} WHERE ({c.consentConditionAccepted} = 0 OR {c.consentConditionAccepted} IS NULL) AND {c.rsCustomer} = 1 AND {c.consentConditionRequired} IS NULL"
    return flexibleSearchService.search(rsCustomerQuery).result as List<B2BCustomerModel>
}

def updateCustomers(final List<B2BCustomerModel> customers) {
    int count = 0
    final List<List<B2BCustomerModel>> partitionedCustomers = ListUtils.partition(customers, CHUNKING_SIZE)
    List<B2BCustomerModel> updateCustomers = new ArrayList<B2BCustomerModel>()
    final Map<String, Object> params = ImmutableMap.of(InterceptorExecutionPolicy.DISABLED_INTERCEPTOR_TYPES, ImmutableSet.of(InterceptorExecutionPolicy.InterceptorType.VALIDATE))
    sessionService.executeInLocalViewWithParams(params, new SessionExecutionBody() {
        @Override
        void executeWithoutResult() {
            for (List<B2BCustomerModel> partition : partitionedCustomers) {
                println("Partitioned: " + partition.size() + " customers")
                partition.forEach { customer ->
                    println("Updating customer: " + customer.getUid())
                    customer.setConsentConditionRequired(Boolean.TRUE)
                    updateCustomers.add(customer)
                    count++
                }
                modelService.saveAll(updateCustomers)
                updateCustomers.clear()
            }
        }
    })
    LOG.info("Consent condition required flag updated for " + count + " customers")
}
