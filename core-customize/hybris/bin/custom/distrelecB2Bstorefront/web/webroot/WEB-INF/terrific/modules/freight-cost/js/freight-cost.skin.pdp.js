(function($) {

    /**
     * Productlist Skin Favorite implementation for the module Productlist Order.
     *
     */
    Tc.Module.FreightCost.Pdp = function () {

        /**
         * override the appropriate methods from the decorated module (ie. this.get = function()).
         * the former/original method may be called via parent.<method>()
         */
        this.on = function (callback) {

            $('.skin-freight-cost-pdp .close').click(function (){
                $('.skin-freight-cost-pdp .mod-freight-cost__pricing').removeAttr('style');
            });

            callback();
        };


    };

})(Tc.$);


