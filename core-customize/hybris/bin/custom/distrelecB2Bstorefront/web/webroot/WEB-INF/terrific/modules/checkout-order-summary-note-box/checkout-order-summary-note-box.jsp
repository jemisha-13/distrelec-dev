<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/form" %>

<spring:message code="checkoutordersummarynotebox.title" var="sTitle"/>
<spring:message code="checkoutordersummarynotebox.enterText" var="sEnterText"/>

<h2 class="head">${sTitle}</h2>
<div class="form-box">
	<textarea id="note" name="note" maxlength="1000" placeholder="${sEnterText}"><c:out value="${note}" /></textarea>
</div>