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
<%@ tag description="Module: checkout-progressbar - Templates: default" %>
<%@ attribute name="processSteps" required="true" type="java.util.List" %>
<%@ attribute name="isExistingOpenOrder" required="false" type="java.lang.Boolean" %>

<%-- Module template selection --%>
<terrific:mod name="checkout-progressbar" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">

    <c:choose>
        <c:when test="${template == 'import'}">
            <%@ include file="/WEB-INF/terrific/modules/checkout-progressbar/checkout-progressbar-import.jsp" %>
        </c:when>
        <c:when test="${template == 'return-items'}">
            <%@ include file="/WEB-INF/terrific/modules/checkout-progressbar/checkout-progressbar-return-items.jsp" %>
        </c:when>
        <c:otherwise>
            <%@ include file="/WEB-INF/terrific/modules/checkout-progressbar/checkout-progressbar.jsp" %>
        </c:otherwise>
    </c:choose>

</terrific:mod>
