(function($) {

    /**
     * Module implementation.
     *
     * @namespace Tc.Module
     * @class Product
     * @extends Tc.Module
     */
    Tc.Module.PasswordStrength = Tc.Module.extend({

        /**
         * Hook function to do all of your module stuff.
         *
         * @method on
         * @param {Function} callback function
         * @return void
         */
        on: function(callback) {


            $('.skin-checkout-register-b2c #passwordB2C').focus(function(){

                var password = $('.skin-checkout-register-b2c #passwordB2C'),
                    mainMeter = $('.skin-checkout-register-b2c #password-meter-strength');

                scriptLoaded(password, mainMeter);

            });

            $('.skin-checkout-register-b2b-existing #passwordB2Bexisting').focus(function(){

                var password = $('.skin-checkout-register-b2b-existing #passwordB2Bexisting'),
                    mainMeter = $('.skin-checkout-register-b2b-existing #password-meter-strength');

                scriptLoaded(password, mainMeter);

            });

            $('.skin-checkout-register-b2c-existing #passwordB2Cexisting').focus(function(){

                var password = $('.skin-checkout-register-b2c-existing #passwordB2Cexisting'),
                    mainMeter = $('.skin-checkout-register-b2c-existing #password-meter-strength');

                scriptLoaded(password, mainMeter);
            });

            $('.skin-checkout-register-b2b #passwordB2B').focus(function(){

                var password = $('.skin-checkout-register-b2b #passwordB2B'),
                    mainMeter = $('.skin-checkout-register-b2b #password-meter-strength');

                scriptLoaded(password, mainMeter);

            });

            $('.skin-standalone-register-b2b #passwordB2B').focus(function(){

                var password = $('.skin-standalone-register-b2b #passwordB2B'),
                    mainMeter = $('.skin-standalone-register-b2b #password-meter-strength');

                scriptLoaded(password, mainMeter);

            });

            $('.skin-standalone-register-b2c #passwordB2C').focus(function(){

                var password = $('.skin-standalone-register-b2c #passwordB2C'),
                    mainMeter = $('.skin-standalone-register-b2c #password-meter-strength');

                scriptLoaded(password, mainMeter);

            });

            $('.js-guest-b2c-form .js-main-pass').focus(function(){
                var password = $(this);
                var mainMeter = $('.js-guest-b2c-form #password-meter-strength');

                scriptLoaded(password, mainMeter);
            });

            function scriptLoaded(password, mainMeter) {

                password[0].addEventListener('input', function() {

                    var val = password.val(),
                        result = zxcvbn(val),
                        meterNumber = 'meter-' + result.score.toString(),
                        resultID = result.score;

                    if(val !== "") {
                        $(mainMeter).removeClass().addClass(meterNumber);
                    } else {
                        $(mainMeter).removeClass();
                    }

                    $('[data-id]').each(function(){

                        var meterID = $(this).data('id');

                        if (meterID === resultID) {
                            $('.meter-text').addClass('hidden');
                            $('.meter-text--' + meterID).removeClass('hidden');
                        } else if (val === '') {
                            $('.meter-text').addClass('hidden');
                        }

                    });

                });

            }

            callback();
        },

        after: function () {

        }

    });

})(Tc.$);
