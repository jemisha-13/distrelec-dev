<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<spring:message code="toolsitem.availability.title" var="sMessageTitle" text="default message sMessageTitle" />
<spring:message code="toolsitem.availability.boxtitle" var="sMessageBoxTitle" text="default message sMessageBoxTitle" />
<spring:message code="toolsitem.availability.boxmessage" var="sMessageBoxMessage" text="default message sMessageBoxMessage" />

<span id="sMessageTitle" style="display:none;">${sMessageTitle}</span>
<span id="sMessageBoxTitle" style="display:none;">${sMessageBoxTitle}</span>
<span id="sMessageBoxMessage" style="display:none;">${sMessageBoxMessage}</span>

<button class="mat-button mat-button--action-blue btn-check-availability" data-warehouse-code="${warehouseCode}"><spring:message code="toolsitem.availability.btn.text" text="Check availability" /></button>

<i class="fa fa-warning hidden" id="icon-${warehouseCode}">&nbsp;</i>
<i class="fa fa-check hidden" id="icon-${warehouseCode}">&nbsp;</i>
