import com.google.common.collect.Lists
import de.hybris.platform.core.Registry
import de.hybris.platform.core.TenantAwareThreadFactory
import de.hybris.platform.jalo.JaloSession
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.SearchResult
import de.hybris.platform.wishlist2.model.Wishlist2Model

import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors

final query = "Select {w:pk} FROM {Wishlist2 as w} where {w.listType} = 8796112093275"
FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(query)

SearchResult<Wishlist2Model> searchResult = flexibleSearchService.search(flexibleSearchQuery)
List<Wishlist2Model> results = searchResult.getResult()
List<List<Wishlist2Model>> split = Lists.partition(results, 100)

final tasks = []

split.each {
    def callable = new Callable() {
        @Override
        Object call() throws Exception {
            favoritesToDelete = []
            favoriteEntriesToDelete = []
            it.each {
                favoritesToDelete.add(it)
                favoriteEntriesToDelete.addAll(it.entries)
            }
            modelService.removeAll favoritesToDelete
            modelService.removeAll favoriteEntriesToDelete
        }
    }
    tasks.add(callable)
}
tenant = Registry.getCurrentTenant()
jaloSession = JaloSession.getCurrentSession()
threadFactory = new TenantAwareThreadFactory(tenant, jaloSession)

threadPool = Executors.newFixedThreadPool(8, threadFactory)

result = threadPool.invokeAll(tasks)

result.each {
    try {
        it.get()
        println("Task finished successfully")
    } catch (InterruptedException e1) {
        println("One of the tasks was cancelled")
        println e1
    } catch (ExecutionException e2) {
        println("One of the tasks threw an execution exception")
        println e2
    } catch (Exception e3) {
        println("One of the tasks threw an exception")
        println e3
    }
}