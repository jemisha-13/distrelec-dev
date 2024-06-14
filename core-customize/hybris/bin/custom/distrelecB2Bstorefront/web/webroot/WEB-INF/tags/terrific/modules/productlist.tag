<%-- Common module settings --%>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="tag" description="The tag name used for the module context (default=div)" type="java.lang.String"
              rtexprvalue="false" %>
<%@ attribute name="htmlClasses" description="The HTML class values to add to the Terrific module element"
              type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="skin" description="The skin used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="dataConnectors" description="The data-connectors used for the module" type="java.lang.String"
              rtexprvalue="false" %>
<%@ attribute name="attributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'"
              type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="template" description="The template used for rendering of the module" type="java.lang.String"
              rtexprvalue="false" %>


<%-- Required tag libraries --%>
<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%-- Module Specific Attributes --%>
<%@ attribute name="searchPageData" required="false"
              type="com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData" %>
<%@ attribute name="currentList" type="com.namics.distrelec.b2b.facades.wishlist.data.NamicsWishlistData"
              required="false" %>
<%@ attribute name="productReferences" type="java.util.ArrayList" required="false" %>
<%@ attribute name="matchingProducts" type="java.util.ArrayList" required="false" %>
<%@ attribute name="showProductFamilyButton" type="java.lang.Boolean" required="false" %>
<%@ attribute name="detailPageShowMorePostfix" type="java.lang.String" required="false" %>

<%-- Module template selection --%>
<terrific:mod name="productlist" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}"
              dataConnectors="${dataConnectors}" attributes="${attributes}">
    <c:set var="isOCI" value="false"/>
    <sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
        <c:set var="isOCI" value="true"/>
    </sec:authorize>

    <c:choose>
        <c:when test="${isOCI eq false}">
            <c:choose>
                <c:when test="${template == 'search'}">
                    <%@ include file="/WEB-INF/terrific/modules/productlist/productlist-search.jsp" %>
                </c:when>
                <c:when test="${template == 'filters'}">
                    <%@ include file="/WEB-INF/terrific/modules/productlist/productlist-filters.jsp" %>
                </c:when>
                <c:when test="${template == 'structure'}">
                    <%@ include file="/WEB-INF/terrific/modules/productlist/productlist-structure.jsp" %>
                </c:when>
                <c:when test="${template == 'products-plp'}">
                    <%@ include file="/WEB-INF/terrific/modules/productlist/productlist-products-plp.jsp" %>
                </c:when>
                <c:when test="${template == 'search-tabular'}">
                    <%@ include file="/WEB-INF/terrific/modules/productlist/productlist-search-tabular.jsp" %>
                </c:when>
                <c:when test="${template == 'search-tabular-simple'}">
                    <%@ include file="/WEB-INF/terrific/modules/productlist/productlist-search-tabular-simple.jsp" %>
                </c:when>
                <c:when test="${template == 'search-tabular-dot-tpl'}">
                    <%@ include file="/WEB-INF/terrific/modules/productlist/productlist-search-tabular-dot-tpl.jsp" %>
                </c:when>
                <c:when test="${template == 'favorite'}">
                    <%@ include file="/WEB-INF/terrific/modules/productlist/productlist-favorite.jsp" %>
                </c:when>
                <c:when test="${template == 'shopping'}">
                    <%@ include file="/WEB-INF/terrific/modules/productlist/productlist-shopping.jsp" %>
                </c:when>
                <c:when test="${template == 'detail-page'}">
                    <%@ include file="/WEB-INF/terrific/modules/productlist/productlist-detail-page.jsp" %>
                </c:when>
                <c:when test="${template == 'bom'}">
                    <%@ include file="/WEB-INF/terrific/modules/productlist/productlist-bom.jsp" %>
                </c:when>
                <c:when test="${template == 'calculate'}">
                    <%@ include file="/WEB-INF/terrific/modules/productlist/productlist-calculate.jsp" %>
                </c:when>

                <c:otherwise>
                    <%@ include file="/WEB-INF/terrific/modules/productlist/productlist.jsp" %>
                </c:otherwise>
            </c:choose>
        </c:when>
        <c:otherwise>
            <c:choose>
                <c:when test="${template == 'search'}">
                    <%@ include file="/WEB-INF/terrific/modules/OCI/productlist/productlist-search.jsp" %>
                </c:when>
                <%--<c:when test="${template == 'filters'}">
                    <%@ include file="/WEB-INF/terrific/modules/OCI/productlist/productlist-filters.jsp" %>
                </c:when>--%>
                <c:when test="${template == 'structure'}">
                    <%@ include file="/WEB-INF/terrific/modules/OCI/productlist/productlist-structure.jsp" %>
                </c:when>
                <c:when test="${template == 'products-plp'}">
                    <%@ include file="/WEB-INF/terrific/modules/OCI/productlist/productlist-products-plp.jsp" %>
                </c:when>
                <c:when test="${template == 'search-tabular'}">
                    <%@ include file="/WEB-INF/terrific/modules/OCI/productlist/productlist-search-tabular.jsp" %>
                </c:when>
                <c:when test="${template == 'search-tabular-simple'}">
                    <%@ include
                            file="/WEB-INF/terrific/modules/OCI/productlist/productlist-search-tabular-simple.jsp" %>
                </c:when>
                <c:when test="${template == 'search-tabular-dot-tpl'}">
                    <%@ include
                            file="/WEB-INF/terrific/modules/OCI/productlist/productlist-search-tabular-dot-tpl.jsp" %>
                </c:when>
                <c:when test="${template == 'favorite'}">
                    <%@ include file="/WEB-INF/terrific/modules/OCI/productlist/productlist-favorite.jsp" %>
                </c:when>
                <c:when test="${template == 'shopping'}">
                    <%@ include file="/WEB-INF/terrific/modules/OCI/productlist/productlist-shopping.jsp" %>
                </c:when>
                <c:when test="${template == 'detail-page'}">
                    <%@ include file="/WEB-INF/terrific/modules/OCI/productlist/productlist-detail-page.jsp" %>
                </c:when>
                <c:when test="${template == 'bom'}">
                    <%@ include file="/WEB-INF/terrific/modules/OCI/productlist/productlist-bom.jsp" %>
                </c:when>
                <c:when test="${template == 'calculate'}">
                    <%@ include file="/WEB-INF/terrific/modules/OCI/productlist/productlist-calculate.jsp" %>
                </c:when>
                <c:otherwise>
                    <%@ include file="/WEB-INF/terrific/modules/productlist/productlist.jsp" %>
                </c:otherwise>
            </c:choose>
        </c:otherwise>
    </c:choose>
</terrific:mod>
