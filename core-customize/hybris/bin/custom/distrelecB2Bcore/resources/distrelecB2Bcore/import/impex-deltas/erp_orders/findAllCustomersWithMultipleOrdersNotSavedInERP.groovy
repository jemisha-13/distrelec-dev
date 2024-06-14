import de.hybris.platform.core.model.order.AbstractOrderModel
import de.hybris.platform.core.model.user.CustomerModel
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import org.apache.commons.lang3.StringUtils

fQuery = new FlexibleSearchQuery("SELECT {o:pk} FROM {Order as o} WHERE {o:creationTime} > TO_DATE('2021/06/02 00:00', 'YYYY/MM/DD HH24:MI')")
fQuery.setResultClassList(Collections.singletonList(AbstractOrderModel.class))
orders = flexibleSearchService.search(fQuery).result


notProcessedOrders = new HashSet<>()
orders.each { order ->
    if (order.erpOrderCode == null) {
        notProcessedOrders.add(order)
    }
}
Set<CustomerModel> customers = new HashSet<>()
notProcessedOrders.each { notProcessedOrders ->
    customers.add(notProcessedOrders.user)
}

List<CustomerModel> customersWithMultipleOrders = new ArrayList<>()
customers.each { customer ->
    int numberOfOrders = 0;
    customer.orders.each { order ->
        if (notProcessedOrders.contains(order))
            numberOfOrders++
    }
    if (numberOfOrders > 1) {
        customersWithMultipleOrders.add(customer);
    }
}
println("orderId ; erpOpenOrderCode ; erpCustomerId; erpContactId; customerUid; products;")
notProcessedOrders.each { order ->
    if (customersWithMultipleOrders.contains(order.user)) {
        Map<String, Integer> productsWithQuantities = new HashMap<>()
        order.entries.each { entry ->
            productsWithQuantities.put(entry.product.code, entry.quantity)
        }
        String orderedProducts = StringUtils.join(productsWithQuantities, ",")
        println(order.code + " ; " + order.erpOpenOrderCode + " ; " + order.user.defaultB2BUnit.erpCustomerID + " ; " + order.user.erpContactID + " ; " + order.user.originalUid + ";" + orderedProducts)
    }
}
