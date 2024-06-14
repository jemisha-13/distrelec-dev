(function($) {

	/**
	 * This module implements the newsletter subscribe functionality.
	 *
	 * @namespace Tc.Module
	 * @class Newslettersubscribe
	 * @extends Tc.Module
	 */
	Tc.Module.Newslettersubscribe = Tc.Module.extend({

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

			this.$inputField = this.$$('.js-newsletterSubscribeFooterInput');
			this.$successMessage = this.$$('.js-newsletterFooterSuccessMessage');
			this.$grecaptcha = this.$$('.js-footerNewsletterRecaptcha').find('.g-recaptcha');
			this.$submitButton = this.$$('.btn-signup');

			this.validationErrorEmail = this.$$('#tmpl-newslettersubscribe-error-email').html();
		}

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		,on: function(callback) {

			var self = this;

			footerSubscribe = function () {
				var email = self.$inputField.val();
				var recaptchaValue = self.$grecaptcha.last().find("[id^='g-recaptcha-response']").val();

				$.ajax({
					url: '/newsletter/subscribe',
					type: 'POST',
					data: {
						"email": email,
						"g-recaptcha-response": recaptchaValue
					},
					success: function(response, textStatus, jqXHR) {
						if(response.doubleOptIn === true) {
							$('.js-reminderPopupEmail').html(email);
							$('.js-doubleOptinReminderModal').modal({'backdrop': 'static'});
						} else {
						    // loop over required elements once submission complete and hide them
			                displaySuccessMessage();
						}
					},
					error: function(jqXHR, textStatus, errorThrown) {
					},
					complete: function() {
						var gid = self.$grecaptcha.data('gid');
						grecaptcha.reset(gid);
					}
				});
			};

			// DISTRELEC-25162
			// As user types into email field, remove error box (if previously error occurred)
			this.$inputField.on('input', function () {
				var errorPopover = $(this).siblings('.popover');

				// If popover element exists in DOM, hide it and remove error class on input
				if (errorPopover.length > 0) {
					$(this).removeClass('error');
					errorPopover.hide();
				}
			});

			// Validate E-Mail on Submit Klick
			this.$submitButton.on('click', function(e) {
				Tc.Utils.validate($('.html-validate-email',self.$ctx), self.validationErrorEmail, 'popover', function(error) {
					if(error) {
						// Since we hide popover element as user types, we are showing it again if error occurs
						self.$inputField.siblings('.popover').show();
						e.preventDefault();
					} else {
                        // disable button when email is verified
                        disableButton(true);
						submitNewsletterForm();
					}
				});
			});

            // Add event listener for the modal closing, display success message on close.
			$('.js-doubleOptinReminderModal').on('hidden.bs.modal', function() {
			    displaySuccessMessage();
			});

			function submitNewsletterForm() {
				self.$successMessage.addClass('hidden');

				var gid = self.$grecaptcha.data('gid');
				if (typeof gid !== 'number') {
					gid = grecaptcha.render(self.$grecaptcha[0],{callback:'footerSubscribe'},true);
					self.$grecaptcha.data({'gid':gid});
				}
				grecaptcha.execute(gid);
			}

            /**
            * Function to set the disabled state of the submit button.
            *
            * @param {Boolean} state the disabled property will be set to.
            */
			function disableButton(state) {
			    self.$submitButton.prop('disabled', state);
			}

            /**
            *
            * Function to loop over and hide specified elements as well as revealing the success message.
            */
			function displaySuccessMessage() {
			    // loop over required elements once submission complete and hide them
                $( ".js-hide-on-submit" ).each(function() {
                    $( this ).addClass('hidden');
                });
				self.$successMessage.removeClass('hidden');
			}

			callback();
		}

	});

})(Tc.$);

