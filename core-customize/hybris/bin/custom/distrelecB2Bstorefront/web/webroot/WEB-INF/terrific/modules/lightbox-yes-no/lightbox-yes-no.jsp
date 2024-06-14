<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<c:if test="${empty lightboxDenyButtonText}">
	<spring:theme code="lightboxYesNo.cancel" text="Cancel" var="lightboxDenyButtonText" />
</c:if>
<spring:theme code="lightboxYesNo.close" text="Close" var="closeButton" />
<div class="modal base" id="modalYesNo" tabindex="-1">
    <div class="hd">
        <div class="-left"> <h3 class="title"></h3> </div>
        <div class="-right"> <a class="btn btn-close" href="#" data-dismiss="modal" aria-hidden="true">${closeButton}</a> </div>
    </div>
    <div class="bd"> <p></p> </div>
    <div class="ft">
		<input type="submit" class="btn btn-secondary btn-cancel" value="${lightboxDenyButtonText}" data-dismiss="modal" aria-hidden="true">
        <input type="submit" class="btn btn-primary btn-confirm" value="${lightboxConfirmButtonText}" />
    </div>
</div>