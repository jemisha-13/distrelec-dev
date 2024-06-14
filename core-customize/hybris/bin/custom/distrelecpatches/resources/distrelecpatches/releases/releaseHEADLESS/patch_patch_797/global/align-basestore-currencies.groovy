package distrelecpatches.releases.releaseHEADLESS.patch_patch_797.global

/*
 * This script copies a default currency to a supported currency on all the base stores.
 */

def baseStoreService = spring.getBean("baseStoreService")
def modelService = spring.getBean("modelService")

def baseStores = baseStoreService.getAllBaseStores()

for (def baseStore in baseStores) {
    for (def cmsSite in baseStore.cmsSites) {
        if (cmsSite.defaultCurrency) {
            def defaultCurrency = cmsSite.defaultCurrency
            baseStore.defaultCurrency = defaultCurrency
            baseStore.currencies = [defaultCurrency]
            modelService.save(baseStore)

            cmsSite.registrationCurrencies = [defaultCurrency]
            modelService.save(cmsSite)
        }
    }
}
