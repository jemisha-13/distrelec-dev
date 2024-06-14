<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="false"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="isVisible" value="${warningComponent.visible}" />
<fmt:formatDate var="visible_from_date" value="${warningComponent.visibleFromDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
<fmt:formatDate var="visible_to_date" value="${warningComponent.visibleToDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
<c:set var="today_date" value="<%=new java.util.Date()%>"/>
<fmt:formatDate var="today_formated_date" value="${today_date}" pattern="yyyy-MM-dd HH:mm:ss"/>  


<c:if test="${(not empty isVisible and isVisible ) or (visible_from_date ge today_date and visible_to_date le today_date)}">
<mod:global-messages
	headline="${warningComponent.headline}"
	body="${warningComponent.body}"
	type="${warningComponent.warningType.code}"
	widthPercent="${warningComponent.componentWidth.code}"
	displayIcon="${warningComponent.displayIcon}"
	ignoreGlobalMessages="true"/>
</c:if>