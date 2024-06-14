package com.namics.distrelec.b2b.backoffice.cockpitng.labels;

import com.hybris.cockpitng.labels.LabelService;

public interface DistLabelService extends LabelService {

    String getObjectLabel(Object object, String config);
}
