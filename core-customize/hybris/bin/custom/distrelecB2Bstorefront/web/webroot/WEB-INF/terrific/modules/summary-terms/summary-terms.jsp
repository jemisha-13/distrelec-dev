<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<div class="summary__terms">
    <p id="checkoutOrderSummaryTermsInfo">
        <c:choose>
            <c:when test="${currentCountry.isocode eq 'IT'}">
                <spring:message code="newcheckout.acceptTerms1" />
                <c:if test="${currentChannel.type ne 'B2B'}">
                    <spring:message code="newcheckout.acceptTerms2" />
                </c:if>
            </c:when>
            <c:otherwise>
                <spring:message code="newcheckout.acceptTerms" />
            </c:otherwise>
        </c:choose>
    </p>
</div>
