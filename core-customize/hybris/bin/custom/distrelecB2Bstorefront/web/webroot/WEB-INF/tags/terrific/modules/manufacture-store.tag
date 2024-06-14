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

<%@ attribute name="manufacturers" type="java.util.Map" required="true" %>

<%-- Specific module settings --%>
<%@ tag description="Module: manufacture-store - Templates: default" %>

<%-- Module template selection --%>
<terrific:mod name="manufacture-store" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<c:choose>
		<c:when test="${template == 'overview-alphabet'}">
			<%@ include file="/WEB-INF/terrific/modules/manufacture-store/manufacture-store-overview-alphabet.jsp" %>
		</c:when>
		<c:when test="${template == 'overview-list'}">
			<%@ include file="/WEB-INF/terrific/modules/manufacture-store/manufacture-store-overview-list.jsp" %>
		</c:when>
	</c:choose>
</terrific:mod>
