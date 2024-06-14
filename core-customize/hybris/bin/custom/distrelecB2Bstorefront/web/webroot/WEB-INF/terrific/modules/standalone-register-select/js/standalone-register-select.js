(function($) {

    /**
     * Module implementation.
     *
     * @namespace Tc.Module
     * @class Product
     * @extends Tc.Module
     */
    Tc.Module.StandaloneRegisterSelect = Tc.Module.extend({

        init: function($ctx, sandbox, id) {

            // call base constructor
            this._super($ctx, sandbox, id);
            this.sandbox.subscribe('captcha', this);
        },

        /**
         * Hook function to do all of your module stuff.
         *
         * @method on
         * @param {Function} callback function
         * @return void
         */
        on: function(callback) {

            var	self = this;

            // User error handling

            var checkoutError = $('.error-handling-class').attr('value');

            if (checkoutError !== null) {

                var checkoutRegister = $('.mod-standalone-register');
                var b2b =  $('.skin-standalone-register-b2b');
                var b2c = $('.skin-standalone-register-b2c');

                if (checkoutError === 'form-b2b') {
                    checkoutRegister.addClass('hidden');
                    checkoutRegister.removeClass('show');
                    b2b.removeClass('hidden');
                    b2b.addClass('show');
                } else if (checkoutError === 'form-b2c') {
                    $('input:radio[name="user_type_select_1"]').filter('[value="false"]').attr('checked', true);
                    checkoutRegister.addClass('hidden');
                    checkoutRegister.removeClass('show');
                    b2c.removeClass('hidden');
                    b2c.addClass('show');
                    $('.standalone-register-holder__steps').addClass('hidden');
                    $('.standalone-register-holder__steps--b2c').removeClass('hidden');

                    if ($('.form-b2c .customerNumber').val() !== null) {
                        document.querySelector('.form-b2c .customerNumber').parentNode.classList.remove('hidden');
                        $('#radioSelectYes').prop('checked', 'checked');
                    }

                }

            }

            // User customer type change function

            $('.main-radio-options .form-check-input').click(function() {
                var typeOfCustomer = $('.main-radio-options input[type=radio]:checked').val();
                var standaloneRegister = $('.mod-standalone-register');
                var userType;

                if (typeOfCustomer === 'true') {
                    userType = 'b2b';
                    var b2b =  $('.skin-standalone-register-b2b');

                    standaloneRegister.addClass('hidden');
                    standaloneRegister.removeClass('show');
                    b2b.removeClass('hidden');
                    b2b.addClass('show');
                    $('.standalone-register-holder__steps--b2c').addClass('hidden');
                    $('.standalone-register-holder__steps--b2b').removeClass('hidden');

                    if(localStorage.getItem('customerNumberB2C') !== null) {
                        localStorage.removeItem('customerNumberB2C');
                    }

                    localStorage.setItem('b2cForm', 'false');

                } else {
                    userType = 'b2c';
                    var b2c = $('.skin-standalone-register-b2c');

                    if (Array.isArray(digitalData.user) || digitalData.user.length) {
                        digitalData.user[0].companyReg = 'existing';
                        digitalData.user[0].registration = {existing : 'no'};
                    }

                    standaloneRegister.addClass('hidden');
                    standaloneRegister.removeClass('show');
                    b2c.removeClass('hidden');
                    b2c.addClass('show');
                    $('.standalone-register-holder__steps').addClass('hidden');
                    $('.standalone-register-holder__steps--b2c').removeClass('hidden');

                    localStorage.setItem('customerNumberB2C', document.querySelectorAll('.customerNumber')[0].value);
                    localStorage.setItem('b2cForm', 'true');

                }

                // Reload Captcha Image

                self.fire('reloadCaptcha', {}, [ 'captcha' ]);

                // Cleanup datalayer variable so we can pickup new ones
                digitalData.page.pageInfo.regPrefAdded = [];

                // Iterate through consent checkboxes
                $('.skin-standalone-register-' + userType + ' .js-consentSection .js-checkbox').each(function(i, el) {
                    var $el = $(el);

                    // If checkbox is checked and it is visible (we have 2 forms, b2b and b2c, only one can be visible)
                    if ($el.is(':checked') && $el.is(':visible')) {
                        // Add consent item into datalayer so we can pickup it up in Ensighten
                        digitalData.page.pageInfo.regPrefAdded.push($el.attr('name'));
                    }
                });
            });

            callback();

        }

    });

})(Tc.$);
