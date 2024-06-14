<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<spring:message code="checkoutordersummarynotebox.title" var="sTitle"/>
<spring:message code="checkoutordersummarynotebox.enterText" var="sEnterText"/>

<h2 class="head">${sTitle}</h2>
<div class="box">
	<p><c:out value="${note}" /></p>
</div>
