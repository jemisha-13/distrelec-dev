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

        onCaptchaSubmitReturnsForm = function() {

            var form = $('#guestRMACreateRequestForm');

            $.ajax({

                type: form.attr('method'),
                url: form.attr('action'),
                data: form.serialize(),
                success: function (data) {

                    if(data === true) {
                        $('.data-response-holder__success').removeClass('hidden');
                        $('.data-response-holder__fail').addClass('hidden');
                        $('#guestRMACreateRequestForm').find('input, select, textarea').not(':button, :submit, :reset, :hidden').val('').removeAttr('selected');
                        $('.rma-guest-return')[0].reset();
                        // Listening for this event in "WEB-INF/terrific/modules/guest-returns-form/js/guest-returns-form.js"
						form.trigger('recaptcha-success');
                    } else {
                        $('.data-response-holder__fail').removeClass('hidden');
                        $('.data-response-holder__success').addClass('hidden');
                    }

                    grecaptcha.reset();
                    $('.g-recaptcha').html('');

                },
                error: function (data) {
                    console.log('An error occurred.');
                    console.log(data);

                    grecaptcha.reset();
                    $('.g-recaptcha').html('');

                }

            });
        };

	</script>

	<div class="g-recaptcha" data-sitekey="${captcha_public_key}" data-callback="${callbackName}" data-size="invisible"></div>
	<script src="https://www.google.com/recaptcha/api.js?hl=${currentLanguage.isocode}&render=explicit" async defer></script>
</c:if>