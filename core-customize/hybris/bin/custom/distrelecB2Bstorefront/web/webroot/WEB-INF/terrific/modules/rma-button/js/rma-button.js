(function($) {

    /**
     * Module implementation.
     *
     * @namespace Tc.Module
     * @class Register
     * @extends Tc.Module
     */
    Tc.Module.RmaButton = Tc.Module.extend({

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

            $('.mod-rma-button .mat-button').click(function (e) {
                e.preventDefault();
                $('#returnModal').addClass('current');
                $('body').append('<div class="modal-backdrop  in"></div>');
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
