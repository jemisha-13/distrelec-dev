(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class LightboxMarketingEmailExample
	 * @extends Tc.Module
	 */
	Tc.Module.LightboxMarketingEmailExample = Tc.Module.extend({

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
			this.sandbox.subscribe('lightboxMarketingEmailExample', this);


			// set module variables
			this.$modal = $('.modal', $ctx);

			_this = this;

			$('.js-consentForm .js-listItem a ').on("click", function(e) {
				e.preventDefault();
				var imageSource = $(e.currentTarget).closest('.js-listItem').data('img-src');
				_this.$modal.find('.js-modalImage').attr('src', '');
				_this.$modal.find('.js-modalImage').attr('src', imageSource);
				_this.openModal();
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
