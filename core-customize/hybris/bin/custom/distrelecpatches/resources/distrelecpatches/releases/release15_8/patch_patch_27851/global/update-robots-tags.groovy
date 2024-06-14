package distrelecpatches.releases.release15_8.patch_patch_27851.global

import de.hybris.platform.cms2.enums.CmsRobotTag
import de.hybris.platform.cms2.model.pages.CategoryPageModel
import de.hybris.platform.cms2.model.pages.ContentPageModel
import de.hybris.platform.cms2.model.pages.ProductPageModel
import com.namics.distrelec.b2b.core.model.cms.ProductFamilyPageModel
import com.namics.distrelec.b2b.core.model.pages.DistManufacturerPageModel

flexibleSearchService = spring.getBean("flexibleSearchService");
modelService = spring.getBean("modelService");

queryForACC = "SELECT {cp.PK} FROM {AbstractPage AS cp} WHERE {robotTag} is null" +
        " AND EXISTS ({{SELECT 1 FROM {CatalogVersion AS cv} WHERE {cv.pk}={cp.catalogVersion} AND {cv.version}='Staged'}})";
result = flexibleSearchService.search(queryForACC);

for (item in result.getResult()) {
    if (item instanceof CategoryPageModel
            || item instanceof ContentPageModel
            || item instanceof DistManufacturerPageModel
            || item instanceof ProductFamilyPageModel
            || item instanceof ProductPageModel) {
        item.setRobotTag(CmsRobotTag.INDEX_FOLLOW);
    } else {
        item.setRobotTag(CmsRobotTag.NOINDEX_FOLLOW)
    }
    modelService.save(item);
}
