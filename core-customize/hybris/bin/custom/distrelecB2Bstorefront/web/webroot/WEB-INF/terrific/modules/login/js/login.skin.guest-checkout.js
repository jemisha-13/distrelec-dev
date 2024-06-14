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
    Tc.Module.Login.GuestCheckout = function (parent) {
        this.$guestCheckoutForm = this.$$('.js-guest-checkout-form');
        this.guestCheckoutGid = this.$$('.g-recaptcha').data('gid');
        this.$guestCheckoutEmail = this.$$('.js-guest-checkout-email');
        this.$guestCheckoutEmailGroup = this.$guestCheckoutEmail.closest('.js-guest-checkout-form-group');
        this.$guestCheckoutSubmit = this.$$('.js-guest-checkout-submit');
        this.$guestTickItemError = this.$$('.js-tickItemError');
        this.$guestCheckoutCaptcha = this.$$('.recaptcha');

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
                self = this,
                captchaSolved = false;

            self.$guestCheckoutForm.on('submit', function (e) {
                Tc.Utils.validate(self.$guestCheckoutEmail, validationErrorEmail, 'triangle', function (error) {
                    if (!error) {
                        if (!captchaSolved) {
                            if (typeof self.$guestCheckoutCaptcha !== 'undefined') {
                                if (self.$guestCheckoutCaptcha.length > 0) {
                                    var $grecaptcha = $('.g-recaptcha', self.$guestCheckoutCaptcha);
                                    var gid = $grecaptcha.eq(0).data('gid');
                                    e.preventDefault();

                                    if (typeof gid !== 'number') {
                                        gid = grecaptcha.render($grecaptcha[0], {callback: 'guestCheckoutRecaptchaCallback'}, true);
                                        $grecaptcha.eq(0).data({'gid': gid});
                                    }

                                    grecaptcha.execute(gid);
                                }
                            }
                        }
                    } else {
                        e.preventDefault();
                        self.$guestCheckoutEmailGroup.removeClass('is-success').addClass('is-error');
                        self.$guestCheckoutEmail.addClass('error');
                    }
                });
            });

            self.$guestCheckoutEmail.on('input', function () {
                self.$guestCheckoutEmailGroup.removeClass('is-error is-success');
                self.$guestCheckoutEmail.removeClass('error');
                self.$guestCheckoutEmailGroup.find('.field-msgs').remove();
            });

            guestCheckoutRecaptchaCallback = function () {
                captchaSolved = true;
                self.$guestCheckoutForm.submit();
            };

            // calling parent method
            parent.after();
        };
    };
})(Tc.$);
