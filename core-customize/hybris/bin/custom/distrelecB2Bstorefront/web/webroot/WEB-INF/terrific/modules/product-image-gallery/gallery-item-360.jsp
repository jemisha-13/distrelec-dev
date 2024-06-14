<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div data-slide-id="magic360" class="gallery-item zoom-gallery-slide magic-360" data-position="${imageIndex}" data-teasertrackingid="${teasertrackingid}">
	<a id="zp-figure-spin" class="Magic360" href="#"
		data-magic360-options="
			autospin: infinite;
			autospin-speed: 6000;
			columns: ${productImage360.cols};
			filename: ${productImage360.pattern};
			fullscreen: false;
			reverse-column: true;
			right-click: true;
			rows: ${productImage360.rows};
			speed: 50;
		"
	>
		<img src="${productImage360.firstImageSmallPath}"
			class="images-in-lightbox"
		    width="522" height="291"
			alt="${product.name}<c:out value=' '/><spring:message code='product.page.title.buy' text='Buy' />" 
			data-index="${imageIndex}" 
			data-lightbox="${productImage360.firstImageMediumPath}" 
			data-lightbox-pattern="${productImage360.pattern}" 
			data-lightbox-magnifier="${productImage360.firstImageLargePath}" 
			data-is-magic-360="true"
			data-m360-auto-spin="infinite"
			data-m360-autospin-speed="6000"
			data-m360-cols="${productImage360.cols}"
			data-m360-reverse-column="true"
			data-m360-right-click="true"
			data-m360-rows="${productImage360.rows}" 
			data-m360-speed="50"
			data-title="${product.name}"
			data-subtitle="<c:out value='${product.distManufacturer.name}'/>"
		/>
	</a>
</div>