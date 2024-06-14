<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>

<c:if test="${not empty slots['PromotionLinks'] && not empty slots['PromotionLinks'].cmsComponents}">
<h2 class="list-title"><spring:message code="text.promotionLinks"/></h2>
<ul>
	<cms:slot var="feature" contentSlot="${slots['PromotionLinks']}">
		<li><cms:component component="${feature}"/></li>
	</cms:slot>
</ul>
</c:if>