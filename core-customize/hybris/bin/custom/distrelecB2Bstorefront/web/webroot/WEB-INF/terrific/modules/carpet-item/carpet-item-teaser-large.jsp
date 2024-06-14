<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>

<c:set var="linkTarget" value="_self"/>
<c:if test="${teaserItemData.linkTarget == 'NEWWINDOW'}">
	<c:set var="linkTarget" value="_blank"/>
</c:if>

<a class="js-item-link" href="${teaserItemData.link}" target="${linkTarget}">
	<c:if test="${not empty teaserItemData.title}">
		<div class="hd base ellipsis" title="${teaserItemData.title}">
			${teaserItemData.title}
		</div>
	</c:if>
	<div class="bd">

	<c:set var="image" value="${teaserItemData.imageLarge}"/>
	<c:if test="${empty teaserItemData.imageLarge}">
		<c:set var="image" value="${teaserItemData.imageSmall}"/>
	</c:if>
		<img class="item-image" alt="${image.altText == null ? sImageMissing : image.altText}" src="${image.url eq null ?  "/_ui/all/media/img/missing_landscape_medium.png" : image.url}" />
	</div>
	<div class="ft">
		<h3 class="base item-name">${teaserItemData.text}</h3>
		<span class="subtext ellipsis" title="${teaserItemData.subText}">${teaserItemData.subText}</span>
	</div>
</a>