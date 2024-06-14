import de.hybris.platform.servicelayer.search.*
import de.hybris.platform.servicelayer.search.impl.*
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.jalo.flexiblesearch.*
import com.namics.hybris.toolbox.spring.SpringUtil
import de.hybris.platform.servicelayer.model.ModelService
import org.apache.commons.collections.CollectionUtils

import java.util.List
import java.util.ArrayList
import java.util.HashMap


modelService = (ModelService) SpringUtil.getBean("modelService")
flexibleSearchService = (FlexibleSearchService) SpringUtil.getBean("flexibleSearchService")

// Mapping of CMS sites UIDs with their salesOrgs
cmsSites = ["distrelec_AT" : "7320",
			"distrelec_BE" : "7800",
			"distrelec_CH" : "7310",
 			"distrelec_CZ" : "7320",
 			"distrelec_DE" : "7350",
 			"distrelec_DK" : "7680",
 			"distrelec_EE" : "7790",
 			"distrelec_EX" : "7801",
	        "distrelec_FI" : "7670",
			"distrelec_HU" : "7320",
			"distrelec_IT" : "7330",
			"distrelec_LT" : "7820",
			"distrelec_LV" : "7810",
			"distrelec_NL" : "7800",
			"distrelec_NO" : "7650",
			"distrelec_PL" : "7660",
			"distrelec_RO" : "7320",
			"distrelec_SE" : "7640",
			"distrelec_SK" : "7320"
		]

totalTime = System.currentTimeMillis()

fsq = new FlexibleSearchQuery("select count({pk}) from {Category!}")
fsq.setResultClassList(Arrays.asList(Integer.class))
totalCatCount =  (Integer) flexibleSearchService.search(fsq).getResult().get(0)

cmsSites.each { entry ->
	salesOrg = entry.value
	
	// Sales Org punchout filter query 
	salesOrgPunchoutQuery = "SELECT {dsopf.product} FROM {DistSalesOrgPunchOutFilter! AS dsopf JOIN DistSalesOrg AS so ON {so.pk}={dsopf.salesOrg}} WHERE {so.code}='${salesOrg}'"

	// Country specific punchout filter query
	countryPunchoutQuery = """SELECT {dcopf.product} FROM {DistCOPunchOutFilter! AS dcopf JOIN DistSalesOrg AS so ON {so.pk}={dcopf.salesOrg}
				JOIN CMSSite AS site ON {site.salesOrg}={so.pk}
			} WHERE {so.code}='${salesOrg}' AND {site.country}={dcopf.country} AND {site.uid}='${entry.key}'
	"""
	
	// Main query to find non empty categories
	query = """SELECT DISTINCT {ca.pk}
	  FROM {
	  	Product AS p JOIN CategoryProductRelation! AS cpr ON {p.pk}={cpr.target} 
	  		 JOIN Category! AS ca ON {ca.pk}={cpr.source}
	         JOIN DistSalesOrgProduct AS dsop ON {dsop.product} = {p.pk}
	         JOIN DistSalesStatus AS st ON {dsop.salesStatus} = {st.pk}
	         JOIN CatalogVersion AS cv ON {p.catalogVersion} = {cv.pk}
	         JOIN Catalog AS c ON {cv.catalog} = {c.pk}
	         JOIN DistSalesOrg AS dso ON {dso.pk}={dsop.salesOrg}
	    }
	  WHERE {dso.code} ='${salesOrg}' AND {st.visibleInShop} = 1 AND {c.id} = 'distrelecProductCatalog' AND {cv.version} = 'Online'
	  AND {p.pk} NOT IN ({{ ${salesOrgPunchoutQuery} }})
	  AND {p.pk} NOT IN ({{ ${countryPunchoutQuery} }})
	"""
	
	nonEmptyCategories = new HashMap<String, CategoryModel>()
	
	time = System.currentTimeMillis()
	
	try {
		// Step 1: find non empty categories.
		searchQuery = new FlexibleSearchQuery(query)
		categories = flexibleSearchService.<CategoryModel>search(searchQuery).getResult();
		if(CollectionUtils.isNotEmpty(categories)) {
			categories.each { category ->
				putIfAbsent(category, nonEmptyCategories)
			}
		}

		// TODO complete implementation by either:
		//   1) marking the non empty categories
		//   2) marking the empty categories
		// Suggestion: the option 1) is much faster than the second.

	} catch(Exception exp) {
		println "ERROR: " + exp.getMessage()
	}

	time = System.currentTimeMillis() - time
	println "salesOrg: ${entry.key} ["
	println "	Total non empty categories : " + nonEmptyCategories.size()
	println "	Total empty categories : " + (totalCatCount - nonEmptyCategories.size())
	println "	Total execution time: " + time + " ms"
	println "]"
}

totalTime = System.currentTimeMillis() - totalTime

println ""
println "Total execution time: " + totalTime +" ms"

def putIfAbsent(CategoryModel category, Map<String,CategoryModel> map) {
	if(category != null 
		&& !map.containsKey(category.getCode()) 
		&& category.getLevel() != null 
		&& category.getLevel().intValue() > 0) {

		// Since we order the list of categories by level DESC, therefore if a category is in the map
		// then its parents should also be in the map (the super tree has been processed)
		map.put(category.getCode(), category)
		if(CollectionUtils.isNotEmpty(category.getSupercategories())) {
			supCategory = category.getSupercategories().find { supCat -> supCat.getClass() ==  CategoryModel.class }
			putIfAbsent(supCategory, map)
		}
	}
}
