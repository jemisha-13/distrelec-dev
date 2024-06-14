<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<spring:message code="related.topsellerproducts" text="Top seller products:" var="sTopSeller"/>
<spring:message code="related.products" text="Related Products:" var="sRelatedProducts"/>
<spring:message code="related.newproducts" arguments="${sourceCategoryName}" text="New Products:" var="sNewProducts"/>

<c:set var="sourceCategoryName" value="${sourceCategoryName}" />

<%-- RELATED_PRODUCT --%>
<c:if test="${not empty relatedProducts and not empty relatedProducts[0].values}">
       <div class="related-page-link">
           <b class="related-page-link__title">${sRelatedProducts}</b>
           <c:forEach items="${relatedProducts[0].values}" var="product" varStatus="status">
               <c:if test="${status.count > 1}"> | </c:if>
               <a href="${product.url}" class="related-page-link__link">${product.name}</a>
           </c:forEach>
       </div>
</c:if>

<%-- NEW_ARRIVAL_PRODUCT --%>
<c:if test="${not empty newArrivalProducts and not empty newArrivalProducts[0].values}">
       <div class="related-page-link">
           <b class="related-page-link__title">${sNewProducts}</b>
           <c:forEach items="${newArrivalProducts[0].values}" var="product" varStatus="status">
               <c:if test="${status.count > 1}"> | </c:if>
               <a href="${product.url}" class="related-page-link__link">${product.name}</a>
           </c:forEach>
       </div>
</c:if>

<%-- TOP_SELLER_PRODUCT --%>
<c:if test="${not empty relatedTopSellerProducts and not empty relatedTopSellerProducts[0].values}">
       <div class="related-page-link">
           <b class="related-page-link__title">${sTopSeller}</b>
           <c:forEach items="${relatedTopSellerProducts[0].values}" var="product" varStatus="status">
               <c:if test="${status.count > 1}"> | </c:if>
               <a href="${product.url}" class="related-page-link__link">${product.name}</a>
           </c:forEach>
       </div>
</c:if>
