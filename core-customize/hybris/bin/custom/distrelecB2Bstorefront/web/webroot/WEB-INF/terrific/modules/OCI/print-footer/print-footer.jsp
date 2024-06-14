<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<spring:message code="text.store.dateformat" var="datePattern" />

<div class="printFooter">	${printFooterContent} </div>
<div class="timedate">
	<jsp:useBean id="nowOCI" class="java.util.Date" />
	<%--Following setLocale doesn't appear to have an influence--%>
	<fmt:setLocale value="${currentSalesOrg.countryIsocode}"/>
	<fmt:formatDate value="${nowOCI}" timeStyle="short" type="time" />&nbsp;
	<fmt:formatDate value="${nowOCI}" dateStyle="medium" timeStyle="short" type="date" pattern="${datePattern}" />
</div>
