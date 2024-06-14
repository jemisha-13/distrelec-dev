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

<%-- Module Specific Attributes --%>
<%@ attribute name="searchPageData" required="false" type="com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData" %>
<%@ attribute name="myAccountSearchPageData" required="false" type="de.hybris.platform.commerceservices.search.pagedata.SearchPageData" %>
<%@ attribute name="pageSize" required="false" type="java.lang.Integer" %>

<%-- Module template selection --%>
<terrific:mod name="productlist-pagination" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">

	<c:set var="isOCI" value="false" />
	<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
		<c:set var="isOCI" value="true" />
	</sec:authorize>

	<c:choose>
		<c:when test="${isOCI eq false}">
			<c:choose>
				<c:when test="${template == 'myaccount'}">
					<%@ include file="/WEB-INF/terrific/modules/productlist-pagination/productlist-pagination-myaccount.jsp" %>
				</c:when>
				<c:when test="${template == 'category'}">
					<%@ include file="/WEB-INF/terrific/modules/productlist-pagination/productlist-pagination-category.jsp" %>
				</c:when>
				<c:when test="${template == 'plp'}">
					<%@ include file="/WEB-INF/terrific/modules/productlist-pagination/productlist-pagination-plp.jsp" %>
				</c:when>
				<c:when test="${template == 'left-side-filters'}">
					<%@ include file="/WEB-INF/terrific/modules/productlist-pagination/productlist-pagination-left-side.jsp" %>
				</c:when>
				<c:otherwise>
					<%@ include file="/WEB-INF/terrific/modules/productlist-pagination/productlist-pagination.jsp" %>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${template == 'category'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/productlist-pagination/productlist-pagination-category.jsp" %>
				</c:when>
				<c:when test="${template == 'plp'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/productlist-pagination/productlist-pagination-plp.jsp" %>
				</c:when>
				<c:otherwise>
					<%@ include file="/WEB-INF/terrific/modules/OCI/productlist-pagination/productlist-pagination.jsp" %>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
</terrific:mod>
