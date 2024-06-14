<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="oscache" uri="http://www.opensymphony.com/oscache" %>
<spring:message code="related.manufacturers" text="Related Manufacturers:" var="sRelatedManufacturers"/>
<spring:message code="related.categories" text="Related Categories:" var="sRelatedCategories"/>

<c:set var="sourceCategoryName" value="${sourceCategoryName}" />
<oscache:cache key="mesh-linking-categorylinks-${categoryCode}" time="${cachingTimeCategoryLink}" scope="application" language="${currentLanguage.isocode}">
    <c:set var="ilRelatedData" value="${categoryPageController.getCategoryRelatedData(categoryCode)}" />
    <c:if test="${not empty ilRelatedData and not empty ilRelatedData.relatedDataMap}">
        <c:forEach items="${ilRelatedData.relatedDataMap}" var="entry">
            <c:choose>
                <c:when test="${entry.key.code eq 'RELATED_MANUFACTURER'}">
                    <c:set var="relatedManufacturers" value="${entry.value}"/>
                </c:when>
                <c:when test="${entry.key.code eq 'RELATED_CATEGORY'}">
                    <c:set var="relatedCategories" value="${entry.value}"/>
                </c:when>
                <c:when test="${entry.key.code eq 'RELATED_PRODUCT'}">
                    <c:set var="relatedProducts" value="${entry.value}"/>
                </c:when>
                <c:when test="${entry.key.code eq 'TOP_SELLER_PRODUCT'}">
                    <c:set var="relatedTopSellerProducts" value="${entry.value}"/>
                </c:when>
                <c:when test="${entry.key.code eq 'NEW_ARRIVAL_PRODUCT'}">
                    <c:set var="newArrivalProducts" value="${entry.value}"/>
                </c:when>
            </c:choose>
        </c:forEach>

        <%-- For categories, related manufacturers comes first --%>
        <c:if test="${not empty ilRelatedData and ilRelatedData.type.code eq 'category'}">
            <%-- RELATED_MANUFACTURER --%>
            <c:if test="${not empty relatedManufacturers and not empty relatedManufacturers[0].values}">
                <div class="related-page-link">
                    <b class="related-page-link__title">${sRelatedManufacturers}</b>
                    <c:forEach items="${relatedManufacturers[0].values}" var="manufacturer" varStatus="status">
                        <c:if test="${status.count > 1}"> | </c:if>
                        <a href="${manufacturer.url}" class="related-page-link__link">${manufacturer.name} </a>
                    </c:forEach>
                </div>
            </c:if>

            <%-- RELATED_CATEGORY --%>
            <c:if test="${not empty relatedCategories and not empty relatedCategories[0].values}">
                <div class="related-page-link">
                    <b class="related-page-link__title">${sRelatedCategories}</b>
                    <c:forEach items="${relatedCategories[0].values}" var="category" varStatus="status">
                        <c:if test="${status.count > 1}"> | </c:if>
                        <a href="${category.url}" class="related-page-link__link">${category.name}</a>
                    </c:forEach>
                </div>
            </c:if>
        </c:if>
        <%-- For manufacturers, related categories comes first --%>
        <c:if test="${not empty ilRelatedData and ilRelatedData.type.code eq 'manufacturer'}">
            <%-- RELATED_CATEGORY --%>
            <c:if test="${not empty relatedCategories and not empty relatedCategories[0].values}">
                <div class="related-page-link">
                    <b class="related-page-link__title">${sRelatedCategories}</b>
                    <c:forEach items="${relatedCategories[0].values}" var="category" varStatus="status">
                        <c:if test="${status.count > 1}"> | </c:if>
                        <a href="${category.url}" class="related-page-link__link">${category.name}</a>
                    </c:forEach>
                </div>
            </c:if>
            <%-- RELATED_MANUFACTURER --%>
            <c:if test="${not empty relatedManufacturers and not empty relatedManufacturers[0].values}">
                <div class="related-page-link">
                    <b class="related-page-link__title">${sRelatedManufacturers}</b>
                    <c:forEach items="${relatedManufacturers[0].values}" var="manufacturer" varStatus="status">
                        <c:if test="${status.count > 1}"> | </c:if>
                        <a href="${manufacturer.url}" class="related-page-link__link">${manufacturer.name} </a>
                    </c:forEach>
                </div>
            </c:if>
        </c:if>

        <%-- Include the different types of related products --%>
        <%@ include file="mesh-linking-products.jsp" %>

    </c:if>
</oscache:cache>
