import java.util.*;
import org.apache.commons.lang.StringUtils;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;


flexibleSearchService = ctx.getBean("flexibleSearchService");
modelService = ctx.getBean("modelService");


postFix = "deactivated_P4C"

println "postFix: ${postFix}"

salesOrg = "7310"

query = "SELECT {c.pk} FROM {B2BCustomer AS c JOIN B2BUnit AS un ON {c.defaultB2BUnit}={un.pk} JOIN DistSalesOrg AS dso ON {un.salesOrg}={dso.pk}} WHERE {dso.code}='${salesOrg}' AND {c.erpContactID} IS NULL"

fquery = new FlexibleSearchQuery(query)

searchResult = flexibleSearchService.<B2BCustomerModel>search(query)

println "count : ${searchResult.getTotalCount()}"

searchResult.getResult().each {
	it -> 
		newUid = "${it.getUid()}_${postFix}"
		println "New UID: ${newUid}"
		it.setUid(newUid)
		it.setActive(Boolean.FALSE)
		modelService.save(it)
}
