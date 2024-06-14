<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<meta name="_csrf" content="${_csrf.token}"/>

<c:set value="${searchPageData.pagination.totalNumberOfResults}" var="matchedProductsCount" />
<c:set value="hidden" var="resetFilterClass" />


<div class="row plp-filter-controllbar">

    <div class="col-6 col-lg-8">

        <div class="row">
            <div class="col plp-filter-controllbar__applied-filter-block">
                <c:if test="${!disableFilter}">
                    <%-- Check for existence of non-category filters and multi-selection values --%>
                    <c:set var="activeFilter" value="" />
                    <c:set var="multiSelectedFacets" value="^" /> <%-- String of multi-selected facets, separated by "^" --%>
                    <c:set var="multiSelectedFacetString" value="" />
                    <c:set var="allSelectedFacets" value="^" />
                    <c:set var="thisFacetCode" value="" />
                    <c:if test="${not empty searchPageData.filters}">
                        <c:forEach items="${searchPageData.filters}" var="filter">
                            <c:if test="${!filter.categoryFilter && !fn:contains(filter.facetCode,'categoryCodePathROOT')}">

                                <c:choose>
                                    <c:when test="${fn:contains(filter.facetCode,'~~')}">
                                        <c:set var="filterFacetCode" value="${fn:split(filter.facetCode, '~~')[0]}" />
                                        <c:set var="multiSelectedFacetString" value="${multiSelectedFacetString}&${filter.filterString}" />
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="filterFacetCode" value="${filter.facetCode}" />
                                    </c:otherwise>
                                </c:choose>

                                <c:choose>
                                    <c:when test="${filterCount.containsKey(filterFacetCode)}">
                                        <c:set target="${filterCount}" property="${filterFacetCode}" value="${filterCount[filterFacetCode] + 1}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <c:set target="${filterCount}" property="${filterFacetCode}" value="1"/>
                                    </c:otherwise>
                                </c:choose>
                                <c:set var="activeFilter" value="true" />

                                <c:set var="allSelectedFacets" value="${allSelectedFacets}${filterFacetCode}^" />

                                <c:if test="${filterFacetCode eq thisFacetCode}">
                                    <c:set var="multiSelectedFacets" value="${multiSelectedFacets}${filterFacetCode}^" />
                                </c:if>

                                <c:choose>
                                    <c:when test="${fn:contains(filter.facetCode,'~~')}">
                                        <c:set var="thisFacetCode" value="${fn:split(filter.facetCode, '~~')[0]}" />
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="thisFacetCode" value="${filter.facetCode}" />
                                    </c:otherwise>
                                </c:choose>

                            </c:if>
                        </c:forEach>
                    </c:if>

                    <c:set var="appliedFilterCount" value="${fn:length(searchPageData.filters)}" />
                    <div class="xmod-filter">

                        <spring:theme code="search.nav.facet.loadProducts.error.boxTitle" var="loadProductsForFacetErrorTitle"/>
                        <spring:theme code="search.nav.facet.loadProducts.error.boxMessage" var="loadProductsForFacetErrorMessage"/>
                        <div  class="plp-filter-controllbar__applied-filter" data-load-products-error-title="${loadProductsForFacetErrorTitle}" data-load-products-error-message="${loadProductsForFacetErrorMessage}">

                            <c:if test="${not empty searchPageData.filters || not empty searchPageData.freeTextSearch}">
                                <c:if test="${ appliedFilterCount > 0}">
                                    <span class="plp-filter-controllbar__applied-filter__title"> <spring:message code="text.plp.applied.filters" /> </span>
                                </c:if>

                                    <ul>
                                    <c:set var="prevFilter" value="" />
                                    <c:set var="allFilterParameters" value="" />


                                    <c:forEach items="${searchPageData.filters}" var="filter">

                                        <c:choose>
                                            <c:when test="${fn:contains(filter.facetCode,'~~')}">
                                                <c:set var="currfilterFacetCode" value="${fn:split(filter.facetCode, '~~')[0]}" />
                                                <c:set var="currfilterStr" value="${fn:split(filter.filterString, '~~')[0]}" />
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="currfilterFacetCode" value="${filter.facetCode}" />
                                                <c:set var="currfilterStr" value="${filter.filterString}" />
                                            </c:otherwise>
                                        </c:choose>

                                        <c:set var="allFilterParameters" value="${allFilterParameters}&${currfilterStr}" />

                                        <c:if test="${!filter.categoryFilter && !fn:contains(currfilterFacetCode,'categoryCodePathROOT')}"> <%-- DISTRELEC-6414 - Do not display categories in filter box --%>

                                            <c:set var="codeWithDelim" value="^${currfilterFacetCode}^" />

                                            <c:set var="isMultiSelected" value="${fn:contains(multiSelectedFacets,codeWithDelim)}" />

                                            <c:if test="${!(isMultiSelected && currfilterFacetCode == prevFilter)}">
                                                <c:set var="prevFilter" value="${currfilterFacetCode}" />

                                                <li class="facet-item plp-filter-controllbar__applied-filter-item">
                                                    <c:url value="${filter.removeQuery.url}" var="removeQueryUrl"/>
                                                    <c:set value="" var="resetFilterClass" />

                                                    <c:if test="${isMultiSelected}">

                                                        <c:forTokens items="${multiSelectedFacetString}" delims="&" var="removeQuery">

                                                            <c:choose>
                                                                <c:when test="${fn:contains(filter.facetCode,'~~')}">
                                                                    <c:if test="${fn:contains(multiSelectedFacetString,currfilterFacetCode)}">
                                                                        <c:set var="query" value="&${removeQuery}" />
                                                                        <c:set var="removeQueryUrl" value="${fn:replace(removeQueryUrl,query,'')}" />
                                                                    </c:if>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <c:set var="removeQueryUrl" value="${fn:replace(removeQueryUrl,fn:substringBefore(filter.filterString,'='),'_dummy')}" />
                                                                </c:otherwise>
                                                            </c:choose>

                                                        </c:forTokens>

                                                    </c:if>

                                                    <a class="facet-link filterBoxElement " href="${removeQueryUrl}"
                                                       data-facet-code="${filter.facetCode}"
                                                       data-facet-value-name="${filter.facetValueCode}"
                                                       data-filter-string="${filter.filterString}"
                                                       data-aainteraction="remove filter"
                                                       data-aafiltername="${filter.facetName}"
                                                    >

                                                        <i class="fas fa-times plp-filter-controllbar__applied-filter-icon"></i>

                                                    </a>

                                                    <span class="plp-filter-controllbar__applied-filter-text">
                                                        <c:choose>
                                                            <c:when test="${not empty filter.facetValuePropertyName}">
                                                                <spring:theme code="${filter.facetValuePropertyName}" arguments="${filter.facetValuePropertyNameArguments}" argumentSeparator="${filter.facetValuePropertyNameArgumentSeparator}" var="facetValueName" />
                                                            </c:when>
                                                            <c:otherwise>
                                                                <c:set value="${filter.facetValueName}" var="facetValueName" />
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <span class="hidden facetValueName">${facetValueName}</span>

                                                        <c:choose>
                                                            <c:when test="${(filter.type.value eq 'checkbox' || filter.type.value eq 'slider') && !isMultiSelected}">
                                                                ${filter.facetName}
                                                            </c:when>
                                                            <c:otherwise>
                                                                <spring:theme code="search.nav.appliedMultiSelectFacet" arguments="${filter.facetName}" />
                                                            </c:otherwise>
                                                        </c:choose>

                                                        (${filterCount[currfilterFacetCode]})

                                                    </span>


                                                </li>
                                            </c:if>
                                        </c:if>
                                    </c:forEach>
                                </ul>
                            </c:if>
                        </div>
                    </div>

                    <a data-aainteraction="clear all filters" class="plp-filter-controllbar__reset-all-filter ${resetFilterClass}" href="${searchPageData.removeFiltersURL}&filtersCleared=true" class="clear-link ${empty activeFilter ? ' hidden' : ''}"><spring:theme code="text.clear.all" text="Remove" /> </a>

                </c:if>

            </div>



        </div>

        <div class="tooltip-hover hidden">
            <div class="tooltip-information">
                <spring:message code="search.nav.facet.tips.text" text="Use the SHIFT key to select a range of options" />
            </div>
            <div class="arrow-down"></div>
        </div>

        <div class="ajax-action-overlay hidden">
            <div class="background-overlay"></div>
        </div>

    </div>

    <fmt:formatNumber type="number" groupingUsed="false" value="${searchPageData.pagination.totalNumberOfResults}" var="matchedProductsCount" />
    <c:choose>
        <c:when test="${matchedProductsCount > 0}">
            <c:set var="matchedProductsStatus" value="success" />
        </c:when>
        <c:otherwise>
            <c:set var="matchedProductsStatus" value="error" />
        </c:otherwise>
    </c:choose>

    <div class="col-6 col-lg-4 mb-o2 align-right">

        <span class="xmod-filter__matched-products ${matchedProductsStatus} plp-filter-controllbar__matched-products">
            <span id="facet_result_count" class="xmod-filter__matched-products-count facet-result-count">${searchPageData.pagination.totalNumberOfResults}</span>

            <c:choose>
                <c:when test="${matchedProductsCount > 1}">
                    <spring:message code="text.plp.matches" />
                </c:when>
                <c:otherwise>
                    <spring:message code="text.plp.match" />
                </c:otherwise>
            </c:choose>

        </span>

        <c:if test="${matchedProductsCount != '1'}">
            <button type="submit" class="plp-filter-controllbar__apply-filter mat-button mat-button__solid--action-green" data-aainteraction="apply filter"><spring:message code="text.apply.filters" /></button>
        </c:if>
    </div>

</div>




