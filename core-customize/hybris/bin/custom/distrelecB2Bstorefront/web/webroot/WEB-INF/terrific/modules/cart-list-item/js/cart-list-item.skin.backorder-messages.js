(function ($) {
	/**
	 * Backorder visibility toggle functionality
	 *
	 * @namespace Tc.Module.CartListItem
	 * @class CartListItem.BackorderMessages
	 * @usage for toggling visibility of the backorder messages in cart item depending on the stock and selected value
	 * @param $item: jQuery selector for cart-item in the list
	 * @param stockAvailable: stock value which is available
	 * @param selectedValue: value with which we are comparing stock value (users choice)
	 */
	Tc.Module.CartListItem.BackorderMessages = function ($item, stockAvailable, selectedValue) {
		// Get list of messages on page
		var $deliveryMessages = $('.js-cart-list-item-delivery-messages', $item);

		// If there are delivery messages on the page
		if ($deliveryMessages.length) {
			// Get each backorder message
			var $backorderMessages = $('.js-backorder-message', $deliveryMessages);

			// Iterate through each backorder message and inject value into it
			$backorderMessages.each(function () {
				// Get current message in iteration
				var $currentMsg = $(this);
				// Make calculation
				var diffCalc = selectedValue - stockAvailable;

				// Only when calculation is greater then zero (we dont want zero and negative values)
				if (diffCalc > 0) {
					// Get element in which we will inject message
					var $currentMsgText = $('.js-msg', $currentMsg);
					// Get translations message from data-attribute
					var backorderCreditBlockedTranslation = $deliveryMessages.data('backorder-credit-blocked-msg');
					// Inject message and replace {0} with value
					$currentMsgText.html(backorderCreditBlockedTranslation.replace('{0}', diffCalc));
					// Since messages on page load are hidden, once logic is done, show the message
					$currentMsg.removeClass('hidden');
				}
			});
		}
	};
})(Tc.$);
