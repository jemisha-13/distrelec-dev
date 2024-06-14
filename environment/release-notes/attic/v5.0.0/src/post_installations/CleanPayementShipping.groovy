import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.core.model.order.CartModel;

modelService = ctx.getBean("modelService");
flexibleSearchService = ctx.getBean("flexibleSearchService")

query = "select {pk} from {cart} where {site} in ({{select {PK} from {cmssite} where {salesOrg} in ({{select {pk} from {distSalesorg} where {code} = '7320' or {code} = '7330'}})}})"

results = flexibleSearchService.<CartModel>search(query).getResult()


results.each{
  result ->
    result.setPaymentMode(null)
    result.setDeliveryAddress(null)
    result.setValidDeliveryModes(null)
    modelService.save(result)
}