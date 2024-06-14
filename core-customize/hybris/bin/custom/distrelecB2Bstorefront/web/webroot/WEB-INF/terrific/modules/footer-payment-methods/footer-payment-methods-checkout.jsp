<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="nam" uri="/WEB-INF/tld/namicscommercetags.tld" %>

<c:set var="mainCssClass" value="card" />

<ul class="cr-footer__credit-cards-list">
	<c:forEach items="${paymentMethods}" var="paymentMethodItem">
		<li id=${paymentMethodItem.icon.name} class="cr-footer__credit-cards-list__item">
			<img src="${paymentMethodItem.icon.url}" alt=${paymentMethodItem.icon.altText}>
		</li>
	</c:forEach>
</ul>
