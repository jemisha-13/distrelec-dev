package com.namics.distrelec.b2b.core.dataimport.batch.task.after;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.dataimport.batch.task.AfterImpexImport;
import com.namics.distrelec.b2b.core.model.DistCOPunchOutFilterModel;
import com.namics.distrelec.b2b.core.model.DistCTPunchOutFilterModel;
import com.namics.distrelec.b2b.core.model.DistCUPunchOutFilterModel;
import com.namics.distrelec.b2b.core.model.DistManufacturerPunchOutFilterModel;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.util.SequenceIdParser;
import de.hybris.platform.servicelayer.impex.ImportConfig;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

public class AfterPunchoutImpexImport implements AfterImpexImport {

    private final Logger LOG = Logger.getLogger(AfterPunchoutImpexImport.class);

    private FlexibleSearchService flexibleSearchService;
    private ModelService modelService;
    private SequenceIdParser sequenceIdParser;

    @Override
    public void after(final ImportConfig importConfig, final BatchHeader batchHeader, final File originalFile, final File impexFile) {
        Long sequenceId = getSequenceIdParser().getSequenceId(originalFile);
        String filename = originalFile.getName();

        // delete all existing CU punchouts with different last modified erp date
        StringBuilder queryString = new StringBuilder();
        final Map<String, Object> params = new HashMap<String, Object>();
        if (filename.contains("erp_punchoutfilt_cu")) {
            queryString.append("SELECT {pof:").append(DistCUPunchOutFilterModel.PK).append("} FROM {").append(DistCUPunchOutFilterModel._TYPECODE)
                    .append(" AS pof}");
            queryString.append(" WHERE");
            queryString.append(" {pof." + DistCUPunchOutFilterModel.SEQUENCEID + "} IS NULL");
            queryString.append(" OR {pof." + DistCUPunchOutFilterModel.SEQUENCEID + "} < (?").append(DistCUPunchOutFilterModel.SEQUENCEID)
                    .append(")");
            params.put(DistCUPunchOutFilterModel.SEQUENCEID, sequenceId);

            final SearchResult<DistCUPunchOutFilterModel> result = getFlexibleSearchService().search(queryString.toString(), params);
            final List<DistCUPunchOutFilterModel> resultList = result.getResult();
            if (resultList != null) {
                LOG.info("Found DistCUPunchOutFilterModel " + resultList.size() + " punchouts to remove!");
                for (DistCUPunchOutFilterModel punchout : resultList) {
                    // remove sap punchout
                    getModelService().remove(punchout);
                }
            }
        }
        if (filename.contains("erp_punchoutfilt_co")) {
            queryString.append("SELECT {pof:").append(DistCOPunchOutFilterModel.PK).append("} FROM {").append(DistCOPunchOutFilterModel._TYPECODE)
                    .append(" AS pof}");
            queryString.append(" WHERE");
            queryString.append(" {pof." + DistCOPunchOutFilterModel.SEQUENCEID + "} IS NULL");
            queryString.append(" OR {pof." + DistCOPunchOutFilterModel.SEQUENCEID + "} < (?").append(DistCOPunchOutFilterModel.SEQUENCEID)
                    .append(")");
            params.put(DistCOPunchOutFilterModel.SEQUENCEID, sequenceId);

            final SearchResult<DistCOPunchOutFilterModel> result = getFlexibleSearchService().search(queryString.toString(), params);
            final List<DistCOPunchOutFilterModel> resultList = result.getResult();
            if (resultList != null) {
                LOG.info("Found DistCOPunchOutFilterModel " + resultList.size() + " punchouts to remove!");
                for (DistCOPunchOutFilterModel punchout : resultList) {
                    // remove sap punchout
                    getModelService().remove(punchout);
                }
            }
        }
        if (filename.contains("erp_punchoutfilt_ct")) {
            queryString.append("SELECT {pof:").append(DistCTPunchOutFilterModel.PK).append("} FROM {").append(DistCTPunchOutFilterModel._TYPECODE)
                    .append(" AS pof}");
            queryString.append(" WHERE");
            queryString.append(" {pof." + DistCTPunchOutFilterModel.SEQUENCEID + "} IS NULL");
            queryString.append(" OR {pof." + DistCTPunchOutFilterModel.SEQUENCEID + "} < (?").append(DistCTPunchOutFilterModel.SEQUENCEID)
                    .append(")");
            params.put(DistCTPunchOutFilterModel.SEQUENCEID, sequenceId);

            final SearchResult<DistCTPunchOutFilterModel> result = getFlexibleSearchService().search(queryString.toString(), params);
            final List<DistCTPunchOutFilterModel> resultList = result.getResult();
            if (resultList != null) {
                LOG.info("Found DistCTPunchOutFilterModel " + resultList.size() + " punchouts to remove!");
                for (DistCTPunchOutFilterModel punchout : resultList) {
                    // remove sap punchout
                    getModelService().remove(punchout);
                }
            }
        }
        if (filename.contains("erp_punchoutfilt_man")) {
            queryString.append("SELECT {pof:").append(DistManufacturerPunchOutFilterModel.PK).append("} FROM {")
                    .append(DistManufacturerPunchOutFilterModel._TYPECODE).append(" AS pof}");
            queryString.append(" WHERE");
            queryString.append(" {pof." + DistManufacturerPunchOutFilterModel.SEQUENCEID + "} IS NULL");
            queryString.append(" OR {pof." + DistManufacturerPunchOutFilterModel.SEQUENCEID + "} < (?")
                    .append(DistManufacturerPunchOutFilterModel.SEQUENCEID).append(")");
            params.put(DistManufacturerPunchOutFilterModel.SEQUENCEID, sequenceId);

            final SearchResult<DistManufacturerPunchOutFilterModel> result = getFlexibleSearchService().search(queryString.toString(), params);
            final List<DistManufacturerPunchOutFilterModel> resultList = result.getResult();
            if (resultList != null) {
                LOG.info("Found DistManufacturerPunchOutFilterModel " + resultList.size() + " punchouts to remove!");
                for (DistManufacturerPunchOutFilterModel punchout : resultList) {
                    // remove sap punchout
                    getModelService().remove(punchout);
                }
            }
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
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public SequenceIdParser getSequenceIdParser() {
        return sequenceIdParser;
    }

    @Required
    public void setSequenceIdParser(SequenceIdParser sequenceIdParser) {
        this.sequenceIdParser = sequenceIdParser;
    }
}
