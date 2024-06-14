import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import de.hybris.platform.core.model.media.MediaModel
import com.namics.distrelec.b2b.core.media.DistMediaService
import de.hybris.platform.catalog.model.CatalogVersionModel
import org.apache.commons.collections.CollectionUtils
import de.hybris.platform.commerceservices.setup.SetupSyncJobService

FlexibleSearchService flexibleSearchService = spring.getBean(FlexibleSearchService.class)
DistMediaService mediaService = spring.getBean(DistMediaService.class)
defaultCatalogVersionService = spring.getBean("defaultCatalogVersionService");


updateImageDimensions("distrelec_PLContentCatalog","Staged")
updateImageDimensions("distrelec_NLContentCatalog","Staged")
updateImageDimensions("distrelec_LVContentCatalog","Staged")
updateImageDimensions("distrelec_FIContentCatalog","Staged")
updateImageDimensions("distrelec_EEContentCatalog","Staged")
updateImageDimensions("distrelec_DEContentCatalog","Staged")
updateImageDimensions("distrelec_SEContentCatalog","Staged")
updateImageDimensions("distrelec_NOContentCatalog","Staged")
updateImageDimensions("distrelec_EXContentCatalog","Staged")
updateImageDimensions("distrelec_DKContentCatalog","Staged")
updateImageDimensions("distrelec_LTContentCatalog","Staged")
updateImageDimensions("distrelec_ITContentCatalog","Staged")
updateImageDimensions("distrelec_ATContentCatalog","Staged")
updateImageDimensions("distrelec_CHContentCatalog","Staged")
updateImageDimensions("Default","Staged")
updateImageDimensions("distrelec_SKContentCatalog","Staged")
updateImageDimensions("distrelec_ROContentCatalog","Staged")
updateImageDimensions("distrelec_HUContentCatalog","Staged")
updateImageDimensions("distrelec_CZContentCatalog","Staged")
updateImageDimensions("distrelec_TRContentCatalog","Staged")
updateImageDimensions("distrelec_BEContentCatalog","Staged")
updateImageDimensions("distrelec_IntContentCatalog","Staged")
updateImageDimensions("distrelec_FRContentCatalog","Staged")

def updateImageDimensions(String catalogId,String version){
   CatalogVersionModel cv = defaultCatalogVersionService.getCatalogVersion(catalogId,version)
   
   println("Processing Catalog "+cv.getCatalog()+" Current start time "+new org.joda.time.DateTime())
   
   final query = "select {PK} from {Media} where {MIME} like '%image/%' and {catalogVersion}='$cv.pk' and {MIME} not like '%image/svg%'"
   
   FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(query)
   
   SearchResult<MediaModel> querySearchResult = flexibleSearchService.search(query)
   
   querySearchResult.result.stream().forEach { media ->
   	 try {
   			CatalogVersionModel catalogVersion = media.catalogVersion
   				 mediaService.updateImageDimensions(media);
   			 
   		 
   	 } catch (Exception e) {
   		  println("Cannot update Image dimension for "+media.getCode()+" due to"+e.getMessage())
   	 }
   }
   
  println ("Updating Image Dimensions done for "+cv.getCatalog()+" $querySearchResult.count")
   	
   println("Finished Processing Catalog "+cv.getCatalog()+" and Current end time "+new org.joda.time.DateTime())

}