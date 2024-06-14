(function($) {

    /**
     * Module implementation.
     *
     * @namespace Tc.Module
     * @class CheckoutOrderSummaryInfoBox
     * @extends Tc.Module
     */
    Tc.Module.CheckoutOrderSummaryInfoBox = Tc.Module.extend({

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
			var $ctx = this.$ctx,
				self = this;

			callback();

		}
    });

})(Tc.$);
