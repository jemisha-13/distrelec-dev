<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>

<c:set var="linkTarget" value="_self"/>
<c:if test="${teaserItemData.linkTarget == 'NEWWINDOW'}">
	<c:set var="linkTarget" value="_blank"/>
</c:if>

<c:choose>
	<c:when test="${fn:contains(wtPageId, 'category') or fn:contains(wtPageId, 'productDetails')}">
		<c:set var="wtAreaCode" value="${wtCatAreaCode}" />
	</c:when>
	<c:otherwise>
		<c:set var="wtAreaCode" value="${wtPageAreaCode}" />
	</c:otherwise>
</c:choose>

<c:if test="${product.buyable or eolWithReplacement}">
	<c:forEach items="${product.activePromotionLabels}"
		var="activePromoLabel" end="0">
		<c:set var="promoLabel" value="${activePromoLabel}" />
		<c:set var="label" value="${activePromoLabel.code}" />
	</c:forEach>
</c:if>

<a class="item-link teaser" href="${teaserItemData.link}" target="${linkTarget}" data-youtubeid="${youTubeID}">
	<div class="bd">
		<img class="teaser-image" alt="${teaserItemData.imageSmall.altText == null ? sImageMissing : teaserItemData.imageSmall.altText}" src="${teaserItemData.imageSmall.url eq null ?  "/_ui/all/media/img/missing_landscape_medium.png" : teaserItemData.imageSmall.url}" />
	</div>
</a>