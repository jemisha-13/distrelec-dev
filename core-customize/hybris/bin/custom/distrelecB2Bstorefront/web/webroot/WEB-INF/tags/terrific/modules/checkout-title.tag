<%-- Common module settings --%>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="tag" description="The tag name used for the module context (default=div)" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="htmlClasses" description="The HTML class values to add to the Terrific module element" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="skin" description="The skin used for the module" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="dataConnectors" description="The data-connectors used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="attributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="template" description="The template used for rendering of the module" type="java.lang.String" rtexprvalue="false" %>

<%-- Required tag libraries --%>
<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%-- Specific module settings --%>
<%@ tag description="Module: cart-title - Templates: default" %>
<%@ attribute name="title" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="href" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="showNextButton" type="java.lang.Boolean" rtexprvalue="true" %>
<%@ attribute name="showDownButton" type="java.lang.Boolean" rtexprvalue="true" %>

<%-- Module template selection --%>
<terrific:mod name="checkout-title" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">

	<c:set var="isOCI" value="false" />
	<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
		<c:set var="isOCI" value="true" />
	</sec:authorize>

	<c:choose>
		<c:when test="${isOCI eq false}">
			<%@ include file="/WEB-INF/terrific/modules/checkout-title/checkout-title.jsp" %>
		</c:when>
		<c:otherwise>
			<%@ include file="/WEB-INF/terrific/modules/OCI/checkout-title/checkout-title.jsp" %>
		</c:otherwise>
	</c:choose>
</terrific:mod>
