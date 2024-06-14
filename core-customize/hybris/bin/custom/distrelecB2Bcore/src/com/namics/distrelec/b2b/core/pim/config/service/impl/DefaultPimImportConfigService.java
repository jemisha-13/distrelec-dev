package com.namics.distrelec.b2b.core.pim.config.service.impl;

import com.namics.distrelec.b2b.core.model.pim.config.PimImportConfigModel;
import com.namics.distrelec.b2b.core.pim.config.dao.PimImportConfigDao;
import com.namics.distrelec.b2b.core.pim.config.service.PimImportConfigService;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultPimImportConfigService implements PimImportConfigService {

    @Autowired
    private PimImportConfigDao pimImportConfigDao;

    @Override
    public PimImportConfigModel getPimImportConfig() {
        return getPimImportConfigDao().getPimImportConfig();
    }

    public PimImportConfigDao getPimImportConfigDao() {
        return pimImportConfigDao;
    }

    public void setPimImportConfigDao(final PimImportConfigDao pimImportConfigDao) {
        this.pimImportConfigDao = pimImportConfigDao;
    }
}
