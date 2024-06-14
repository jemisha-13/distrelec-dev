/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.v2.controller;

import static com.namics.distrelec.b2b.core.util.LocalDateUtil.convertLocalDateToDate;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ForbiddenException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.namics.distrelec.b2b.core.constants.DistConfigConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistOnlineInvoiceHistoryPageableData;
import com.namics.distrelec.b2b.facades.invoice.ws.dto.DistB2BInvoiceHistoryListWsDTO;
import com.namics.distrelec.b2b.facades.invoice.ws.dto.DistB2BInvoiceHistoryWsDTO;
import com.namics.distrelec.b2b.facades.order.invoice.InvoiceHistoryFacade;
import com.namics.distrelec.b2b.facades.order.invoice.data.DistB2BInvoiceHistoriesData;
import com.namics.distrelec.b2b.facades.order.invoice.data.DistB2BInvoiceHistoryData;
import com.namics.distrelec.b2b.facades.user.DistUserFacade;
import com.namics.distrelec.occ.core.security.SecuredAccessConstants;
import com.namics.distrelec.occ.core.v2.annotations.B2ERestricted;
import com.namics.distrelec.occ.core.v2.dto.invoice.InvoiceHistorySearchInputWsDTO;
import com.namics.distrelec.occ.core.v2.enums.ShowMode;
import com.namics.distrelec.occ.core.v2.helper.InvoiceHelper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@RequestMapping(value = "/{baseSiteId}")
@Tag(name = "Invoice")
public class InvoiceController extends BaseCommerceController {
    private static final Logger LOG = LoggerFactory.getLogger(InvoiceController.class);

    private static final String BLANK_STRING = "";

    @Resource(name = "invoiceInputValidator")
    private Validator invoiceInputValidator;

    @Resource(name = "erp.invoiceHistoryFacade")
    private InvoiceHistoryFacade invoiceHistoryFacade;

    @Resource(name = "wsCustomerFacade")
    private CustomerFacade customerFacade;

    @Autowired
    private DistUserFacade userFacade;

    @Resource
    private InvoiceHelper invoiceHelper;

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @B2ERestricted
    @RequestMapping(value = "/users/{userId}/invoice-history", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(operationId = "getInvoiceHistory", summary = "Fetch Invoice history for a customer based on search criteria ", description = "Fetch Invoice history for a customer based on search criteria")
    @ApiBaseSiteIdAndUserIdParam
    @ResponseBody
    public DistB2BInvoiceHistoryListWsDTO getInvoiceHistory(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET)
    final String fields,
                                                            @Parameter(description = "Search Criteria for invoice history", required = true) @RequestBody
                                                            final InvoiceHistorySearchInputWsDTO invoiceHistorySearchInputWsDTO) {

        final List<String> orgContactList = userFacade.getMemberCustomersForB2BUnit(customerFacade.getCurrentCustomer().getUnit().getUid());
        if (null != invoiceHistorySearchInputWsDTO.getContactId()
                && !invoiceHistorySearchInputWsDTO.getContactId().isEmpty()
                && !orgContactList.contains(invoiceHistorySearchInputWsDTO.getContactId())) {
            throw new ForbiddenException("User is not allowed to access");
        }

        invoiceHelper.prefillDateFields(invoiceHistorySearchInputWsDTO);
        final Errors errors = new BeanPropertyBindingResult(invoiceHistorySearchInputWsDTO, "invoiceHistorySearchInputWsDTO");
        invoiceInputValidator.validate(invoiceHistorySearchInputWsDTO, errors);
        if (errors.hasErrors()) {
            LOG.error("{} Binding ERRORS: {}", DistConstants.ErrorSource.HYBRIS_BINDING_ERROR, errors.getFieldErrors());
            throw new WebserviceValidationException(errors);
        }
        invoiceHelper.setSortValues(invoiceHistorySearchInputWsDTO);
        invoiceHelper.setArticleNumber(invoiceHistorySearchInputWsDTO);
        invoiceHelper.setPageSize(invoiceHistorySearchInputWsDTO);

        final DistOnlineInvoiceHistoryPageableData pageableDataIF12 = createPageableDataIF12(invoiceHistorySearchInputWsDTO);
        final SearchPageData<DistB2BInvoiceHistoryData> searchPageData = invoiceHistoryFacade.getInvoiceSearchHistory(pageableDataIF12); // IF12
        final DistB2BInvoiceHistoryListWsDTO response = getDataMapper().map(createInvoiceHistoriesData(searchPageData), DistB2BInvoiceHistoryListWsDTO.class,
                                                                            fields);
        response.setInvoices(getDataMapper().mapAsList(searchPageData.getResults(), DistB2BInvoiceHistoryWsDTO.class, fields));
        invoiceHelper.populateInvoiceDates(invoiceHistorySearchInputWsDTO, response);
        return response;
    }

