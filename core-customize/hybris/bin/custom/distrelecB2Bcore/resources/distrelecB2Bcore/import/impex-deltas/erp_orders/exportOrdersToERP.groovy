import com.google.common.collect.Iterables
import de.hybris.platform.b2b.model.B2BCustomerModel
import de.hybris.platform.basecommerce.model.site.BaseSiteModel
import de.hybris.platform.commerceservices.enums.SiteChannel
import de.hybris.platform.core.Registry
import de.hybris.platform.core.model.order.AbstractOrderModel
import de.hybris.platform.core.model.order.OrderModel
import de.hybris.platform.payment.enums.PaymentTransactionType
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.session.SessionExecutionBody
import de.hybris.platform.store.BaseStoreModel

fQuery = new FlexibleSearchQuery("SELECT {o:pk} FROM {Order as o} WHERE {o:creationTime} > TO_DATE('2021/06/02 00:00', 'YYYY/MM/DD HH24:MI')")
fQuery.setResultClassList(Collections.singletonList(AbstractOrderModel.class))
List<OrderModel> orders = flexibleSearchService.search(fQuery).result


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

creditCardsOrders.each { creditCardsOrder ->
    setBaseStoreAndExportOrder(creditCardsOrder)
}

paypalOrders.each { paypalOrder ->
    setBaseStoreAndExportOrder(paypalOrder)
}


invoiceOrders.each { invoiceOrder ->
    setBaseStoreAndExportOrder(invoiceOrder)
}

def setBaseStoreAndExportOrder(OrderModel order) {
    sessionService.executeInLocalView(
            new SessionExecutionBody()
            {
                @Override
                Object execute() {
                    BaseStoreModel baseStore = null
                    if (order.user instanceof B2BCustomerModel) {
                        order.site.stores.each { store ->
                            if (store.channel == SiteChannel.B2B)
                                baseStore = store
                        }
                    } else {
                        order.site.stores.each { store ->
                            if (store.channel == SiteChannel.B2C)
                                baseStore = store
                        }
                    }
                    BaseSiteModel baseSite = Iterables.get(baseStore.getCmsSites(), 0)
                    baseSiteService.setCurrentBaseSite(baseSite, true)
                }
            }
    )
    def checkoutService = Registry.getApplicationContext().getBean("sap.checkoutService")
    checkoutService.exportOrder(order)
}