<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<spring:message code="verify-address.title" var="sTitle"/>
<spring:message code="verify-address.text" var="sText"/>
<spring:message code="validate.error.required" var="sFieldRequired"/>



<h2 class="head">${sTitle}</h2>
<div class="form-box">
	<div class="row base">
		<div class="gu-1 label-box">
			 <input type="checkbox" id="checkbox" class="verify-address-checkbox">
		</div>
		<div class="gu-12">
			<label class="verify-text">${sText}</label>
			<div class="field-required"><i></i><span>${sFieldRequired}</span></div>
		</div>
	</div>
</div>