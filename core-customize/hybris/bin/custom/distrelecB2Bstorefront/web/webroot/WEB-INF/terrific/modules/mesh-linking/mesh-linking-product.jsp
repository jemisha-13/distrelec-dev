<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="oscache" uri="http://www.opensymphony.com/oscache" %>
<oscache:cache key="mesh-linking-product-${product.code}" time="${cachingTimeCategoryLink}" scope="application" language="${currentLanguage.isocode}">
	<c:set var="ilRelatedData" value="${productPageController.getProductRelatedData(product)}" />
	<c:if test="${not empty ilRelatedData and not empty ilRelatedData.relatedDataMap}">
		<c:forEach items="${ilRelatedData.relatedDataMap}" var="entry">
			<c:set var="div_id" value="related_products" />
			<c:choose>
				<c:when test="${entry.key.code eq 'RELATED_MANUFACTURER'}">
					<spring:message code="related.manufacturers" text="Related Manufacturers" var="related_title" />
					<c:set var="div_id" value="related_manufacturers" />
				</c:when>
				<c:when test="${entry.key.code eq 'RELATED_CATEGORY'}">
					<spring:message code="related.categories" text="Related Categories" var="related_title" />
					<c:set var="div_id" value="related_categories" />
				</c:when>
				<c:when test="${entry.key.code eq 'RELATED_PRODUCT'}">
					<spring:message code="related.products" text="Related Products" var="related_title" />
				</c:when>
			</c:choose>

			<div id="${div_id}">
				<b><c:out value="${related_title}" />: </b>
				<c:forEach items="${entry.value[0].values}" var="ciValue" varStatus="status">
					<c:if test="${status.count > 1}"> | </c:if>
					<a href="${ciValue.url}">${ciValue.name}</a>
				</c:forEach>
			</div>
		</c:forEach>
	</c:if>
</oscache:cache>
