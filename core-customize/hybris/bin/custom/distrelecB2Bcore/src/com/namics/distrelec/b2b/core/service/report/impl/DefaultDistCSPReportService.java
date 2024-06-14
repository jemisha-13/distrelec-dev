package com.namics.distrelec.b2b.core.service.report.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import com.namics.distrelec.b2b.core.dto.CspReport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.distrelec.b2b.core.basesite.data.CSPData;
import com.distrelec.b2b.core.basesite.data.CSPReportData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.namics.distrelec.b2b.core.jalo.security.report.CSPViolations;
import com.namics.distrelec.b2b.core.model.feedback.DistNetPromoterScoreModel;
import com.namics.distrelec.b2b.core.model.security.report.CSPViolationsModel;
import com.namics.distrelec.b2b.core.service.report.DistCSPReportService;
import com.namics.distrelec.b2b.core.service.report.dao.DistCSPReportDao;
import com.namics.hybris.toolbox.DateTimeUtils;

import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultDistCSPReportService implements DistCSPReportService{
   

    
    @Autowired
    private DistCSPReportDao distCSPReportDao;
    
    @Override
    public List<CSPViolationsModel> searchViolationsByCode(String code, int page, int pageSize) {
        return distCSPReportDao.searchViolationsByCode(code, page, pageSize);
        
    }

    @Override
    public List<CSPViolationsModel> searchViolationsByCreationDate(String code, Date startDatetime, Date endDatetime) {
        
        return distCSPReportDao.searchViolationsByCreationDate(code, startDatetime, endDatetime);
    }
    
    @Override
    public List<CSPViolationsModel> findExported(final boolean exported) {
        return distCSPReportDao.findExported(exported);
    }

    
    @Override
    public CSPViolationsModel saveViolationReport(CspReport data)  {
        
        return distCSPReportDao.saveViolationReport(data);
    }
    
    

}
