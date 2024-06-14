import de.hybris.platform.core.model.order.AbstractOrderModel
import de.hybris.platform.core.model.order.OrderModel
import de.hybris.platform.payment.enums.PaymentTransactionType
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import org.apache.commons.lang3.StringUtils

fQuery = new FlexibleSearchQuery("SELECT {o:pk} FROM {Order as o} WHERE {o:creationTime} > TO_DATE('2021/06/02 00:00', 'YYYY/MM/DD HH24:MI')")
fQuery.setResultClassList(Collections.singletonList(AbstractOrderModel.class))
List<OrderModel> orders = flexibleSearchService.search(fQuery).result

notProcessedOrders = new HashSet<>()
orders.each { order ->
    if (order.erpOrderCode == null) {
        notProcessedOrders.add(order)
    }
}

Set<OrderModel> notProcessedOrders = new HashSet<>()
Set<OrderModel> creditCardsOrders = new HashSet<>()
Set<OrderModel> paypalOrders = new HashSet<>()
Set<OrderModel> invoiceOrders = new HashSet<>()
orders.each { order ->
    if (order.erpOrderCode == null) {
        notProcessedOrders.add(order)
        if (order.paymentMode.code == 'CreditCard')
            order.paymentTransactions.each { transaction ->
                transaction.entries.each { entry ->
                    if (entry.type == PaymentTransactionType.SUCCESS_RESPONSE || entry.type == PaymentTransactionType.NOTIFY)
                        if (entry.transactionStatus == "OK" || entry.transactionStatus == "AUTHORIZED")
                            creditCardsOrders.add(order)
                }
            }
        if (order.paymentMode.code == 'PayPal')
            order.paymentTransactions.each { transaction ->
                transaction.entries.each { entry ->
                    if (entry.type == PaymentTransactionType.SUCCESS_RESPONSE || entry.type == PaymentTransactionType.NOTIFY)
                        if (entry.transactionStatus == "OK" || entry.transactionStatus == "AUTHORIZED")
                            paypalOrders.add(order)
                }
            }
        if (order.paymentMode.code.contains('Invoice'))
            invoiceOrders.add(order)
    }
}

println("Total number of orders exported:" + orders.size())
println("Total number of not processed orders exported:" + notProcessedOrders.size())
println("Total number of not processed credit cards orders exported:" + creditCardsOrders.size())
println("Total number of not processed paypal orders exported:" + paypalOrders.size())
println("Total number of not processed invoice orders exported:" + invoiceOrders.size())

println("#### CSV BELOW #### ")
println("orderId ; erpOpenOrderCode ; erpCustomerId; erpContactId; customerUid; paymentMode; products;")
creditCardsOrders.each { creditCardOrder ->
    printOrder(creditCardOrder, "CreditCart")
}
paypalOrders.each { paypalOrder ->
    printOrder(paypalOrder, "PayPal")
}
invoiceOrders.each { invoiceOrder ->
    printOrder(invoiceOrder, "Invoice")
}


def printOrder(OrderModel order, String paymentMode) {
    Map<String, Integer> productsWithQuantities = new HashMap<>()
    order.entries.each { entry ->
        productsWithQuantities.put(entry.product.code, entry.quantity)
    }
    String orderedProducts = StringUtils.join(productsWithQuantities, ",")
    println(order.code + " ; " + order.erpOpenOrderCode + " ; " + order.user.defaultB2BUnit.erpCustomerID + " ; " + order.user.erpContactID + " ; " + order.user.originalUid + " ; " + paymentMode + " ; " + orderedProducts)
}