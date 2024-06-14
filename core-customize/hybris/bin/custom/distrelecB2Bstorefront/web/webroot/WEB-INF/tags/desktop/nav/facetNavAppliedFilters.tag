<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="pageData" required="true" type="com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>


<c:if test="${not empty pageData.filters || not empty pageData.freeTextSearch}">
<div class="nav_column">
	<div class="title_holder">
		<div class="title">
			<div class="title-top">
				<span></span>
			</div>
		</div>
		<h2><spring:theme code="search.nav.appliedFilters"/></h2>
	</div>
	<div class="item">
		<ul class="facet_block">
			<c:forEach items="${pageData.filters}" var="filter">
				<li>					
					<c:url value="${filter.removeQuery.url}" var="removeQueryUrl"/>
					<a href="${removeQueryUrl}">
						<c:choose>
							<c:when test="${filter.multiSelect}">
								<spring:theme code="search.nav.appliedMultiSelectFacet" arguments="${filter.facetName}" />
							</c:when>
							<c:otherwise>
								<spring:theme code="search.nav.appliedFacet" arguments="${filter.facetName}^${filter.facetValueName}" argumentSeparator="^"/>
							</c:otherwise>
						</c:choose>
					</a>
					<span class="remove">					
						<a href="${removeQueryUrl}">						
							<spring:theme code="search.nav.removeAttribute" var="removeFacetAttributeText"/>
							<theme:image code="img.iconSearchFacetDelete"  />
						</a>
					</span>
				</li>
			</c:forEach>
		</ul>
	</div>
</div>
</c:if>
