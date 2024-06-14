<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>

<div>
    <mod:global-messages/>
    <div class="js-global-messages"></div>

    <c:if test="${showDoubleOptInInfoMessage}">
        <div class="js-doubleOptInInfoMessage">
            <br>
            <mod:banner-ux type="info" icon="material-icons-round" iconCode="&#xe88e;">
                <p id="doubleOptInInfoMessage" class="fw-r"><spring:message code="text.preferences.updated.below"/></p>
                <p><a id="doubleOptInInfoMessageEmailLink" class="ux-link ux-link--raw" href="mailto:${user.email}">${user.email}</a></p>
            </mod:banner-ux>
        </div>
    </c:if>

    <c:if test="${displayVatWarningExportShop}">
        <mod:banner-ux type="warning" icon="material-icons-round" iconCode="&#xe88e;">
            <div id="vatWarningExportShop">
                <spring:message code="${customerType eq 'B2B' || customerType eq 'B2B_KEY_ACCOUNT' ? 'checkout.vat.message.b2b' : 'checkout.vat.message.general'}" />
            </div>
        </mod:banner-ux>
    </c:if>
</div>
