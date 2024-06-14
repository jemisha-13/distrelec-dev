(function($) {
	/**
	 * OrderDetail Skin implementation for the module CartListItem.
	 *
	 */
	Tc.Module.CartListItem.ReturnItems = function (parent) {
		// subscribe to connector channel/s
		this.sandbox.subscribe('lightboxReturnRequest', this);

		// Get module element
		var $ctx = this.$ctx;

		this.on = function (callback) {
			parent.on(callback);
		};
	};
})(Tc.$);
