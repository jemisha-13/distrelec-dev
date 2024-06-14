<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<div class="modal js-rohsAdditionalInformation" tabindex="-1">
    <c:choose>
        <c:when test="${rohsCode eq '99'}">
            <h2 id="rohs.underReview"><spring:message code="rohs.underReview"/></h2>
            <p id="rohs.additionalInformation"><spring:message code="rohs.additionalInformation"/></p>
        </c:when>
        <c:when test="${rohsCode eq '13'}">
            <h2 id="rohs.notCompliant"><spring:message code="rohs.notCompliant"/></h2>
            <p id="rohs.notCompliantInformation"><spring:message code="rohs.notCompliantInformation"/></p>
        </c:when>
        <c:when test="${rohsCode eq '11'}">
            <h2 id="rohs.notApplicable"><spring:message code="rohs.notApplicable"/></h2>
            <p id="rohs.notApplicableInformation"><spring:message code="rohs.notApplicableInformation"/></p>
        </c:when>
    </c:choose>
    <button id="rohs.underReview.continueShopping" class="btn btn-primary continueShopping" data-dismiss="modal" aria-hidden="true"><spring:message code="rohs.underReview.continueShopping"/></button>
</div>
