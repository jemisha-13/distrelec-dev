<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<spring:theme code="text.sortby" text="Sort by" var="sSortBy"/>
<spring:theme code="text.filter.results" text="Filter Results" var="sfilterResults"/>

<div class="mod-mobile-filter-switch__holder row">
    <div class="col">
        <span class="mod-mobile-filter-switch__holder__sort">
            ${sSortBy}
        </span>
    </div>
    <div class="col">
        <span class="mod-mobile-filter-switch__holder__filter col" data-aainteraction="open filter list">
            ${sfilterResults}
        </span>
    </div>
</div>