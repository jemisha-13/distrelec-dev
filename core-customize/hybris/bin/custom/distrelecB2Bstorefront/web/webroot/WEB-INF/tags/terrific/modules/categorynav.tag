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
<%@ tag description="Module: categorynav - Templates: default" %>
<%@ attribute name="searchPageData" description="Search Page Data" type="com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData" required="false" %>

<c:set var="isOCI" value="false" />
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
    <c:set var="isOCI" value="true" />
</sec:authorize>

<%-- Module template selection --%>
<terrific:mod name="categorynav" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
    <c:choose>
        <c:when test="${isOCI eq false}">
            <c:choose>
                <c:when test="${template == 'facets'}">
                    <%@ include file="/WEB-INF/terrific/modules/categorynav/categorynav-facets.jsp" %>
                </c:when>
                <c:when test="${template == 'plp'}">
                    <%@ include file="/WEB-INF/terrific/modules/categorynav/categorynav-plp.jsp" %>
                </c:when>
                <c:when test="${template == 'promotion'}">
                    <%@ include file="/WEB-INF/terrific/modules/categorynav/categorynav-promotion.jsp" %>
                </c:when>
                <c:otherwise>
                    <%@ include file="/WEB-INF/terrific/modules/categorynav/categorynav.jsp" %>
                </c:otherwise>
            </c:choose>
        </c:when>
        <c:otherwise>
            <c:choose>
                <c:when test="${template == 'facets'}">
                    <%@ include file="/WEB-INF/terrific/modules/OCI/categorynav/categorynav-facets.jsp" %>
                </c:when>
                <c:when test="${template == 'plp'}">
                    <%@ include file="/WEB-INF/terrific/modules/OCI/categorynav/categorynav-plp.jsp" %>
                </c:when>
                <c:when test="${template == 'promotion'}">
                    <%@ include file="/WEB-INF/terrific/modules/OCI/categorynav/categorynav-promotion.jsp" %>
                </c:when>
                <c:otherwise>
                    <%@ include file="/WEB-INF/terrific/modules/OCI/categorynav/categorynav.jsp" %>
                </c:otherwise>
            </c:choose>
        </c:otherwise>
    </c:choose>
</terrific:mod>
