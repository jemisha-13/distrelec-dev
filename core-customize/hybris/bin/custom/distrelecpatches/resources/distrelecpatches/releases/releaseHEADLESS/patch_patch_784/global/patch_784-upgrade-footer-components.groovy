package distrelecpatches.releases.releaseHEADLESS.patch_patch_784.global

import com.namics.distrelec.b2b.core.model.cms2.components.DistFooterComponentModel
import com.namics.distrelec.b2b.core.model.cms2.components.DistLocalCatalogFilterComponentContainerModel
import de.hybris.platform.acceleratorcms.model.components.FooterComponentModel
import de.hybris.platform.catalog.model.CatalogVersionModel
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import groovy.transform.Field

/*
 * This script copies a default currency to a supported currency on all the base stores.
 */

@Field def flexSearchService = spring.getBean("flexibleSearchService")
@Field def modelService = spring.getBean("modelService")

List<FooterComponentModel> footerComps = getFooterComponents()

for (FooterComponentModel footerComp : footerComps) {
    CatalogVersionModel catalogVersion = footerComp.catalogVersion
    if (catalogVersion != null && catalogVersion.version == "Staged") {
        def intCompContainers = getInternationComponentContainersForComponent(footerComp)
        def distFooterComp = getNonCheckoutDistFooterComponentForCatalogVersion(catalogVersion.catalog)

        for (DistLocalCatalogFilterComponentContainerModel intCompContainer : intCompContainers) {
            reassignDistFooterComponentToInternationalCompContainer(footerComp, distFooterComp, intCompContainer)
            modelService.save(intCompContainer)
        }
    }

    modelService.remove(footerComp)
}

List<FooterComponentModel> getFooterComponents() {
    String query = """select {fc.pk} from {FooterComponent as fc}"""
    return flexSearchService.search(query).result as List<FooterComponentModel>
}

DistFooterComponentModel getNonCheckoutDistFooterComponentForCatalogVersion(def catalog) {
    String query = """select {dfc.pk} from {DistFooterComponent as dfc} where {dfc.checkout}=0 and exists({{select 1 from {CatalogVersion as cv} where {dfc.catalogVersion}={cv.pk} and {cv.version}='Online' and {cv.catalog}=?catalog}})"""
    def params = [catalog: catalog]
    def searchQuery = new FlexibleSearchQuery(query, params)
    return flexSearchService.searchUnique(searchQuery)
}

List<DistLocalCatalogFilterComponentContainerModel> getInternationComponentContainersForComponent(def cmsComp) {
    String query = "select {c.pk} from {DistLocalCatalogFilterComponentContainer as c} where {c.applicableComponents} like '%" + cmsComp.pk +  "%'"
    return flexSearchService.search(query).result as List<DistLocalCatalogFilterComponentContainerModel>
}

void reassignDistFooterComponentToInternationalCompContainer(FooterComponentModel footerComp,
                                                             DistFooterComponentModel distFooterComp,
                                                             DistLocalCatalogFilterComponentContainerModel intCompContainer) {
    def applicableComps = new ArrayList<>(intCompContainer.applicableComponents)

    def footerApplicableComp = null
    def alreadyAdded = false
    for (def applicableComp : applicableComps) {
        if (applicableComp.pk == footerComp.pk) {
            footerApplicableComp = applicableComp
        }
        if (applicableComp.pk == distFooterComp.pk) {
            alreadyAdded = true
        }
    }

    applicableComps.remove(footerApplicableComp)
    if (!alreadyAdded) {
        applicableComps.add(distFooterComp)
    }
    intCompContainer.setApplicableComponents(applicableComps)
}
