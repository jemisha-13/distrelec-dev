package com.namics.distrelec.b2b.core.internal.link.service;

import com.namics.distrelec.b2b.core.message.queue.model.CInternalLinkData;

import java.util.List;

public interface DistInternalLinkService {

    CInternalLinkData findInternalLink(String code, String site, String type, String language);

    List<CInternalLinkData> findInternalLinks(String code, String site, String type);

    boolean createInternalLink(CInternalLinkData iLinkData);

    void updateInternalLink(CInternalLinkData iLinkData);

    boolean lockInternalLink(CInternalLinkData iLinkData);

    boolean unlockInternalLink(CInternalLinkData iLinkData);
}
