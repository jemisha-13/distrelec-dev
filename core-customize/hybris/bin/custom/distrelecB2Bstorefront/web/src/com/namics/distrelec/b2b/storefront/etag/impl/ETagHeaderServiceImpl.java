package com.namics.distrelec.b2b.storefront.etag.impl;

import com.namics.distrelec.b2b.storefront.etag.ETagHeaderService;
import de.hybris.platform.store.services.BaseStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public class ETagHeaderServiceImpl implements ETagHeaderService {

    @Autowired
    private BaseStoreService baseStoreService;

    @Override
    public void setCacheControlHeader(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Vary", "Content-Language, Distrelec-Channel");
    }

}
