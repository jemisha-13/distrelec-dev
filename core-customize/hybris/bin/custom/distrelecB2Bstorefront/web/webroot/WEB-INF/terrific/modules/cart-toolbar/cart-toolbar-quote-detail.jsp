<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<div class="bd">
	<div class="_left">
		<c:if test="${not empty quotation.quotationDocURL}">
			<a href="${quotation.quotationDocURL}" target="_blank" data-quoteID="${quotation.quotationId}" data-aainteraction="file download" class="pdf-quotes" title="<spring:message code="accountlist.quotationHistoryList.POnumber" text="Quote reference"/>">
				<i class="far fa-file-pdf" aria-hidden="true"></i>
			</a>
		</c:if>
		<ul class="tools-bar">
			<mod:toolsitem template="toolsitem-print" skin="print skin-toolsitem-narrow" tag="li" htmlClasses="tooltip-hover" />
		</ul>
	</div>
		<div class="_right">
			* <spring:message code="text.quote.changes.restricted" text="Quantity changes may be restricted on some quoted items" />
		</div>
</div>
