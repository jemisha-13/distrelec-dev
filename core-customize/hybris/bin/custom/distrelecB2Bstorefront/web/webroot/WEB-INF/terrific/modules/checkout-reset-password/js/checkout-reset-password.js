(function($) {

    /**
     * Module implementation.
     *
     * @namespace Tc.Module
     * @class Login
     * @extends Tc.Module
     */
    Tc.Module.CheckoutResetPassword = Tc.Module.extend({

        /**
         * Initialize.
         *
         * @method init
         * @return {void}
         * @constructor
         * @param {jQuery} $ctx the jquery context
         * @param {Sandbox} sandbox the sandbox to get the resources from
         * @param {Number} id the unique module id
         */
        init: function($ctx, sandbox, id) {
            // call base constructor
            this._super($ctx, sandbox, id);
        },

        /**
         * Hook function to do all of your module stuff.
         *
         * @method on
         * @param {Function} callback function
         * @return void
         */
        on: function(callback) {
            var firstPasswordInput = $('.first-password-input'),
                secondPasswordInput = $('.second-password-input'),
                passNotMatchValidationText = $('.field-msgs').data( "reset-password-valid-text" ),
                backToLoginLinkForgottenForm = $('.js-back-to-login-reset-password-form');

            /** Reveal password on the icon click and hide on second click **/
            $('.mod-checkout-reset-password .pwd-reveal').click(function() {
                var pwdField = $(this).prev('input'),
                    pwdFieldIcon = $(this).find('.form-group__pwd-reveal-icon'),
                    pwdFieldIconOpen = $(this).find('.form-group__pwd-reveal-icon.fa-eye'),
                    pwdFieldIconClose = $(this).find('.form-group__pwd-reveal-icon.fa-eye-slash');

                pwdFieldIcon.addClass('hidden');

                if (pwdField.attr('type') === 'password') {
                    pwdField.attr('type', 'text');
                    pwdFieldIconClose.removeClass('hidden');
                } else {
                    pwdField.attr('type', 'password');
                    pwdFieldIconOpen.removeClass('hidden');
                }

            });

            /** Listen to remove min char error if user typed minimum 6 characters **/
            var input = document.querySelector('.first-password-input');
            input.addEventListener('input', updateValue);

            function updateValue(e) {
                if (e.target.value.length > 5 && firstPasswordInput.hasClass('error')) {
                    removeMinCharactersError();
                }
            }

            /** Show an error, if user types less than 6 characters **/
            $(document).click(function() {
                if (firstPasswordInput.val().length > 0 && firstPasswordInput.val().length < 6) {

                    /** Check if other errors are present, if yes, remove them **/
                    if ($('.field-msgs').is(':visible')) {
                        removePasswordsDoNotMatchError();
                    }

                    addMinCharactersError();
                }
            });

            /** Prevent users from copy pasting their password **/
            secondPasswordInput.on("cut copy paste",function(e) {
                e.preventDefault();
            });

            function addMinCharactersError() {
                firstPasswordInput.addClass('error');
                $('small').css('color', 'red');
            }

            function removeMinCharactersError() {
                firstPasswordInput.removeClass('error');
                $('small').css('color', '');
            }

            function addPasswordsDoNotMatchError() {
                firstPasswordInput.addClass('error');
                secondPasswordInput.addClass('error');

                /** Avoid this message being shown multiple times **/
                if (!$('.field-msgs').is(':visible')) {
                    $('.field-msgs').removeClass('hidden');
                    $('.field-msgs').append('<div class="error">' + passNotMatchValidationText + '<i></i></div>');
                }
            }

            backToLoginLinkForgottenForm.on('click', function() {
                $('.mod-checkout-reset-password').addClass('hidden');
                $('.skin-login-checkout').removeClass('hidden');
            });

            function removePasswordsDoNotMatchError() {
                firstPasswordInput.removeClass('error');
                secondPasswordInput.removeClass('error');
                $('.js-second-password-check .field-msgs').addClass('hidden');
                $('.field-msgs .error').remove();
            }

            /** Make ajax request if passwords match
             * If we request throws an error, display login section with the returned error message
             **/
            function resetPasswordRequest() {
                var pwd = $('.first-password-input').val(),
                    checkPwd = $('.second-password-input').val(),
                    token = $('.resetPassword').data('token'),
                    loginSection = $('.skin-login-checkout'),
                    resetPasswordSection = $('.mod-checkout-reset-password');

                if (secondPasswordInput.val() !== firstPasswordInput.val()) {
                    addPasswordsDoNotMatchError();
                } else {
                    removePasswordsDoNotMatchError();

                    $.ajax({
                        url: '/login/checkout/pw/change/async',
                        type: 'POST',
                        data: {
                            "pwd": pwd,
                            "checkPwd": checkPwd,
                            "token": token
                        },
                        success: function() {
                            loginSection.removeClass('hidden');
                            resetPasswordSection.remove();
                            $('.login__title-success-message').removeClass('hidden');
                        },
                        error: function(response) {
                            loginSection.removeClass('hidden');
                            resetPasswordSection.addClass('hidden');
                            $('.login__title').append('<p class="login__title-error-message message">' + response.responseJSON.message + '</p>');
                        },
                        complete: function() {
                        }
                    });
                }

            }

            /** If password input is 6 or more, make the request **/
            $('#submitResetPassword').click(function() {
                if (firstPasswordInput.val().length > 5) {
                    resetPasswordRequest();
                }
            });

            callback();
        },


        /**
         * Hook function to trigger your events.
         *
         * @method after
         * @return void
         */
        after: function() {
        }

    });

})(Tc.$);