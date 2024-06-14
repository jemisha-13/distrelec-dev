(function ($) {

    /**
     * Module implementation.
     *
     * @namespace Tc.Module
     * @class Product-image-gallery
     * @extends Tc.Module
     */
    Tc.Module.BackOrderItemSave = Tc.Module.extend({

        /**
         * Hook function to do all of your module stuff.
         *
         * @method on
         * @param {Function} callback function
         * @return void
         */
        on: function (callback) {

            $('.notify-me-toggle').click(function(){
               $('.mod-stock-notification').toggleClass('mod-stock-notification--active');
            });

            var isOCI = (!!$('.isOCI').length);

            $('.btn-checkout').click(function(){
                var responseString = [];
                var self = $(this);

                if(isOCI === false) {
                    $.each($('.joinStrings'), function(i,v){
                        if($(v).html() !== '') {
                            responseString.push($(v).html());
                        }
                    });
                } else {
                    responseString = $('#hiddenResponse').html();
                }


                if(responseString.length !== 0) {

                    $(document).trigger('cart', {
                        actionIdentifier: 'orderDetailAddToCart',
                        type: 'addBulk',
                        productsJson: '[' + responseString + ']'
                    });

                    self.unbind();
                }
            });

            callback();
        }

    });

})(Tc.$);