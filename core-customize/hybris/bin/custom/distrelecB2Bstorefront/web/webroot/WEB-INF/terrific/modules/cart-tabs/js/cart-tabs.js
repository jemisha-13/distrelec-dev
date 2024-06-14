(function($) {

    /**
     * Module implementation.
     *
     * @namespace Tc.Module
     * @class Category-thumbs
     * @extends Tc.Module
     */
    Tc.Module.CartTabs = Tc.Module.extend({

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

            $('.tabs-holder__header__item').click(function(){

               var dataAttr = $(this).attr('data-tab-id');

               $('.tabs-holder__header__item').removeClass('tabs-holder__header__item--active');
               $('.tabs-holder__content__item').removeClass('tabs-holder__content__item--active');
               $(this).addClass('tabs-holder__header__item--active');
               $('.tabs-holder__content__item[data-tab-id = '+dataAttr+']').addClass('tabs-holder__content__item--active');

            });
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
