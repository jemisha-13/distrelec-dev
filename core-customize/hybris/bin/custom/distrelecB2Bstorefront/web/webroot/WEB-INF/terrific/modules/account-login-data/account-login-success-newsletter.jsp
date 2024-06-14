<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<section class="newsletter-welcome">
    <h1 class="newsletter-welcome__heading"><spring:message code="text.preferences.welcome.thanks"/></h1>
        <div class="newsletter-welcome__sections">
            <div class="newsletter-welcome__box">
            <h2><spring:message code="text.preferences.existing.question"/></h2>
            <p><spring:message code="text.preferences.updated.existing.message.1"/></p>
            <p><spring:message code="text.preferences.updated.existing.message.2"/></p>
            <p><spring:message code="text.preferences.updated.existing.message.3"/></p>
            <c:url value="/my-account/preference-center" var="preferenceCenterUrl"/>
            <div class="newsletter-welcome__footer">
                <a href="${preferenceCenterUrl}" class="btn btn-primary" data-aainteraction="comms preferences"><spring:message code="text.preferences.updated.comm.button"/></a>
            </div>
        </div>

        <div class="newsletter-welcome__box">
            <h2><spring:message code="text.preferences.new.question"/></h2>
            <p><spring:message code="text.preferences.updated.new.message.1"/></p>
            <p><spring:message code="text.preferences.updated.new.message.2"/></p>
            <c:url value="/register" var="registerUrl"/>
            <div class="newsletter-welcome__footer">
                <a href="${registerUrl}" class="btn btn-primary" data-aainteraction="create account"><spring:message code="text.preferences.updated.new.button"/></a>
            </div>
        </div>
    </div>
</section>
