<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<c:if test="${empty lightboxDenyButtonText}">
	<spring:theme code="lightboxYesNo.cancel" text="Cancel" var="lightboxDenyButtonText" />
</c:if>
<spring:theme code="lightboxYesNo.close" text="Close" var="closeButton" />
<div class="modal base" id="modalVideo" tabindex="-1">
    <div class="hd"> <div class="-right"> <a class="btn btn-close" href="#" data-dismiss="modal" aria-hidden="true"><spring:message code="base.close" /></a> </div></div>
    <div class="bd"> <div class="videoContainer"> </div></div>
</div>