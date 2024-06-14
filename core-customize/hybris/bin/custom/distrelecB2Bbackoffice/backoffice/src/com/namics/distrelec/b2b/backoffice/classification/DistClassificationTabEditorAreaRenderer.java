package com.namics.distrelec.b2b.backoffice.classification;

import java.util.Map;

import org.apache.commons.collections.MapUtils;

import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.platformbackoffice.classification.ClassificationTabEditorAreaRenderer;
import de.hybris.platform.servicelayer.config.ConfigurationService;

public class DistClassificationTabEditorAreaRenderer extends ClassificationTabEditorAreaRenderer {

    private ConfigurationService configurationService;

    @Override
    protected Editor createEditor(Feature feature, WidgetInstanceManager widgetInstanceManager, boolean canWriteFeature) {
        boolean isFeatureReadOnly = getConfigurationService().getConfiguration().getBoolean("backoffice.productfeature.readonly", false);
        if (isFeatureReadOnly) {
            return super.createEditor(feature, widgetInstanceManager, false);
        }
        return super.createEditor(feature, widgetInstanceManager, canWriteFeature);
    }

    @Override
    protected void saveFeatures(ProductModel productModel, Map<String, Feature> modifiedProductFeatures) {
        if (MapUtils.isEmpty(modifiedProductFeatures)) {
            return;
        }
        super.saveFeatures(productModel, modifiedProductFeatures);
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
