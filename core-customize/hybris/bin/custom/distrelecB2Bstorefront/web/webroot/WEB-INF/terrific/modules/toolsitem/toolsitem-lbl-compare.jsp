<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%-- Label needs to be in a div to use the ellipsis class --%>
<a class="ico ico-compare" title="<spring:message code='toolsitem.compare' />" data-aainteraction="compare" data-location="${dataLocation}" data-product-code="${productId}">
    <span class="icon-holder">
        <i class="fas fa-exchange-alt" aria-hidden="true"></i>
    </span>
    <div class="text-holder"><spring:message code="toolsitem.compare" /></div>
</a>
