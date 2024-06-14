import de.hybris.platform.catalog.model.CatalogVersionModel
import de.hybris.platform.cms2.model.restrictions.CMSTimeRestrictionModel
import org.apache.commons.collections.CollectionUtils

flexibleSearchService = spring.getBean("flexibleSearchService");
defaultCatalogVersionService = spring.getBean("defaultCatalogVersionService");
modelService = spring.getBean("modelService");

Collection<CatalogVersionModel> allCatalogVersions = defaultCatalogVersionService.getAllCatalogVersions();

for (cv in allCatalogVersions) {
    queryForACC = "SELECT {acc.PK} FROM {AbstractCMSComponent AS acc} WHERE {catalogVersion}='$cv.pk'";
    result = flexibleSearchService.search(queryForACC);

    for (item in result.getResult()) {

        if (item.getVisibleFromDate() != null || item.getVisibleToDate() != null) {
            CMSTimeRestrictionModel restriction = modelService.create(CMSTimeRestrictionModel.class);
            restriction.setUid("timeRestriction_for_" + item.getUid());
            restriction.setName("timeRestriction_for_" + item.getName());

            restriction.setCatalogVersion(item.getCatalogVersion());

            if (item.getVisibleFromDate() != null) {
                restriction.setActiveFrom(item.getVisibleFromDate());
            }
            if (item.getVisibleToDate() != null) {
                restriction.setActiveUntil(item.getVisibleToDate());
            }

            modelService.save(restriction);
            List<CMSTimeRestrictionModel> restrictions = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(item.getRestrictions())) {
                restrictions.addAll(item.getRestrictions());
            }
            restrictions.add(restriction);
            item.setRestrictions(restrictions);

            modelService.save(item);
        }
    }
}
