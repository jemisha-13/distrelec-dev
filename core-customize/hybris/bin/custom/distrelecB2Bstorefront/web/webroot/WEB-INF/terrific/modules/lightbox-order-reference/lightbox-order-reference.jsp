<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<spring:theme code="lightbox.orderReference.button.close" var="sCancel" />
<spring:theme code="lightbox.orderReference.button.save" var="sSubmit" />

<div class="modal" tabindex="-1">
	<div class="hd">
		<div class="-left">
			<h3 class="title"><spring:theme code="lightbox.orderReference.title" /></h3>
		</div>
		<div class="-right">
			<a class="btn btn-close" href="#" data-dismiss="modal" aria-hidden="true"><spring:theme code="lightbox.orderReference.button.close" /></a>
		</div>
	</div>
	<div class="bd base">
		<p><spring:theme code="lightbox.orderReference.intro" /></p>
		
		<form:form class="form" method="GET">
			<div class="dropdown-wrapper">
				<label for="orderReference"><spring:message code="lightbox.orderReference.label"/></label>
				<input type="text" id="orderReference" class="field order-reference" name="orderReference" value="${cart.projectNumber}" /></input>
			</div>
		</form:form>
	</div>
	 <div class="ft">
		 <input type="reset" class="btn btn-secondary btn-cancel" value="${sCancel}" />
		 <input type="submit" class="btn btn-primary btn-submit" value="${sSubmit}" />
	</div>
</div>