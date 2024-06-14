(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class LightboxRejectOrder
	 * @extends Tc.Module
	 */
	Tc.Module.LightboxOrderReference = Tc.Module.extend({

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
			this.sandbox.subscribe('lightboxOrderReference', this);

			// set module variables
			this.$modal = this.$$('.modal');
			this.$body = this.$$('.bd');

			this.$btnConfirm = this.$$('.btn-submit');
			this.$btnCancel = this.$$('.btn-cancel');
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {
			var self = this;


			self.$btnConfirm.on('click', function() {

				var orderReference = self.$$('.order-reference').val();
				$(".open-order-reference").text(orderReference);
				
				self.hideModal();
				
				self.fire('dialogConfirm', { orderReference: orderReference},['lightboxOrderReference']);
			});

			self.$btnCancel.on('click', function() {
				self.hideModal();
			});

			callback();
		},

		openModal: function (data) {
			var self = this;

			// Display overlay/modal
			this.$modal.modal();
			Tc.Utils.calculateModalHeight(self.$modal);

		},

		hideModal: function() {
			// hide overlay/modal
			this.$modal.modal('hide');
		},

		/**
		 * Register Event Listeners
		 */

		onEditOrderReference: function(data){
			var self = this;

			this.$modal.find('.order-reference').val(data.orderReference);

			self.openModal();
		},

		onLightboxConfirm: function(){
        	Tc.Utils.lightboxConfirm(this);
        }
	});

})(Tc.$);