    @RequestMapping(value = "/invoice-document-url/archive", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(operationId = "getInvoiceDocument", summary = "Fetch Invoice document", description = "Fetch Invoice document")
    @ApiBaseSiteIdParam
    @ResponseBody
    public ResponseEntity<byte[]> getInvoiceDocument(final HttpServletRequest request) {
        final String invoiceDocumentArchiveUrl = getConfigurationService().getConfiguration().getString(
                                                                                                        DistConfigConstants.InvoiceDocumentArchive.INVOICE_DOCUMENT_ARCHIVE_URL);
        HttpURLConnection con = null;
        try {
            final String queryString = request.getQueryString();
            final URL url = new URL(invoiceDocumentArchiveUrl + "/archive?" + queryString);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            final int status = con.getResponseCode();
            if (status == HttpStatus.OK.value()) {
                final HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                final String filename = "invoiceDocument.pdf";

                final ContentDisposition.Builder disposition = ContentDisposition.builder("inline");
                disposition.filename(filename);
                headers.setContentDisposition(disposition.build());

                headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
                final ResponseEntity<byte[]> response = new ResponseEntity<>(IOUtils.toByteArray(con.getInputStream()), headers, HttpStatus.OK);
                return response;
            }

        } catch (final Exception e) {
            LOG.error("Unable to get invoice from archive", e);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }

        return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
    }

    private DistOnlineInvoiceHistoryPageableData createPageableDataIF12(final InvoiceHistorySearchInputWsDTO invoiceHistorySearchInputWsDTO) {
        final DistOnlineInvoiceHistoryPageableData pagableData = new DistOnlineInvoiceHistoryPageableData();

        final int pageSize = (ShowMode.ALL == invoiceHistorySearchInputWsDTO.getShow()) ? DEFAULT_SEARCH_MAX_SIZE
                                                                                        : invoiceHistorySearchInputWsDTO.getPageSize();
        pagableData.setPageSize(pageSize);

        final int offset = (invoiceHistorySearchInputWsDTO.getPage() * pageSize);
        pagableData.setResultOffset(offset);
        pagableData.setSalesOrganisation(invoiceHelper.getCurrentSalesOrg().getCode());
        pagableData.setCustomerID(invoiceHelper.getUser().getUnit().getErpCustomerId());
        pagableData.setSort(invoiceHistorySearchInputWsDTO.getSort());
        final boolean isAscendingSort = invoiceHistorySearchInputWsDTO.getSortType().equalsIgnoreCase("asc");
        pagableData.setSortAscending(isAscendingSort);
        pagableData.getSalesOrderNumbers().add(invoiceHistorySearchInputWsDTO.getOrderNumber());
        pagableData.getInvoiceNumbers().add(invoiceHistorySearchInputWsDTO.getInvoiceNumber());

        final String salesOrderReferenceNumber = (null == invoiceHistorySearchInputWsDTO.getOrdernf()) ? BLANK_STRING
                                                                                                       : invoiceHistorySearchInputWsDTO.getOrdernf();
        pagableData.getSalesOrderReferenceNumbers().add(salesOrderReferenceNumber);
        pagableData.getInvoicesContainingArticle().add(invoiceHistorySearchInputWsDTO.getArticleNumber());

        final String invoiceStatusType = (null == invoiceHistorySearchInputWsDTO.getStatus()) ? "00" : invoiceHistorySearchInputWsDTO.getStatus();
        pagableData.setInvoiceStatusType(invoiceStatusType);

        String contactId = null;
        if (StringUtils.isNotBlank(invoiceHistorySearchInputWsDTO.getContactId())) {
            contactId = invoiceHistorySearchInputWsDTO.getContactId();
        } else {
            if (!invoiceHelper.isCompanyInvoiceAdminUser()) {
                contactId = invoiceHelper.getUser().getContactId();
            }
        }

        pagableData.getContactPersonIDs().add(contactId);
        pagableData.setDueDateFrom(getInvoiceDate(invoiceHistorySearchInputWsDTO.getFromDueDate()));
        pagableData.setDueDateTo(getInvoiceDate(invoiceHistorySearchInputWsDTO.getToDueDate()));

        pagableData.setCurrentPage(invoiceHistorySearchInputWsDTO.getPage());
        pagableData.setSortCriteriaType(invoiceHelper.getSortType(invoiceHistorySearchInputWsDTO.getSort()));
        pagableData.setInvoiceDateFrom(getInvoiceDate(invoiceHistorySearchInputWsDTO.getFromDate()));
        pagableData.setInvoiceDateTo(getInvoiceDate(invoiceHistorySearchInputWsDTO.getToDate()));

        pagableData.setTotalAmountFrom(invoiceHistorySearchInputWsDTO.getMinTotal());
        pagableData.setTotalAmountTo(invoiceHistorySearchInputWsDTO.getMaxTotal());

        return pagableData;
    }

    private Date getInvoiceDate(final LocalDate dateToConvert) {
        return dateToConvert != null ? convertLocalDateToDate(dateToConvert) : null;
    }

    private DistB2BInvoiceHistoriesData createInvoiceHistoriesData(final SearchPageData<DistB2BInvoiceHistoryData> searchPageData) {
        final DistB2BInvoiceHistoriesData invoiceHistoriesData = new DistB2BInvoiceHistoriesData();
        invoiceHistoriesData.setSorts(searchPageData.getSorts());
        searchPageData.getPagination().setCurrentPage(searchPageData.getPagination().getCurrentPage() + 1);
        invoiceHistoriesData.setPagination(searchPageData.getPagination());
        return invoiceHistoriesData;
    }
}
