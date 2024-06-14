package com.namics.distrelec.b2b.core.util;

import com.namics.distrelec.b2b.core.model.DistPromotionLabelModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.PromoLabel;

@Component("promoLabelUtil")
public class PromoLabelUtil {

    @Autowired
    private ConfigurationService configurationService;

    private static final String NEW = "new";
    private static final String TOP = "top";


    public boolean onlyContainsNewAndTopLabels(final List<DistPromotionLabelModel> labels){
        if(CollectionUtils.isEmpty(labels)){
            return false;
        }

        final String labelNew = getConfigurationService().getConfiguration().getString(PromoLabel.ATTRIBUTE_SINGLE_LABEL_TO_HIDE, NEW);
        final String labelTop = getConfigurationService().getConfiguration().getString(PromoLabel.ATTRIBUTE_SINGLE_LABEL_TO_REPLACE_THE_HIDDEN_LABEL, TOP);

        final List<String> validLabels = labels.stream().map(DistPromotionLabelModel::getCode)
                .filter(label -> label.equalsIgnoreCase(labelNew) || label.equalsIgnoreCase(labelTop))
                .collect(Collectors.toList());

        return validLabels.containsAll(Stream.of(labelNew, labelTop).collect(Collectors.toList()));
    }

    public List<DistPromotionLabelModel> hideNewToUsLabel(final List<DistPromotionLabelModel> labels){
        if(CollectionUtils.isEmpty(labels)){
            return labels;
        }

        final String labelNew = getConfigurationService().getConfiguration().getString(PromoLabel.ATTRIBUTE_SINGLE_LABEL_TO_HIDE, NEW);
        return labels.stream()
                .filter(label -> !label.getCode().equalsIgnoreCase(labelNew))
                .collect(Collectors.toList());
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
