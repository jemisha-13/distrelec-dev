<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:url value="/" var="shopRoot" />

<div class="row">
	<div class="gu-4">&#160;</div>
	<div class="gu-4">
		<a class="btn btn-primary" href="${shopRoot}"><spring:message code="newsletter.success.backToShop" /><i></i></a>
	</div>
</div>
