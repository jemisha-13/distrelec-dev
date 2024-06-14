<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<spring:message code="cal.url" var="pdfUrl" />
<spring:eval expression="@configurationService.configuration.getString('factfinder.json.search.url')" var="ffSearchUrl" scope="application" />
<spring:message code="cal.uncalibrated.alternativeText" var="sAlternativeText" />

<div class="cal-box uncalibrated">
	<h3 class="uncalibrated__title"><i class="fa fa-tachometer" aria-hidden="true"></i><spring:message code="cal.uncalibrated.title" /></h3>
	<p class="uncalibrated__text"><a href="${pdfUrl}" class="button read-more-button" target="_blank"><spring:message code="cal.button" /></a><spring:message code="cal.calibrated.text" /></p>
	<div class="uncalibrated__view">
		<div class="calibrated-item" data-product-id="${itemArtNo}" data-ff-search-channel="${ffsearchChannel}" data-ff-search-url="${ffSearchUrl}">
			<div class="center"><a href="${itemArtNo}">${sAlternativeText}<i class="fa fa-angle-right" aria-hidden="true"></i></a></div>
		</div>
	</div>
</div>
