<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="pageData" required="true" type="com.namics.hybris.ffsearch.data.facet.FactFinderFacetSearchPageData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>

<div class="nav_column">
	<div class="title_holder">
		<div class="title">
			<div class="title-top">
				<span></span>
			</div>
		</div>
		<h2><spring:theme code="search.nav.refinements"/></h2>
	</div>

	<c:forEach items="${pageData.categories}" var="facet">
		<nav:facetNavRefinementFacet facetData="${facet}"/>
	</c:forEach>
	
	<c:forEach items="${pageData.otherFacets}" var="facet">
		<nav:facetNavRefinementFacet facetData="${facet}"/>
	</c:forEach>

</div>
