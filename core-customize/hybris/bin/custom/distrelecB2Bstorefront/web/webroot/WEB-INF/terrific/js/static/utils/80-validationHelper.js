
	/***********************************************************************************
	*
	* 	Awesome Validation Helper: Validates Form Fields, shows Errors
	* 	-> Choose uiType: Popover Error or Triangle Error (Backend Style)
	*	-> Callback Returns true if an Error occured
	*
	* 	Usage see:
	* 	- mod-newslettersubscribe
	*  	- mod-shoppinglist-meta-actions
	*	- mod-lightbox-share-email
	*	...
	*
	 **********************************************************************************/


	/***********************************************************************************
	 *
	 * Validate Main Function
	 *
	 **********************************************************************************/


	/**
	 * Validates one or more $elements that have one of the following classes
	 *
	 * -> validate-empty
	 * -> validate-email
	 * -> validate-email-repeat
	 * -> validate-dropdown
	 * -> validate-checkbox-group
	 * -> validate-radio-group
	 * -> validate-min-max
	 * -> captcha
	 * -> validate-range
	 *
	 * @param {jQuery} $elements - jQuery DOM Elements to validate
	 * @param {string} errorContent - Error Text or HTML
	 * @param {string} [uiType=popover] - 'triangle' or 'popover'
	 * @callback [callback] - Returns true|false
	 */

	Tc.Utils.validate = function($elements, errorContent, uiType, callback) {

		var  self = this

			// Boolean Flag, will be returned as !validState in the callback
			,validState = true

			// Show Validation Error either as Popover (above Inputfield) or Triangle (below Inputfield)
			,uiType = (uiType === 'undefined') ? 'popover' : uiType

			// The callback which returns a boolean error state
			,callback = (typeof callback === 'undefined') ? {} : callback
		;

		/*
		 * Iterate over each element that needs to be validated
		 */
		$.each($elements, function() {

			var $element = $(this),
				elementIsValid = true
			;

			// Validate Element Types
			elementIsValid = Tc.Utils._validateField($element);

			// Dropdown: Work on the the SelectBoxIt Dropdown instead of the original <select>
			if($element.hasClass('validate-dropdown')) {
				$element = $element.next().children().first();
			}

			//  Uo-oh, not valid
			if(elementIsValid == false) {

				validState = false;

				// Only proceed if element does not have an .error class already
				// for people who click on the submit button multiple times..
				if($element.hasClass('error') == false) {

					// Get custom error content if available
					var errorContentCustom = $element.data('custom-error-message');
					if(errorContentCustom) { errorContent = errorContentCustom }

					// Show Validation Error
					Tc.Utils._showValidationError($element, uiType, errorContent);

					// Remove Validation Error on Focus
					Tc.Utils._bindEventHideError($element, uiType);
				}
			}
		});

		// Execute callback, send validState as "error" object
		if(typeof callback !== 'undefined') {
			callback(!validState);
		};
	}

	/**
	 * Scrolls to the first .error in $ctx
	 *
	 * @param $ctx
	 */
	Tc.Utils.scrollToFirstError = function($ctx, speed, offset) {
		// If selectboxit is applied, we need to use span next to it since main select is hidden
		if ($ctx.hasClass('js-selectboxit')) {
			$ctx = $ctx.siblings('span.selectboxit-container');
		}

		$('html, body').animate({
			scrollTop: $ctx.offset().top - (offset ? offset : 200)
		}, speed ? speed : 200);
	}


	/***********************************************************************************
	 *
	 * Validate Helper Functions
	 *
	 **********************************************************************************/


	/**
	 * Validates the differenty types of elements (input, dropdown...)
	 *
	 * @param $element
	 * @returns {boolean}
	 * @private
	 */
	Tc.Utils._validateField = function($element) {

		//ie7 fix $element.hasClass undefined wont run code any further if its not a jQuery Object or undefined
		if(!($element instanceof jQuery) || (typeof($element) === "undefined")) {
			return false;
		}

		if($element.hasClass('validate-empty')) {
			return $element.val() !== '';
		}

		// Removes blank spaces, ensures that value doesn't contain only empty spaces
		if($element.hasClass('validate-empty-trim')) {
			return $element.val().trim() !== '';
		}

		if($element.hasClass('html-validate-email') && $element.attr('type') === "email") {
			return $element[0].validity.valid;
		}

		if($element.hasClass('validate-email')) {

			var  emailAddress = $element.val()
				,atpos = emailAddress.indexOf('@')
				,dotpos = emailAddress.lastIndexOf('.');

			return (atpos >= 1 && dotpos > atpos + 2 && dotpos + 2 <= emailAddress.length);
		}

		if($element.hasClass('validate-dropdown')) {
			return $element.val() !== null;
		}
		
		if ($element.hasClass('validate-checkbox')) {
			return $element.is(':checked');
		}

		if($element.hasClass('validate-checkbox-group')) {
			return $('input[type=checkbox]:checked', $element).length > 0;
		}

		if($element.hasClass('validate-radio-group')) {

			return $('input[type=radio]:checked', $element).length > 0;

		}

		// Check if the number of digits is greater than or equal to the minimum value and less than
		// or equal to the maximum value within the element using data-min and data-max
		if($element.hasClass('validate-min-max')) {

			var  minValue = $element.data('min')
				,maxValue = $element.data('max');

			return ($element.val().length >= minValue && $element.val().length <= maxValue);
		}

		//check if the number of digits is into min and max
		if($element.hasClass('validate-min-max-digits')) {

			var  minValue = $element.data('min')
				,maxValue = $element.data('max');

			return ($element.val() !== '' && parseInt($element.val().length) >= minValue && parseInt($element.val().length) <= maxValue);
		}		
		
		// String must have the length specified by a data attribute
		if($element.hasClass('validate-length')) {

			var  length = $element.data('length');

			return (length == undefined || $element.val().length == length);
		}

		// String must have the minimum length specified by a data attribute
		if($element.hasClass('validate-min-length')) {

			var  length = $element.data('min-length');

			return (length == undefined || $element.val().length >= length);
		}

		// String must have the maximum length specified by a data attribute
		if($element.hasClass('validate-max-length')) {

			var  length = $element.data('max-length');

			return (length == undefined || $element.val().length <= length);
		}

		// String is not mandatory, but it must have length 12
		if($element.hasClass('validate-length-ch')) {

			var length = $element.data('length');

			return ($element.val() == '' || $element.val().length == length);
		}		

		// Captcha: Valid if inputfield not empty. Real validation is made in the backend
		if ($element.hasClass('captcha')) {
			var $captchaInputfield = $('#captchaAnswer', $element),
				captchaValue = $captchaInputfield.val();

			return (captchaValue !== '' && captchaValue !== undefined);
		}

		// range: valid if min <= max
		if ($element.hasClass('validate-range')) {
			var _min = parseInt($element.find('.customMin').val(), 10),
				_max = parseInt($element.find('.customMax').val(), 10);

			return _min <= _max;
		}
		
		// e-mail repeat: valid if addresses match
		if ($element.hasClass('validate-email-repeat')) {
			var _id=$element.attr('id');
			var $email=$('#'+_id.substr(0,_id.length-7));  // get the input field with same id minus "-repeat"
			return $element.val().toLowerCase()==$email.val().toLowerCase();
		}
		
		if($element.hasClass('validate-vat-italy')) {

			var country = $('.countrySelectExportShop').val();
			
			if (country == 'IT'){
				var val = $element.val();
				return (val !== '' && $element.val().length == 11);
			}
			if (country == 'SM'){
				
				var val = $element.val();
				var firstTwoLetters = val.substring(0,2);
				var secondFiveDigits = val.substring(2,18);
				
				
				if (firstTwoLetters.length !== 2 || secondFiveDigits.length !== 5){
					return false;
				}
				
				// 2 lettres
				var areTwoFirstCharactersLetters = /^[a-zA-Z()]+$/.test(firstTwoLetters);
				
				// 5 digits
				var areSecondFiveCharactersDigits = /^\d+$/.test(secondFiveDigits);
				
				
				if (val !== '' && $element.val().length == 7  && (areTwoFirstCharactersLetters  && areSecondFiveCharactersDigits) ){
					return true;
				}
				else{
					return false;
				}
			}
			if (country == 'VA'){
				return true;
			}
					

		}		
	}

	/**
	 * Show the validation error on $element
	 *
	 * @param $element
	 * @param uiType
	 * @private
	 */
	Tc.Utils._showValidationError = function($element, uiType, errorContent) {
		// Markup for showing the Triangle Validation Error Message
		var $triangleErrorMarkup = $('<div class="field-msgs"><div id="field.msgs.error" class="error"><i></i></div></div>')

		// Only show an error if there is an error message
		if(errorContent !== undefined && errorContent.length > 0) {

			// Mark item as invalid
			$element.addClass('error');

			// Popover Style
			if(uiType === 'popover') {
				this.showPopover($element, errorContent);

			// Triangle Backend Style
			} else if(uiType === 'triangle') {

				// Dropdown: Append it after the options
				if($element.hasClass('validate-dropdown')) {
					$element = $element.next();
				}

				// Checkbox: Append it after the label
				if($element.hasClass('validate-checkbox')) {
					$element = $element.siblings(':last');
				}


				// Min Max: Append it after the input box
				if($element.hasClass('validate-min-max')) {
					$element = $element.siblings().next();
				}

				// captcha
				if ($element.hasClass('captcha')) {
					$element = $element.find('#captchaAnswer');
				}

				$triangleErrorMarkup
					.clone()						// get a copy of the errorMarkup
					.insertAfter($element)			// insert it after our input field
					.find('.error')					// look for the .error div
					.prepend(errorContent)			// insert the error message into the .error div
				;
			}
		}
	}

	/**
	 * Removes any validation errors from $element
	 *
	 * @param $element
	 * @param uiType
	 * @private
	 */
	Tc.Utils._hideValidationError = function($element, uiType) {

		var $fieldMsg = $element.next('.field-msgs');

		$element.removeClass('error');

		// Popover Style
		if(uiType == 'popover') {

			this.hidePopover($element);

		// Triangle Backend Style
		} else if(uiType === 'triangle') {

			// Dropdown: Get original <select> to bind event
			if($element.hasClass('validate-dropdown')) {
				$fieldMsg = $element.siblings('.field-msgs');
			}

			// Checkbox
			if($element.hasClass('validate-checkbox')) {
				$fieldMsg = $element.siblings('.field-msgs');
			}

			// Min Max
			if($element.hasClass('validate-min-max')) {
				$fieldMsg = $element.siblings('.field-msgs');
			}

			// captcha
			if ($element.hasClass('captcha')) {
				$fieldMsg = $element.find('.field-msgs');
			}

			// range
			if ($element.hasClass('validate-range')) {
				$fieldMsg = $element.siblings('.field-msgs');
			}

			$fieldMsg
				.animate({'opacity': 0 }, 200)
					.slideUp(function() {
						$(this).remove();
					})
			;
		}
	}

	/**
	 * Binds a focus or change event to the element to remove a validation error
	 *
	 * @param $element
	 * @param uiType
	 * @private
	 */
	Tc.Utils._bindEventHideError = function($element, uiType) {

		var $evTargetEl = $element;

		// Dropdown: Get original <select> to bind event
		if($element.hasClass('validate-dropdown')) {
			$evTargetEl = $element.parent().prev();
		}

		$evTargetEl.off('change.validation').on('change.validation', function() {

			if(Tc.Utils._validateField($evTargetEl)) {
				Tc.Utils._hideValidationError($element, uiType);
			}
		});
	}


	/***********************************************************************************
	 *
	 * Utility Function
	 *
	 **********************************************************************************/


	/**
	 * Shows a popover on the $element
	 *
	 * @param $element
	 * @param errorContent
	 */
	Tc.Utils.showPopover = function ($element, errorContent) {

		$element.popover({
			content: errorContent
			,placement: 'top'
			,trigger: 'manual'
			,html: true
		})
		.popover('show');
	};

	/**
	 * Hides an (eventually) existing popover
	 *
	 * @param $element
	 */
	Tc.Utils.hidePopover = function ($element) {
		$element.popover('destroy');
	};

	/**
	 * Validation for phone number based on Google library for phone validation "https://github.com/google/libphonenumber"
	 *
	 * @param $element
	 */
	Tc.Utils.validateGoogleLibphonenumber = function ($element) {
		var elementValue = $element.val();
		// Define parent element (element which holds group for each form element)
		var $currentFormGroup = $element.closest('.js-form-group');
		// Find error element inside form-group
		var $currentFormGroupError = $('.js-form-group-error', $currentFormGroup);
		// Find error element inside form-group which can be added from BE side on page load (after form submit)
		var $currentFormGroupErrorBE = $element.siblings('.field-msgs');
		// Get isocode from "body" element
		var isoCodeBody = $('body').data('isocode');
		// Get isocode from "country" select element
		var isoCodeSelect = $('.js-libphonenumber-isocode-select:visible').val();
		// If "country" select is selected use selected value, otherwise use value from body
		var isoCode = !!isoCodeSelect ? isoCodeSelect : isoCodeBody;

		// If value is not selected in "country" field, and EX is isocode from body,
		// set isoCode as empty since we dont have country with EX isoCode and Google plugin will throw an error
		if (isoCode === 'EX') {
			isoCode = '';
		}

		// If isoCode is Norther Ireland (XI), then we use GB (same logic in backend) - DISTRELEC-27709
		if (isoCode === 'XI') {
			isoCode = 'GB';
		}

		// Remove BE validation (since FE takes in place) which appeared after form submit
		$currentFormGroupErrorBE.remove();

		// If isoCode exists, check it
		// Otherwise throw an error that phone is not valid since country is not selected (edge case only for EX where user enters value before he selects country)
		if (!!isoCode && elementValue.trim().length > 1) {
			var isPhoneNumberValid = libphonenumber.isValidNumberForRegion(elementValue, isoCode);

			// If Switzerland or Liechtenstein, we are validating number for both countries (same as in BE)
			if (isoCode === 'CH' || isoCode === 'LI') {
				isPhoneNumberValid = libphonenumber.isValidNumberForRegion(elementValue, 'CH') || libphonenumber.isValidNumberForRegion(elementValue, 'LI');
			}

			// Validate
			if (isPhoneNumberValid) {
				// Hide form error since validation passed
				$currentFormGroupError.hide();
				$element.removeClass('error');
			} else {
				// Show form error since validation failed
				$currentFormGroupError.show();
				$element.addClass('error');
			}
		} else {
			if (elementValue.trim().length > 0) {
				// When we have no isoCode (on Biz if user didnt selected any country in "select"), show error
				$currentFormGroupError.show();
				$element.addClass('error');
			}
		}
	};

	/**
	 * Validation for phone number based on Google library for phone validation "https://github.com/google/libphonenumber"
	 *
	 * @param $element
	 */
	Tc.Utils.isPhoneNumberValidByGoogleLibphonenumber = function ($element) {
		// Since Google plugin throws an error for invalid phone numbers, we need to use try/catch
		try {
			var $scopeForm = $element.closest('.js-iv-form');
			var phoneNumber = $element.val();
			// Get isocode from "body" element
			var isoCodeBody = $('body').data('isocode');
			// Get isocode from "country" select element
			var $isoCodeSelect = $('.js-libphonenumber-isocode-select:visible');
			// Init isoCode from select
			var isoCodeSelect = '';

			if ($isoCodeSelect.length > 1) {
				$isoCodeSelect = $scopeForm.find($isoCodeSelect);
			}

			// If isoCode select is span (selectboxit plugin), then use value from hidden "select" next to selectboxit element
			// Otherwise use value from select itself
			if ($isoCodeSelect.is('span')) {
				isoCodeSelect = $isoCodeSelect.parent().siblings('.js-libphonenumber-isocode-select').val();
			} else {
				isoCodeSelect = $isoCodeSelect.val();
			}

			// If "country" select is selected use selected value, otherwise use value from body
			var isoCode = !!isoCodeSelect ? isoCodeSelect : isoCodeBody;

			// If select don't exists (if only 1 country)
			// Check if we have different country on address than it is on shop
			if (!$isoCodeSelect.length) {
				// For all forms except B2E
				var countryIsoVal = $scopeForm.find('[name="countryIso"]').val();
				// For B2E form
				var countryCodeVal = $scopeForm.find('[name="countryCode"]').val();
				// We have 2 types of country fields so we need to select those one which is shown on page
				var countryFieldVal = !!countryIsoVal ? countryIsoVal : (!!countryCodeVal ? countryCodeVal : '');

				if (!!countryFieldVal) {
					if (countryFieldVal !== isoCode) {
						isoCode = countryFieldVal;
					}
				}
			}


			// If value is not selected in "country" field, and EX is isocode from body,
			// set isoCode as empty since we dont have country with EX isoCode and Google plugin will throw an error
			if (isoCode === 'EX') {
				isoCode = '';
			}

			// If isoCode is Norther Ireland (XI), then we use GB (same logic in backend) - DISTRELEC-27709
			if (isoCode === 'XI') {
				isoCode = 'GB';
			}

			// If Switzerland or Liechtenstein, we are validating number for both countries (same as in BE)
			if (isoCode === 'CH' || isoCode === 'LI') {
				return libphonenumber.isValidNumberForRegion(phoneNumber, 'CH') || libphonenumber.isValidNumberForRegion(phoneNumber, 'LI');
			} else {
				return libphonenumber.isValidNumberForRegion(phoneNumber, isoCode);
			}
		} catch (error) {
			return false;
		}
	};
