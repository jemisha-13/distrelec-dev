package com.namics.distrelec.b2b.facades.report.csp.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.dto.CspReport;
import com.namics.distrelec.b2b.core.service.report.DistCSPReportService;
import com.namics.distrelec.b2b.facades.report.csp.DistCSPReportFacade;

public class DefaultDistCSPReportFacade implements DistCSPReportFacade {

    @Autowired
    private DistCSPReportService distCSPReportService;

    public void saveViolationReport(final CspReport data) {

        distCSPReportService.saveViolationReport(data);
    }
}
