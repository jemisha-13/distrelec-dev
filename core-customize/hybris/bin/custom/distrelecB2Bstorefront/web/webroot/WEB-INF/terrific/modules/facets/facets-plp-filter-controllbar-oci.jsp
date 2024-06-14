<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<meta name="_csrf" content="${_csrf.token}"/>

<c:set value="${searchPageData.pagination.totalNumberOfResults}" var="matchedProductsCount" />
<c:set value="hidden" var="resetFilterClass" />

<div class="row plp-filter-controllbar">

    <div class="help-prompts hidden help-prompts--apply-view">

        <span class="help-prompts__label">
            <spring:message code="plp.helpprompts.facetlistactionbar.applyview.label1" />
        </span>

        <span class="help-prompts__info">
            <spring:message code="plp.helpprompts.facetlistactionbar.applyview.info1" />
        </span>

        <a href="#" class="help-prompts__cta-skip"><spring:message code="text.skip" /> </a>
        <a href="#" class="help-prompts__cta-next mat-button mat-button__normal--action-blue"><spring:message code="text.next" /> <i class="fas fa-angle-right"></i> </a>
    </div>

    <div class="help-prompts hidden help-prompts--detail-view">

        <span class="help-prompts__label">
            <spring:message code="plp.helpprompts.facetlistactionbar.detailview.label1" />
        </span>

        <span class="help-prompts__info">
            <spring:message code="plp.helpprompts.facetlistactionbar.detailview.info1" />
        </span>

        <span class="help-prompts__label">
            <spring:message code="plp.helpprompts.facetlistactionbar.detailview.label2" />
        </span>

        <span class="help-prompts__info">
            <spring:message code="plp.helpprompts.facetlistactionbar.detailview.info2" />
        </span>

        <a href="#" class="mat-button mat-button__normal--action-blue help-prompts__cta-skip help-prompts__cta-skip--cta-done"><i class="fas fa-check"></i> <spring:message code="text.done" /> </a>
    </div>

    <div class="col-6 col-lg-2 mb-o2">
        <c:if test="${matchedProductsCount != '1'}">
            <button type="submit" class="plp-filter-controllbar__apply-filter mat-button mat-button__solid--action-green" data-aainteraction="apply filter"><spring:message code="text.apply.filters" /></button>
            <button type="submit" class="plp-filter-controllbar__toggle-filter mat-button mat-button__normal--action-blue" >Toggle Filter<i></i></button>
        </c:if>
    </div>

    <div class="col-6 col-lg-10">

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
                                                    >

                                                        <i class="fas fa-times plp-filter-controllbar__applied-filter-icon"></i>

                                                    </a>

                                                    <span class="plp-filter-controllbar__applied-filter">
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
                                                                <spring:theme code="search.nav.appliedFacet" arguments="${filter.facetName}^${facetValueName}" argumentSeparator="^"/>
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

                        <a data-aainteraction="reset filters"  class="plp-filter-controllbar__reset-all-filter ${resetFilterClass}" href="${searchPageData.removeFiltersURL}" class="clear-link ${empty activeFilter ? ' hidden' : ''}"><spring:theme code="text.reset.all" text="Remove" /> </a>

                    </div>
                </c:if>

            </div>

            <div class="plp-filter-controllbar__onoffswitch" data-aainteraction="change view type">
                <input type="checkbox" name="onoffswitch" class="plp-filter-controllbar__onoffswitch-checkbox" id="myonoffswitch" data-aainteraction="change view type">

                <span class="plp-filter-controllbar__onoffswitch-text"><spring:message code="text.compact.view"  /></span>
                <label class="plp-filter-controllbar__onoffswitch-label" for="myonoffswitch">
                    <span class="plp-filter-controllbar__onoffswitch-switch"></span>
                </label>
                <span class="plp-filter-controllbar__onoffswitch-text"><spring:message code="text.detailed.view"  /></span>
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
</div>