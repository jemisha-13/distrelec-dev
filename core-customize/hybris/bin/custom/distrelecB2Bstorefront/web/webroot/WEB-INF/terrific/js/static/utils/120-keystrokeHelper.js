/*
 *
 *    general text utilities
 *
 *
 * */
Tc.Utils.lightboxConfirm = function(self) {	// Central location for lightbox handler that responds to ENTER key

	if (self.$btnConfirm && self.$btnConfirm.is(':visible')){
		self.$btnConfirm.trigger('click');
	}
}