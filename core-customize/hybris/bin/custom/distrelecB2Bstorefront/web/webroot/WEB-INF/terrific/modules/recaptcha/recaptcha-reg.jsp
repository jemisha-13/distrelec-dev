<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:choose>
    <c:when test="${htmlClasses eq 'form-b2b'}">
        <c:set var="callbackName" value="onSubmitRegB2B" />
    </c:when>
    <c:otherwise>
        <c:set var="callbackName" value="onSubmitRegB2C" />
    </c:otherwise>
</c:choose>

<div class="g-recaptcha" data-sitekey="${captcha_public_key}" data-callback="${callbackName}" data-size="invisible"></div>
<script src="https://www.google.com/recaptcha/api.js?hl=${currentLanguage.isocode}&render=explicit" async defer></script>

<script type="text/javascript">

    var onSubmitRegB2B = function() {

        var formData = $('.form-b2b');
        var formSubUrl = window.location.origin + '/' + formData.attr('action');

        $.ajax({
            url: formSubUrl,
            method: "POST",
            data: formData.serialize(),
            dataType: "json",
            cache: false
        })
        .done(function (data) {
            window.location.href = window.location.origin + data.redirectTo;
        })
        .fail(function (data) {

            $('.ajax-product-loader').addClass('d-none');

            if (data.responseJSON.globalErrors !== null) {
                var errorTemplate =
                    '<div class="bd error">' +
                        '<div class="ct">' +
                            '<div class="c-center">' +
                                '<div class="c-center-content">' +
                                    '<div class="col-c">' +
                                        '<p>' +
                                            data.responseJSON.globalErrors[0] +
                                        '</p>' +
                                    '</div>' +
                                '</div>' +
                            '</div>' +
                        '</div>' +
                    '</div>'
                ;

                $('.standalone-register-holder .fe-global-error').addClass('hidden');
                $('.standalone-register-holder .field-msgs-fe').addClass('hidden');
                $('.order-1 .mod-global-messages').html(errorTemplate);

                // Scroll to top to see errors
                $("html, body").animate({scrollTop: 0}, 500);
            }

            if (data.responseJSON.validationErrors !== null) {
                count = 0;

                $('.standalone-register-holder .field-msgs-fe').addClass('hidden');

                $.each(data.responseJSON.validationErrors, function(count) {
                    var b2bField = $('.form-b2b input[name=' + data.responseJSON.validationErrors[count].fieldName + ']');

                    if(b2bField.attr("name") === "vatId"){
                        b2bField.parent().addClass('error');
                        b2bField.parent().next().addClass('hidden');
                        b2bField.parent().siblings(".fa-check")
                            .addClass("hidden");
                        b2bField.parent().after(
                            '<div class="field-msgs">' +
                            '<div class="error error-message">' +
                            '<span id="' + data.responseJSON.validationErrors[count].fieldName + '.errors' + '">' +
                            data.responseJSON.validationErrors[count].errorMessage +
                            '</span>' +
                            '</div>' +
                            '</div>'
                        );
                    }else {
                        b2bField.addClass('error');
                        b2bField.next().addClass('hidden');

                        b2bField.after(
                            '<div class="field-msgs">' +
                            '<div class="error error-message">' +
                            '<span id="' + data.responseJSON.validationErrors[count].fieldName + '.errors' + '">' +
                            data.responseJSON.validationErrors[count].errorMessage +
                            '</span>' +
                            '</div>' +
                            '</div>'
                        );
                    }
                    count++;
                });

            }

        });

    };

    var onSubmitRegB2C = function() {

        var formData = $('.form-b2c');
        var formSubUrl = window.location.origin + '/' + formData.attr('action');

        $('.ajax-product-loader').removeClass('d-none');

        $.ajax({
            url: formSubUrl,
            method: "POST",
            data: formData.serialize(),
            dataType: "json",
            cache: false
        })
        .done(function (data) {
            window.location.href = window.location.origin + data.redirectTo;
        })
        .fail(function (data) {

            $('.ajax-product-loader').addClass('d-none');

            if (data.responseJSON.globalErrors !== null) {
                var errorTemplate =
                    '<div class="bd error">' +
                        '<div class="ct">' +
                            '<div class="c-center">' +
                                '<div class="c-center-content">' +
                                    '<div class="col-c">' +
                                        '<p>' +
                                            data.responseJSON.globalErrors[0] +
                                        '</p>' +
                                    '</div>' +
                                '</div>' +
                            '</div>' +
                        '</div>' +
                    '</div>'
                ;

                $('.standalone-register-holder .fe-global-error').addClass('hidden');
                $('.standalone-register-holder .field-msgs-fe').addClass('hidden');
                $('.order-1 .mod-global-messages').html(errorTemplate);

                // Scroll to top to see errors
                $("html, body").animate({scrollTop: 0}, 500);
            }

            if (data.responseJSON.validationErrors !== null) {
                count = 0;

                $('.standalone-register-holder .field-msgs-fe').addClass('hidden');

                $.each(data.responseJSON.validationErrors, function(count) {
                    var b2cField = $('.form-b2c input[name=' + data.responseJSON.validationErrors[count].fieldName + ']');

                    b2cField.addClass('error');
                    b2cField.next().addClass('hidden');

                    b2cField.after(
                        '<div class="field-msgs">' +
                            '<div class="error">' +
                                '<span id="' + data.responseJSON.validationErrors[count].fieldName + '.errors' + '">' +
                                    data.responseJSON.validationErrors[count].errorMessage +
                                '</span>' +
                            '</div>' +
                        '</div>'
                    );

                    count++;
                });

            }

        });

    };

</script>

