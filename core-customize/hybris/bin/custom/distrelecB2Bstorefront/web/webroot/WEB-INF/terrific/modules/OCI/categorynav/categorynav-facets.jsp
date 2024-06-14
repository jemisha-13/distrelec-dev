<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"  %>

<c:if test="${not empty searchPageData.categories.values && not empty categoryDisplayDataList && !isProductListLevel}">
	<div class="mod-categorynav__wrapper" data-total-categories="${fn:length(categoryDisplayDataList)}">

		<c:forEach items="${categoryDisplayDataList}" var="category1data" varStatus="items" end="${fn:length(categoryDisplayDataList)}">

			<c:url value="${category1data.url}" var="facetValueQueryUrl"/>

			<div class="mod-categorynav__wrapper__item">
				<div class="mod-categorynav__wrapper__item__header">
					<a href="${facetValueQueryUrl}">
						<c:set var="productImage" value="${category1data.image}"/>
						<c:set var="portraitSmallJpg" value="${not empty productImage.portrait_small.url ? productImage.portrait_small.url : '/_ui/all/media/img/missing_portrait_small.png' }"/>
						<c:set var="portraitSmallWebP" value="${not empty productImage.portrait_small_webp.url ? productImage.portrait_small_webp.url : portraitSmallJpg}"/>
						<picture>
							<source srcset="${portraitSmallWebP}">
							<img alt="${category1data.name} Thumbnail" src="${portraitSmallJpg}"/>
						</picture>
						<h3>${category1data.name} (${category1data.count})</h3>
					</a>
				</div>
			</div>

		</c:forEach>

	</div>

</c:if>



