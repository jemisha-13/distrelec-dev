(function ($) {
    /**
     * Dev Skin implementation for the module Layout.
     *
     * @author Dominic Modalek
     * @namespace Tc.Module.Default
     * @class Basic
     * @extends Tc.Module
     * @constructor
     */
    Tc.Module.Login.CheckoutForgottenPassword = function (parent) {
        this.$backToLoginLinkForgottenForm = this.$$('.js-back-to-login-forgotten-form');
        this.$forgottenPasswordEmailInput = this.$$('.js-forgotten-password-email');
        this.$forgottenPasswordSubmit = this.$$('.js-forgotten-password-submit');
        this.$captcha = this.$$('.recaptcha');
        this.forgottenPasswordGid = this.$$('.g-recaptcha').data('gid');

        var $forgottenPasswordSuccessSection = $('.js-login-checkout-fp-success');
        var $backToLoginLinkForgottenFormSuccess = $('.js-back-to-login-forgotten-form-success');
        var $forgottenPasswordLink = $('.js-to-forgotten-form');
        var $checkoutLoginSection = $('.js-login-checkout');
        var validationErrorEmail = $('#tmpl-login-validation-error-email').html();

        /**
         * override the appropriate methods from the decorated module (ie. this.get = function()).
         * the former/original method may be called via parent.<method>()
         */
        this.on = function (callback) {
            var $ctx = this.$ctx,
                self = this;

            // calling parent method
            parent.on(callback);
        };

        this.after = function () {
            var $ctx = this.$ctx,
                self = this;

            /** remove from login form any error message div's and and error classes to email/password inputs **/
            removeLoginErrorMessageValidation = function () {
                if ($checkoutLoginSection.find('.field-msgs').length > 0) {
                    $('.login__form__field.email .field-msgs').remove();
                    $('.login__form__field.password .field-msgs').remove();
                }

                if ($('.skin-login-checkout').find('.error').length > 0) {
                    $('#j_username').removeClass('error');
                    $('#j_password').removeClass('error');
                }
            };


            checkAndRemoveErrorMessage = function (name) {
                if ($(name).length > 0) {
                    $(name).remove();
                }
            };

            /** remove from forgotten password form error message div and error class to email input **/
            removeForgottenPasswordErrorMessageValidation = function () {
                if ($('#forgottenPassword').find('.field-msgs').length > 0) {
                    $('.js-forgotten-password-email').removeClass('error');
                    $('.js-forgotten-form-field .field-msgs').remove();
                }
            };

            /** remove from forgotten password form error message div and error class to email input **/
            addForgottenPasswordErrorMessage = function (message) {
                $('.js-forgotten-password-email').addClass('error');
                $('.forgottenPassword__form__field').append('<div class="field-msgs"><div class="error">' + message + '.' + '<i></i></div></div>');
            };

            /** display forgotten password section and remove any old F P validation messages **/
            $forgottenPasswordLink.on('click', function () {
                $checkoutLoginSection.addClass('hidden');
                $ctx.removeClass('hidden');
                self.$forgottenPasswordEmailInput.val($('#j_username').val());
                checkAndRemoveErrorMessage('.login__title .message');
                checkAndRemoveErrorMessage($(".mod-global-messages").find(".error"));
                checkAndRemoveErrorMessage($(".js-login-addl-error"));
                removeForgottenPasswordErrorMessageValidation();
            });

            /** display login section when back to login when on Forgotten Password Form and remove any old login validation messages**/
            self.$backToLoginLinkForgottenForm.on('click', function () {
                $ctx.addClass('hidden');
                $checkoutLoginSection.removeClass('hidden');
                checkAndRemoveErrorMessage('.js-forgotten-pass-error-msg');
                removeLoginErrorMessageValidation();
            });

            $backToLoginLinkForgottenFormSuccess.on('click', function () {
                $checkoutLoginSection.removeClass('hidden');
                $forgottenPasswordSuccessSection.addClass('hidden');
                removeLoginErrorMessageValidation();
            });

            /**
             * Make a POST ajax call to submit forgotten password and pass recaptcha
             *
             * @method on
             * @return void
             */
            forgottenPasswordRequest = function () {
                var email = $('.js-forgotten-password-email').val(),
                    captchaResponse = $ctx.find('.g-recaptcha').last().find('#g-recaptcha-response').val();

                $.ajax({
                    url: '/login/checkout/pw/request/async',
                    type: 'POST',
                    data: {
                        "email": email,
                        "g-recaptcha-response": captchaResponse
                    },
                    success: function () {
                        $('.js-login-checkout-fp').addClass('hidden');
                        $('.js-login-checkout-fp-success').removeClass('hidden');

                        removeForgottenPasswordErrorMessageValidation();
                        checkAndRemoveErrorMessage('.js-forgotten-pass-error-msg');
                    },
                    error: function (response) {
                        if (!$('.js-forgotten-password-email').hasClass('error')) {
                            addForgottenPasswordErrorMessage(response.responseJSON.message);
                        }
                        /** if any error validation present, remove it and display a new one **/
                        else {
                            removeForgottenPasswordErrorMessageValidation();
                            addForgottenPasswordErrorMessage(response.responseJSON.message);
                        }
                    },
                    complete: function () {
                        if (!!captchaResponse) {
                            grecaptcha.reset(self.forgottenPasswordGid);
                        }
                    }
                });
            };

            /**
             * Call recaptcha and forgottenPasswordRequest function
             *
             * @method on
             * @return void
             */
            submitForgottenPassword = function () {
                var forgottenPasswordRecaptcha = $ctx.find('.g-recaptcha');

                if (typeof self.$captcha !== 'undefined') {
                    console.log(self.$captcha.length);
                    if (self.$captcha.length > 0) {
                        if (typeof self.forgottenPasswordGid !== 'number') {
                            self.forgottenPasswordGid = grecaptcha.render(forgottenPasswordRecaptcha[0], {callback: 'forgottenPasswordRequest'}, true);
                            forgottenPasswordRecaptcha.data({'gid': self.forgottenPasswordGid});
                        }
                        grecaptcha.reset(self.forgottenPasswordGid);
                        grecaptcha.execute(self.forgottenPasswordGid);
                    } else {
                        forgottenPasswordRequest();
                    }
                }
            };

            /** Submit forgotten password request **/
            self.$forgottenPasswordSubmit.on('click', function (e) {
                e.preventDefault();

                Tc.Utils.validate(self.$forgottenPasswordEmailInput, validationErrorEmail, 'triangle', function (error) {
                    if (!error) {
                        submitForgottenPassword();
                    }

                    self.$forgottenPasswordEmailInput.closest('.js-form-group').toggleClass('is-error', error);
                });
            });

            // calling parent method
            parent.after();
        };
    };
})(Tc.$);
