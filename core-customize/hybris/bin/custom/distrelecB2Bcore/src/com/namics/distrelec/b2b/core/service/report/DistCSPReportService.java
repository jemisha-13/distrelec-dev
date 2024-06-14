package com.namics.distrelec.b2b.core.service.report;

import java.util.Date;
import java.util.List;

import com.distrelec.b2b.core.basesite.data.CSPReportData;
import com.namics.distrelec.b2b.core.dto.CspReport;
import com.namics.distrelec.b2b.core.jalo.security.report.CSPViolations;
import com.namics.distrelec.b2b.core.model.security.report.CSPViolationsModel;

public interface DistCSPReportService {

    public List<CSPViolationsModel> searchViolationsByCode(final String code, int page, int pageSize);
    public List<CSPViolationsModel> searchViolationsByCreationDate(final String code,  Date startDatetime ,  Date endDatetime);
    public CSPViolationsModel saveViolationReport(CspReport data);
    public List<CSPViolationsModel> findExported(final boolean exported);
    
}
