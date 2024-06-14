
/**
 * Groovy script to delete duplicate records - Distrelec-19134
 * 
 * Before Running script please take backup of existing table Refer Ticket for  steps for Execution 
 */

import com.namics.distrelec.b2b.core.model.seo.DistMetaDataModel
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import com.namics.distrelec.b2b.core.model.DistManufacturerModel
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.apache.commons.collections.CollectionUtils;
import de.hybris.platform.servicelayer.model.ModelService

	ModelService modelService = spring.getBean("modelService")

	String manufacturerList="select {PK} from {DistMetaData} where  {manufacturer} in ({{select {manufacturer} from {DistMetaData} group by p_language,p_manufacturer,p_country having count(*)=2}}) order by {manufacturer} desc";

	String duplicateRecords="select {PK} from {DistMetaData} where  {manufacturer}= (?manu) and {language} = (?lan) and {country}=(?country) order by {creationtime}";

	FlexibleSearchService flexibleSearchService = spring.getBean("flexibleSearchService")
									
	FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(manufacturerList);
  
	List<DistMetaDataModel> customers=flexibleSearchService.search(searchQuery).result as List<DistMetaDataModel>
	Set<DistMetaDataModel> itemsToBeDelted=new HashSet<DistMetaDataModel>();
	for(DistMetaDataModel customer:customers){		
	  final FlexibleSearchQuery duplicateQuery = new FlexibleSearchQuery(duplicateRecords);
	  duplicateQuery.addQueryParameter("manu", customer.getManufacturer());
	  duplicateQuery.addQueryParameter("lan", customer.getLanguage());
	  duplicateQuery.addQueryParameter("country", customer.getCountry());
	  List<DistMetaDataModel> duplicateRecordsList=flexibleSearchService.search(duplicateQuery).result as List<DistMetaDataModel>
	   if(duplicateRecordsList.size()==2){
			itemsToBeDelted.add(((DistMetaDataModel)duplicateRecordsList.get(1)).getPk());
	   }
   }
	List<DistMetaDataModel> deleteModels=new ArrayList<DistMetaDataModel>(itemsToBeDelted);
	println('toBeDeleted Size::'+deleteModels.size())
	int removedCount = 0
	
	deleteModels.forEach { dbItem ->
		modelService.remove(dbItem)
		removedCount++
	}
	
	println('Deleted List Size::'+removedCount)
   
