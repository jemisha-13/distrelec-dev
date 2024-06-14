(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class LightboxDoubleOptinNotice
	 * @extends Tc.Module
	 */
	Tc.Module.LightboxDoubleOptinNotice = Tc.Module.extend({

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
			var self = this;
			self.$modal.modal({'backdrop': 'static'});

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
