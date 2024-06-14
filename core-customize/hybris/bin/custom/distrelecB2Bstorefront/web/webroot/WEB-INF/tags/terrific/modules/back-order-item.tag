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

<%@ attribute name="orderEntry" type="de.hybris.platform.commercefacades.order.data.OrderEntryData" required="false" %>

<%-- Specific module settings --%>
<%@ tag description="Module: back-order-item - Templates: normal" %>

<c:set var="isOCI" value="false" />
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
    <c:set var="isOCI" value="true" />
</sec:authorize>

<%-- Module template selection --%>
<terrific:mod name="back-order-item" tag="${tag}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
    <c:choose>
        <c:when test="${isOCI eq false}">
            <%@ include file="/WEB-INF/terrific/modules/back-order-item/back-order-item.jsp" %>
        </c:when>
        <c:otherwise>
            <%@ include file="/WEB-INF/terrific/modules/OCI/back-order-item/back-order-item.jsp" %>
        </c:otherwise>
    </c:choose>
</terrific:mod>
