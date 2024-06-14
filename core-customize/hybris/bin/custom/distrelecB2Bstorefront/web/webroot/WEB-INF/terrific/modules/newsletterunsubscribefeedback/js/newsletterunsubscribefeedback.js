(function($) {

	/**
	 * This module implements the newsletter unsubscribe feedback functionality.
	 *
	 * @namespace Tc.Module
	 * @class Newsletterunsubscribefeedback
	 * @extends Tc.Module
	 */
	Tc.Module.Newsletterunsubscribefeedback = Tc.Module.extend({

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

			// Get item which holds hole scope
			this.$feedbackScope = this.$$('.js-unsubscribeFeedback');
			// Get feedback form
			this.$feedbackForm = this.$$('.js-unsubscribeFeedback-form', this.$feedbackScope);
			// Item which contain each reason scope
			this.$feedbackReasons = this.$$('.js-unsubscribeFeedback-reasons', this.$feedbackForm);
			// All checkboxes
			this.$feedbackReasonsCheckboxes = this.$$('input[type="checkbox"]', this.$feedbackReasons);
			// Item for which as user types, we enable/disable submit button
			this.$feedbackReasonsOnTypeFields = this.$$('.js-on-type-check', this.$feedbackReasons);
			// All form elements (checkboxes, inputs, texteareas etc.)
			this.$feedbackReasonsALLInputs = this.$$(':input', this.$feedbackReasons);
			// Submit button in form
			this.$feedbackSubmit = this.$$('.js-unsubscribeFeedback-submit', this.$feedbackForm);
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

			// Function for toggling visibility of additional field below checkbox
			function additionalFieldCheck ($field) {
				// Get state of checkbox (is it checked or unchecked)
				var state = $field.is(':checked');
				// Get element which we will toggle
				var $additionalField = $field.closest('.js-unsubscribeFeedback-reason').find('.js-unsubscribeFeedback-reason-additional');

				// If additional field exists next to the checkbox
				if ($additionalField.length) {
					// Toggle its state
					$additionalField.toggle(state);
				}
			}

			// Function for iterating through the checkboxes and returning the state which shows us if there is at least one checkbox which is selected
			// Note: Checkbox can contain sub-element (additional element) which needs to be populated in addition to pass validation
			function interateAndCheckSelection () {
				// At beginning setting temporary state to false (for main checkbox)
				var tempStateMain = false;
				// At beginning setting temporary state to false (for additional item in checkbox like email field or textearea)
				var tempStateAdditional = false;
				// Flag for checking if additional mandatory item exists
				var tempStateAdditionalMandatoryExists = false;
				// State which is used for setting button to be enabled or disabled
				var buttonStateEnabled = false;

				// Iterate through every checkbox
				self.$feedbackReasonsCheckboxes.each(function () {
					var $currentInput = $(this);

					// If current checkbox is selected
					if ($currentInput.is(':checked')) {
						// Get additional field scope (if exists) which can contain mandatory element (if contains class "js-is-mandatory")
						var $additionalField = $currentInput.closest('.js-unsubscribeFeedback-reason').find('.js-unsubscribeFeedback-reason-additional');

						// If: additional field scope exists and if there is class "js-is-mandatory"
						// Else: there is no additional field, so return true
						if ($additionalField.length) {
							// If: class for mandatory item exists, we need to check if value is populated for it
							// Else: not exists, set true since main checkbox is selected
							if ($additionalField.hasClass('js-is-mandatory')) {
								// Set temp bool to true since mandatory item exists
								tempStateAdditionalMandatoryExists = true;

								// If mandatory element contains value
								if ($(':input', $additionalField).val()) {
									// Set state to true
									tempStateAdditional = true;
								}
							} else {
								// Set state to true
								tempStateMain = true;
							}
						} else {
							// Set state to true
							tempStateMain = true;
						}
					}
				});

				// If: main checkbox is selected and additional mandatory item exists
				// Else if: only main checkbox is selected
				// Else if: only additional mandatory item exists
				if (tempStateMain && tempStateAdditionalMandatoryExists) {
					// If value for additional item is filled
					if (tempStateAdditional) {
						buttonStateEnabled = true;
					}
				} else if (tempStateMain && !tempStateAdditionalMandatoryExists) {
					buttonStateEnabled = true;
				} else if (!tempStateMain && tempStateAdditionalMandatoryExists) {
					// If value for additional item is filled
					if (tempStateAdditional) {
						buttonStateEnabled = true;
					}
				}

				// Return state
				return buttonStateEnabled;
			}

			// On page load check if there is preselected checkbox and toggle its additional field if exists
			// Once user submits form and then go back on form page, form stays populated
			self.$feedbackReasonsCheckboxes.each(function () {
				var $self = $(this);
				additionalFieldCheck ($self);
			});

			// As user is clicking on checkboxes, check additional field and enable/disable submit button based on state
			self.$feedbackReasonsCheckboxes.on('change', function () {
				var $clickedCheckbox = $(this);

				// Check for additional field
				additionalFieldCheck ($clickedCheckbox);
				// enable/disable submit button based on state
				self.$feedbackSubmit.prop('disabled', !interateAndCheckSelection());
			});

			// As user types on specific elements, enable/disable submit button based on state
			self.$feedbackReasonsOnTypeFields.on('input', function () {
				self.$feedbackSubmit.prop('disabled', !interateAndCheckSelection());
			});

			// enable/disable submit button based on state
			self.$feedbackSubmit.prop('disabled', !interateAndCheckSelection());

			// When user click on button
			self.$feedbackSubmit.on('click', function () {
				var $clickedButton = $(this);
				// Temporary disable it so user won't be able to execute form submit multiple times at once
				$clickedButton.prop('disabled', true);

				// Set email validation to true, if email is not valid it will be set to false below in validation function
				var isEmailValid = true;

				// Iterate through each reason, in future we could have multiple emails
				self.$feedbackReasonsCheckboxes.each(function () {
					var $currentInput = $(this);

					// If current checkbox is selected
					if ($currentInput.is(':checked')) {
						// Find scope of current element
						var $currentInputScope = $currentInput.closest('.js-unsubscribeFeedback-reason');
						// Find input field with class for email validation
						var $currentInputScopeEmailValidate = $('.validate-email', $currentInputScope);

						// If element with class for email validation exists
						if ($currentInputScopeEmailValidate.length) {
							// Get error message
							var errorMsg = $currentInputScope.data('email-error-msg');

							// Validate email if something is entered into field, field is optional
							if ($currentInputScopeEmailValidate.val().length > 0) {
								// Execute field validation
								Tc.Utils.validate($currentInputScopeEmailValidate, errorMsg, 'triangle', function(error) {
									if (error) {
										isEmailValid = false;
										// Unbind input so we dont have multiple binded events on same element
										$currentInputScopeEmailValidate.unbind('input');
										// When user enters something, remove error classes and elements
										$currentInputScopeEmailValidate.on('input', function () {
											var $currentInput = $(this);

											$currentInput.removeClass('error');
											$currentInput.siblings('.field-msgs').remove();
										});
									}
								});
							}
						}
					}
				});

				// If there is at least one checkbox selected and email is valid
				if (interateAndCheckSelection() && isEmailValid) {
					// Trigger event on form which means that piwik logic can be started
					self.$feedbackForm.trigger('piwikAnalytics-start');
				} else {
					// If form is invalid, activate button immediately
					self.$feedbackSubmit.prop('disabled', false);
				}

				// After short delay, enable button again
				setTimeout(function () {
					$clickedButton.prop('disabled', false);
				}, 5000);
			});

			// Once piwik logic has been finished, proceed with form submission
			self.$feedbackForm.on('piwikAnalytics-end', function () {
				$unsubscribeFeedbackForm.submit();
			});

			callback();
		}
	});
})(Tc.$);
