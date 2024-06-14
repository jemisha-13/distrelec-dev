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
    Tc.Module.Login.Main = function (parent) {
        this.$loginButton = this.$$('.js-login-button');
        this.$captcha = this.$$('.recaptcha');
        this.$username = this.$$('.js-username');
        this.$password = this.$$('.js-password');
        this.$rememberMe = this.$$('.js-remember');

        var validationErrorEmpty = $('#tmpl-login-validation-error-empty').html();
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

            recaptchaLoginCallback = function () {
                self.$captcha.closest('form').submit();
            };

            recaptchaLogin = function (e) {
                if (typeof self.$captcha !== 'undefined') {
                    // only validate captcha if shown (currently after 3 failed submits)
                    if (self.$captcha.length > 0) {
                        e.preventDefault();

                        var $grecaptcha = $('.g-recaptcha', self.$captcha);
                        var gid = $grecaptcha.eq(0).data('gid');

                        if (typeof gid !== 'number') {
                            gid = grecaptcha.render($grecaptcha[0], {callback: 'recaptchaLoginCallback'}, true);
                            $grecaptcha.eq(0).data({'gid': gid});
                        }
                        grecaptcha.reset(gid);
                        grecaptcha.execute(gid);
                    }
                }
            };

            self.$loginButton.on('click', function (e) {
                var formInvalid = false;

                Tc.Utils.validate($('.validate-empty', $ctx), validationErrorEmpty, 'triangle', function (error) {
                    if (error) {
                        e.preventDefault();
                        formInvalid = true;
                    }
                });

                Tc.Utils.validate($('.validate-email', $ctx), validationErrorEmail, 'triangle', function (error) {
                    if (error) {
                        e.preventDefault();
                        formInvalid = true;
                    }
                });

                //DISTRELEC-9087
                self.$username.val(self.$username.val().trim());
                self.$password.val(self.$password.val().trim());

                if (!formInvalid) {
                    recaptchaLogin(e);
                }
            });

            self.$rememberMe.on('click', function (e) {
                var $thisRememberMe = $(this);

                $thisRememberMe.prop('checked', $thisRememberMe.is(":checked"));
                localStorage.setItem("LoginRememberMe", $thisRememberMe.prop('checked'));
                localStorage.setItem("emailStore", self.$username.val());
            });

            // calling parent method
            parent.after();
        };


    };

})(Tc.$);
