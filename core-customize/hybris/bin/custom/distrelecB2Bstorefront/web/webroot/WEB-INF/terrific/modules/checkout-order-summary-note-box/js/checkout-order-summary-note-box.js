(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class CheckoutOrderSummaryNoteBox
	 * @extends Tc.Module
	 */
	Tc.Module.CheckoutOrderSummaryNoteBox = Tc.Module.extend({

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
				self = this,
				$textarea = self.$$('textarea'),
				$form = self.$$('form');

			$textarea.on('blur', function() {
				var text = $(this).val();
				$.ajax({
					url: '/checkout/review/addNote',
					type: 'post',
					data: {
						note: text
					},
					success: function (data) {
					}
				});
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