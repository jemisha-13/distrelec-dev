/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import com.namics.distrelec.b2b.storefront.controllers.pages.AbstractSearchPageController.ShowMode;

/**
 * Form Object for the order approval Dashboard
 * 
 * @author <a href="mailto:nabil.benothman@distrelec.com">Nabil Benothman</a>
 * @since Namics Extensions 1.0
 * 
 */
public class OrderApprovalForm {

    private int page;
    // Default value is 10
    private int pageSize = 10;
    private ShowMode show;
    private String sort;
    private String sortType = "DESC";

    /**
     * Create a new instance of {@code OrderApprovalDashboardForm}
     */
    public OrderApprovalForm() {
        super();
    }

    /**
     * Create a new instance of {@code OrderApprovalDashboardForm}
     * 
     * @param page
     * @param pageSize
     * @param show
     * @param sortCode
     */
    public OrderApprovalForm(final int page, final int pageSize, final ShowMode show, final String sortCode) {
        this.page = page;
        this.pageSize = pageSize;
        this.show = show;
        this.sort = sortCode;
    }

    public int getPage() {
        return page;
    }

    public void setPage(final int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }

    public ShowMode getShow() {
        return show;
    }

    public void setShow(final ShowMode show) {
        this.show = show;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(final String sortCode) {
        this.sort = sortCode;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(final String sortType) {
        if ("asc".equalsIgnoreCase(sortType) || "desc".equalsIgnoreCase(sortType)) {
            this.sortType = sortType.toUpperCase();
        }
    }
}
