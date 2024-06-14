<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<spring:message code="checkoutregister.strength.veryWeak" var="sVeryWeak" />
<spring:message code="checkoutregister.strength.weak" var="sWeak" />
<spring:message code="checkoutregister.strength.normal" var="sNormal" />
<spring:message code="checkoutregister.strength.medium" var="sMedium" />
<spring:message code="checkoutregister.strength.strong" var="sStrong" />
<spring:message code="checkoutregister.strength.veryStrong" var="sVeryStrong" />


<section class="meter-holder">
    <div id="password-meter-strength">
        <span class="meter-text meter-text--0 hidden" data-id="0">
            &nbsp;
        </span>
        <span class="meter-text meter-text--1 hidden" data-id="1">
            &nbsp;
        </span>
        <span class="meter-text meter-text--2 hidden" data-id="2">
            &nbsp;
        </span>
        <span class="meter-text meter-text--3 hidden" data-id="3">
            &nbsp;
        </span>
        <span class="meter-text meter-text--4 hidden" data-id="4">
            &nbsp;
        </span>
    </div>
</section>