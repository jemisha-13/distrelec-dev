<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="tag" description="The tag name used for the module context (default=div)" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="htmlClasses" description="The HTML class values to add to the Terrific module element" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="skin" description="The skin used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="dataConnectors" description="The data-connectors used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="attributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="template" description="The template used for rendering of the module" type="java.lang.String" rtexprvalue="false" %>
<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="searchPageData" type="de.hybris.platform.commerceservices.search.pagedata.SearchPageData" %>
<%@ attribute name="isInitialVisit" description="If cookie is empty, the lightbox will be showed automatically" type="java.lang.Boolean" rtexprvalue="true" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="isOCI" value="false" />
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
    <c:set var="isOCI" value="true" />
</sec:authorize>

<terrific:mod name="servicenav" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
    <c:choose>
        <c:when test="${isOCI eq false}">
            <c:choose>
                <c:when test="${template == 'checkout'}">
                    <%@ include file="/WEB-INF/terrific/modules/servicenav/servicenav-checkout.jsp" %>
                </c:when>
                <c:otherwise>
                    <%@ include file="/WEB-INF/terrific/modules/servicenav/servicenav.jsp" %>
                </c:otherwise>
            </c:choose>
        </c:when>
        <c:otherwise>
            <%@ include file="/WEB-INF/terrific/modules/OCI/servicenav/servicenav.jsp" %>
        </c:otherwise>
    </c:choose>
</terrific:mod>
