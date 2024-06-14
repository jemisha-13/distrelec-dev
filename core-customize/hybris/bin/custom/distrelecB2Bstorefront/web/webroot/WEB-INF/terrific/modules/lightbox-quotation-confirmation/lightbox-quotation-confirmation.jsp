<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<div class="modal" id="modalQuotationConfirmation" tabindex="-1">
	<div class="hd">
		<div class="-left">
			<h3 class="title"><spring:message code="lightboxquotationconfirmation.title" /></h3>
		</div>
		<div class="-right">
			<a class="btn btn-close" href="#" data-dismiss="modal" aria-hidden="true"><spring:message code="base.close" /></a>
		</div>
	</div>
	<div class="bd">
		<%-- confirmation --%>
		<p><spring:message code="lightboxquotationconfirmation.text" /></p>
	</div>
</div>
