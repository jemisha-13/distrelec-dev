(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class LightboxLoginConsentForm
	 * @extends Tc.Module
	 */
	Tc.Module.LightboxLoginConsentForm = Tc.Module.extend({

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
			var initialCheckboxState;
			this.$modal = $('.js-preferenceConsentModal', $ctx);

			this.formSubmitControl();
			saveCheckboxStates();

			this.$ctx.on('click', '.js-preferenceConsentModal .js-submitConsentForm', function(e) {
				e.preventDefault();
				var isDe = $('.js-preferenceConsentModal').data('is-de');
				var preferenceStatusCheckboxes = {};
				var emailChecked = $('.js-preferenceConsentModal .js-emailCheckbox').prop('checked');

				preferenceStatusCheckboxes.newsLetterConsent = emailChecked;
				preferenceStatusCheckboxes.saleAndClearanceConsent = emailChecked;
				preferenceStatusCheckboxes.knowHowConsent = emailChecked;
				preferenceStatusCheckboxes.personalisedRecommendationConsent = emailChecked;
				preferenceStatusCheckboxes.customerSurveysConsent = emailChecked;

				$('.js-preferenceConsentModal .js-checkbox').each(function(i,element) {
					var checkbox = $(element);
					var inputName = checkbox.attr('name');

					preferenceStatusCheckboxes[inputName] = checkbox.prop('checked');
				});

				$.ajax({
					url: '/my-account/preference-center',
					type: 'POST',
					dataType: 'json',
					contentType: "application/json; charset=utf-8",
					method: 'post',
					data: JSON.stringify(preferenceStatusCheckboxes),
					success: function () {
						_this.hideModal();
						if(emailChecked && isDe === true) {
							setTimeout(function() {
								$('.js-doubleOptinReminderModal').modal({'backdrop': 'static'});
							}, 400);
						}
					}
				});

			});

			this.$ctx.on('change','.js-preferenceConsentModal .js-checkbox', function(e) {
				digitalData.page.pageInfo.prefRemoved = [];
				digitalData.page.pageInfo.prefAdded = [];
				$.each(initialCheckboxState, function( index, value ) {
					var element = value.ref;
					var parentNames = '';
					var name = element.attr('name');

					if((value.checked !== element.prop('checked')) || value.changed) {
						value.changed = true;

						if(value.ref.prop('checked')) {
							digitalData.page.pageInfo.prefAdded.push(parentNames + name);
						} else {
							digitalData.page.pageInfo.prefRemoved.push(parentNames + name);
						}
					}

				});


			});

			function saveCheckboxStates() {
				initialCheckboxState = [];
				$('.js-preferenceConsentModal .js-checkbox').each(function(i,element) {
					var elementState = {};
					elementState.ref = $(element);
					elementState.checked = $(element).prop('checked');
					initialCheckboxState.push(elementState);
				});
			}

			this.$ctx.on('click','.js-termsCheckbox', function() {
				_this.formSubmitControl();
			});

			// subscribe to connector channel/s
			this.sandbox.subscribe('lightboxLoginConsentForm', this);

			this.openModal();
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

			$('.modal-backdrop').off('click');

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
		},

		formSubmitControl: function() {
			var termsChecked = $('.js-termsCheckbox').is(':checked');
			$('.js-submitConsentForm').prop('disabled', !termsChecked);
		}
	});

})(Tc.$);
