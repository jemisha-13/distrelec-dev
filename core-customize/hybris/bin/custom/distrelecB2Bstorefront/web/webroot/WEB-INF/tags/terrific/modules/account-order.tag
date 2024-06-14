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

<%-- Specific module settings --%>
<%@ tag description="Module: account-order - Templates: default" %>

<%-- Module template selection --%>
<terrific:mod name="account-order" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<c:choose>
		<c:when test="${template == 'addresses'}">
			<%@ include file="/WEB-INF/terrific/modules/account-order/account-order-addresses.jsp" %>
		</c:when>
		<c:when test="${template == 'approval-request'}">
			<%@ include file="/WEB-INF/terrific/modules/account-order/account-order-approval-request.jsp" %>
		</c:when>
		<c:when test="${template == 'order-history'}">
			<%@ include file="/WEB-INF/terrific/modules/account-order/account-order-order-history.jsp" %>
		</c:when>
		<c:when test="${template == 'invoice-history'}">
			<%@ include file="/WEB-INF/terrific/modules/account-order/account-order-invoice-history.jsp" %>
		</c:when>
		<c:when test="${template == 'quotation-history'}">
			<%@ include file="/WEB-INF/terrific/modules/account-order/account-order-quotation-history.jsp" %>
		</c:when>
		<c:when test="${template == 'user-management'}">
			<%@ include file="/WEB-INF/terrific/modules/account-order/account-order-user-management.jsp" %>
		</c:when>
		<c:otherwise>
			<%@ include file="/WEB-INF/terrific/modules/account-order/account-order.jsp" %>
		</c:otherwise>
	</c:choose>
</terrific:mod>