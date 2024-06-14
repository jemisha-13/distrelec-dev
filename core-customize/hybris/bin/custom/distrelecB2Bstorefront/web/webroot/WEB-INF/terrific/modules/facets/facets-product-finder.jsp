<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>

<%-- read from PropertiesFactoryBean named 'propertiesFile' --%>
<spring:eval expression="@configurationService.configuration.getString('search.facets.open.count')" var="openFacetsCount" scope="application"/>
<fmt:parseNumber var="numberOfOpenFacets" type="number" value="${openFacetsCount}" />

<div class="xmod-filter">
	<div class="hd">
		<h2 class="list-title"><spring:theme code="search.nav.title" text="Filter" /></h2>
	</div>
	<div class="bd">
		<c:if test="${not empty searchPageData.filters || not empty searchPageData.freeTextSearch}">
			<ul>
				<c:forEach items="${searchPageData.filters}" var="filter">
					<li class="facet-item" data-facet-type="${facet.type.value}">
						<c:url value="${filter.removeQuery.url}" var="removeQueryUrl"/>
						<a class="facet-link" href="${removeQueryUrl}">
							<span>
								<c:choose>
									<c:when test="${filter.type.value eq 'checkbox'}">
										<spring:theme code="search.nav.appliedFacet" arguments="${filter.facetName}^${filter.facetValueName}" argumentSeparator="^"/>
									</c:when>
									<c:otherwise>
										<spring:theme code="search.nav.appliedMultiSelectFacet" arguments="${filter.facetName}" />
									</c:otherwise>
								</c:choose>
							</span>
							<i></i>
						</a>
					</li>
				</c:forEach>
			</ul>
		</c:if>
		<c:if test="${empty searchPageData.filters}">
			<span class="empty-filter-msg"><spring:theme code="search.nav.noFilter" text="No Filter" /></span>
		</c:if>
	</div>
</div>

<a class="btn btn-primary" href="<c:url value="${productFinderRefineSearchUrl}" />"><i></i><spring:message code="productfinder.btn.refinesearch" /></a>
