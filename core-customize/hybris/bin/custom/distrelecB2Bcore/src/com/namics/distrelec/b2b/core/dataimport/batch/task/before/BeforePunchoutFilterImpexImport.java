/*
 * Copyright 2000-2015 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.dataimport.batch.task.before;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.dataimport.batch.task.BeforePunchoutFilterImport;
import com.namics.distrelec.b2b.core.model.DistCOPunchOutFilterModel;
import com.namics.distrelec.b2b.core.model.DistCTPunchOutFilterModel;
import com.namics.distrelec.b2b.core.model.DistCUPunchOutFilterModel;
import com.namics.distrelec.b2b.core.model.DistManufacturerPunchOutFilterModel;
import com.namics.distrelec.b2b.core.model.DistPunchOutFilterModel;

import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

/**
 * BeforeEachPunchOutFilterManufacturer.
 * 
 * @author dathusir, Distrelec
 * 
 */

public class BeforePunchoutFilterImpexImport implements BeforePunchoutFilterImport {

    private static final Logger LOG = Logger.getLogger(BeforePunchoutFilterImpexImport.class);

    private FlexibleSearchService flexibleSearchService;
    private ModelService modelService;

    @Override
    public void deletePunchoutFilter(String filename) {
        removePunchoutFilter(filename);
    }

    protected void removePunchoutFilter(final String filename) {
        StringBuilder queryString = new StringBuilder();

        if (filename.equals("erp_punchoutfilt_man")) {
            queryString.append("SELECT {pof:").append(DistManufacturerPunchOutFilterModel.PK).append("} FROM {")
                    .append(DistManufacturerPunchOutFilterModel._TYPECODE).append(" AS pof}");
            final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString.toString());
            final List<DistManufacturerPunchOutFilterModel> result = getFlexibleSearchService().<DistManufacturerPunchOutFilterModel> search(query).getResult();

            LOG.info("Found " + result.size() + " PunchoutFilter to delete!");

            for (final DistPunchOutFilterModel punchoutFilter : result) {
                try {
                    getModelService().remove(punchoutFilter);
                } catch (final ModelRemovalException e) {
                    LOG.error("Can not remove punchout filter row " + punchoutFilter);
                }
            }
            queryString = null;
        } else if (filename.equals("erp_punchoutfilt_cu")) {
            queryString.append("SELECT {pof:").append(DistCUPunchOutFilterModel.PK).append("} FROM {").append(DistCUPunchOutFilterModel._TYPECODE)
                    .append(" AS pof}");
            final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString.toString());
            final List<DistCUPunchOutFilterModel> result = getFlexibleSearchService().<DistCUPunchOutFilterModel> search(query).getResult();

            LOG.info("Found " + result.size() + " PunchoutFilter to delete!");

            for (final DistPunchOutFilterModel punchoutFilter : result) {
                try {
                    getModelService().remove(punchoutFilter);
                } catch (final ModelRemovalException e) {
                    LOG.error("Can not remove punchout filter row " + punchoutFilter);
                }
            }
            queryString = null;
        } else if (filename.equals("erp_punchoutfilt_ct")) {
            queryString.append("SELECT {pof:").append(DistCTPunchOutFilterModel.PK).append("} FROM {").append(DistCTPunchOutFilterModel._TYPECODE)
                    .append(" AS pof}");
            final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString.toString());
            final List<DistCTPunchOutFilterModel> result = getFlexibleSearchService().<DistCTPunchOutFilterModel> search(query).getResult();

            LOG.info("Found " + result.size() + " PunchoutFilter to delete!");

            for (final DistPunchOutFilterModel punchoutFilter : result) {
                try {
                    getModelService().remove(punchoutFilter);
                } catch (final ModelRemovalException e) {
                    LOG.error("Can not remove punchout filter row " + punchoutFilter);
                }
            }
            queryString = null;
        } else if (filename.equals("erp_punchoutfilt_co")) {
            queryString.append("SELECT {pof:").append(DistCOPunchOutFilterModel.PK).append("} FROM {").append(DistCOPunchOutFilterModel._TYPECODE)
                    .append(" AS pof}");
            final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString.toString());
            final List<DistCOPunchOutFilterModel> result = getFlexibleSearchService().<DistCOPunchOutFilterModel> search(query).getResult();

            LOG.info("Found " + result.size() + " PunchoutFilter to delete!");

            for (final DistPunchOutFilterModel punchoutFilter : result) {
                try {
                    getModelService().remove(punchoutFilter);
                } catch (final ModelRemovalException e) {
                    LOG.error("Can not remove punchout filter row " + punchoutFilter);
                }
            }
            queryString = null;
        }

    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    @Required
    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

}
