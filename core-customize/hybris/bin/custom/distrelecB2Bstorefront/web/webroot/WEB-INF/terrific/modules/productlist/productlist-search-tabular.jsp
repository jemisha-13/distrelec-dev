<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="formatprice" tagdir="/WEB-INF/tags/shared/terrific/format" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>

<c:set var="productFamily" value="false"/>
<c:choose>
    <c:when test="${not empty param['filter_productFamilyCode']}">
        <c:set var="productFamily" value="true"/>
    </c:when>
    <c:when test="${not empty param['filter_CuratedProducts']}">
        <c:set var="productFamily" value="true"/>
    </c:when>
    <c:otherwise>
        <c:set var="productFamily" value="false"/>
    </c:otherwise>
</c:choose>
<c:set var="requestPath" value="${requestScope['javax.servlet.forward.request_uri']}"/>
<c:if test="${productFamily =='false' && fn:contains(requestPath, '/new')}">
    <c:set var="productFamily" value="true"/>
</c:if>
<c:if test="${productFamily =='false' && fn:contains(requestPath, '/clearance')}">
    <c:set var="productFamily" value="true"/>
</c:if>
<c:if test="${productFamily =='false' && fn:contains(requestPath, '/manufacturer/')}">
    <c:set var="productFamily" value="true"/>
</c:if>

<c:set var="EditCart" value="${(empty EditCart or EditCart) and not isSubItem}"/>

<c:forEach items="${searchPageData.pagination.productsPerPageOptions}" var="productsPerPageOption">
    <c:if test="${productsPerPageOption.selected}">
        <c:set var="pageSize" value="${productsPerPageOption.value}"/>
    </c:if>
</c:forEach>

<c:set var="codeErpRelevant" value="${product.codeErpRelevant == undefined ? 'x' : product.codeErpRelevant}"/>
<spring:message code="product.family.linkTextExtended" var="sProductFamilyLinkText"/>

<%-- Variables are needed to determine if the product link should be displayed or not --%>
<c:if test="${product.buyable or eolWithReplacement}">
    <c:choose>
        <c:when test="${product.buyable and fn:length(product.activePromotionLabels) gt 0}">
            <c:set var="promotionLabelsPresences" value="true"/>
            <c:forEach items="${product.activePromotionLabels}" var="activePromoLabel" end="0">
                <c:set var="promoLabel" value="${activePromoLabel}"/>
                <c:set var="label" value="${activePromoLabel.code}"/>
                <c:choose>
                    <c:when test="${label eq 'noMover' }">
                        <c:set var="teasertrackingid" value="${wtTeaserTrackingId}.${codeErpRelevant}-cre.-"/>
                    </c:when>
                    <c:when test="${label eq 'new' }">
                        <c:set var="teasertrackingid" value="${wtTeaserTrackingId}.${codeErpRelevant}-new.-"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="teasertrackingid" value="${wtTeaserTrackingId}.${codeErpRelevant}.-"/>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <c:set var="teasertrackingid" value="${wtTeaserTrackingId}.${codeErpRelevant}.-"/>
        </c:otherwise>
    </c:choose>
</c:if>

<div class="search-results productlist" data-current-query-url="${searchPageData.currentQuery.url}"
     data-page="${searchPageData.pagination.currentPage}" data-page-size="${pageSize}">

    <c:set var="maxNumOtherAttrs" value="0"/>
    <!-- Other Attributes HEADER  -->
    <c:set var="attribute_headers_counter" value="0"/>

    <mod:productlist template="products-plp" skin="products-plp" searchPageData="${searchPageData}"/>
</div>
