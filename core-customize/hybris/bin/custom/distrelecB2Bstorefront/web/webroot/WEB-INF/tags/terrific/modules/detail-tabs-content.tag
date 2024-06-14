<%-- Common module settings --%>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="tag" description="The tag name used for the module context (default=div)" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="htmlClasses" description="The HTML class values to add to the Terrific module element" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="skin" description="The skin used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="dataConnectors" description="The data-connectors used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="attributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="template" description="The template used for rendering of the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="position" type="java.lang.Integer" %>

<%-- Required tag libraries --%>
<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%-- Specific module settings --%>
<%@ tag description="Module: detail-tabs-content - Templates: default" %>
<%@ attribute name="downloadSection" required="false" type="com.namics.distrelec.b2b.facades.product.data.DistDownloadSectionData" %>
<%@ attribute name="product" required="false" type="de.hybris.platform.commercefacades.product.data.ProductData" %>

<c:set var="isOCI" value="false" />
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
	<c:set var="isOCI" value="true" />
</sec:authorize>

<%-- Module template selection --%>
<terrific:mod name="detail-tabs-content" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<c:choose>
		<c:when test="${isOCI eq false}">
			<c:choose>
				<c:when test="${template == 'download'}">
					<%@ include file="/WEB-INF/terrific/modules/detail-tabs-content/detail-tabs-content-download.jsp" %>
				</c:when>
				<c:when test="${template == 'product-information'}">
					<%@ include file="/WEB-INF/terrific/modules/detail-tabs-content/detail-tabs-content-product-information.jsp" %>
				</c:when>
				<c:when test="${template == 'technical-information'}">
					<%@ include file="/WEB-INF/terrific/modules/detail-tabs-content/detail-tabs-content-technical-information.jsp" %>
				</c:when>
				<c:when test="${template == 'technical-information-compare-list'}">
					<%@ include file="/WEB-INF/terrific/modules/detail-tabs-content/detail-tabs-content-technical-information-compare-list.jsp" %>
				</c:when>
				<c:otherwise>
					<%@ include file="/WEB-INF/terrific/modules/detail-tabs-content/detail-tabs-content.jsp" %>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${template == 'download'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/detail-tabs-content/detail-tabs-content-download.jsp" %>
				</c:when>
				<c:when test="${template == 'product-information'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/detail-tabs-content/detail-tabs-content-product-information.jsp" %>
				</c:when>
				<c:when test="${template == 'technical-information'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/detail-tabs-content/detail-tabs-content-technical-information.jsp" %>
				</c:when>
				<c:when test="${template == 'technical-information-compare-list'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/detail-tabs-content/detail-tabs-content-technical-information-compare-list.jsp" %>
				</c:when>
				<c:otherwise>
					<%@ include file="/WEB-INF/terrific/modules/OCI/detail-tabs-content/detail-tabs-content.jsp" %>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
</terrific:mod>
