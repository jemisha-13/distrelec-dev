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
    Tc.Module.Login.Checkout = function (parent) {
        this.validationErrorEmpty = $('#tmpl-login-validation-error-empty').html();
        this.validationErrorEmail = $('#tmpl-login-validation-error-email').html();
        this.validationErrorPassword = $('#tmpl-login-validation-error-password').html();
        this.validationErrorUsername = $('#tmpl-login-validation-error-username').html();
        this.$continueButton = this.$$('.js-login-continue');
        this.$formFields = this.$$('.js-login-form-field');
        this.$rememberMe = this.$$('.js-remember-me');
        this.$loginCaptcha = this.$$('.recaptcha');
        this.rememberMeStore = localStorage.getItem("LoginRememberMe");
        this.emailStore = localStorage.getItem("emailStore");

        /**
         * override the appropriate methods from the decorated module (ie. this.get = function()).
         * the former/original method may be called via parent.<method>()
         */
        this.on = function (callback) {
            var $ctx = this.$ctx;
            var self = this;

            // calling parent method
            parent.on(callback);
        };

        this.after = function () {
            var $ctx = this.$ctx;
            var self = this;
            var $validateEmptyFields = $('.validate-empty', self.$ctx);
            var $validateUsernameField = $('.js-validate-username', self.$ctx);
            var $validatePasswordField = $('.js-validate-password', self.$ctx);
            var $validateEmailFields = $('.validate-email', self.$ctx);

            var loginRecaptcha = function (e) {
                if (typeof self.$loginCaptcha !== 'undefined') {
                    if (self.$loginCaptcha.length > 0) {
                        var $grecaptcha = $('.g-recaptcha', self.$loginCaptcha);
                        var gid = $grecaptcha.eq(0).data('gid');

                        e.preventDefault();

                        if (typeof gid !== 'number') {
                            gid = grecaptcha.render($grecaptcha[0], {}, true);
                            $grecaptcha.eq(0).data({'gid': gid});
                        }

                        grecaptcha.reset(gid);
                        grecaptcha.execute(gid);
                    }
                }
            };

            self.$continueButton.on('click', function (e) {
                var formInvalid = false;

                // Remove any visible error message since later we will add new one
                self.$formFields.find('.field-msgs').remove();

                // When BE validation takes place, we need to manually remove class "error" which was added by BE in addition to see error msgs
                $validateUsernameField.removeClass('error');
                Tc.Utils.validate($validateUsernameField, self.validationErrorUsername, 'triangle', function (error) {
                    if (error) {
                        e.preventDefault();
                        formInvalid = true;
                    }

                    $validateUsernameField.closest('.js-form-group').toggleClass('is-error', error);
                });

                // When BE validation takes place, we need to manually remove class "error" which was added by BE in addition to see error msgs
                $validatePasswordField.removeClass('error');
                Tc.Utils.validate($validatePasswordField, self.validationErrorPassword, 'triangle', function (error) {
                    if (error) {
                        e.preventDefault();
                        formInvalid = true;
                    }

                    $validatePasswordField.closest('.js-form-group').toggleClass('is-error', error);
                });

                //DISTRELEC-9087
                $('#j_username').val($('#j_username').val().trim());
                $('#j_password').val($('#j_password').val().trim());

                if (!formInvalid) {
                    // only validate captcha if shown (currently after 3 failed submits)
                    loginRecaptcha(e);
                }
            });

            self.$rememberMe.on('click', function () {
                $('#j_remember').prop('checked', $(this).is(":checked"));
                localStorage.setItem("LoginRememberMe", $(this).prop('checked'));

                var inputEmail = document.getElementById("j_username");
                localStorage.setItem("emailStore", inputEmail.value);
            });

            if (self.rememberMeStore === 'true') {
                $('#j_remember').prop('checked', true);
                $('#metahd-account-remember').prop('checked', 'checked');
                $("#j_username").val(this.emailStore);
                $("#metahd-account-login").val(this.emailStore);
            } else {
                $('#j_remember').prop('checked', false);
                $('#metahd-account-remember').prop('checked', false);
                localStorage.setItem("emailStore", '');
                $("#j_username").val('');
                $("#metahd-account-login").val('');
            }

            // calling parent method
            parent.after();
        };
    };
})(Tc.$);
