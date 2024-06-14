package com.namics.distrelec.b2b.core.suggestion;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.text.NumberFormat;
import java.util.Locale;

public class DistSuggestionEnergyEfficencyPopulator<SOURCE extends ProductModel, TARGET extends SuggestionEnergyEfficencyData>
        implements Populator<SOURCE, TARGET> {

    private static final String EFFICENCY_FEATURE = "energyclasses_lov";
    private static final String POWER_FEATURE = "leistung_w";

    @Autowired
    private ClassificationService classificationService;

    @Override
    public void populate(final SOURCE source, final TARGET target) throws ConversionException {
        populateFeatures(target, source);
        if (source.getManufacturer() != null) {
            target.setManufacturer(source.getManufacturer().getName());
        }
        target.setType(source.getTypeName());
    }

    private void populateFeatures(final SuggestionEnergyEfficencyData target, final ProductModel product) {

        final FeatureList featureList = classificationService.getFeatures(product);

        if (!featureList.isEmpty()) {
            boolean efficiency_ok = false;
            boolean power_ok = false;
            for (final Feature feature : featureList) {
                if (feature.getCode().endsWith(EFFICENCY_FEATURE) && CollectionUtils.isNotEmpty(feature.getValues())) {
                    target.setEfficency((String) feature.getValues().get(0).getValue());
                    efficiency_ok = true;
                } else if (feature.getCode().endsWith(POWER_FEATURE) && CollectionUtils.isNotEmpty(feature.getValues())) {
                    NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH);
                    final String value = numberFormat.format(feature.getValues().get(0).getValue());
                    target.setPower((value != null ? value : "") + " kWh / 1000 h");
                    power_ok = true;
                }

                if (power_ok && efficiency_ok) {
                    break;
                }
            }
        }
    }

    public ClassificationService getClassificationService() {
        return classificationService;
    }

    public void setClassificationService(final ClassificationService classificationService) {
        this.classificationService = classificationService;
    }
}
