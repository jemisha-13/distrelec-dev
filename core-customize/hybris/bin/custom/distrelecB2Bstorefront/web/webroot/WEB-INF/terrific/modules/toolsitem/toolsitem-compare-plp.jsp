<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<a class="ico ico-compare ico-compare--plp-compare" data-aainteraction="compare" data-position="${positionIndex}" data-product-code="${productId}" title="<spring:message code='toolsitem.compare' />">
	<i class="fas fa-exchange-alt"></i>
	<input class="" type="checkbox" value="1" data-product-code="${productId}"><span class="label"><spring:message code='toolsitem.compare' /></span>
</a>
