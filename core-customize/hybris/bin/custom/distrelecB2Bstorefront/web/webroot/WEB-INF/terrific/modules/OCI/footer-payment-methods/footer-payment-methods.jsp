<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="nam" uri="/WEB-INF/tld/namicscommercetags.tld" %>

<c:set var="mainCssClass" value="card" />

<h3 class="${mainCssClass}__title">	<spring:theme code="footer-payment-methods.teaser" text="" /> </h3>
<ul class="${mainCssClass}__list ${mainCssClass}__list-OCI">
	<c:forEach items="${paymentMethods}" var="paymentMethodItem">
		<c:set var="listItem">
			<span class="${mainCssClass}__list__wrapper__image"><img class="image" src="${paymentMethodItem.icon.url}" alt="${paymentMethodItem.title}" /></span>
			<span class="${mainCssClass}__list__wrapper__value"><span class="value">${paymentMethodItem.title}</span></span>
		</c:set>
		<li class="${mainCssClass}__list__item">
			<c:choose>
				<c:when test="${not empty paymentMethodItem.url}">
					<a class="${mainCssClass}__list__wrapper" href="${paymentMethodItem.url}" title="${paymentMethodItem.title}"> ${listItem} </a>
				</c:when>
				<c:otherwise>
					<div class="${mainCssClass}__list__wrapper"> ${listItem} </div>
				</c:otherwise>
			</c:choose>
		</li>
	</c:forEach>
</ul>
