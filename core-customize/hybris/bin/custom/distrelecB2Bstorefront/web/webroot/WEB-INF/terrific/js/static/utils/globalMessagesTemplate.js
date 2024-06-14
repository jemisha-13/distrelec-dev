
/**
 * Removes an item from an array
 *
 *  @param object {object|array}
 *  return {object|array}
 */

Tc.Utils.globalMessagesTemplate = function (globalMessagesData) {
	var tmplGlobalMessages = doT.template($('#tmpl-global-messages').html());
	var $globalMessages = $('.js-global-messages');

	globalMessagesData = {
		messages: globalMessagesData
	};

	$globalMessages.html(tmplGlobalMessages(globalMessagesData));

	setTimeout(function () {
		$globalMessages.find('.js-global-messages-item').show(300);

		$globalMessages.find('.js-global-messages-close').on('click', function () {
			var $thisMessageItem = $(this).closest('.js-global-messages-item');
			$thisMessageItem.hide(300);

			setTimeout(function () {
				$thisMessageItem.remove();
			}, 300);
		});

		Tc.Utils.scrollToFirstError($globalMessages, 500, 300);
	}, 100);
};

// Closing all error msgs
Tc.Utils.globalMessagesTriggerClose = function () {
	var $globalMessagesCloseButton = $('.js-global-messages-close');
	if ($globalMessagesCloseButton.length) {
		$globalMessagesCloseButton.trigger('click');
	}
};
