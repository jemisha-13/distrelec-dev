(function ($) {

	Tc.Module.CheckoutConsent = Tc.Module.extend({

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

			this.inputField = this.$$('.js-checkoutConsentEmail');
			this.checkbox= this.$$('.js-checkoutConsentCheckbox');
			this.submitButton = this.$$('.js-submitCheckoutConsent');
			this.validationErrorEmail = this.$$('.js-checkoutConsentEmailError').html();
			this.successMessage = this.$$('.js-checkoutConsentSuccessMessage');
			this.grecaptcha = this.$$('.js-checkoutConsentRecaptcha').find('.g-recaptcha');
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

			consentSubscribe = function () {

				var email = self.inputField.val();
				var isPersonalizationChecked = self.checkbox.is(':checked');

				$.ajax({
					url: '/newsletter/subscribe',
					type: 'POST',
					data: {
						"email": email,
						"personalization": isPersonalizationChecked,
						"g-recaptcha-response": self.grecaptcha.last().find('#g-recaptcha-response').val(),
						"checkout": true
					},
					success: function(response, textStatus, jqXHR) {
						if(response.doubleOptIn === true) {
							$('.js-reminderPopupEmail').html(email);
							$('.js-doubleOptinReminderModal').modal({'backdrop': 'static'});
						} else {
							self.successMessage.removeClass('hidden');
						}
					},
					error: function(jqXHR, textStatus, errorThrown) {
					},
					complete: function() {
						var gid = self.grecaptcha.data('gid');
						grecaptcha.reset(gid);
					}
				});
			};

			this.submitButton.on('click', function(e) {
				Tc.Utils.validate($('.html-validate-email',self.$ctx), self.validationErrorEmail, 'popover', function(error) {
					if(error) {
						e.preventDefault();
					} else {
						submitCheckoutConsentForm();
					}
				});
			});

			this.checkbox.on('click', function() {
				digitalData.page.pageInfo.personalisation = $(this).is(':checked') ? 'subscribe' : '';
			});

			function submitCheckoutConsentForm() {
				self.successMessage.addClass('hidden');

				var gid = self.grecaptcha.data('gid');
				if (typeof gid !== 'number') {
					gid = grecaptcha.render(self.grecaptcha[0],{callback:'consentSubscribe'},true);
					self.grecaptcha.data({'gid':gid});
				}
				grecaptcha.execute(gid);
			}

		}
	});

})(Tc.$);


