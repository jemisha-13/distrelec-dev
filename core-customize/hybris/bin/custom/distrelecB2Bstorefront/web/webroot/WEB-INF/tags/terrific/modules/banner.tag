<%-- Common module settings --%>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="tag" description="The tag name used for the module context (default=div)" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="skin" description="The skin used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="dataConnectors" description="The data-connectors used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="attributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="template" description="The template used for rendering of the module" type="java.lang.String" rtexprvalue="false" %>


<%-- Required tag libraries --%>
<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%-- Specific module settings --%>
<%@ tag description="Module: banner - Templates: normal" %>

<%-- Module template selection --%>
<terrific:mod name="banner" tag="${tag}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
    <%-- set isOCI var --%>
    <c:set var="isOCI" value="false" />
    <sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
        <c:set var="isOCI" value="true" />
    </sec:authorize>

    <c:choose>
        <c:when test="${isOCI eq false}">
            <c:choose>
                <c:when test="${mode eq 'NORMAL'}">
                    <%@ include file="/WEB-INF/terrific/modules/banner/banner-normal.jsp" %>
                </c:when>
                <c:when test="${mode eq 'CARD'}">
                    <%@ include file="/WEB-INF/terrific/modules/banner/banner-card.jsp" %>
                </c:when>
                <c:when test="${mode eq 'ARTICLE'}">
                    <%@ include file="/WEB-INF/terrific/modules/banner/banner-article.jsp" %>
                </c:when>
                <c:when test="${mode eq 'HERO'}">
                    <%@ include file="/WEB-INF/terrific/modules/banner/banner-hero.jsp" %>
                </c:when>
            </c:choose>
        </c:when>
        <c:otherwise>
            <c:choose>
                <c:when test="${mode eq 'NORMAL'}">
                    <%@ include file="/WEB-INF/terrific/modules/OCI/banner/banner-normal.jsp" %>
                </c:when>
                <c:when test="${mode eq 'CARD'}">
                    <%@ include file="/WEB-INF/terrific/modules/OCI/banner/banner-card.jsp" %>
                </c:when>
                <c:when test="${mode eq 'ARTICLE'}">
                    <%@ include file="/WEB-INF/terrific/modules/OCI/banner/banner-article.jsp" %>
                </c:when>
                <c:when test="${mode eq 'HERO'}">
                    <%@ include file="/WEB-INF/terrific/modules/OCI/banner/banner-hero.jsp" %>
                </c:when>
            </c:choose>
        </c:otherwise>
    </c:choose>
</terrific:mod>
