(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Lightboxquotation
	 * @extends Tc.Module
	 */
	Tc.Module.LightboxQuotationConfirmation = Tc.Module.extend({

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


			// subscribe to connector channel/s
			this.sandbox.subscribe('lightboxQuotationConfirmation', this);


			// set module variables
			this.$modal = $('.modal', $ctx);
		},


		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {
			callback();
		},


		/**
		*
		* @method openModal
		*
		*/
		openModal: function() {
			var self = this,
				$btnGo = self.$ctx.find('.btn-go');


			$btnGo.off('click').on('click', function() {
				self.hideModal();
			});

			self.$modal.modal();

			Tc.Utils.calculateModalHeight(self.$modal);
		},


		/**
		*
		* @method hideModal
		*
		*/
		hideModal: function() {
			this.$modal.modal('hide');
		},


		/**
		*
		* @method onConfirm
		*
		*/
		onConfirm: function() {
			this.openModal();
		}
	});

})(Tc.$);
