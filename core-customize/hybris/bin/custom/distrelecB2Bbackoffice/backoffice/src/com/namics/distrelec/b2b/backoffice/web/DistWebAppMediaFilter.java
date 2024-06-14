package com.namics.distrelec.b2b.backoffice.web;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import de.hybris.platform.servicelayer.web.WebAppMediaFilter;
import org.apache.commons.validator.GenericValidator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DistWebAppMediaFilter extends WebAppMediaFilter {

    @Override
    protected Iterable<String> createLocalMediawebUrlContext(String encodedMediaCtx) {
        Preconditions.checkArgument(!GenericValidator.isBlankOrNull(encodedMediaCtx), "incorrect media context in request");
        Iterable<String> mediaContext = CTX_SPLITTER.split(decodeBase64(encodedMediaCtx));
        Preconditions.checkArgument(Iterables.size(mediaContext) == 6, "incorrect media context in request");
        List<String> mediaContextList = new ArrayList();
        Iterator var4 = mediaContext.iterator();

        while (var4.hasNext()) {
            String e = (String) var4.next();
            mediaContextList.add(e);
        }
        return mediaContextList;
    }
}
