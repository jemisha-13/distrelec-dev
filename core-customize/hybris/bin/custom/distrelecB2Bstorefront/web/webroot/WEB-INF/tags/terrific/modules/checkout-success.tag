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
<%@ attribute name="orderCode" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="pickupLocation" type="com.namics.distrelec.b2b.facades.order.warehouse.data.WarehouseData" rtexprvalue="true" %>
<%@ attribute name="userId" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="exeededBudget" type="java.lang.String" rtexprvalue="true" %>

<%-- Module template selection --%>
<terrific:mod name="checkout-success" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<c:choose>
		<c:when test="${template == 'approval'}">
			<%@ include file="/WEB-INF/terrific/modules/checkout-success/checkout-success-approval.jsp" %>
		</c:when>
		<c:when test="${template == 'pickup'}">
			<%@ include file="/WEB-INF/terrific/modules/checkout-success/checkout-success-pickup.jsp" %>
		</c:when>
		<c:when test="${template == 'nps'}">
			<%@ include file="/WEB-INF/terrific/modules/checkout-success/checkout-success-nps.jsp" %>
		</c:when>		
		<c:otherwise>
			<%@ include file="/WEB-INF/terrific/modules/checkout-success/checkout-success.jsp" %>
		</c:otherwise>
	</c:choose>
</terrific:mod>
