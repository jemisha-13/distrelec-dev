<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<spring:message code="product.manufacturer.image.missing" text="Image not found" var="sImageMissing"/>

<c:if test="${showLink}">
	<c:choose>
		<c:when test="${showExternalLink}">
			<a href="${distManufacturer.url}" target="_blank">
		</c:when>
		<c:otherwise>
			<a href="${distManufacturer.urlId}">
			<%-- <a href="${distManufacturer.webshopUrl}"> --%>
		</c:otherwise>
	</c:choose>
</c:if>
<c:choose>
	<c:when test="${not empty distManufacturer}">
		<c:if test="${distManufacturer.image.brand_logo.url ne null}">
			<img width="70" height="14" class="manufacturer-logo" alt="${distManufacturer.name == null ? sImageMissing : distManufacturer.name}" src="${distManufacturer.image.brand_logo.url}" />
		</c:if>
	</c:when>
	<c:otherwise>
		<c:if test="${manufacturerLogoUrl ne null}">
			<img width="70" height="14" class="manufacturer-logo" alt="${manufacturerLogoAltText == null ? sImageMissing : manufacturerLogoAltText}" src="${manufacturerLogoUrl}" />
		</c:if>
	</c:otherwise>
</c:choose>

<c:if test="${showLink}">
	</a>
</c:if>
