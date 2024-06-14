
/***********************************************************************************
 *
 * 	Helper to set the height of the modal windows according to the available space
 * 	The height is set if the height of the body of the modal gets to high.
 *	It can also be reused after a validation happened. After a height was set, the class height-adjusted gets
 *  set on the modal window to prevent setting it back after another validation occured
 *
 * 	Usage see:
 * 	Tc.Utils.calculateModalHeight(self.$modal);
 *
 *	Argument: the modal window
 *	...
 *
 **********************************************************************************/

Tc.Utils.calculateModalHeight = function ($modal) {
	var $modalBd = $('.bd', $modal),
		modalHeight = $modal.height(),
		modalPos = $modal.position(),
		modalHdHeight = $modal.find('.hd').height(),
		modalFtHeight = $modal.find('.ft').height(),
		backdropHeight = $('.modal-backdrop').height(),
		newHeight;

	if (modalHeight > 0) {
		// only if height was not already previously adjusted
		if(!$modal.hasClass('heigt-adjusted')){
			if ((modalHeight + modalPos.top) > backdropHeight) {
				if (modalHdHeight === undefined && modalHdHeight === null) {
					modalHdHeight = 0;
				}
				if (modalFtHeight === undefined && modalFtHeight === null) {
					modalFtHeight = 0;
				}

				newHeight = backdropHeight - modalPos.top - modalHdHeight - modalFtHeight - modalPos.top  - modalPos.top;
				$modal.addClass('height-adjusted');
				$modalBd.attr('style', 'height:'+newHeight+'px;');
			}

		}
	}
	else {
		console.error('function calculateModalHeight: the modal was not displayed');
	}
};