import com.namics.distrelec.b2b.core.service.category.impl.DefaultDistCategoryService
import de.hybris.platform.commercefacades.product.data.CategoryData
import de.hybris.platform.core.model.user.CustomerModel

import java.util.logging.Logger
import de.hybris.platform.servicelayer.config.ConfigurationService
import groovy.transform.Field
import org.apache.commons.collections4.ListUtils
import org.apache.commons.lang3.StringUtils
import com.namics.distrelec.b2b.core.model.ObsolescenceCategoryModel
import de.hybris.platform.category.model.CategoryModel
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import org.apache.commons.configuration.Configuration
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import java.util.stream.Collectors
import de.hybris.platform.servicelayer.search.SearchResult;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.interceptor.impl.InterceptorExecutionPolicy

/**
*  Script to populate all users by salesOrg, the initial Obsolescence Notifications.
*  @Author Neil Clarke.
*/

@Field
ModelService modelService = spring.getBean(ModelService.class)
@Field
FlexibleSearchService searchService = spring.getBean(FlexibleSearchService.class)
@Field
ConfigurationService configurationService = spring.getBean(ConfigurationService.class)
@Field
DefaultDistCategoryService categoryService = spring.getBean(DefaultDistCategoryService.class)
@Field
SessionService sessionService = spring.getBean(SessionService.class)
@Field
static final int CHUNKING_SIZE = 20
@Field
static final String COMMA = ","
@Field
static final Logger LOG = Logger.getLogger("17633-groovy")

//Run Script
try {
             main()
                return "FINISHED"
} catch (Exception e){
    println("Exception occurred: stacktrace :" + e.printStackTrace())
    return "ERROR"

}

/**
 *  Main Method
 */
def main(){
	LOG.info("Starting Obsolescence Cron job")
    List<CategoryModel> topCategories = getTopCategoriesFromDatabase()
    List<CategoryData> convertedTopCategories = convertTopCategories(topCategories)
    List<CategoryData> filteredTopCategories = filterUnwantedCategoryData(convertedTopCategories)
    

    final String countryCodes = "7310,7320,7330,7640,7650,7660,7670,7680,7810,7820,7790,7801,7800"
    for(String code: countryCodes.split(COMMA)){
		LOG.info("Processing countryCode"+7310)
        processUsersByCountry(code,filteredTopCategories) //, customers)
    }
	LOG.info("End Obsolescence Cron job")
}

/**
* Searches for Level 1 categories
* @return a list of top categories from the DB. These categories are the Level 1 categories found in the menu on webshops.
*/
List<CategoryModel> getTopCategoriesFromDatabase(){
    LOG.info("Getting top level categories.")
    String topLevelQuery = """SELECT {c.PK} FROM {Category! AS c 
                              JOIN DistPimCategoryType AS dpct ON {c.pimCategoryType}={dpct.PK}} 
                              WHERE {c.level} = 1 AND {dpct.visible} = 1 and {c.code} not in ('cat-DNAV_90','cat-L2D_653174','cat-L1D_379523') ORDER BY {c.level} ASC"""
    return searchService.search(topLevelQuery).result as List<CategoryModel>
}

/**
* Converts top categories
* @param topCategories
* @return a list of converted CategoryData objects..
*/
List<CategoryData> convertTopCategories(List<CategoryModel> topCategories){
    LOG.info("Converting categories")
    List<CategoryData> converted = new ArrayList<>()
    for(final CategoryModel source : topCategories) {
        final CategoryData target = new CategoryData()
        target.setCode(source.getCode())
        target.setName(source.getName())
        target.setNameEN(source.getName(Locale.ENGLISH))
        converted.add(target)
    }
    return converted
}

/**
* Filters out unwanted categories
* @param topCategories
* @return a list of filtered Categories.
*/
List<CategoryData> filterUnwantedCategoryData(List<CategoryData> topCategories){
    LOG.info("Filtering unwanted top level categories.")
    final Configuration configuration =  configurationService.getConfiguration()
    final String ignoredCategoryCodesConfig = configuration.getString("searchBar.ignoredCategoryCodes")
    if (StringUtils.isEmpty(ignoredCategoryCodesConfig)){
        LOG.warning("List of ignored categories will be ignored.")
        return topCategories
    }

    def ignoredCategories = ignoredCategoryCodesConfig.split(COMMA) as String []
    Set<String> ignoredCategoryCodes = new HashSet<>()
    ignoredCategories.each {config ->
        ignoredCategoryCodes.add(config.trim())
    }

    return topCategories.stream()
            .filter({ c -> !ignoredCategoryCodes.contains(c.getCode())})
            .collect(Collectors.toList())
}

