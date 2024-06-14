package com.namics.distrelec.b2b.core.internal.link.dao;

import com.namics.distrelec.b2b.core.model.internal.link.DistInternalLinkModel;
import com.namics.distrelec.b2b.core.model.internal.link.DistRelatedDataModel;

import java.util.Set;

public interface DistInternalLinkDao {

    DistInternalLinkModel findInternalLinkByLanguage(String code, String site, String type, String language);

    DistInternalLinkModel findInternalLink(String code, String site, String type);

    boolean createInternalLink(DistInternalLinkModel internalLink);

    void updateInternalLink(DistInternalLinkModel storedLink, Set<DistRelatedDataModel> relatedDataList, String language);

    boolean lockInternalLink(DistInternalLinkModel internalLink);

    boolean unlockInternalLink(DistInternalLinkModel internalLink);
}
