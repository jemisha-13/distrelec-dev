(function($) {

    /**
     * Module implementation.
     *
     * @namespace Tc.Module
     * @class Rating
     * @extends Tc.Module
     */
    Tc.Module.MobileFilterSwitch = Tc.Module.extend({

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

            // Do stuff here
            //...
        },

        /**
         * Hook function to do all of your module stuff.
         *
         * @method on
         * @param {Function} callback function
         * @return void
         */
        on: function(callback) {
            var $ctx = this.$ctx,
                self = this;

            var totalAttr = $('.plp-filter-controllbar__applied-filter-item').length;
            
            if(totalAttr !== 0) {
                $( ".mod-mobile-filter-switch__holder__filter" ).prepend( "(" + totalAttr + ")" );
            }

            $('.mod-mobile-filter-switch__holder__filter').click(function(){
               $('.skin-facets-plp-filter-search').toggleClass('skin-facets-plp-filter-search--mobile');
               $('.productlistpage__filter-search').toggleClass('productlistpage__filter-search--mobile');
               $('.productlistpage__filter-action-bar').toggleClass('mobile');
               $('body').toggleClass('mobile-category-filter');
            });

            $('.mod-mobile-filter-switch__holder__sort').click(function(){
                $('.skin-productlist-order-plp-mobile').toggleClass('mobile');
                $('body').toggleClass('mobile-category-filter');
            });

            $('.page-title__back-navigation').click(function(e){
                e.preventDefault();
                window.history.back();
            });

            callback();
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
