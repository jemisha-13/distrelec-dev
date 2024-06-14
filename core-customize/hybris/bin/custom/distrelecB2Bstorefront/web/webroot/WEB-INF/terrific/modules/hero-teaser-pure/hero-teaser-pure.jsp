<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="visibleItems" value="${fn:length(heroTeaserData)}"/>

<h2 class="base padding-left">${title}</h2>


<section class="teaser-content ${componentWidth} c ${componentHeight}">
	<div class="teaser-content-carousel">
		
		<c:forEach items="${heroTeaserData}" var="itemData" varStatus="status">

			<c:set var="linkTarget" value="_self"/>
			<c:if test="${itemData.linkTarget == 'NEWWINDOW'}">
				<c:set var="linkTarget" value="_blank"/>
			</c:if>

			<c:choose>
				<c:when test="${itemData.video}"> 
					<div class="teaser-item video" data-teasertrackingid="${teaserTrackingId}" data-youtubeid="${itemData.youTubeID}" data-position="${status.index + 1}" data-video-id="${itemData.videoData.brightcoveVideoId}" data-video-autoplay="${itemData.videoAutoplay}">
						<c:set var="linkTarget" value="_self"/>
						<c:if test="${itemData.linkTarget == 'NEWWINDOW'}">
							<c:set var="linkTarget" value="_blank"/>
						</c:if>
			
						<mod:video videoId="${itemData.videoData.brightcoveVideoId}" playerId="3101360480001" videoWidth="740" videoHeight="300" />
						
						<a href="${itemData.url}" class="teaser-item-url image-side-link" target="${linkTarget}">
							<img class="item-image c ${componentWidth}" alt="${itemData.picture.altText}" src="${itemData.picture.url}" />
						</a>
					</div>
				</c:when>
				<c:otherwise>
					<!-- Position is used for the Module JS Logic !-->
					<div class="teaser-item hero-teaser-pure" data-youtubeid="${itemData.youTubeID}" data-position="${status.index + 1}">
						<a href="${itemData.url}" class="teaser-item-url" target="${linkTarget}">
							<%-- HeroTeaserPure shows only an image --%>
							<img class="item-image c ${componentWidth}" alt="${itemData.picture.altText == null ? sImageMissing : itemData.picture.altText}" src="${itemData.picture.url eq null ? '/_ui/all/media/img/missing_landscape_large.png' : itemData.picture.url}" />
						</a>
					</div>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</div>

	<div class="direction-arrows c ${componentHeight}">
		<div class="arrow_prev-teaser arrow active">
	    	<a id="scroll-prev" href="#" class="btn"></a>
		</div>
		<div class="arrow_next-teaser arrow active">
		    <a id="scroll-next" href="#" class="btn ${componentWidth}"></a>
		</div>		
	</div>
	
	
</section>



<section class="teaser-dots ${componentWidth}">
	<!-- Position is used for the Module JS Logic !-->
	<div class="teaser-dot-wrapper" data-autoplay="${autoplay}" data-timeout="${autoplayTimeout}" data-items-visible="${visibleItems}">
		<c:forEach items="${heroTeaserData}" var="itemData" varStatus="status">
			<a href="#" class="dot${status.index == 0 ? ' first-item' : ''}" data-product-id="" data-position="${status.index + 1}"><span></span></a>
		</c:forEach>
	</div>
</section>

<script type="text/javascript">

	var teaserGenericItemDataLayer = ${teaserGenericItemDataLayer}
	
	
</script>


