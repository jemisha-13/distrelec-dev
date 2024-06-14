<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<c:set var="cssFloat" value="${rightFloat ? 'right' : 'left'}" />

<div class="card-box float-${cssFloat }">
	<div class="image">
		<img class="round" alt="${image.altText}" src="${image.url}" />
	</div>
	<div class="name">${name}<c:if test="${not empty jobTitle}">,</c:if></div>
	<div class="jobTitle">${jobTitle}</div>
	<div class="organisation">${organisation}</div>
	<div class="divider"></div>
	<div class="quote">${quote}</div>
	<div class="tipp">${tipp}</div>
	<a title="${ctaText}" class="button" href="${ctaLink}">${ctaText}</a>
</div>
