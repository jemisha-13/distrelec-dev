package com.namics.distrelec.occ.core.v2.helper.quality;

import static com.namics.distrelec.occ.core.v2.util.DistCustomerAddressUtil.getCustomerAddress;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.occ.core.qualityandlegal.ws.dto.QualityAndLegalDownloadWsDTO;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.servicelayer.i18n.I18NService;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

@Component
public class DistQualityAndLegalPdfExportHelper extends DistQualityAndLegalBaseHelper {

    private static final String JASPER_REPORT_TEMPLATE_PATH = "WEB-INF/quality-and-legal-report-templates/qualityAndLegalTemplate_EN.jrxml";

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private I18NService i18nService;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    @Qualifier("b2bCustomerFacade")
    private DistCustomerFacade b2bCustomerFacade;

    private ClassLoader classLoader;

    public void getPDFStreamForQualityAndLegalReport(QualityAndLegalDownloadWsDTO qualityAndLegalDownloadWsDTO, OutputStream output) throws IOException,
                                                                                                                                     JRException {
        List<ProductData> b2bProducts = new ArrayList<>(getProductsForReport(qualityAndLegalDownloadWsDTO.getProductCodes()));
        java.net.URL imageUrl = getClassLoader().getResource("../../media/distrelec_logo_ql_xlsx_export.png");

        JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(b2bProducts);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("CollectionBeanParam", itemsJRBean);
        parameters.put("logoUrl", imageUrl.toString());
        addCustomerParams(parameters, qualityAndLegalDownloadWsDTO);
        addLabels(parameters);

        InputStream input = getInputStreamForPath();
        JasperDesign jasperDesign = JRXmlLoader.load(input);
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
        JasperExportManager.exportReportToPdfStream(jasperPrint, output);
    }

    private InputStream getInputStreamForPath() throws IOException {
        Resource resource = resourceLoader.getResource(JASPER_REPORT_TEMPLATE_PATH);
        return resource.getInputStream();
    }

    private void addCustomerParams(final Map<String, Object> parameters, QualityAndLegalDownloadWsDTO qualityAndLegalDownloadWsDTO) {
        CustomerData b2bCustomer = b2bCustomerFacade.getCurrentCustomer();
        parameters.put("customerName", getCustomerName(b2bCustomer, qualityAndLegalDownloadWsDTO.getCustomerName()));
        parameters.put("customerNumber", b2bCustomer.getUnit().getErpCustomerId());
        parameters.put("customerAddress", getCustomerAddress(b2bCustomer));
        parameters.put("customerEmail", b2bCustomer.getEmail());
    }

    private void addLabels(final Map<String, Object> parameters) {
        String title = messageSource.getMessage("qualityAndLegal.excel.export.customerDetails.title", null, "Customer Details", i18nService.getCurrentLocale());
        String customerName = messageSource.getMessage("qualityAndLegal.excel.export.customerDetails.customerName", null, "Customer Name:",
                                                       i18nService.getCurrentLocale());
        String customerNumber = messageSource.getMessage("qualityAndLegal.excel.export.customerDetails.customerNumber", null, "Customer Number:",
                                                         i18nService.getCurrentLocale());
        String customerAddress = messageSource.getMessage("qualityAndLegal.excel.export.customerDetails.customerAddress", null, "Customer Address:",
                                                          i18nService.getCurrentLocale());
        String customerEmail = messageSource.getMessage("qualityAndLegal.excel.export.customerDetails.customerEmailAddress", null, "Customer E-Mail Address:",
                                                        i18nService.getCurrentLocale());
        String date = messageSource.getMessage("qualityAndLegal.excel.export.reportDate.title", null, "Report Date:", i18nService.getCurrentLocale());
        String header = messageSource.getMessage("qualityAndLegal.excel.export.header.title", null,
                                                 "Distrelec Article Environmental Compliance Report \n" +
                                                                                                    "RoHS 2011/65/EU (RoHS 2015/863/EU) | REACH 1907/2006/EC |\"SCIP\" EU Waste Framework Directive (EU)2018/851",
                                                 i18nService.getCurrentLocale());

        String disclaimer = messageSource.getMessage("qualityAndLegal.excel.export.disclaimer.text",
                                                     null,
                                                     "The above information we provide to you is based solely on the information presented to us, under good faith that our suppliers act within their legal requirements. \n"
                                                           +
                                                           "Distrelec is therefore in no way responsible for any damages or penalties suffered by you as a result of using or relying upon such information.\n"
                                                           +
                                                           "This document was electronically created and is valid without official signature.",
                                                     i18nService.getCurrentLocale());

        parameters.put("titleLabel", title);
        parameters.put("customerNameLabel", customerName);
        parameters.put("customerNumberLabel", customerNumber);
        parameters.put("customerAddressLabel", customerAddress);
        parameters.put("customerEmailLabel", customerEmail);
        parameters.put("dateLabel", date);
        parameters.put("headerLabel", header);
        parameters.put("disclaimerLabel", disclaimer);
    }

    public ClassLoader getClassLoader() {
        if (classLoader == null) {
            return this.getClass().getClassLoader();
        }

        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
