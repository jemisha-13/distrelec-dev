package com.namics.distrelec.b2b.facades.basesites;

import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.facades.basesites.seo.DistLink;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface DistrelecOCCBaseSiteFacade {


    <T> DistLink getCanonicalLink(final String path);

    <T> DistLink getCanonicalLink(final String baseSiteId, final T model, final DistUrlResolver<T> urlResolver);

     List<DistLink> setupBaseSiteLinks();

     List<DistLink> setupBaseSiteLinksForLang(final String lang);

    <T> List<DistLink> setupAlternateHreflangLinks(final T model, final DistUrlResolver<T> urlResolver);

    <T> List<DistLink> setupAlternateHreflangLinksForLang(final String lang, final T model, final DistUrlResolver<T> urlResolver);

}
