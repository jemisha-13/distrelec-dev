<%-- Common module settings --%>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="tag" description="The tag name used for the module context (default=div)" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="htmlClasses" description="The HTML class values to add to the Terrific module element" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="skin" description="The skin used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="dataConnectors" description="The data-connectors used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="attributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="template" description="The template used for rendering of the module" type="java.lang.String" rtexprvalue="false" %>

<%-- Required tag libraries --%>
<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%-- Specific module settings --%>
<%@ tag description="Module: cart-pricecalcbox - Templates: default" %>
<%@ attribute name="cartData" description="The current cart of the user" type="de.hybris.platform.commercefacades.order.data.CartData" required="false" %>
<%@ attribute name="orderData" description="The chosen order of the user" type="de.hybris.platform.commercefacades.order.data.OrderData" required="false" %>
<%@ attribute name="currentList" type="com.namics.distrelec.b2b.facades.wishlist.data.NamicsWishlistData"  %>
<%@ attribute name="showCheckoutButton" type="java.lang.Boolean" %>
<%@ attribute name="showContinueCheckoutButton" type="java.lang.Boolean" %>
<%@ attribute name="showTerms" type="java.lang.Boolean" %>
<%@ attribute name="actionUrl" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="showTitle" type="java.lang.Boolean" %>
<%@ attribute name="href" type="java.lang.String" rtexprvalue="true" %>
<%--Attributes needed for the Checkout Address Page Template --%>
<%@ attribute name="deliveryType" type="java.lang.String" rtexprvalue="true" %>

<%-- Module template selection --%>
<terrific:mod name="cart-pricecalcbox" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<c:set var="isOCI" value="false" />
	<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
		<c:set var="isOCI" value="true" />
	</sec:authorize>

	<c:choose>
		<c:when test="${isOCI eq false}">
			<c:choose>
				<c:when test="${template == 'approval-request'}">
					<%@ include file="/WEB-INF/terrific/modules/cart-pricecalcbox/cart-pricecalcbox-approval-request.jsp" %>
				</c:when>
				<c:when test="${template == 'open-order'}">
					<%@ include file="/WEB-INF/terrific/modules/cart-pricecalcbox/cart-pricecalcbox-open-order.jsp" %>
				</c:when>
				<c:when test="${template == 'shopping'}">
					<%@ include file="/WEB-INF/terrific/modules/cart-pricecalcbox/cart-pricecalcbox-shopping.jsp" %>
				</c:when>
				<c:when test="${template == 'quote'}">
					<%@ include file="/WEB-INF/terrific/modules/cart-pricecalcbox/cart-pricecalcbox-quote.jsp" %>
				</c:when>
				<c:when test="${template == 'review'}">
					<%@ include file="/WEB-INF/terrific/modules/cart-pricecalcbox/cart-pricecalcbox-review.jsp" %>
				</c:when>
				<c:when test="${template == 'cart'}">
					<%@ include file="/WEB-INF/terrific/modules/cart-pricecalcbox/cart-pricecalcbox-cart.jsp" %>
				</c:when>
				<c:otherwise>
					<%@ include file="/WEB-INF/terrific/modules/cart-pricecalcbox/cart-pricecalcbox.jsp" %>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${template == 'approval-request'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/cart-pricecalcbox/cart-pricecalcbox-approval-request.jsp" %>
				</c:when>
				<c:when test="${template == 'open-order'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/cart-pricecalcbox/cart-pricecalcbox-open-order.jsp" %>
				</c:when>
				<c:when test="${template == 'shopping'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/cart-pricecalcbox/cart-pricecalcbox-shopping.jsp" %>
				</c:when>
				<c:when test="${template == 'quote'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/cart-pricecalcbox/cart-pricecalcbox-quote.jsp" %>
				</c:when>
				<c:when test="${template == 'review'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/cart-pricecalcbox/cart-pricecalcbox-review.jsp" %>
				</c:when>
				<c:when test="${template == 'cart'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/cart-pricecalcbox/cart-pricecalcbox-cart.jsp" %>
				</c:when>
				<c:otherwise>
					<%@ include file="/WEB-INF/terrific/modules/OCI/cart-pricecalcbox/cart-pricecalcbox.jsp" %>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
</terrific:mod>
