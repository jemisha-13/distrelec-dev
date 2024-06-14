<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<c:set var="cssErrorClass" value="" />
<c:set var="action" value="/login-data/send" />
<c:set var="customerType" value="${user.customerType.code}" />

<!-- Your Account !-->
<div class="form-box">
	<div class="row">
		<div class="gu-4">
			<h2 class="form-title"><spring:theme code="logindata.yourAccount" /></h2>
		</div>
		<div class="gu-2">
			<c:choose>
				<c:when test="${customerType eq 'B2C'}">
					&nbsp;
				</c:when>
				<c:otherwise>
					<p><spring:message code="logindata.customerType.${customerType}" /></p>
				</c:otherwise>
			</c:choose>
		</div>
		<div class="gu-2 contactid">
			<c:if test="${customerType eq 'B2C' && customerId != ''}">
				<p>ID: ${customerId}</p>
			</c:if>
		</div>
	</div>
</div>