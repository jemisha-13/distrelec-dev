<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<spring:message code="ariba.page.title" var="sPageTitle"/>

<h1 class="base page-title">${sPageTitle}</h1>
<c:set var="target" value="" />
<c:set var="method" value="POST" />
<c:if test="${openInNewWindow}">
	<c:set var="target" value="_top" />
</c:if>           

<c:if test="${not empty form_method}">
	<c:set var="method" value="${form_method}" />
</c:if>


<form:form action="${aribaFormPost}" class="ariba-form" name="AribaOrderForm" method="${method}" target="${target}">
	<input type="hidden" name="${fn:startsWith(aribaCXml, '%3C') ? 'cxml-urlencoded' : 'cxml-base64'}" value="${aribaCXml}" />
	<input type="hidden" id="sendForm" name="sendForm" value="${sendForm}" />
	<pre><c:out value="${aribaCXml}" /></pre>
		<input type="submit" name="submit1" id="submit1" value="SubmitForm" />
</form:form>
