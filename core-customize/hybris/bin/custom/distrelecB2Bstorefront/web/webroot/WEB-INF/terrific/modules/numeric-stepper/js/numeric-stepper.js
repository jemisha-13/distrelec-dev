(function ($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Lightboxshoppinglist
	 * @extends Tc.Module
	 */
	Tc.Module.NumericStepper = Tc.Module.extend({

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
		init: function ($ctx, sandbox, id) {

			// call base constructor
			this._super($ctx, sandbox, id);

			this.$ctx = $ctx;
	
			
			var self = this;
			
			self.$numeric = $('.numeric', self.$ctx);
			
			Tc.Utils.numericStepper(self.$numeric);

				

		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function (callback) {
			var self = this;

            callback();
		}
		
		
		
		



		
	});

})(Tc.$);


