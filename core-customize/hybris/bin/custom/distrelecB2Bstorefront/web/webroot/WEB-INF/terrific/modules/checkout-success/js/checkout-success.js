(function ($) {

	Tc.Module.CheckoutSuccess = Tc.Module.extend({

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

			this.checkForOrderNumber = $.proxy(this, 'checkForOrderNumber');
			this.initUpdateProfileInformation = $.proxy(this, 'initUpdateProfileInformation');

			this.nCalls = 0;
			this.delay = 1000;

			this.$updateProfile = $ctx.find('.update-profile');
			this.validationErrorDropdown = this.$updateProfile.data('mandatory-message');
			this.$guestB2CForm = $ctx.find('.js-guest-b2c-form');
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

			// If b2c form is on page
			if (self.$guestB2CForm.length) {
				// Execute function which contains all binds for b2c form
				self.bindGuestB2CForm(self.$guestB2CForm);
			}

			this.checkForOrderNumber();
			this.initUpdateProfileInformation();

			window._peq = window._peq || []; window._peq.push(["init"]);

			window._peq.push(["add-to-trigger",{
				"campaign_name": "PushEngage Cart Abandonment",
				"event_name": "checkout",
				"data": {
					"revenue": digitalData.cart.price.cartTotal
				}
			}]);

			var goal = {
				name: 'revenue',
				count: 1,
				value: digitalData.cart.price.cartTotal,
			};
			_peq.push(['goal', goal]);

            callback();
		},

		bindGuestB2CForm: function ($form) {
			// Get icon which toggles visibility of characters inside password field
			var $pwdReveal = $('.js-pwd-reveal', $form);
			// Get all form elements inside form
			var $fields = $('.js-guest-b2c-field', $form);
			// Get main password form element
			var $mainPassField = $('.js-main-pass', $form);
			// Get confirm password form element
			var $confirmPassField = $('.js-confirm-pass', $form);
			// Get recaptcha
			var $guestB2CCaptcha = $('.js-recaptcha', $form);
			// Get submit button
			var $guestB2CSubmitBtn = $('.js-guest-b2c-form-submit', $form);
			// Get element in which we will inject error message from BE
			var $errorMessageFromBackend = $('.js-form-error-be-msg', $form);
			// Get error message which indicates that password fields dont contain same passwords
			var $notMatchErrorMessage = $('.js-is-match-error', $form);
			// Counter which we are using for counting how many times user submitted form
			var submitCounter = 0;

			// When user clicks on submit button
			$guestB2CSubmitBtn.on('click', function () {
				// Increase counter
				submitCounter++;
				// Disable submit button
				$guestB2CSubmitBtn.attr('disabled', true).addClass('is-loading');

				// If user submits the form for 4th time, we are executing captcha
				// Else just execute ajax
				if (submitCounter > 3) {
					if (typeof $guestB2CCaptcha !== 'undefined') {
						if ($guestB2CCaptcha.length > 0) {
							var $grecaptcha = $('.g-recaptcha', $guestB2CCaptcha);
							var gid = $grecaptcha.eq(0).data('gid');

							if (typeof gid !== 'number') {
								gid = grecaptcha.render($grecaptcha[0], {callback: 'guestB2CRecaptchaCallback'}, true);
								$grecaptcha.eq(0).data({'gid': gid});
							}

							grecaptcha.reset(gid);
							grecaptcha.execute(gid);
						}
					}
				} else {
					// Execute ajax request and validations
					ajaxValidationRequest();
				}
			});

			// As user types main password
			$mainPassField.on('input', function () {
				var $self = $(this);
				// Remove empty spaces in value
				$self.val($self.val().trim());
				// Execute validation for field if password contains at least 6 numbers
				validatePdwInputField(6, $self, $confirmPassField);
			});

			// As user types confirm password
			$confirmPassField.on('input', function () {
				var $self = $(this);
				// Remove empty spaces in value
				$self.val($self.val().trim());
				// Execute validation for field to see if main and confirm passwords matches
				validatePwdInputMatches(6, $mainPassField, $self);
			});

			$pwdReveal.on('click', function () {
				var $clickedPwdReveal = $(this);
				var $thisFormGroup = $clickedPwdReveal.closest('.js-guest-b2c-form-group');
				var $eyeIcon = $('.js-eye', $thisFormGroup);
				var $eyeSlashIcon = $('.js-eye-slash', $thisFormGroup);
				var $pwdField = $('.js-guest-b2c-field', $thisFormGroup);
				var isPasswordType = $pwdField.attr('type') === 'password';

				$pwdField.attr('type', isPasswordType ? 'text' : 'password');
				$eyeIcon.toggleClass('hidden', !isPasswordType);
				$eyeSlashIcon.toggleClass('hidden', isPasswordType);
			});

			// Function which contain ajax request and validations
			var ajaxValidationRequest = function () {
				$.ajax({
					url: '/registration/checkout/register/guest',
					type: 'POST',
					data: $form.serialize(),
					success: function () {
						// On success, redirect user to welcome page
						window.location.href = "/welcome";
					},
					error: function (data) {
						// On error, enable button again
						$guestB2CSubmitBtn.attr('disabled', false).removeClass('is-loading');
						// Add class on all form elements
						$fields.addClass('error');

						// If BE is sending us message in response, inject message into element and show it
						// Else just show FE error messages
						if (!!data.responseJSON) {
							$errorMessageFromBackend.removeClass('hidden').find('.js-form-error-be-msg-text').html(data.responseJSON.message);
						} else {
							$errorMessageFromBackend.addClass('hidden');
							$notMatchErrorMessage.removeClass('hidden');
						}
					}
				});
			};

			guestB2CRecaptchaCallback = function () {
				// Execute ajax request and validations when recaptcha is successfully solved
				ajaxValidationRequest();
			};

			function validatePdwInputField(minCharacters, $pwdField, $pwdFieldRepeat) {
				if ($pwdField.val().length < minCharacters) {
					$pwdField.addClass('error').removeClass('success');
				} else {
					$pwdField.removeClass('error').addClass('success');
				}

				if ($pwdFieldRepeat.val() !== '') {
					validatePwdInputMatches(minCharacters, $pwdField, $pwdFieldRepeat);
				}
			}

			function validatePwdInputMatches(minCharacters, $pwdField, $pwdFieldRepeat) {
				var $pwdFieldRepeatError = $pwdFieldRepeat.closest('.js-guest-b2c-form-group').find('.js-form-error-msg');
				var $formSubmitBtn = $pwdFieldRepeat.closest('.js-guest-b2c-form').find('.js-guest-b2c-form-submit');

				if ($pwdField.val() !== $pwdFieldRepeat.val()) {
					$pwdFieldRepeat.addClass('error').removeClass('success');
					$pwdFieldRepeatError.removeClass('hidden');
					$formSubmitBtn.attr('disabled', true);
				} else {
					if ($pwdFieldRepeat.val() === '' || ($pwdFieldRepeat.val().length < minCharacters)) {
						$pwdFieldRepeat.addClass('error').removeClass('success');
						$pwdFieldRepeatError.removeClass('hidden');
						$formSubmitBtn.attr('disabled', true);
					} else {
						$pwdFieldRepeat.removeClass('error').addClass('success');
						$pwdFieldRepeatError.addClass('hidden');
						$errorMessageFromBackend.addClass('hidden');
						$formSubmitBtn.attr('disabled', false);
					}
				}
			}
		},

		initUpdateProfileInformation: function () {
			var self = this,
				$select = this.$updateProfile.find('select'),
				$button = this.$updateProfile.find('.btn-update-profile');

			// Lazy Load SelectBoxIt Dropdown
			if (!Modernizr.touch && ! Modernizr.isie7  && ! Modernizr.iseproc) {
				Modernizr.load([
					{
						load: '/_ui/all/cache/dyn-jquery-selectboxit.min.js',
						complete: function () {
							$select.selectBoxIt({
								autoWidth: false,
								defaultText: $select.data('pretext'),
								dynamicPositioning: false
							});

						}
					}
				]);
			}
			$button.click (function (ev) {
				self.updateProfileInformation (ev);
			});
		},

		updateProfileInformation: function (ev) {
			ev.preventDefault();
			var self = this,
			hasValidationErrors = false;

			Tc.Utils.validate($('.validate-dropdown',self.$ctx), self.validationErrorDropdown, 'triangle', function(error) {
				if (error) { hasValidationErrors = true; }
			});

			if (!hasValidationErrors) {

				var $select = this.$updateProfile.find('select'),
					departmentCode = $select.filter('#department').val(),
					functionCode = $select.filter('#function').val(),
					data = '{';

				if (departmentCode !== undefined) {
					data += '"departmentCode":"' + departmentCode + '"';
				}

				if (departmentCode !== undefined && functionCode !== undefined) {
					data += ',';
				}

				if (functionCode !== undefined) {
					data += '"functionCode":"' + functionCode + '"';
				}

				data += '}';

				$.ajax({
					url: '/register/updateuserInfo',
					data: {updateUserInfoJson: data},
					method: 'POST',
					success: function() {
						self.$ctx.find('.btn-checkout').removeClass('hidden');
						self.$updateProfile.slideUp();
					},
					error: function() {
						self.$updateProfile.find('.error').text(self.$updateProfile.data('error-message'));
					}
				});
			}
		},

        checkForOrderNumber: function () {
			var mod = this
				, erpOK = false
                , orderCode = this.$$('.order-code-loading').data('order-code')
            ;

			$.getJSON({
				url: '/checkOrder/erpCode',
				data: {
					code: orderCode
				},
				cache: false,
				success: function (data) {
					var jsonData = typeof data === 'object' ? data : typeof data === 'string' ? $.parseJSON(data) : {};

					if (typeof jsonData.status !== 'undefined' && jsonData.status === "ok") {
						mod.$$('.order-code .big').html(jsonData.erpCode);

						if (typeof jsonData.voucher !== 'undefined' && jsonData.voucher.code) {
							mod.$$('.voucher-code').html(jsonData.voucher.code);
							mod.$$('.voucher-value').html(mod.$$('.voucher').data('voucher-value-text').replace('{0}', jsonData.voucher.value));
							mod.$$('.voucher-end-date').html(mod.$$('.voucher').data('voucher-end-date-text').replace('{0}', jsonData.voucher.toDate));
							mod.$$('.voucher').show();
						}
						mod.$$('.order-code-loading').hide();
						mod.$$('.order-code').show();
						erpOK = true;
					}
				},
				complete: function () {
					mod.nCalls++;
					if (!erpOK && mod.nCalls < 8) {
						mod.delay = (mod.nCalls == 7 ? 58000 : (mod.delay * 2));

						setTimeout(function () {
							mod.checkForOrderNumber();
						}, mod.delay);

					} else if (!erpOK) {
						var orderCodeErrorMessage = mod.$$('.order-code').data('load-order-code-error');
						mod.$$('.meta, .big').remove();
						mod.$$('.confirmation-text-2').html(orderCodeErrorMessage);
						mod.$$('.order-code-loading').hide();
						mod.$$('.order-code').show();
					}
				}
			});
		}
	});

})(Tc.$);


