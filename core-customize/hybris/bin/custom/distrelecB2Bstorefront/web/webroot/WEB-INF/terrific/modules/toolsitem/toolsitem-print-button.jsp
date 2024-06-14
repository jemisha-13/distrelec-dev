<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<spring:message code="toolsitem.print" var="sPrintLabel" />

<a class="ico ico-print mat-button mat-button--action-red" href="#" title="<spring:message code="toolsitem.print" />" data-aainteraction="confirm print shipping label" data-aaorder-id="${orderData.code}" data-aarma-id="return items">
    <i class="fa fa-print" aria-hidden="true"></i>${sPrintLabel}
</a>