/**
* Creates the Obsolescence Models, populates them, and saves them to the DB.
* @param obsCategoryModels
* @return a list of ObsolescenceCategoryModels
*/
List<ObsolescenceCategoryModel> createObsolescenceModels(final List<CategoryData> filteredTopCategories){
    LOG.info("Creating Obsolescence Models.")

    List<ObsolescenceCategoryModel> obsCategoryModels = new ArrayList<>(filteredTopCategories.size())

    filteredTopCategories.forEach { category ->
        final CategoryModel model = categoryService.getCategoryForCode(category.getCode())
        final ObsolescenceCategoryModel obsolescenceCategoryModel = new ObsolescenceCategoryModel()
        obsolescenceCategoryModel.setCategory(model)
        obsolescenceCategoryModel.setObsolCategorySelected(Boolean.TRUE)
        obsCategoryModels.add(obsolescenceCategoryModel)
    }

    modelService.saveAll(obsCategoryModels)
    return obsCategoryModels
}


/**
* Retrieves users by salesOrg.
* @return
*/
List<CustomerModel> getUsersByCountry(final String countryCode){
    LOG.info("Searching for users in "+countryCode)
    String usersByCountryQuery = """SELECT {user.PK} FROM { B2BCustomer as user 
                                    JOIN Company AS company ON {user.defaultB2BUnit}={company.PK} 
                                    JOIN DistSalesOrg AS salesOrganisation ON {company.salesOrg}={salesOrganisation.PK}} 
                                    WHERE {salesOrganisation.code}=?countryCode"""

    final HashMap<String, Object> queryParams = new HashMap<>()
    queryParams.put("countryCode", countryCode)
    return searchService.search(usersByCountryQuery, queryParams).result as List<CustomerModel>
}

/**
* Retrieves Contacts with Duplicate ERPContact ID.
* @return
*/
List<String>  getUsersWithDuplicateERPContactId(){
            String usersByCountryQuery = "select {PK} from {B2BCustomer as user} where {user.erpContactID} in ({{select {erpContactID} from {B2BCustomer} group by {erpContactID} having count(*) > 1}})"
            final HashMap<String, Object> queryParams = new HashMap<>()
            List<CustomerModel> customers=searchService.search(usersByCountryQuery, queryParams).result as List<CustomerModel>
            List<String> duplicateERPCustomerId=new ArrayList<String> ();
            for(CustomerModel customer:customers){
                        duplicateERPCustomerId.add(customer.getPk())
            }
            return duplicateERPCustomerId;
}


/**
* Processes users by distSalesOrg.
* @param code the
* @param obsCategoryModels
*/
def processUsersByCountry(final String distSalesOrg,List<CategoryData> filteredTopCategories){
    LOG.info("Processing users for "+distSalesOrg)
    final List<CustomerModel> customers = getUsersByCountry(distSalesOrg)
    int count = 0
            List<String> duplicateContactId=  getUsersWithDuplicateERPContactId()
    final List<List<CustomerModel>> partitionedCustomers = ListUtils.partition(customers, CHUNKING_SIZE)
            List<CustomerModel> updateCustomers=new ArrayList<CustomerModel>();
    final Map<String, Object> params = ImmutableMap.of(InterceptorExecutionPolicy.DISABLED_INTERCEPTOR_TYPES,
                ImmutableSet.of(InterceptorExecutionPolicy.InterceptorType.VALIDATE));
	sessionService.executeInLocalViewWithParams(params, new SessionExecutionBody()
				{
					@Override
					public void executeWithoutResult()
					{
				
						for(List<CustomerModel> partition: partitionedCustomers){
							List<ObsolescenceCategoryModel> obsCategoryModels = createObsolescenceModels(filteredTopCategories)
							println("Partitioned: "+partition.size()+ " "+distSalesOrg+" customers")
							partition.forEach{ customer ->
							println(" Processing Customer " +customer.getOriginalUid())
							if(((B2BCustomerModel)customer).getDefaultB2BUnit().getActive() && !duplicateContactId.contains(customer.getPk()) ){
									customer.setObsolescenceCategories(obsCategoryModels)
									customer.setOptedForObsolescence(Boolean.TRUE)
									customer.setAllObsolCatSelected(Boolean.TRUE)
									customer.setObsolescenceCategories(obsCategoryModels)
									updateCustomers.add(customer);
									count++
								}
							   
							}
							modelService.saveAll(updateCustomers)
											updateCustomers.clear();
							println(count + " customers saved for " +distSalesOrg)
						}
					}
	});
}
