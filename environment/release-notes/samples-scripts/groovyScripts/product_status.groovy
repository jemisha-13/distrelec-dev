import java.util.*
import de.hybris.platform.servicelayer.search.*
import de.hybris.platform.servicelayer.search.impl.*
import de.hybris.platform.b2b.model.*
import de.hybris.platform.core.*
import de.hybris.platform.product.*
import de.hybris.platform.jalo.*
import de.hybris.platform.jalo.user.*
import de.hybris.platform.jalo.flexiblesearch.*
import com.namics.hybris.toolbox.spring.SpringUtil
import de.hybris.platform.servicelayer.model.ModelService
import org.apache.commons.lang.StringUtils
import com.namics.distrelec.b2b.core.jalo.*



if (!Registry.hasCurrentTenant()) {
    Registry.activateMasterTenant()
}

JaloSession.getCurrentSession().setUser(UserManager.getInstance().getUserByLogin("admin"))
modelService = (ModelService) SpringUtil.getBean("modelService")
flexibleSearchService = (FlexibleSearchService) SpringUtil.getBean("flexibleSearchService")
productService = (ProductService) SpringUtil.getBean("productService")

// change salesOrg value to the target
String salesOrg = 7310
// Article-number comma seperated list for example '14022002','11045604'
productCode = ['14022002','11045604']
/** 
manufacturer code one at a time for example 'man_mc' 
  doesn't suport comma seperated list for now
 if you set manufacturer code then productCodes list will be discarded
**/
String manufCode = ''

today = new Date()


query = """ select {p.code}, {so.code}, {dss.code}, {dss.buyableInShop}, {dss.visibleInShop}, {dss.nameErp[en]} from 
                    {
                      Product as p join DistSalesOrgProduct as dsop on {dsop.product} = {p.pk} join 
                      DistSalesOrg as so on {dsop.salesOrg} = {so.pk} join 
                      DistSalesStatus as dss on {dss.pk} = {dsop.salesStatus}
                    } where {so.code} = ?scode
                      and {p.code} in (?pcode)"""


 inc_manuf_query = """ select {p.code}, {so.code}, {dss.code}, {dss.buyableInShop}, {dss.visibleInShop}, {dss.nameErp[en]} from 
                    {
                      Product as p join DistSalesOrgProduct as dsop on {dsop.product} = {p.pk} join 
                      DistSalesOrg as so on {dsop.salesOrg} = {so.pk} join 
                      DistSalesStatus as dss on {dss.pk} = {dsop.salesStatus} join
                      DistManufacturer as dmf on {dmf.pk} = {p.manufacturer}
                    } where {so.code} = ?scode
                      and {dmf.code} = ?manufCode"""

 distProductPunchOutFilter_query = """ select {pk}  from {DistProductPunchOutFilter}  
                                        where {product}  = ?product and {validUntilDate} >= ?validUntilDate"""


 findPricerow_query = """ select * from {distpricerow} where {product} = ?product 
                          and {endtime} >= ?endtime
                          and {ug} in ({{select {pk}  from {UserPriceGroup} where {name}  like '%$salesOrg%'}})"""

println "Article-number;manufacturer;salesStatus;pimId;sales-status-visible;sales-status-buyable;has_valid_price;has_punchout"


articleNumber=''
manufacturer=''
salesStatus=''
pimid ='' 
salesStatus_isVisiable=false
salesStatus_isBuyable=false
hasValidPrice=false
hasPunchOut=false

q = new FlexibleSearchQuery(query)
	
	if(StringUtils.isNotBlank(manufCode)){ // if manufCode is set switch to inc_manuf_query
		q = new FlexibleSearchQuery(inc_manuf_query)
		q.addQueryParameter('manufCode', manufCode)
	}else{
		q.addQueryParameter('pcode', productCode)	
	}
 
 q.addQueryParameter('scode', salesOrg)
 q.setResultClassList(Arrays.asList(String.class, String.class, String.class, String.class, String.class, String.class))
 try
 {
   final SearchResult<List<String>> sr = flexibleSearchService.search(q)
   sr.getResult().each
   {
   
        articleNumber = it.get(0)
        productModel = productService.getProductForCode(articleNumber)
        manufacturer = productModel.getManufacturer()?.getCode()
        salesStatus = it.get(2)
        pimid=productModel.getPimId()
        salesStatus_isVisiable = it.get(4) ? true : false
        salesStatus_isBuyable = it.get(3)  ? true : false

       distProductPunchOutFilter_flexibleSearchQuery = new FlexibleSearchQuery(distProductPunchOutFilter_query)
       distProductPunchOutFilter_flexibleSearchQuery.addQueryParameter('product', articleNumber)
       distProductPunchOutFilter_flexibleSearchQuery.addQueryParameter('validUntilDate', today)

        try
        {
            hasPunchOut = flexibleSearchService.search(distProductPunchOutFilter_flexibleSearchQuery).getResult().size()!=0
        }catch(Exception ex){print ex}


        findPricerow_flexibleSearchQuery = new FlexibleSearchQuery(findPricerow_query)
        findPricerow_flexibleSearchQuery.addQueryParameter('product', productModel)
        findPricerow_flexibleSearchQuery.addQueryParameter('endtime', today)

        try
        {
            hasValidPrice = flexibleSearchService.search(findPricerow_flexibleSearchQuery).getResult().size()>0
        }catch(Exception ex){print ex}

        println "${articleNumber};${manufacturer};${salesStatus};${pimid};${salesStatus_isVisiable};${salesStatus_isBuyable};${hasValidPrice};${hasPunchOut}"
	  }
  }catch(Exception ex){print ex}
