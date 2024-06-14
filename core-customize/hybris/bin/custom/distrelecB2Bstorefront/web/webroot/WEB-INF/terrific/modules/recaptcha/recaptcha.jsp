<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="isOCI" value="false" />
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
	<c:set var="isOCI" value="true" />
</sec:authorize>

<c:set var="callbackName" value="${callback}" />
<c:if test="${empty callbackName}">
	<c:set var="callbackName" value="onCaptchaSubmit" />
</c:if>

<c:if test="${isOCI == false}">
	<script type="text/javascript">
        ${callbackName} = function() {
            var formClass = '${htmlClasses}';

            if (formClass) {
                $('.' + formClass + ' .g-recaptcha').parents('form').submit();
            } else {
                $('.g-recaptcha').parents('form').submit();
            }
        }
	</script>

	<div class="g-recaptcha" data-sitekey="${captcha_public_key}" data-callback="${callbackName}" data-size="invisible"></div>
	<script src="https://www.google.com/recaptcha/api.js?hl=${currentLanguage.isocode}&render=explicit" async defer></script>
</c:if>
