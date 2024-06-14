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
<%@ tag description="Module: shipping-information - Templates: default" %>
<%@ attribute name="product" description="The Product" type="de.hybris.platform.commercefacades.product.data.ProductData" rtexprvalue="true" %>

<c:set var="isOCI" value="false" />
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
	<c:set var="isOCI" value="true" />
</sec:authorize>

<%-- Module template selection --%>
<terrific:mod name="shipping-information" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<c:choose>
		<c:when test="${isOCI eq false}">
			<c:choose>
				<c:when test="${template == 'compare-list'}">
					<%@ include file="/WEB-INF/terrific/modules/shipping-information/shipping-information-comparelist.jsp" %>
				</c:when>
				<c:when test="${template == 'category'}">
					<%@ include file="/WEB-INF/terrific/modules/shipping-information/shipping-information-category.jsp" %>
				</c:when>
				<c:when test="${template == 'compare-list-new'}">
					<%@ include file="/WEB-INF/terrific/modules/shipping-information/shipping-information-comparelist-new.jsp" %>
				</c:when>
				<c:when test="${template == 'compare-page-list'}">
					<%@ include file="/WEB-INF/terrific/modules/shipping-information/shipping-information-compare-page-list.jsp" %>
				</c:when>
				<c:when test="${template == 'pdp'}">
					<%@ include file="/WEB-INF/terrific/modules/shipping-information/shipping-information-pdp-new.jsp" %>
				</c:when>
				<c:when test="${template == 'plp-new'}">
					<%@ include file="/WEB-INF/terrific/modules/shipping-information/shipping-information-plp-new.jsp" %>
				</c:when>
				<c:otherwise>
					<%@ include file="/WEB-INF/terrific/modules/shipping-information/shipping-information.jsp" %>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${template == 'compare-list-new'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/shipping-information/shipping-information-comparelist-new.jsp" %>
				</c:when>
				<c:otherwise>
					<%@ include file="/WEB-INF/terrific/modules/OCI/shipping-information/shipping-information-pdp-new.jsp" %>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
</terrific:mod>
