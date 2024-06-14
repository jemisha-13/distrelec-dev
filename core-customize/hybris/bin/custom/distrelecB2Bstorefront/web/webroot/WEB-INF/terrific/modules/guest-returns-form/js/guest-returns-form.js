(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Logo
	 * @extends Tc.Module
	 */
	Tc.Module.GuestReturnsForm = Tc.Module.extend({

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
		 * Hook function to trigger your events.
		 *
		 * @method after
		 * @return void
		 */
		after: function() {
			var self = this;
            // Get RMA form element
            var $rmagForm = $('.js-rmag-form', self.$ctx);
            // Get element which holds subreason elements
            var $rmagSubreason = $('.js-rmag-subreason', $rmagForm);
            // Get submit button
            var $rmagSubmitBtn = $('.js-rmag-submit-button', $rmagForm);
            // Get all select elements inside RMA form
            var $rmaReasonsSelects = $('.js-selectpicker', $rmagForm);
            // Get "select" element which contains main reasons
            var $rmagMainReasonSelect = $('.js-rmag-main-reason', $rmagForm);
            // Get "select" element which contains sub reasons
            var $rmagSubReasonSelect = $('.js-rmag-sub-reason', $rmagForm);
            // Get input element in which we will store value for sending to BE (ID of reason)
            var $rmagReasonForBEid = $('.js-reason-for-BE-id', $rmagForm);
            // Get input element in which we will store value for sending to BE (EN translation)
            var $rmagReasonForBEsubreason = $('.js-reason-for-BE-subreason', $rmagForm);

            // Function for checking if value is whole number
            function isNumeric(value) {
                return (/^\d+$/).test(value);
            }

            // DISTRELEC-27559: Getting EN translation from <option> inside select based on value
            function getEnglishTranslationFromOption ($select, value) {
                return $select.find('option[value="' + value + '"]').data('english-translation');
            }

            setTimeout(function () {
                if(!Modernizr.isie7) {
                    // Lazy Load dropdown
                    Modernizr.load([{
                        load: '/_ui/all/cache/dyn-jquery-selectboxit.min.js',
                        complete: function () {
                            $rmaReasonsSelects.selectBoxIt({
                                autoWidth: false,
                                isMobile: function() {
                                    return false;
                                }
                            });
                        }
                    }]);
                }
            }, 300);

            // When user selects main reason
            $rmagMainReasonSelect.on('change', function () {
                // Get changed select
                var $changedReason = $(this);
                // Get value which user selected
                var changedValue = $changedReason.val();
                // Split value into array so we know which sub-reasons we should show
                var changedValueSplit = changedValue.split('|');
                // Selectboxit element for sub-reason select item
                var subReasonSelectbox = $rmagSubReasonSelect.data("selectBox-selectBoxIt");
                // Selector for "selectboxit" select item
                var selectboxitSubReasonsSelector = '.selectboxit-container ul.selectboxit-list > li';

                // Remove value on sub-reason select and refresh selectboxit element
                subReasonSelectbox.selectOption('').refresh();
                // Find all selectboxit list-items instead of first one and hide them
                $rmagSubreason.find(selectboxitSubReasonsSelector + ':not(:first)').addClass('hidden');
                // Removes last item from splitted object since it is empty
                changedValueSplit.splice(-1, 1);

                var targetCounter = 0;
                // Iterate through statuses from selected main-reason (each reason can contain multiple sub-reasons with different code)
                for (var i=0; i < changedValueSplit.length; i++) {
                    var targetOption = $rmagSubreason.find(selectboxitSubReasonsSelector + '[data-code="' + changedValueSplit[i] + '"]');

                    if (targetOption.length) {
                        // Find sub-reason with code from main-reason and show it
                        $rmagSubreason.find(selectboxitSubReasonsSelector + '[data-code="' + changedValueSplit[i] + '"]').removeClass('hidden');
                        targetCounter++;
                    }
                }

                // Show sub-reason holder item
                $rmagSubreason.toggleClass('hidden', !changedValue || targetCounter === 0);

                // // Set value which we will send to BE, if there are no sub-reasons, then select default one from its value
                $rmagReasonForBEid.val(targetCounter === 0 ? changedValue : '');
                // DISTRELEC-27559: Sending EN translation so BE clearly knows which option is selected
                $rmagReasonForBEsubreason.val(targetCounter === 0 ? getEnglishTranslationFromOption($changedReason, changedValue) : '');
            });

            // When user changes sub-reason select element
            $rmagSubReasonSelect.on('change', function () {
                // Get changed select
                var $changedSubReason = $(this);
                // Get value which user selected
                var changedValue = $changedSubReason.val();

                // Set value which we will send to BE, since multiple items can contain same code, we needed to add "_x" as separator where "x" is index
                // So once we need to send it to BE, we need to remove "_X" and send first part only -> code
                $rmagReasonForBEid.val(changedValue.split('_')[0]);
                // DISTRELEC-27559: Sending EN translation so BE clearly knows which option is selected
                $rmagReasonForBEsubreason.val(getEnglishTranslationFromOption($changedSubReason, changedValue));
            });

            $(':input', $rmagForm).on('input', function () {
                var $currentInput = $(this);
                var currentValue = $currentInput.val();

                // As user types, remove error
                $currentInput.closest('.js-input-holder').removeClass('has-error');

                // If changed input is article input
                if ($currentInput.hasClass('js-validate-article')) {
                    // User should be able to enter only spaces, alphanumeric, commas, colons, semi-colons, hyphens - DISTRELEC-26561
                    var regex = /^[0-9,:;-\s]+$/g;
                    // Check if user entered allowed chars
                    var inputIsValid = regex.test(currentValue);

                    // If user entered some chars which are not allowed, remove them and populate input with valid chars
                    if (!inputIsValid) {
                        $currentInput.val(currentValue.replace(/[^0-9,:;-\s]/g, ''));
                    }
                }
            });

            // When user changes select value, remove error
            $('select', $rmagForm).on('change', function () {
                $(this).closest('.js-input-holder').removeClass('has-error');
            });

            // When user clicks on submit, execute validation and captcha
            $rmagSubmitBtn.on('click', function () {
                // Set flag to false, based on this flag we will or wont execute captacha
                var hasValidationErrors = false;

                // Iterate through each mandatory field
                $('.js-validate-empty:not(.selectboxit-btn)', $rmagForm).each(function() {
                    // Get current element
                    var $currentElement = $(this);
                    // Get current value and remove blank spaces around
                    var currentValue = $currentElement.val().trim();
                    // Get parent (scope) element of current item
                    var $currentElementHolder = $currentElement.closest('.js-input-holder');

                    // If value is empty, show error and set flag to true
                    // Else: when value is entered, check other criterias for specific fields
                    if (currentValue === '') {
                        $currentElementHolder.addClass('has-error');
                        hasValidationErrors = true;
                    } else {
                        // For article, value need to have at least 8 characters
                        if ($currentElement.hasClass('js-validate-article')) {
                            // On current value, get all chars which are NOT numbers ("[^0-9]") and replace them with nothing
                            // By this we would get only numbers in value (i.e. "123-45-678." => "12345678.", notice that dashes, dots etc. are removed)
                            // If length of numbers is equal or more than 8, validation is success
                            var numbersInCurrentValue = currentValue.replace(/[^0-9]/g, '').length;
                            // Check if value contains 8 or more numbers
                            var inputIsValidMinNumbers = numbersInCurrentValue >= 8;

                            // Toggle class based on flag
                            $currentElementHolder.toggleClass('has-error', !inputIsValidMinNumbers);
                            // Set bool to true, if we have error in validation
                            if (!inputIsValidMinNumbers) { hasValidationErrors = true; }
                        // For quantity, value needs to be whole number, not decimal
                        } else if ($currentElement.hasClass('js-rmag-qty')) {
                            // Toggle class based on flag
                            $currentElementHolder.toggleClass('has-error', !isNumeric(currentValue) || currentValue < 1);
                            // Set bool to true, if we have error in validation
                            if (!isNumeric(currentValue) || currentValue < 1) { hasValidationErrors = true; }
                        // Email needs to be in specific format
                        } else if ($currentElement.hasClass('js-validate-email')) {
                            // Regex for email
                            var emailRegex = /\S+@\S+\.\S+/;
                            // Check if email is valid
                            var emailIsValid = emailRegex.test(currentValue);

                            // Toggle class based on flag
                            $currentElementHolder.toggleClass('has-error', !emailIsValid);
                            // Set bool to true, if we have error in validation
                            if (!emailIsValid) { hasValidationErrors = true; }
                        } else {
                            $currentElementHolder.removeClass('has-error');
                        }
                    }
                });

                // If we dont have validation errors, proceed with captcha and submit
                if (!hasValidationErrors) {
                    var gid = $('.rma-guest-return .g-recaptcha',self.$ctx).eq(0).data('gid');
                    if (typeof gid !== 'number') {
                        gid = grecaptcha.render($('.rma-guest-return .g-recaptcha',self.$ctx)[0],{},true);
                        $('.rma-guest-return .g-recaptcha',self.$ctx).eq(0).data({'gid':gid});
                    }

                    grecaptcha.reset(gid);
                    grecaptcha.execute(gid);
                }
            });

            // Functionality for showing entered characters number next to the form element (character counter)
            // When user types something into element
            $('.js-char-counter-element', $rmagForm).on('input', function () {
                var $self = $(this);
                // Get "max" value from element
                var max = $self.attr('maxlength');
                // Inside scope find output element and display values (on page we can have more than one character counter elements)
                $self.closest('.js-char-counter').find('.js-char-counter-output').html($self.val().length + ' / ' + max);
            });

            // When event for success recaptcha is occured, refresh selectboxit items
            $rmagForm.on('recaptcha-success', function () {
                // // Selectboxit element for all select items
                var rmaReasonsSelectbox = $rmaReasonsSelects.data("selectBox-selectBoxIt");
                // // Remove value on all selects and refresh selectboxit element
                rmaReasonsSelectbox.selectOption('').refresh();
            });
        }
	});

})(Tc.$);
