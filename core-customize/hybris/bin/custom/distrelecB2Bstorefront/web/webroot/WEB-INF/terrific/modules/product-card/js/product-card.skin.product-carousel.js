(function($) {

    /**
     * Productlist Skin Favorite implementation for the module Productlist Order.
     *
     */
    Tc.Module.ProductCard.ProductCarousel = function () {

        /**
         * override the appropriate methods from the decorated module (ie. this.get = function()).
         * the former/original method may be called via parent.<method>()
         */

        /*
         * product carousel vue slider is now in alternatives as there was 2 calls being made when it was the same logic
         * and these are the only 2 child mods in vue
         */

        this.on = function (callback) {

            this.$ctx.on('click', '.alternatives-holder__all-alternatives', function(e) {
                e.preventDefault();
                $('html, body').animate({
                    scrollTop: $("#product-alternatives-list").offset().top - 100
                }, 1000);

            });

            callback();
        
        };

    };

})(Tc.$);


