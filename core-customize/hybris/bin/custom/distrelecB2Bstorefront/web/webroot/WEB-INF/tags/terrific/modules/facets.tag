<%-- Common module settings --%>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="tag" description="The tag name used for the module context (default=div)" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="htmlClasses" description="The HTML class values to add to the Terrific module element" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="skin" description="The skin used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="dataConnectors" description="The data-connectors used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="attributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="template" description="The template used for rendering of the module" type="java.lang.String" rtexprvalue="false" %>

<%-- Required tag libraries --%>
<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%-- Specific module settings --%>
<%@ tag description="Module: facets - Templates: default" %>

<%-- Module Specific Attributes --%>
<%@ attribute name="searchPageData" description="Search Page Data" type="com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData" required="false" %>
<%@ attribute name="ffFilterStrings" description="fact finder Filterstrings" type="java.util.Collection" required="false" %>

<%-- Module template selection --%>
<terrific:mod name="facets" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<jsp:useBean id="filterCount" class="java.util.HashMap" scope="request"/>
	<c:set var="isOCI" value="false" />
	<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
		<c:set var="isOCI" value="true" />
	</sec:authorize>

	<c:choose>
		<c:when test="${isOCI eq false}">
			<c:choose>
				<c:when test="${template == 'product-finder'}">
					<%@ include file="/WEB-INF/terrific/modules/facets/facets-product-finder.jsp" %>
				</c:when>
				<c:when test="${template == 'plp-filter-view'}">
					<%@ include file="/WEB-INF/terrific/modules/facets/facets-plp-filter-view.jsp" %>
				</c:when>
				<c:when test="${template == 'plp-filter-search'}">
					<%@ include file="/WEB-INF/terrific/modules/facets/facets-plp-filter-search.jsp" %>
				</c:when>
				<c:when test="${template == 'plp-filter-controllbar'}">
					<%@ include file="/WEB-INF/terrific/modules/facets/facets-plp-filter-controllbar.jsp" %>
				</c:when>
				<c:otherwise>
					<%@ include file="/WEB-INF/terrific/modules/facets/facets.jsp" %>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${template == 'product-finder'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/facets/facets-product-finder.jsp" %>
				</c:when>
				<c:when test="${template == 'plp-filter-view'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/facets/facets-plp-filter-view.jsp" %>
				</c:when>
				<c:when test="${template == 'plp-filter-search'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/facets/facets-plp-filter-search.jsp" %>
				</c:when>
				<c:when test="${template == 'plp-filter-controllbar'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/facets/facets-plp-filter-controllbar.jsp" %>
				</c:when>
				<c:otherwise>
					<%@ include file="/WEB-INF/terrific/modules/OCI/facets/facets.jsp" %>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
</terrific:mod>
