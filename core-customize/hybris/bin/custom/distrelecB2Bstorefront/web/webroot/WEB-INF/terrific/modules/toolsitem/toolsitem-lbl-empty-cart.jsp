<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<spring:message code="toolsitem.empty.cart" var="toolsItemEmptyCartTxt" />

<a class="ico ico-list" data-aainteraction="empty cart" title="${toolsItemEmptyCartTxt}">
	<span>${toolsItemEmptyCartTxt}</span>
</a>
