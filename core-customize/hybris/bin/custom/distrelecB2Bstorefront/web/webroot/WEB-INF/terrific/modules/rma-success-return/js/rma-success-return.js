(function($) {

    /**
     * Module implementation.
     *
     * @namespace Tc.Module
     * @class Register
     * @extends Tc.Module
     */
    Tc.Module.RmaSuccessReturn = Tc.Module.extend({

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

            var self = this,
                $ctx = this.$ctx;

            var str = $('.rma-address-text').text();
            str = str.replace(/([;])+/g, '<br>');
            $('.rma-address-text').html(str);


            $('.click-here-btn').click(function(e){
                e.preventDefault();

                var ID = $(".page-tagline").find('b').text();
                var rmaJson = $.parseJSON($('.return-request-page-holder__json').text());

                var rmaData = {
                    'rmaId': ID,
                    'createRMARequestForm': rmaJson
                };

                $.ajax({
                    type: 'POST',
                    contentType: 'application/json',
                    dataType: 'json',
                    url: '/returns/user',
                    data: JSON.stringify(rmaData),
                    success: function(data){

                        if(data === false) {
                            $('.customer-assistance-section--false').removeClass('d-none');
                        } else {
                            $('.customer-assistance-section--success').removeClass('d-none');
                        }

                    },
                    error: function (data) {
                        console.log('An error occurred.');
                        console.log(data);
                    }
                });

            });

        },


        /**
         * Hook function to trigger your events.
         *
         * @method after
         * @return void
         */
        after: function() {
            // Do stuff here or remove after method
            //...
        }

    });

})(Tc.$);
