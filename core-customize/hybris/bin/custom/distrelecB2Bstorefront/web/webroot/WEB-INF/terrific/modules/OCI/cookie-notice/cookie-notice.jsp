<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>

<spring:message code="shopsettingsfooter.info" var="sInfo" />
<spring:message code="shopsettingsfooter.ok" var="sOkButton" />

<div class="row">
	<div class="info warning">
		<span class="cookie-info">${sInfo}</span>
		<a class="btn btn-ok" href="#">${sOkButton}</a>
	</div>
</div>
