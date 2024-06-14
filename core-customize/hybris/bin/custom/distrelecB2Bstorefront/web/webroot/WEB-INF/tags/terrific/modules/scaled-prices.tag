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
<%@ tag description="Module: scaled-prices - Templates: default" %>
<%@ attribute name="product" description="The Product" type="de.hybris.platform.commercefacades.product.data.ProductData" rtexprvalue="true" %>

<c:set var="isOCI" value="false" />
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
	<c:set var="isOCI" value="true" />
</sec:authorize>

<%-- Module template selection --%>
<terrific:mod name="scaled-prices" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<c:choose>
		<c:when test="${isOCI eq false}">
			<c:choose>
				<c:when test="${template == 'compare-list'}">
					<%@ include file="/WEB-INF/terrific/modules/scaled-prices/scaled-prices-compare-list.jsp" %>
				</c:when>
				<c:when test="${template == 'single'}">
					<%@ include file="/WEB-INF/terrific/modules/scaled-prices/scaled-prices-single.jsp" %>
				</c:when>
				<c:when test="${template == 'product-list'}">
					<%@ include file="/WEB-INF/terrific/modules/scaled-prices/scaled-prices-product-list.jsp" %>
				</c:when>
				<c:when test="${template == 'product-list-technical'}">
					<%@ include file="/WEB-INF/terrific/modules/scaled-prices/scaled-prices-product-list-technical.jsp" %>
				</c:when>
				<c:when test="${template == 'productlist-technical-dot-tpl'}">
					<%@ include file="/WEB-INF/terrific/modules/scaled-prices/scaled-prices-productlist-technical-dot-tpl.jsp" %>
				</c:when>
				<c:when test="${template == 'dot-tpl'}">
					<%@ include file="/WEB-INF/terrific/modules/scaled-prices/scaled-prices-dot-tpl.jsp" %>
				</c:when>
				<c:when test="${template == 'compare-head'}">
					<%@ include file="/WEB-INF/terrific/modules/scaled-prices/scaled-prices-compare-head.jsp" %>
				</c:when>
				<c:when test="${template == 'prices-tabular'}">
					<%@ include file="/WEB-INF/terrific/modules/scaled-prices/scaled-prices-tabular.jsp" %>
				</c:when>
				<c:when test="${template == 'product-shopping'}">
					<%@ include file="/WEB-INF/terrific/modules/scaled-prices/scaled-prices-product-shopping.jsp" %>
				</c:when>
				<c:otherwise>
					<%@ include file="/WEB-INF/terrific/modules/scaled-prices/scaled-prices.jsp" %>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${template == 'prices-tabular'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/scaled-prices/scaled-prices-tabular.jsp" %>
				</c:when>
				<c:otherwise>
					<%@ include file="/WEB-INF/terrific/modules/OCI/scaled-prices/scaled-prices-single.jsp" %>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
</terrific:mod>
