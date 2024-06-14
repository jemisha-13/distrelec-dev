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
<%@ tag description="Loading State Module - Templates: default" %>

<%-- Module Specific Attributes --%>
<%@ attribute name="loadingStateMessage" type="java.lang.String" required="false" %>

<%-- Module template selection --%>
<terrific:mod name="loading-state" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
    <%-- set isOCI var --%>
    <c:set var="isOCI" value="false" />
    <sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
        <c:set var="isOCI" value="true" />
    </sec:authorize>

    <c:choose>
        <c:when test="${isOCI eq false}">
            <c:choose>
                <c:when test="${template == 'high-priority'}">
                    <%@ include file="/WEB-INF/terrific/modules/loading-state/loading-state-high-priority.jsp" %>
                </c:when>
                <c:otherwise>
                    <%@ include file="/WEB-INF/terrific/modules/loading-state/loading-state.jsp" %>
                </c:otherwise>
            </c:choose>
        </c:when>
        <c:otherwise>
            <c:choose>
                <c:when test="${template == 'high-priority'}">
                    <%@ include file="/WEB-INF/terrific/modules/OCI/loading-state/loading-state-high-priority.jsp" %>
                </c:when>
                <c:otherwise>
                    <%@ include file="/WEB-INF/terrific/modules/OCI/loading-state/loading-state.jsp" %>
                </c:otherwise>
            </c:choose>
        </c:otherwise>
    </c:choose>
</terrific:mod>
