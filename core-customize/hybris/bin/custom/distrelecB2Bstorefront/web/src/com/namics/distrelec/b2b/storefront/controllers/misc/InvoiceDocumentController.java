package com.namics.distrelec.b2b.storefront.controllers.misc;

import com.namics.distrelec.b2b.core.constants.DistConfigConstants.InvoiceDocumentArchive;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.HttpURLConnection;
import java.net.URL;

@Controller
@RequestMapping("/invoice-document-url")
public class InvoiceDocumentController {

    private static final Logger LOG = LoggerFactory.getLogger(InvoiceDocumentController.class);

    @Autowired
    private ConfigurationService configurationService;

    @RequestMapping(value = "/archive", method = RequestMethod.GET)
    public void getInvoice(HttpServletRequest request, HttpServletResponse response) {
        String invoiceDocumentArchiveUrl = getConfigurationService().getConfiguration().getString(
                InvoiceDocumentArchive.INVOICE_DOCUMENT_ARCHIVE_URL);

        try {
            URL url = new URL(invoiceDocumentArchiveUrl + "/archive?" + request.getQueryString());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            response.setContentType(con.getContentType());
            response.setStatus(status);
            if (status == HttpStatus.SC_OK) {
                IOUtils.copy(con.getInputStream(), response.getOutputStream());
            }
            con.disconnect();
        } catch (Exception e) {
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            LOG.error("Unable to get invoice from archive", e);
        }
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
