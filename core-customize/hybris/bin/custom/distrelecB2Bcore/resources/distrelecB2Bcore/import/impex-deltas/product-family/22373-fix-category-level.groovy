import de.hybris.platform.category.model.CategoryModel
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import org.apache.commons.collections4.CollectionUtils
import org.apache.commons.lang3.time.DurationFormatUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.Duration
import java.time.Instant

Logger LOG = LoggerFactory.getLogger("FIX_CATEGORY_LEVEL_SCRIPT")

Instant start = Instant.now()

String categoriesWithoutLevelQueryText = """
SELECT {c.pk} FROM {Category! AS c}
    WHERE {c.level} IS NULL
""".trim()

FlexibleSearchService flexibleSearchService = spring.getBean(FlexibleSearchService.class)

SearchResult<CategoryModel> searchResult = flexibleSearchService.search(categoriesWithoutLevelQueryText)

LOG.info("Found ${searchResult.getTotalCount()}) categories that are missing level attribute")

Set<CategoryModel> affectedPLPCategories = new HashSet<>()

ModelService modelService = spring.getBean(ModelService.class)

searchResult.result.stream().forEach{ category ->
    affectedPLPCategories.addAll(category.getSupercategories())
    if(CollectionUtils.isNotEmpty(category.getSupercategories())) {
        CategoryModel superCategory = category.getSupercategories().get(0)
        category.setLevel(superCategory.getLevel() + 1)
        modelService.save(category)
    }else{
        LOG.warn("Category ${category.code} can't be fixed because it doesn't have supercategory")
        println("Category ${category.code} can't be fixed because it doesn't have supercategory")
    }
}

println("\n Affected PLP categories:")
affectedPLPCategories.forEach{category ->
    println("\t${category.code}")
    LOG.info("Affected supercategory: ${category.code}")
}

Duration elapsed = Duration.between(start, Instant.now())
String elapsedText = DurationFormatUtils.formatDurationHMS(elapsed.toMillis())

LOG.info("Executed in ${elapsedText})")

return "Executed in ${elapsedText}"