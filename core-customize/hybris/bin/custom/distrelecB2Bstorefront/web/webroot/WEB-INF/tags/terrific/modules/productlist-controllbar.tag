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

<%-- Module Specific Attributes --%>
<%@ attribute name="pageData" required="false" type="com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData" %>
<%@ attribute name="productListOrders" type="com.namics.distrelec.b2b.core.enums.ProductListOrder[]" required="false" %>
<%@ attribute name="productsInListCount" type="java.lang.Integer" required="false" %>

<c:set var="isOCI" value="false" />
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
    <c:set var="isOCI" value="true" />
</sec:authorize>

<terrific:mod name="productlist-controllbar" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
    <c:choose>
        <c:when test="${isOCI eq false}">
            <c:choose>
                <c:when test="${template == 'favorite'}">
                    <%@ include file="/WEB-INF/terrific/modules/productlist-controllbar/productlist-controllbar-favorite.jsp" %>
                </c:when>
                <c:when test="${template == 'shopping'}">
                    <%@ include file="/WEB-INF/terrific/modules/productlist-controllbar/productlist-controllbar-shopping.jsp" %>
                </c:when>
                <c:when test="${template == 'category'}">
                    <%@ include file="/WEB-INF/terrific/modules/productlist-controllbar/productlist-controllbar-category.jsp" %>
                </c:when>
                <c:when test="${template == 'category-list'}">
                    <%@ include file="/WEB-INF/terrific/modules/productlist-controllbar/productlist-controllbar-category-list.jsp" %>
                </c:when>
                <c:when test="${template == 'plp'}">
                    <%@ include file="/WEB-INF/terrific/modules/productlist-controllbar/productlist-controllbar-plp.jsp" %>
                </c:when>
                <c:when test="${template == 'plp-bottom'}">
                    <%@ include file="/WEB-INF/terrific/modules/productlist-controllbar/productlist-controllbar-plp-bottom.jsp" %>
                </c:when>
                <c:otherwise>
                    <%@ include file="/WEB-INF/terrific/modules/productlist-controllbar/productlist-controllbar.jsp" %>
                </c:otherwise>
            </c:choose>
        </c:when>
        <c:otherwise>
            <%@ include file="/WEB-INF/terrific/modules/OCI/productlist-controllbar/productlist-controllbar-plp.jsp" %>
        </c:otherwise>
    </c:choose>
</terrific:mod>

