import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.collections.CollectionUtils;

import de.hybris.platform.cms2.model.navigation.*;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.model.ModelService;

flexibleSearchService = ctx.getBean("flexibleSearchService");
modelService = ctx.getBean("modelService");

counter = new AtomicInteger(0);

contentCatalog = "distrelec_CHContentCatalog";

query = "select {pk} from {CMSNavigationNode} where {catalogVersion}=({{ select {pk} from {CatalogVersion} where {catalog}= ({{ select {pk} from {Catalog} where {id}='" + contentCatalog + "' }}) and {version}='Staged' }}) and {uid}='MainCategoryNavNode'";

searchQuery = new FlexibleSearchQuery(query);

try {
	mainCategoryNavNode = flexibleSearchService.<CMSNavigationNodeModel>searchUnique(searchQuery);
	if(CollectionUtils.isNotEmpty(mainCategoryNavNode.getChildren())) {
		for (child in mainCategoryNavNode.getChildren()) {
			delete(child, counter);
		}
	}
} catch(Exception exp) {
	println exp.getMessage();
}

def delete(navNode, counter) {
	println "Removing navigation node (" + counter.incrementAndGet() + "): " + navNode.getName() ;
	if(CollectionUtils.isNotEmpty(navNode.getChildren())) {
		for (child in navNode.getChildren()) {
			delete(child, counter);
		}
	}

	if(CollectionUtils.isNotEmpty(navNode.getEntries())) {
		for( entry in navNode.getEntries()) {
			println "Deleting entry --> " + entry.getName();
			modelService.remove(entry);
		}
	}

	modelService.remove(navNode);
}