package com.namics.distrelec.occ.core.v2.helper;

import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.distrelec.webservice.if12.v1.SortCriteriaType;
import com.namics.distrelec.b2b.facades.invoice.ws.dto.DistB2BInvoiceHistoryListWsDTO;
import com.namics.distrelec.occ.core.v2.dto.invoice.InvoiceHistorySearchInputWsDTO;

/**
 * Utility or helper class for invoice related.
 */
@Component
public class InvoiceHelper extends AbstractHelper {

    private static final List<String> INVOICE_HISTORY_SORT_TYPE_LIST = List.of("byDate:asc", "byDate:desc", "byDueDate:asc", "byDueDate:desc",
                                                                               "byStatus:asc", "byStatus:desc", "byTotalPrice:asc",
                                                                               "byTotalPrice:desc");

    private static final String BY_DATE = "byDate";

    private static final List<String> INVOICE_HISTORY_SORT_TYPE_LIST_MIN = List.of(BY_DATE, "byDueDate", "byStatus", "byTotalPrice");

    private static final String DESC = "DESC";

    private static final String ASC = "ASC";

    private static final String REGEX_COLON = ":";

    public void prefillDateFields(final InvoiceHistorySearchInputWsDTO invoiceHistorySearchInputWsDTO) {
        if (StringUtils.isEmpty(invoiceHistorySearchInputWsDTO.getOrderNumber()) && StringUtils.isEmpty(invoiceHistorySearchInputWsDTO.getInvoiceNumber())
                && StringUtils.isEmpty(invoiceHistorySearchInputWsDTO.getArticleNumber()) && invoiceHistorySearchInputWsDTO.getMinTotal() == null
                && invoiceHistorySearchInputWsDTO.getMaxTotal() == null && invoiceHistorySearchInputWsDTO.getFromDueDate() == null
                && invoiceHistorySearchInputWsDTO.getToDueDate() == null) {

            if (invoiceHistorySearchInputWsDTO.getFromDate() == null && invoiceHistorySearchInputWsDTO.getToDate() == null) {
                final LocalDate fromDate = LocalDate.now().minusMonths(1);
                final LocalDate toDate = LocalDate.now();
                invoiceHistorySearchInputWsDTO.setFromDate(fromDate);
                invoiceHistorySearchInputWsDTO.setToDate(toDate);
            }
        }

        if (invoiceHistorySearchInputWsDTO.getFromDate() == null && invoiceHistorySearchInputWsDTO.getToDate() != null) {
            final LocalDate date = invoiceHistorySearchInputWsDTO.getToDate().minusMonths(1);
            invoiceHistorySearchInputWsDTO.setFromDate(date);
        } else if (invoiceHistorySearchInputWsDTO.getFromDate() != null && invoiceHistorySearchInputWsDTO.getToDate() == null) {
            final LocalDate date = invoiceHistorySearchInputWsDTO.getFromDate().plusMonths(1);
            invoiceHistorySearchInputWsDTO.setToDate(date);
        }

        if (invoiceHistorySearchInputWsDTO.getFromDueDate() == null && invoiceHistorySearchInputWsDTO.getToDueDate() != null) {
            final LocalDate date = invoiceHistorySearchInputWsDTO.getToDueDate().minusMonths(1);
            invoiceHistorySearchInputWsDTO.setFromDueDate(date);
        } else if (invoiceHistorySearchInputWsDTO.getFromDueDate() != null && invoiceHistorySearchInputWsDTO.getToDueDate() == null) {
            final LocalDate date = invoiceHistorySearchInputWsDTO.getFromDueDate().plusMonths(1);
            invoiceHistorySearchInputWsDTO.setToDueDate(date);
        }
    }

    public SortCriteriaType getSortType(final String sortTypeOnForm) {
        SortCriteriaType selectedSort;
        switch (sortTypeOnForm.toLowerCase()) {
            case "bydate":
                selectedSort = SortCriteriaType.INVOICE_DATE;
                break;
            case "bystatus":
                selectedSort = SortCriteriaType.INVOICE_STATUS;
                break;
            case "bytotalprice":
                selectedSort = SortCriteriaType.INVOICE_TOTAL;
                break;
            default:
                selectedSort = SortCriteriaType.INVOICE_DATE;
                break;
        }
        return selectedSort;
    }

    public void setSortValues(InvoiceHistorySearchInputWsDTO invoiceHistorySearchInputWsDTO) {
        if (invoiceHistorySearchInputWsDTO.getSort() == null || !(INVOICE_HISTORY_SORT_TYPE_LIST.contains(invoiceHistorySearchInputWsDTO.getSort())
                || INVOICE_HISTORY_SORT_TYPE_LIST_MIN.contains(invoiceHistorySearchInputWsDTO.getSort()))) {
            invoiceHistorySearchInputWsDTO.setSort(BY_DATE);
            invoiceHistorySearchInputWsDTO.setSortType(DESC);
        } else {
            final String[] tab = invoiceHistorySearchInputWsDTO.getSort().split(REGEX_COLON);
            if (tab.length >= 1) {
                invoiceHistorySearchInputWsDTO.setSort(tab[0]);
            }
            if (tab.length >= 2) {
                if ("asc".equalsIgnoreCase(tab[1]) || "desc".equalsIgnoreCase(tab[1])) {
                    invoiceHistorySearchInputWsDTO.setSortType(tab[1].toUpperCase());
                }
            }
            if (!INVOICE_HISTORY_SORT_TYPE_LIST_MIN.contains(invoiceHistorySearchInputWsDTO.getSort())) {
                invoiceHistorySearchInputWsDTO.setSort(BY_DATE);
            }
            if (invoiceHistorySearchInputWsDTO.getSortType() == null
                    || !(ASC.equals(invoiceHistorySearchInputWsDTO.getSortType()) || DESC.equals(invoiceHistorySearchInputWsDTO.getSortType()))) {
                invoiceHistorySearchInputWsDTO.setSortType(DESC);
            }
        }
    }

    public void setArticleNumber(InvoiceHistorySearchInputWsDTO invoiceHistorySearchInputWsDTO) {
        if (invoiceHistorySearchInputWsDTO.getArticleNumber() != null) {
            final String filteredArticleNumber = invoiceHistorySearchInputWsDTO.getArticleNumber().replaceAll("-", StringUtils.EMPTY);
            invoiceHistorySearchInputWsDTO.setArticleNumber(filteredArticleNumber);
        }
    }

    public void setPageSize(InvoiceHistorySearchInputWsDTO invoiceHistorySearchInputWsDTO) {
        if (invoiceHistorySearchInputWsDTO.getPage() == null || invoiceHistorySearchInputWsDTO.getPage() < 1) {
            invoiceHistorySearchInputWsDTO.setPage(0);
        } else {
            invoiceHistorySearchInputWsDTO.setPage(invoiceHistorySearchInputWsDTO.getPage() - 1);
        }
    }

    public void populateInvoiceDates(InvoiceHistorySearchInputWsDTO searchInputWsDTO, DistB2BInvoiceHistoryListWsDTO invoiceHistoryListWsDTO) {
        invoiceHistoryListWsDTO.setInvoiceFromDate(searchInputWsDTO.getFromDate());
        invoiceHistoryListWsDTO.setInvoiceToDate(searchInputWsDTO.getToDate());
        invoiceHistoryListWsDTO.setDueFromDate(searchInputWsDTO.getFromDueDate());
        invoiceHistoryListWsDTO.setDueToDate(searchInputWsDTO.getToDueDate());
    }
}
