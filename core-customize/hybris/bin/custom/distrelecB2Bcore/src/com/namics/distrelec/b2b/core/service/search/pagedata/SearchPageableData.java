/**
 * 
 */
package com.namics.distrelec.b2b.core.service.search.pagedata;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;

/**
 * {@code SearchPageableData}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.6
 */
public class SearchPageableData extends PageableData {

    private String sortType = "DESC";
    private boolean technicalView;

    public String getSortType() {
        return sortType;
    }

    public void setSortType(final String sortType) {
        if ("ASC".equalsIgnoreCase(sortType) || "DESC".equalsIgnoreCase(sortType)) {
            this.sortType = sortType;
        }
    }

    public boolean isTechnicalView() {
        return technicalView;
    }

    public void setTechnicalView(final boolean technicalView) {
        this.technicalView = technicalView;
    }
}
