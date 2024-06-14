(function ($) {

	/**
	 * OrderDetail Skin implementation for the module CartList.
	 *
	 * @author Céline Müller
	 * @namespace Tc.Module.CartList
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.CartList.ReturnItems = function (parent) {
		// subscribe to connector channel/s
		this.sandbox.subscribe('cartlistBulkAction', this);
		this.sandbox.subscribe('lightboxShoppinglist', this);
		
		var self = this,
			$ctx = this.$ctx;

        this.on = function (callback) {
        	// String selector for quantity input fields
        	var rmaQtyInputSelector = '.js-rma-qty';
        	// Get error message which shows if user submits form without or with wrong data
			var $rmaErrorMessage = $('.js-error-message', $ctx);
			// Get RMA form element inside module
			var $rmaForm = $('.js-rma-form', $ctx);
			// Get element which holds "reason" elements inside RMA form
			var $rmaReasons = $('.js-rma-reasons', $rmaForm);
			// Get all select elements inside RMA form
			var $rmaReasonsSelects = $('.js-selectpicker', $rmaForm);
			// Get all quantity input fields
			var $rmaQtyInput = $(rmaQtyInputSelector, $rmaForm);
			// Get "Select max" element inside RMA form
			var $rmaQtySelectMax = $('.js-rma-qty-select-max', $rmaForm);
			// Get "select" element which contains main reasons
			var $rmaMainReasonSelect = $('.js-rma-main-reason', $rmaForm);
			// Get "select" element which contains sub reasons
			var $rmaSubReasonSelect = $('.js-rma-sub-reason', $rmaForm);
			// Get submit button
			var $rmaSubmitButton = $('.js-rma-form-submit', $rmaForm);

			// After short delay, load selectboxit script, init selectboxit plugin and reveal RMA form
			setTimeout(function () {
				// Lazy Load SelectBoxIt Dropdown
				if(!Modernizr.isie7) {
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

			// Function for checking if value is whole number
			function isNumeric(value) {
				return (/^\d+$/).test(value);
			}

			// Function which iterates through visible input fields and enables/disables submit button based on value
			function enableDisableSubmitButton () {
				// Get all visible quantity inputs
				var $visibleQtyInputs = $rmaForm.find(rmaQtyInputSelector + ':visible');

				// If there is visible item, iterate through them and check if it contains value, if it contain the enable button
				// Else disable submit button
				if ($visibleQtyInputs.length) {
					var anyFilled = false;

					$visibleQtyInputs.each(function () {
						var $currentQty = $(this);

						if ($currentQty.val().length) {
							anyFilled = true;
						}
					});

					$rmaSubmitButton.prop('disabled', !anyFilled);
				} else {
					$rmaSubmitButton.prop('disabled', true);
				}
			}

			// DISTRELEC-27559: Getting EN translation from <option> inside select based on value
			function getEnglishTranslationFromOption ($select, value) {
				return $select.find('option[value="' + value + '"]').data('english-translation');
			}

			// When user selects main reason
			$rmaMainReasonSelect.on('change', function () {
				// Get changed select
				var $changedReason = $(this);
				// Get value which user selected
				var changedValue = $changedReason.val();
				// Split value into array so we know which sub-reasons we should show
				var changedValueSplit = changedValue.split('|');
				// Find scope of changed element, since on page we can have multiple products
				var $scope = $changedReason.closest('.js-rma-item');
				// Get element which holds every reason item
				var $reasonItems = $('.js-rma-reasons-item', $scope);
				// Get element which holds only sub-reason items
				var $scopeSubReason = $('.js-is-sub-reason', $scope);
				// Get select inside this scope which contains sub-reason choices
				var $scopeSubReasonSelect = $scope.find($rmaSubReasonSelect);
				// Selectboxit element for sub-reason select item
				var scopeSubReasonSelectbox = $scopeSubReasonSelect.data("selectBox-selectBoxIt");
				// Selector for "selectboxit" select item
				var selectboxitSubReasonsSelector = '.selectboxit-container ul.selectboxit-list > li';

				// Remove value on sub-reason select and refresh selectboxit element
				scopeSubReasonSelectbox.selectOption('').refresh();
				// Find all selectboxit list-items instead of first one and hide them
				$scopeSubReason.find(selectboxitSubReasonsSelector + ':not(:first)').addClass('hidden');
				// Removes last item from splitted object since it is empty
				changedValueSplit.splice(-1, 1);

				var targetCounter = 0;
				// Iterate through statuses from selected main-reason (each reason can contain multiple sub-reasons with different code)
				for (var i=0; i < changedValueSplit.length; i++) {
					var targetOption = $scopeSubReason.find(selectboxitSubReasonsSelector + '[data-code="' + changedValueSplit[i] + '"]');

					if (targetOption.length) {
						// Find sub-reason with code from main-reason and show it
						$scopeSubReason.find(selectboxitSubReasonsSelector + '[data-code="' + changedValueSplit[i] + '"]').removeClass('hidden');
						targetCounter++;
					}
				}

				// Show sub-reason holder item
				$scopeSubReason.toggleClass('hidden', !changedValue || targetCounter === 0);

				// If user selects first empty option, then hide all holder items besides main and sub reason
				if (!changedValue) {
					$reasonItems.not('.js-is-main-reason').not('.js-is-sub-reason').addClass('hidden');
				}

				// Enable/disable submit button
				enableDisableSubmitButton();

				// Set value which we will send to BE, if there are no sub-reasons, then select default one from its value
				$('.js-reason-for-BE-id', $scope).val(targetCounter === 0 ? changedValue : '');
				// DISTRELEC-27559: Sending EN translation so BE clearly knows which option is selected
				$('.js-reason-for-BE-subreason', $scope).val(targetCounter === 0 ? getEnglishTranslationFromOption($changedReason, changedValue) : '');
			});

			// When user changes sub-reason select element
			$rmaSubReasonSelect.on('change', function () {
				// Get changed select
				var $changedSubReason = $(this);
				// Get value which user selected
				var changedValue = $changedSubReason.val();
				// Find scope of changed element, since on page we can have multiple products
				var $scope = $changedSubReason.closest('.js-rma-item');
				// Get element which holds every reason item
				var $reasonItems = $('.js-rma-reasons-item', $scope);

				// Show/hide all holder items besides main and sub reason based on is user selected something or not
				$reasonItems.not('.js-is-main-reason').not('.js-is-sub-reason').toggleClass('hidden', !changedValue);

				// Enable/disable submit button
				enableDisableSubmitButton();

				// Set value which we will send to BE, since multiple items can contain same code, we needed to add "_x" as separator where "x" is index
				// So once we need to send it to BE, we need to remove "_X" and send first part only -> code
				$('.js-reason-for-BE-id', $scope).val(changedValue.split('_')[0]);
				// DISTRELEC-27559: Sending EN translation so BE clearly knows which option is selected
				$('.js-reason-for-BE-subreason', $scope).val(getEnglishTranslationFromOption($changedSubReason, changedValue));
			});

			// As user is typing into quantity input, enable/disable button, check min/max
			$rmaQtyInput.on('input', function () {
				var $changedInput = $(this);
				var value = $changedInput.val();
				var max = $changedInput.data('max-quantity');

				if (value > max) {
					$changedInput.val(max);
				}

				if (value < 0) {
					$changedInput.val(1);
				}

				enableDisableSubmitButton();
				$changedInput.removeClass('error');
			});

			// When user clicks on "Select max", add max value into quantity input
			$rmaQtySelectMax.on('click', function () {
				var $qtyInput = $(this).closest($rmaForm).find($rmaQtyInput);
				$qtyInput.val($qtyInput.data('max-quantity'));
			});

			// Functionality for showing entered characters number next to the form element (character counter)
			// When user types something into element
			$('.js-char-counter-element', $rmaForm).on('input', function () {
				var $self = $(this);
				// Get "max" value from element
				var max = $self.attr('maxlength');
				// Inside scope find output element and display values (on page we can have more than one character counter elements)
				$self.closest('.js-char-counter').find('.js-char-counter-output').html($self.val().length + ' / ' + max);
			});

			// When user clicks on submit button, check if form is valid
			$rmaSubmitButton.on('click', function () {
				// Get all visible quantity inputs
				var $allVisibleQty = $rmaForm.find(rmaQtyInputSelector + ':visible');

				// Remove "name" attribute from all fields which we are sending to BE
				// Later below we will move it back if field is populated
				$rmaForm.find('.js-rma-field-attr-name').attr('name', '');

				// If there is at least one visible quantity field, check validation
				// Else throw error message
				if ($allVisibleQty.length) {
					// Set bool for checking if form has at least one valid item
					var formIsValid = false;

					$allVisibleQty.each(function () {
						var $self = $(this);
						var value = $self.val();
						var $scope = $self.closest('.js-rma-item');
						// Value needs to be whole number and greater or equal to 1
						var isValidValue = isNumeric(value) && value >= 1;

						// Toggle error class based on value validation
						$self.toggleClass('error', !isValidValue);
						// Toggle hidden class based on value validation
						$rmaErrorMessage.toggleClass('hidden', isValidValue);

						// If number validation is fine
						if (isValidValue) {
							// Remove error class on all other quantities, since one is valid which is fine for submit
							$allVisibleQty.removeClass('error');

							// Iterate through each field for BE
							$scope.find('.js-rma-field-attr-name').each(function () {
								var $currentItem = $(this);
								// Move "name" attribute back to field, since without it field won't be sent to BE
								$currentItem.attr('name', $currentItem.data('name'));
							});

							// Since item is valid, set this to true so form can be submitted
							formIsValid = true;
						}
					});

					// After short delay, submit form
					setTimeout(function () {
						// If form is valid and can be submitted
						if (formIsValid) {
							$rmaForm.submit();
						} else {
							$([document.documentElement, document.body]).animate({
								scrollTop: $rmaErrorMessage.offset().top - 150
							}, 1000);
						}
					}, 300);
				} else {
					$rmaErrorMessage.removeClass('hidden');

					$([document.documentElement, document.body]).animate({
						scrollTop: $rmaErrorMessage.offset().top - 150
					}, 1000);
				}
			});

            parent.on(callback);
        };
    };
})(Tc.$);
