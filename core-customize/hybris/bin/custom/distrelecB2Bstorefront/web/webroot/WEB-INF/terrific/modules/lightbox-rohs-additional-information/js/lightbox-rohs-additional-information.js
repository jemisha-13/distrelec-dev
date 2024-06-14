(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class LightboxDoubleOptinNotice
	 * @extends Tc.Module
	 */
	Tc.Module.LightboxRohsAdditionalInformation = Tc.Module.extend({

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

			var self = this;

			this._super($ctx, sandbox, id);

			// set module variables
			this.$modal = $('.js-rohsAdditionalInformation', $ctx);

			var htmlBodyClass = $('html');

			$('.js-openRohsAdditionalInformationModal').on('click', function(e){
				e.preventDefault();
				self.openModal();


				if(htmlBodyClass.hasClass('isie7')) {

					$('.col-12.d-print-flex').css('z-index', 9999);
					$('.col-12.d-print-flex').css('z-index', 9999);
					$('.link.js-openRohsAdditionalInformationModal').css('visibility', 'hidden');
					$('.link').css('visibility', 'hidden');
				}
			});

			$('.continueShopping').on('click',function(){
				if(htmlBodyClass.hasClass('isie7')) {
					$('.link.js-openRohsAdditionalInformationModal').removeAttr('style');
					$('.link').removeAttr('style');
				}
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
