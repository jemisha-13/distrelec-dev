<%-- Common module settings --%>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="tag" description="The tag name used for the module context (default=div)" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="htmlClasses" description="The HTML class values to add to the Terrific module element" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="skin" description="The skin used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="dataConnectors" description="The data-connectors used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="attributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="template" description="The template used for rendering of the module" type="java.lang.String" rtexprvalue="false" %>

<%-- Required tag libraries --%>
<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%-- Specific module settings --%>
<%@ tag description="Module: cart-list-item - Templates: default" %>
<%@ attribute name="orderEntry" type="de.hybris.platform.commercefacades.order.data.OrderEntryData" required="false" %>
<%@ attribute name="orderData" type="de.hybris.platform.commercefacades.order.data.OrderData" required="false" %>
<%@ attribute name="quoteEntry" type="com.namics.distrelec.b2b.facades.order.quotation.data.QuotationEntry" required="false" %>
<%@ attribute name="subEntry" type="com.namics.distrelec.b2b.facades.order.data.SubOrderEntryData" required="false" %>
<%@ attribute name="showListPrice" type="java.lang.Boolean" required="false" %>
<%@ attribute name="showMyPrice" type="java.lang.Boolean" required="false" %>
<%@ attribute name="position" type="java.lang.Integer" %>
<%@ attribute name="isBOM" type="java.lang.Boolean" required="false" description="True if this item is a BOM (Bill of Materials)"%>
<%@ attribute name="isSubItem" type="java.lang.Boolean" required="false" description="True if this item is part of a BOM (Bill of Materials)"%>
<%@ attribute name="isQuoteItem" type="java.lang.Boolean" required="false" description="True if this item is part of a quotation"%>
<%@ attribute name="isDummyItem" type="java.lang.Boolean" required="false" description="True if this item is a dummy item"%>
<%@ attribute name="quoteStatus" type="java.lang.String" required="false" description="The status code of the quote (00-04)"%>
<%@ attribute name="quotationData" type="com.namics.distrelec.b2b.facades.order.quotation.data.QuotationData" required="false" description="The Quotation Data object"%>
<%@ attribute name="isOpenOrder" type="java.lang.Boolean" required="false" description="True if the order is an open order"%>
<%@ attribute name="loopNumber" description="number of loop" type="java.lang.String" rtexprvalue="true" %>

<%-- Module template selection --%>
<terrific:mod name="cart-list-item" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<c:set var="isOCI" value="false" />
	<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
		<c:set var="isOCI" value="true" />
	</sec:authorize>

	<c:choose>
		<c:when test="${isOCI eq false}">
			<c:choose>
				<c:when test="${template == 'dot-tmpl'}">
					<%@ include file="/WEB-INF/terrific/modules/cart-list-item/cart-list-item-dot-tmpl.jsp" %>
				</c:when>
				<c:when test="${template == 'review'}">
					<%@ include file="/WEB-INF/terrific/modules/cart-list-item/cart-list-item-review.jsp" %>
				</c:when>
				<c:when test="${template == 'order-detail'}">
					<%@ include file="/WEB-INF/terrific/modules/cart-list-item/cart-list-item-order-detail.jsp" %>
				</c:when>
				<c:when test="${template == 'quote-detail'}">
					<%@ include file="/WEB-INF/terrific/modules/cart-list-item/cart-list-item-quote-detail.jsp" %>
				</c:when>
				<c:when test="${template == 'approval-request'}">
					<%@ include file="/WEB-INF/terrific/modules/cart-list-item/cart-list-item-approval-request.jsp" %>
				</c:when>
				<c:when test="${template == 'return-items'}">
					<%@ include file="/WEB-INF/terrific/modules/cart-list-item/cart-list-item-return-items.jsp" %>
				</c:when>
				<c:when test="${template == 'quote-head'}">
					<%@ include file="/WEB-INF/terrific/modules/cart-list-item/cart-list-item-quote-head.jsp" %>
				</c:when>
				<c:when test="${template == 'cart'}">
					<%@ include file="/WEB-INF/terrific/modules/cart-list-item/cart-list-item-cart.jsp" %>
				</c:when>
				<c:otherwise>
				   <%@ include file="/WEB-INF/terrific/modules/cart-list-item/cart-list-item.jsp" %>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${template == 'dot-tmpl'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/cart-list-item/cart-list-item-dot-tmpl.jsp" %>
				</c:when>
				<c:when test="${template == 'review'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/cart-list-item/cart-list-item-review.jsp" %>
				</c:when>
				<c:when test="${template == 'order-detail'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/cart-list-item/cart-list-item-order-detail.jsp" %>
				</c:when>
				<c:when test="${template == 'quote-detail'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/cart-list-item/cart-list-item-quote-detail.jsp" %>
				</c:when>
				<c:when test="${template == 'approval-request'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/cart-list-item/cart-list-item-approval-request.jsp" %>
				</c:when>
				<c:when test="${template == 'return-items'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/cart-list-item/cart-list-item-return-items.jsp" %>
				</c:when>
				<c:when test="${template == 'quote-head'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/cart-list-item/cart-list-item-quote-head.jsp" %>
				</c:when>
				<c:when test="${template == 'cart'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/cart-list-item/cart-list-item-cart.jsp" %>
				</c:when>
				<c:otherwise>
					<%@ include file="/WEB-INF/terrific/modules/OCI/cart-list-item/cart-list-item.jsp" %>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
</terrific:mod>
