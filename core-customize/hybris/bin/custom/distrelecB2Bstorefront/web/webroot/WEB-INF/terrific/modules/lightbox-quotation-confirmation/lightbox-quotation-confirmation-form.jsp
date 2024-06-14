<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<div class="modal" id="modalQuotation" tabindex="-1">
	<div class="hd">
		<div class="-left">
			<h3 class="title confirmation-quote-message hidden"><spring:message code="quote.request.thank.you" /></h3>
			<h3 class="title limit-quote-message hidden"><spring:message code="quote.limit.message.title" /></h3>
			<h3 class="title error-quote-message hidden"><spring:message code="quote.resubmit.error.title" /></h3>
		</div>
		<div class="-right">
			<a class="btn btn-close quoteformclose" href="#" data-dismiss="modal" aria-hidden="true"><spring:message code="base.close" /></a>
		</div>
	</div>
	<div class="bd">

		<%-- confirmation --%>
		<p class="hidden confirmation-quote-message"><spring:message code="quote.request.confirmation" /></p>
		<p class="hidden limit-quote-message"><spring:message code="quote.limit.message" /></p>
		<p class="hidden error-quote-message"><spring:message code="quote.resubmit.error" /></p>
	</div>
</div>
