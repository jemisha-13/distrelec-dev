<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>

<c:url value="${ycommerce:cmsLinkComponentUrl(component.link, request)}" var="encodedUrl" />

<mod:content-list 
	title="${component.title}" 
	subTitle="${component.subTitle}" 
	text="${component.text}" 
	url="${encodedUrl}"
/>
