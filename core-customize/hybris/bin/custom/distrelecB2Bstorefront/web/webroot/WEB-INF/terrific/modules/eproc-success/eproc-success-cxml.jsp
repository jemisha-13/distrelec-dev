<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<spring:message code="ariba.page.title" var="sPageTitle"/>
<h1 class="base page-title">${sPageTitle}</h1>

<form:form action="${cxmlFormPost}" name="CxmlOrderForm" method="post">
	<input type="hidden" name="buyercookie" value="${cxmlParams["BUYERCOOKIE"]}" />
	<input type="hidden" name="prosupplierid" value="${cxmlParams["PROSUPPLIERID"]}" />
	<input type="hidden" name="prosuppliername" value="${cxmlParams["PROSUPPLIERNAME"]}" />
	<input type="hidden" name="shoppingbasket" value="${cxmlDocument}" />
	<input type="hidden" id="sendForm" name="sendForm" value="${sendForm}" />
	<input type="submit" name="submit1" id="submit1" value="SubmitForm" />
	<pre><c:out value="${cxmlDocument}" /></pre>
</form:form>

