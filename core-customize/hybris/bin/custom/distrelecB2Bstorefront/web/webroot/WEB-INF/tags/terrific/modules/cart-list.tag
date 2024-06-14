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
<%@ tag description="Module: cart-list - Templates: default" %>
<%@ attribute name="cartData" type="de.hybris.platform.commercefacades.order.data.CartData" rtexprvalue="true" %>
<%@ attribute name="cartItemList" type="java.util.List" rtexprvalue="true" %>
<%@ attribute name="returnOnlineAction" description="Create return online" type="java.lang.String" rtexprvalue="true" %>


<%-- Module template selection --%>
<terrific:mod name="cart-list" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<c:set var="isOCI" value="false" />
	<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
		<c:set var="isOCI" value="true" />
	</sec:authorize>

	<c:choose>
		<c:when test="${isOCI eq false}">
			<c:choose>
				<c:when test="${template == 'review'}">
					<%@ include file="/WEB-INF/terrific/modules/cart-list/cart-list-review.jsp" %>
				</c:when>
				<c:when test="${template == 'review-open-order'}">
					<%@ include file="/WEB-INF/terrific/modules/cart-list/cart-list-review-open-order.jsp" %>
				</c:when>
				<c:when test="${template == 'order-detail'}">
					<%@ include file="/WEB-INF/terrific/modules/cart-list/cart-list-order-detail.jsp" %>
				</c:when>
				<c:when test="${template == 'approval-request'}">
					<%@ include file="/WEB-INF/terrific/modules/cart-list/cart-list-approval-request.jsp" %>
				</c:when>
				<c:when test="${template == 'return-items'}">
					<%@ include file="/WEB-INF/terrific/modules/cart-list/cart-list-return-items.jsp" %>
				</c:when>
				<c:when test="${template == 'quote-detail'}">
					<%@ include file="/WEB-INF/terrific/modules/cart-list/cart-list-quote-detail.jsp" %>
				</c:when>
				<c:when test="${template == 'cart'}">
					<%@ include file="/WEB-INF/terrific/modules/cart-list/cart-list-cart.jsp" %>
				</c:when>
				<c:otherwise>
					<%@ include file="/WEB-INF/terrific/modules/cart-list/cart-list-checkout.jsp" %>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${template == 'review'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/cart-list/cart-list-review.jsp" %>
				</c:when>
				<c:when test="${template == 'review-open-order'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/cart-list/cart-list-review-open-order.jsp" %>
				</c:when>
				<c:when test="${template == 'order-detail'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/cart-list/cart-list-order-detail.jsp" %>
				</c:when>
				<c:when test="${template == 'approval-request'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/cart-list/cart-list-approval-request.jsp" %>
				</c:when>
				<c:when test="${template == 'return-items'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/cart-list/cart-list-return-items.jsp" %>
				</c:when>
				<c:when test="${template == 'quote-detail'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/cart-list/cart-list-quote-detail.jsp" %>
				</c:when>
				<c:when test="${template == 'cart'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/cart-list/cart-list-cart.jsp" %>
				</c:when>
				<c:otherwise>
					<%@ include file="/WEB-INF/terrific/modules/OCI/cart-list/cart-list-checkout.jsp" %>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
</terrific:mod>
