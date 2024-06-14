package distrelecpatches.releases.release15_8.patch_patch_27851.global

def contentCatalogService = spring.getBean("contentCatalogService")
def setupSyncJobService = spring.getBean("b2bSetupSyncJobService")

for (contentCatalog in contentCatalogService.contentCatalogs) {
    setupSyncJobService.executeCatalogSyncJob(contentCatalog.id)
}
