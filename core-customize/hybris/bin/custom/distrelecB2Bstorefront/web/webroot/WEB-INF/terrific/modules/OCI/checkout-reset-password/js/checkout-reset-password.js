(function($) {

    /**
     * Module implementation.
     *
     * @namespace Tc.Module
     * @class Login
     * @extends Tc.Module
     */
    Tc.Module.Login = Tc.Module.extend({

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


            // validation helpers for login checkout
            this.validationErrorEmpty = this.$$('#tmpl-login-validation-error-empty').html();
            this.validationErrorEmail = this.$$('#tmpl-login-validation-error-email').html();
            this.validationErrorCaptcha = this.$$('#tmpl-login-validation-error-captcha').html();

            this.rememberme = localStorage.getItem("LoginRememberMe");
            this.emailStore = localStorage.getItem("emailStore");

            this.$captcha = this.$$('#loginForm').find('.recaptcha');

            if(this.emailStore !== null) { // Populates email input field with stored value
                $('#j_username').val(this.emailStore);
            }

        },

        /**
         * Hook function to do all of your module stuff.
         *
         * @method on
         * @param {Function} callback function
         * @return void
         */
        on: function(callback) {
            var $ctx = this.$ctx;

            callback();
        },


        /**
         * Hook function to trigger your events.
         *
         * @method after
         * @return void
         */
        after: function() {
            $('.js-password-check .pwd-reveal').click(function() {
                console.log("clicking");
                var pwdField = $('.first-password-input'),
                    pwdFieldIcon = $(this).find('.form-group__pwd-reveal-icon'),
                    pwdFieldIconOpen = $(this).find('.form-group__pwd-reveal-icon.fa-eye'),
                    pwdFieldIconClose = $(this).find('.form-group__pwd-reveal-icon.fa-eye-slash');

                console.log("pwdField ", pwdField.attr('type'));
                console.log("pwdField ", pwdField);

                pwdFieldIcon.addClass('hidden');

                if (pwdField.attr('type') === 'password') {
                    pwdField.attr('type', 'text');
                    pwdFieldIconClose.removeClass('hidden');
                } else {
                    pwdField.attr('type', 'password');
                    pwdFieldIconOpen.removeClass('hidden');
                }
            });
        }

    });

})(Tc.$);