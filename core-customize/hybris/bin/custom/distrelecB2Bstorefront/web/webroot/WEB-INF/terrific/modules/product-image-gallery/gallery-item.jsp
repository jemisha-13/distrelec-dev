<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<spring:message code="productgallery.text" var="sTextHint" />
<c:set var="linkOpeningTag" value='<a href="#">' />
<c:set var="linkClosingTag" value='</a>' />
<c:set var="ghsImageWrapper" value='ghs_image--no-lamp' />

<%-- disable lightbox if it is a catalog plus product --%>
<c:if test="${product.catPlusItem}">
	<c:set var="linkOpeningTag" value='<span class="no-lightbox">' />
	<c:set var="linkClosing" value='</span>' />
</c:if>

<%-- display correct image wrapper class if lamp label is present --%>
<c:if test="${not empty product.energyEfficiency || not empty product.energyClassesFitting || not empty product.energyClassesBuiltInLed}">
	<c:set var="ghsImageWrapper" value='ghs_image--with-lamp' />
</c:if>
<c:set var="landscapeLargeJpg" value="${not empty productImage.landscape_large.url ? productImage.landscape_large.url : '/_ui/all/media/img/missing_landscape_medium.png'}" />
<c:set var="landscapeLarge" value="${not empty productImage.landscape_large_webp.url ? productImage.landscape_large_webp.url : not empty productImage.landscape_large.url ? productImage.landscape_large.url : '/_ui/all/media/img/missing_landscape_medium.png'}" />

<div class="gallery_image">
	<a class="MagicZoom" id="zoom-item-v" data-options="zoomMode: magnifier; zoomPosition: inner; textHoverZoomHint: ${sTextHint}" href="${landscapeLargeJpg}">
	    <img class="images-in-lightbox" ${imageIndex == 0 ? 'itemprop="image"' : ''}
	    alt="<spring:message code='product.page.title.buy' text='Buy' arguments='${product.name}' argumentSeparator='kkkkkkkkk' />"
		width="428" height="239"
		src="${landscapeLargeJpg}"/>
	</a>
	<c:if test="${not empty product.ghsImages}">
			<div class="ghs_image ${ghsImageWrapper}">
				<c:forEach var="url" items="${product.ghsImages}">
					<img class="img-fluid" alt="GHS Image" width="47" height="26" src=${url}>
				</c:forEach>
			</div>
	</c:if>
</div>
