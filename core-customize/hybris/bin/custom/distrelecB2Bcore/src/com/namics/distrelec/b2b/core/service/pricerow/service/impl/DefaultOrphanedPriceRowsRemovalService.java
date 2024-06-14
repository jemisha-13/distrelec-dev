package com.namics.distrelec.b2b.core.service.pricerow.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.service.pricerow.dao.DistPriceRowDao;
import com.namics.distrelec.b2b.core.service.pricerow.service.OrphanedPriceRowsRemovalService;

import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultOrphanedPriceRowsRemovalService implements OrphanedPriceRowsRemovalService {

    private static final Logger LOG = Logger.getLogger(DefaultOrphanedPriceRowsRemovalService.class);

    private static final int ITERATION_CHUNK_SIZE = 100;

    @Autowired
    private DistPriceRowDao distPriceRowDao;

    @Autowired
    private ModelService modelService;

    @Override
    public void removeOrphanedPriceRows(int limit) {
        int iterations = (int) Math.ceil((double) limit / ITERATION_CHUNK_SIZE);
        for (int i = 0; i < iterations; i++) {
            removeOrphanedPriceRowsChunk();
        }
    }

    private void removeOrphanedPriceRowsChunk() {
        try {
            modelService.removeAll(distPriceRowDao.findOrphanedPriceRowsWithLimit(ITERATION_CHUNK_SIZE));
        } catch (Exception e) {
            LOG.error("An error occurred while removing the orphaned price rows", e);
        }
    }

}
