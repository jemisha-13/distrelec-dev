package com.namics.distrelec.b2b.core.service.report.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.namics.distrelec.b2b.core.dto.CspReport;
import com.namics.distrelec.b2b.core.model.feedback.DistNetPromoterScoreModel;
import com.namics.distrelec.b2b.core.model.security.report.CSPViolationsModel;
import com.namics.distrelec.b2b.core.service.report.dao.DistCSPReportDao;
import com.namics.distrelec.b2b.core.service.report.impl.DefaultDistCSPReportService;

import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

public class DefaultDistCSPReportDao  extends DefaultGenericDao<CSPViolationsModel> implements DistCSPReportDao{
    
    private static final Logger LOG = LoggerFactory.getLogger(DefaultDistCSPReportService.class);

    private static final String QUERY_PREFIX = "SELECT {" + CSPViolationsModel.PK + "} FROM {" + CSPViolationsModel._TYPECODE + "}";
    private static final String FIND_BY_EXPORTED = QUERY_PREFIX + " WHERE {" + CSPViolationsModel.EXPORTED + "}=?" + CSPViolationsModel.EXPORTED;

    @Autowired
    private ModelService modelService;

    public DefaultDistCSPReportDao() {
        super(CSPViolationsModel._TYPECODE);
    }
    
    @Override
    public List<CSPViolationsModel> searchViolationsByCode(final String code, int page, int pageSize)
    {
        validateParameterNotNullStandardMessage("code", code);
        FlexibleSearchQuery query = new FlexibleSearchQuery("SELECT {v.pk} FROM {CSPViolations AS v } WHERE {v.code} IS LIKE ?code");
        query.addQueryParameter("term", "%" + code + "%");
        query.setStart(page * pageSize);
        query.setCount(pageSize);
        final SearchResult<CSPViolationsModel> searchResult = getFlexibleSearchService().search(query);
            
        return searchResult.getResult();
    }

    @Override
    public List<CSPViolationsModel> searchViolationsByCreationDate(final String code,  Date startDatetime ,  Date endDatetime)
    {
        validateParameterNotNullStandardMessage("code", code);
        FlexibleSearchQuery query = new FlexibleSearchQuery("SELECT {v.pk} FROM {CSPViolations AS v } WHERE {v.code} IS LIKE ?code AND "+CSPViolationsModel.CREATIONTIME);
        query.addQueryParameter("term", "%" + code + "%");
        query.addQueryParameter(CSPViolationsModel.CREATIONTIME, startDatetime);
        query.addQueryParameter(CSPViolationsModel.CREATIONTIME, endDatetime);
        final SearchResult<CSPViolationsModel> searchResult = getFlexibleSearchService().search(query);
            
        return searchResult.getResult();
    }
   
    @Override
    public CSPViolationsModel saveViolationReport(CspReport data)  {
        CSPViolationsModel violationModel = new CSPViolationsModel();

        violationModel.setCode(UUID.randomUUID().toString());
        
        violationModel.setData(data.toString());
        violationModel.setViolatedDirective(data.getViolatedDirective());
        violationModel.setBlockedUri(data.getBlockedUri());
        violationModel.setDocumentUri(data.getDocumentUri());
        violationModel.setRemoteAddress(data.getRemoteAddress());
        ObjectMapper object = new ObjectMapper();
        String jsonData;
        try {
            jsonData = object.writeValueAsString(data);
            violationModel.setData(jsonData);
        } catch (JsonProcessingException e) {
            LOG.error("Error while serializing report json:{}", e.getMessage());
        }
        
        modelService.save(violationModel);
        return violationModel;
    }
    @Override
    public List<CSPViolationsModel> findExported(final boolean exported) {
        return find(FIND_BY_EXPORTED, CSPViolationsModel.EXPORTED, Boolean.valueOf(exported));
    }
    
    private List<CSPViolationsModel> find(final String query, final String param, final Object value) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);
        if (param != null && value != null) {
            searchQuery.addQueryParameter(param, value);
        }
        return getFlexibleSearchService().<CSPViolationsModel> search(searchQuery).getResult();
    }
}
