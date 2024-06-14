package distrelecpatches.releases.releaseHEADLESS.patch_patch_2426.global


import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.store.BaseStoreModel
import de.hybris.platform.store.services.BaseStoreService


BaseStoreService baseStoreService = spring.getBean('baseStoreService');
ModelService modelService = spring.getBean('modelService');
List<BaseStoreModel> baseStores = baseStoreService.getAllBaseStores();
for (baseStore in baseStores) {
    BaseStoreModel baseStoreModel = (BaseStoreModel)baseStore;
    if(baseStoreModel.getDeliveryCountries() != null) {
        baseStoreModel.setBillingCountries(baseStoreModel.getDeliveryCountries());
        modelService.save(baseStoreModel);
    }
}
