<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<spring:message code="cal.url" var="pdfUrl" />
 
<div class="cal-box">
        <h3><spring:message code="cal.calibrated.title" /></h3>
        <spring:message code="cal.calibrated.text" />
        <div class="center border-top"><a href="${pdfUrl}" class="button read-more-button" target="_blank"><spring:message code="cal.button" /></a></div> 
</div>