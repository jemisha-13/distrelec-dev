/**
 *
 */
package com.namics.distrelec.occ.core.v2.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.namics.distrelec.b2b.core.constants.DistConfigConstants;
import com.namics.distrelec.b2b.core.dto.CSP;
import com.namics.distrelec.b2b.facades.report.csp.DistCSPReportFacade;
import com.namics.distrelec.occ.core.xstream.DistMediaTypes;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author abhinayjadhav
 */
@Controller
@RequestMapping(value = "/report/csp-report", consumes = {DistMediaTypes.APPLICATION_CSP_REPORT_VALUE})
@CacheControl(directive = CacheControlDirective.NO_CACHE)
public class ReportsController extends BaseCommerceController {

    private static final Logger LOG = LoggerFactory.getLogger(ReportsController.class);

    public static final String[] HEADERS_TO_TRY =
            {"X-Client-IP", "X-Forwarded-By", "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "REMOTE_ADDR",
                    "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP",
                    "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA"};

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private DistCSPReportFacade distCSPReportFacade;

    @Autowired
    private ConfigurationService configurationService;

    @RequestMapping(method = RequestMethod.POST, consumes = {DistMediaTypes.APPLICATION_CSP_REPORT_VALUE})
    @ResponseStatus(value = HttpStatus.OK)
    public void reportCSPViolation(final HttpServletRequest httpRequest, final HttpServletResponse httpResponse)
            throws Exception {
        final boolean saveCSPOriginIP = configurationService.getConfiguration()
                .getBoolean(DistConfigConstants.SecurityResponseHeaders.ATTRIBUTE_CSP_SAVE_IP);
        final boolean saveCSPReport = configurationService.getConfiguration()
                .getBoolean(DistConfigConstants.SecurityResponseHeaders.ATTRIBUTE_SAVE_REPORT_IN_DB);

        final CSP csp = mapper.readValue(httpRequest.getInputStream(), CSP.class);
        if (saveCSPReport && null != csp && null != csp.getCspReport()) {
            csp.getCspReport()
                    .setRemoteAddress(getClientIpAddress(httpRequest, saveCSPOriginIP));
            distCSPReportFacade.saveViolationReport(csp.getCspReport());
        } else if (null != csp && null != csp.getCspReport()) {
            String jsonData;
            try {
                jsonData = mapper.writeValueAsString(csp);
                LOG.warn("CSP Violation report :{}", jsonData);
            } catch (final JsonProcessingException e) {
                LOG.error("Error while serializing report json:{}", e.getMessage());
            }

        } else {
            LOG.warn("CSP report is empty.");
        }

    }

    private static String getClientIpAddress(final HttpServletRequest request, final boolean saveCSPOriginIP) {

        final StringBuilder possibleIPs = new StringBuilder();
        if (saveCSPOriginIP) {
            for (final String header : HEADERS_TO_TRY) {
                final String ip = request.getHeader(header);
                if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                    possibleIPs.append(header + " : " + ip + " | ");
                }
            }
        }
        return possibleIPs.toString();
    }

}
