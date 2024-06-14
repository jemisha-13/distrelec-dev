(function($) {
	/**
	 * OrderDetail Skin implementation for the module CartListItem.
	 *
	 */
	Tc.Module.CartListItem.OrderDetail = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. parent.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			var self = this;
			var $ctx = this.$ctx;

			this.sandbox.subscribe('lightboxReturnRequest', this);

			// Click on a return link
			$ctx.on('click', '.link-return', function(e) {
				e.preventDefault();

				var $target = $(e.target);

				// Request: Show Lightbox
				if ($target.hasClass('link-return')) {

					var orderCode = $target.attr('data-order-code'),
						orderDate = $target.attr('data-order-date'),
						productCode = $target.attr('data-product-code'),
						entryQuantity = $target.attr('data-entry-quantity');

					self.fire(
						'returnRequestAction',
						{
							orderCode: orderCode,
							orderDate: orderDate,
							productCode: productCode,
							entryQuantity: entryQuantity
						},
						['lightboxReturnRequest']
					);
				}
			});

			parent.on(callback);
		};

		this.onDialogConfirm = function(data) {
			var self = this,
				productCode = data.productCode;

			var $returnLink = this.$ctx.find('a[data-product-code=' + productCode + ']');
			// set data returned to true show lightbox with step2
			$returnLink.attr('data-returned','true');
		};

	};
})(Tc.$);
