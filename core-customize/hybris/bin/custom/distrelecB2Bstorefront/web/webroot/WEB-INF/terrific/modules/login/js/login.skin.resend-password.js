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
    Tc.Module.Login.ResendPassword = function (parent) {
        this.$submitButton = this.$$('.js-submit-form');
        this.$captcha = this.$$('.recaptcha');

        var validationErrorEmail = this.$$('#tmpl-login-validation-error-email').html();

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

            recaptchaLoginResendPassCallback = function () {
                self.$captcha.closest('form').submit();
            };

            recaptchaLoginResendPass = function (e) {
                if (typeof self.$captcha !== 'undefined') {
                    // only validate captcha if shown (currently after 3 failed submits)
                    if (self.$captcha.length > 0) {
                        e.preventDefault();

                        var $grecaptcha = $('.g-recaptcha', self.$captcha);
                        var gid = $grecaptcha.eq(0).data('gid');

                        if (typeof gid !== 'number') {
                            gid = grecaptcha.render($grecaptcha[0], {callback: 'recaptchaLoginResendPassCallback'}, true);
                            $grecaptcha.eq(0).data({'gid': gid});
                        }
                        grecaptcha.reset(gid);
                        grecaptcha.execute(gid);
                    } else {
                        self.$submitButton.closest('form').submit();
                    }
                }
            };

            self.$submitButton.on('click', function (e) {
                var formInvalid = false;

                Tc.Utils.validate($('.js-validate-email', $ctx), validationErrorEmail, 'triangle', function (error) {
                    if (error) {
                        e.preventDefault();
                        formInvalid = true;
                    }
                });

                if (!formInvalid) {
                    recaptchaLoginResendPass(e);
                }
            });

            // calling parent method
            parent.after();
        };
    };
})(Tc.$);
