(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class LightboxLoginConsentForm
	 * @extends Tc.Module
	 */
	Tc.Module.LightboxAvailabilityPopup = Tc.Module.extend({

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
			var _this = this;
			this.$modal = $('.js-availabilityPopupModal', $ctx);
			var $stockNotificationForm = this.$$('.js-stock-notification-form');

			$('.js-availabilityNotify').on('click', function() {
				_this.openModal();
			});

			$stockNotificationForm.on('submit', function (event) {
				event.preventDefault();

				var $productCode = $('#product_code').val(),
				$emailInput = $('.stock-notification__form--emailinput'),
				$stockNotificationEmail = $emailInput.val();

				var hasValidationErrors = _this.checkOutOfStockValidation();

				if (!hasValidationErrors) {
					$emailInput.removeClass('error');

					$.ajax({
						type : "POST",
						url : "/notifyZeroStock",
						data : {
							customerEmail : $stockNotificationEmail,
							articleNumber : $productCode
						},
						success : function(result) {

							if(result){
								$(".stock-notification__form").addClass('hidden');
								$(".stock-notification__success").removeClass('hidden');
								$(".stock-notification__failure").addClass('hidden');
							}else{
								$(".stock-notification__form").addClass("hidden");
								$(".stock-notification__success").addClass("hidden");
								$(".stock-notification__failure").removeClass('hidden');
							}
						}
					});
				}
			});


			this.checkOutOfStockValidation = function () {

				var hasValidationErrors = false,
				isValidEmail,
				isEmpty,
				$emptyValid = $('.stock-notification--error.error-empty'),
				$emailValid = $('.stock-notification--error.error-emailvalid'),
				$emailInput = $('.stock-notification__form--emailinput'),
				$stockNotificationEmail = $emailInput.val();

				$('.stock-notification--error').addClass('hidden');

				isEmpty = ( $emailInput.val().length < 1 );

				if (isEmpty) {
					$emptyValid.removeClass('hidden');
					hasValidationErrors = true;
					$emailInput.addClass('error');
				} else {
					$emptyValid.addClass('hidden');
					hasValidationErrors = false;
					$emailInput.removeClass('error');
				}

				isValidEmail = this.validateEmail($stockNotificationEmail);

				if (!isEmpty) {
					if (isValidEmail) {
						$emailValid.addClass('hidden');
						hasValidationErrors = false;
						$emailInput.removeClass('error');
					} else {
						$emailValid.removeClass('hidden');
						hasValidationErrors = true;
						$emailInput.addClass('error');
					}
				}

				return hasValidationErrors;

			};

			this.validateEmail = function (inputText) {
				var mailformat = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/igm;

				if(inputText.match(mailformat)) {
					return true;
				} else {
					return false;
				}
			};

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

			self.$modal.modal();

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
