<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="tag" description="The tag name used for the module context (default=div)" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="htmlClasses" description="The HTML class values to add to the Terrific module element" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="skin" description="The skin used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="dataConnectors" description="The data-connectors used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="attributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="template" description="The template used for rendering of the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="product" description="Product info per line" type="de.hybris.platform.commercefacades.product.data.ProductData" rtexprvalue="true" %>

<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ tag description="Module: Schema with Various Templates" %>

<%-- Module template selection --%>
<terrific:mod name="schema" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<%-- set isOCI var --%>
	<c:set var="isOCI" value="false" />
	<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
		<c:set var="isOCI" value="true" />
	</sec:authorize>

	<c:choose>
		<c:when test="${isOCI eq false}">
			<c:choose>
				<c:when test="${template == 'breadcrumb'}">
					<%@ include file="/WEB-INF/terrific/modules/schema/schema-breadcrumb.jsp" %>
				</c:when>
				<c:when test="${template == 'organisation'}">
					<%@ include file="/WEB-INF/terrific/modules/schema/schema-organisation.jsp" %>
				</c:when>
				<c:when test="${template == 'product'}">
					<%@ include file="/WEB-INF/terrific/modules/schema/schema-product.jsp" %>
				</c:when>
				<c:when test="${template == 'product-list'}">
					<%@ include file="/WEB-INF/terrific/modules/schema/schema-product-list.jsp" %>
				</c:when>
				<c:when test="${template == 'sitelink-search'}">
					<%@ include file="/WEB-INF/terrific/modules/schema/schema-sitelink-search.jsp" %>
				</c:when>
				<c:otherwise>
					<%--Do Nothing--%>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${template == 'breadcrumb'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/schema/schema-breadcrumb.jsp" %>
				</c:when>
				<c:when test="${template == 'organisation'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/schema/schema-organisation.jsp" %>
				</c:when>
				<c:when test="${template == 'product'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/schema/schema-product.jsp" %>
				</c:when>
				<c:when test="${template == 'product-list'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/schema/schema-product-list.jsp" %>
				</c:when>
				<c:when test="${template == 'sitelink-search'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/schema/schema-sitelink-search.jsp" %>
				</c:when>
				<c:otherwise>
					<%--Do Nothing--%>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
</terrific:mod>
