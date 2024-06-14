/**
 *
 * @method numericStepper
 *
 * bind handler on given numeric markup
 *
 * the events object accept functions (create, success, error, warning, change, increase, decrease)
 *
 * Validation of the numeric stepper has an Error state and a Warning State. Use the two states if you want to  e.g.
 * show the popover, but still continue with your action in certain cases.
 *
 * If doAutoAdjust is set to true, values are automatically adjusted according to numMin and numStep Rules
 *
 * @param $numeric
 * @param events
 *
 *
 * Markup:
 *
 *  <div class="numeric numeric-small" data-min="1" data-step="1" data-min-error="Die Mindestbestellmenge für den Artikel 7050545 ist 1 Stück">
 *      <button class="btn numeric-btn numeric-btn-down "><i></i></button>
 *      <input type="text" name="countItems" class="ipt" value="1">
 *      <button class="btn numeric-btn numeric-btn-up"><i></i></button>
 *      <div class="numeric-popover popover top">
 *          <div class="arrow"></div>
 *          <div class="popover-content"></div>
 *      </div>
 *  </div>
 *
 */
Tc.Utils.numericStepper = function ($numeric, events, event) {
	var defaultEvents = {
		create: {},
		success: {},
		error: {},
		warning: {},
		change: {},
		increase: {},
		decrease: {}
	}
	var candidateNumMin = parseInt($numeric.data('min'))
		, candidateNumMax = parseInt($numeric.data('max'))
		, candidateNumStep = parseInt($numeric.data('step'))
		, numMin = isNaN(candidateNumMin) ? 1 : candidateNumMin
		, numMax = isNaN(candidateNumMax) ? 0 : candidateNumMax
		, numStep = isNaN(candidateNumStep) ? 1 : candidateNumStep
		, textMinError = $numeric.data('min-error')
		, textMaxError = $numeric.data('max-error')
		, textStepError = $numeric.data('step-error')
		, $btnUp = $('.numeric-btn-up', $numeric)
		, $btnDown = $('.numeric-btn-down', $numeric)
		, $popover = $('.numeric-popover', $numeric)
		, $ipt = $('.ipt', $numeric)
		, popoverTimer = null
		, popoverHideDelay = 4000
		, $priceTable = $numeric.closest('article').find('.mod-scaled-prices .table')
		, $priceRows = $priceTable.find('.table-row')
		, priceScale = typeof $priceTable.data('price-scale') === 'string' ? $priceTable.data('price-scale').split(';').slice(1).map(Number) : []
	;

	var buttonCart = $('.btn-cart');


	if (buttonCart.closest('.plp-filter-products__product__info').length) {
		buttonCart = $numeric.closest('.num-stepper').siblings('.plp-filter-products__cart-cta').find('.btn-cart');
	}

	// Check values of the ipt when page has loaded
	if(parseInt($ipt.val(), 10) > parseInt(numMin, 10)) {
		$btnDown.removeClass('disabled');
	} else {
		$btnDown.addClass('disabled');
	}

	/**
	 *
	 * @function getValuesFromDataAttribute
	 *
	 * @returns {boolean}
	 */
	var updateValuesFromDataAttribute = function(){
		candidateNumMin = parseInt($numeric.attr('data-min'));
		candidateNumMax = parseInt($numeric.attr('data-max'));
		candidateNumStep = parseInt($numeric.attr('data-step'));
		numMin = isNaN(candidateNumMin) ? 1 : candidateNumMin;
		numMax = isNaN(candidateNumMax) ? 0 : candidateNumMax;
		numStep = isNaN(candidateNumStep) ? 1 : candidateNumStep;
		textMinError = $numeric.attr('data-min-error');
		textMaxError = $numeric.attr('data-max-error');
		textStepError = $numeric.attr('data-step-error');
	}

	/**
	 *
	 * @function checkValue
	 *
	 * @returns {boolean}
	 */
	var checkValue = function (stepValue, doAutoAdjust, event) {
		var inputValue = $ipt.val()
			,parsedInputValue = parseInt(inputValue)
			,parsedInputValueWithStepsValue = parsedInputValue + stepValue
			;
		var val = parseInt($ipt.val());

		if(typeof $ipt.data('max-waldom-stock') !== 'undefined') {

			if (parsedInputValue >= $ipt.data('max-waldom-stock') && ($(event.currentTarget).hasClass('numeric-btn-up') || $(event.currentTarget).hasClass('ipt'))){
				$ipt.val($ipt.data('max-waldom-stock'));
				return false;
			}

			if($ipt.data('max-waldom-stock') === 0) return false;
		}

		// Case NaN, e.g. Letters => set to numMin and show Warning Popover
		if (isNaN(inputValue)) {
			if (typeof events.warning === 'function') {
				events.warning.call($numeric, numMin, textMinError);
			}
			$ipt.val(numMin);

			return true;
		}
		// Case empty field and no button click
		else if (inputValue === '' && stepValue === 0) {
			if (typeof events.error === 'function') {
				events.error.call($numeric, inputValue, textMinError);
			}
			stepperError(textMinError);

			return false;
		}
		// Case +/- Button Click and empty field => set to numMin and show Warning popover
		else if (stepValue !== 0 && inputValue === '' ) {
			$ipt.val(numMin);
			if (typeof events.warning === 'function') {
				events.warning.call($numeric, numMin, textMinError);
			}

			return true;
		}
		// Case "-" Button Click and quantity <= numMin => set to numMin and show Warning popover
		else if (stepValue < 0 && parsedInputValue <= numMin) {
			$ipt.val(numMin);
			if (typeof events.warning === 'function') {
				events.warning.call($numeric, numMin, textMinError);
			}

			return true;
		}
		// Case +/- Button Click and quantity < numMin => set to numMin and show Warning popover
		else if (stepValue !== 0 && parsedInputValue < numMin) {
			$ipt.val(numMin);
			if (typeof events.warning === 'function') {
				events.warning.call($numeric, numMin, textMinError);
			}

			return true;
		}
		// Case +/- Button Click and quantity > numMin but not Valid multiplier => set to next higher or lower possible Quantity without error message
		else if (stepValue !== 0 && ((parsedInputValueWithStepsValue - numMin) % stepValue !== 0)) {
			// ParseInt again to ceil to the next number
			var factor = parseInt((parsedInputValue - numMin) / numStep)
				,newValue = 0;
			;
			if(stepValue > 0){
				newValue = numMin + (factor * numStep) + numStep;
			}
			else{
				newValue = numMin + (factor * numStep);
			}
			$ipt.val(newValue);
			if (typeof events.success === 'function') {
				events.success.call($numeric, newValue);
			}

			return true;
		}
		// Case inputValue is smaller than numMin
		else if (parsedInputValueWithStepsValue < numMin) {
			stepperError(textMinError);
			if(doAutoAdjust){
				if (typeof events.success === 'function') {
					events.success.call($numeric, numMin);
				}
				$ipt.val(numMin);
				return true;
			}
			else{
				if (typeof events.error === 'function') {
					events.error.call($numeric, inputValue, textMinError);
				}
				$ipt.val(inputValue);
				return false;
			}
		}
		// Case Input Value is bigger than numMin but not within allowed Steps
		else if (((parsedInputValue - numMin) % numStep !== 0)) {
			stepperError(textStepError);
			if(doAutoAdjust){
				var factor = Math.round((parsedInputValue - numMin) / numStep)
					,newValue = numMin + (factor * numStep)
				;
				if (typeof events.success === 'function') {
					events.success.call($numeric, newValue);
				}
				$ipt.val(newValue);
				return true;
			}
			else{
				if (typeof events.error === 'function') {
					events.error.call($numeric, inputValue, textStepError);
				}
				if ( isNaN(parsedInputValueWithStepsValue) ) {
                    parsedInputValueWithStepsValue = numStep;
				}
				$ipt.val(parsedInputValueWithStepsValue);
				return false;
			}
		}
		// Case inputValue smaller than maxNum (only for quotes)
		else if (numMax > 0 && ((stepValue > 0 && parsedInputValue >= numMax) || parsedInputValueWithStepsValue > numMax)) {
			$ipt.val(numMax);
			if (typeof events.warning === 'function') {
				events.warning.call($numeric, numMax, textMaxError);
			}
			return true;
		} 
		// Valid Case
		else {
			$ipt.val(parsedInputValueWithStepsValue);
			if (typeof events.success === 'function') {
				events.success.call($numeric, parsedInputValueWithStepsValue);
			}

			return true;
		}
	};

	/**
	 *
	 * @function stepperChange
	 *
	 * @param value, validateSteps
	 */
	var stepperChange = function (stepValue, doAutoAdjust, event) {

		checkValue(stepValue, doAutoAdjust, event);

		// Check for input value when it is changed manually
		if (parseInt($ipt.val()) >= parseInt(numMin)) {
			$numeric.removeClass('numeric-error');
			if (buttonCart !== undefined) {
				// Check if element which contains message that product is out of stock is visible
				// Availability message logic is done upon "/availability" ajax based on which we show/hide related messages
				var checkIfOutOfStockProduct = $numeric.closest('.js-check-if-out-of-stock-scope').find('.js-check-if-out-of-stock-msg:visible').length > 0;
				// Get class which indicates that we are on PLP page
				var $plpProductItem = $numeric.closest('.js-plp-product-list-item');

				// If user is on PLP page
				if ($plpProductItem.length) {
					// Since on PLP pages we have many of add-to-cart buttons, we need to target only button which is in related product, not all of them
					buttonCart = $plpProductItem.find(buttonCart);
				}

				// If there isn't class which disables numeric stepper ("js-disable-qty")
				// If there isn't visible element which indicates that product is out of stock
				if ($numeric.closest('.js-disable-qty').length === 0 && !checkIfOutOfStockProduct) {
					buttonCart.removeAttr('disabled').removeClass('disabled');
				}
			}
		}

		if (typeof events.change === 'function') {
			events.change.call($numeric, $ipt.val());
		}

		if (stepValue < 0) {
			// Check numMin value when ipt decreased
			if(parseInt($ipt.val(), 10) > parseInt(numMin, 10)) {
				$btnDown.removeClass('disabled');
			} else {
				$btnDown.addClass('disabled');
			}

			if (typeof events.decrease === 'function') {
				events.decrease.call($numeric, $ipt.val());
			}
		} else {

			// Check numMin value when ipt increased
			if(parseInt($ipt.val(), 10) > parseInt(numMin, 10)) {
				$btnDown.removeClass('disabled');
			} else {
				$btnDown.addClass('disabled');
			}

			if (typeof events.increase === 'function') {
				events.increase.call($numeric, $ipt.val());
			}
		}
		
		highlightPrice($ipt.val());
	};

	/**
	 *
	 * @function stepperError
	 * adding the numeric-error class prevents the actions from happeing (addToCart ect.)
	 *
	 * @param text
	 */
	var stepperError = function (text) {
		$numeric.removeClass('numeric-warning');
		$numeric.addClass('numeric-error');
		$popover.find('.popover-content').html(text);
	};

	/**
	 *
	 * @function destroy
	 *
	 */
	var stepperDestroy = function () {
		$btnUp.off('click.numericStepper');
		$btnDown.off('click.numericStepper');
		$ipt.off('keyup.numericStepper');
		$ipt.off('blur.numericStepper');
	};


	var highlightPrice = function(ipt) {
		var len = priceScale.length,
			qty = parseInt(ipt),
			ix = 0;
		if (len === 0) return;
		for (var i = 0; i < len; i++) {
			if (qty >= priceScale[i] ) {
				ix = i;
			}
		}
		$priceRows.each(function(n) {
			$(this).toggleClass('current', n === ix);
		});
		if (ix > 2) {
			$priceRows.last().addClass('current');
		}
	};


	if (typeof events !== 'string') {
		events = $.extend(defaultEvents, events);

		if (typeof events.create === 'function') {
			events.create.call($numeric, {
				numMin: numMin,
				numMax: numMax,
				numStep: numStep,
				textMinError: textMinError,
				textMaxError: textMaxError,
				textStepError: textStepError,
				$btnUp: $btnUp,
				$btnDown: $btnDown,
				$popover: $popover,
				$ipt: $ipt
			});
		}

		stepperDestroy();

		// Check initial value
		checkValue(0, false);

		highlightPrice($ipt.val());

		// NO Validation on Blur because of autocorrect in some cases and that would remove the error message and the
		// user would not know why the quantity was corrected

		$btnUp.on('click.numericStepper', function (e) {
			updateValuesFromDataAttribute();
			stepperChange(numStep, false,e);
			$btnDown.removeClass('disabled');
			// Trigger custom event on input so we can listen when it's value has been updated
			$(this).siblings('input.ipt').trigger('qtyChanged');
		});
		$btnDown.on('click.numericStepper', function (e) {
			updateValuesFromDataAttribute();
			stepperChange(-numStep, false, e);
			// Trigger custom event on input so we can listen when it's value has been updated
			$(this).siblings('input.ipt').trigger('qtyChanged');
		});
		$ipt.on('keyup.numericStepper', function (e) {
			updateValuesFromDataAttribute();
			var increment = 0
			;
			stepperChange(increment, false, e); // KR
			// Trigger custom event on input so we can listen when it's value has been updated
			$(this).trigger('qtyChanged');
		});
		$ipt.on('click', function (e) {
			$(this).blur();
			$(this).focus();
		});

		// When value has been changed (by user input or click on +/- buttons)
		$ipt.on('qtyChanged', function () {
			var $currentQty = $(this);
			// With class "js-numeric-stepper-without-buying-section" we are attaching logic for updating "[data-product-min-order-quantity]" value
			// Value is updated on-the-fly with JS directly on DOM element and then we are sending it to BE with ajax "/shopping/add"
			// This value is used for sending users choice from current page to the shopping page
			// Same functionality is attached to "mod-buying-section" but we have places where num-stepper IS NOT inside "mod-buying-section" (PLP, BOM-tool...)
			// If num-stepper is placed inside "mod-buying-section" element (PDP), then this class is not needed "js-numeric-stepper-without-buying-section"
			var $withoutBuyingSectionScope = $currentQty.closest('.js-numeric-stepper-without-buying-section');

			// If class on some parent exists, attach logic for updating data-attr value on-the-fly
			if ($withoutBuyingSectionScope.length) {
				// String selector for element which contains attribute for quantity
				var stringQtyAttribute = 'product-min-order-quantity';
				// Get element with selector above
				var $qtyElement = $('[data-' + stringQtyAttribute + ']', $withoutBuyingSectionScope);
				// Get min quantity which is defined in BE
				var minQty = $qtyElement.data('min-quantity');
				// Get value from input
				var value = $currentQty.val();

				// Change data-attribute on-the-fly so we can fetch it for the ajax request
				// "modules/toolsitem/js/toolsitem.skin.shopping.js"
				// --> "productQuantity = $icoList.data('product-min-order-quantity')" and fire of "checkUserLoggedIn"
				// If there is no value or value is zero, add minQty from BE
				$qtyElement.data(stringQtyAttribute, (!!value && value > 0) ? value : minQty);
			}
		});

		// If some of the parents element contains class "js-disable-qty", we are unbinding all events, user cannot change quantity
		if ($btnUp.closest('.js-disable-qty').length > 0) {
			$btnUp.unbind();
			$btnDown.unbind();
			$ipt.unbind();
		}
    }
	// event is used to trigger the stepperChange externally after ajax direct order
	else if (events === 'error') {
		var increment = 0
			,doAutoAdjust = true
		;
		updateValuesFromDataAttribute();
		stepperChange(increment,doAutoAdjust); // KR
	}else if (events === 'destroy') {
		stepperDestroy();
	}
};
