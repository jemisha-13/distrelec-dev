(function($) {

	/**
	 * Bom-Remove Skin implementation for the module Toolsitem.
	 *
	 * @author Céline Müller
	 * @namespace Tc.Module.Toolsitem
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.Toolsitem.Notice = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			// calling parent method
			parent.on(callback);

			var mod = this;
			
			var popoverContent = this.$$('.ico-notice').data('message');

			// Change popover position to be within its own compare list item
			var outerModule = $('.mod-checkout-order-budget-approval-bar');
			var pageOffset = outerModule.offset().left + outerModule.width();
			var arrowWidth = 22;
			
			this.$$('.popover').offset({
				left: pageOffset - this.$$('.popover').outerWidth()
			});
			
			this.$$('.popover .arrow').offset({
				left: this.$ctx.offset().left + this.$ctx.width()/2 - (arrowWidth/2)
			});

			this.$$('.ico-notice')
			.popover({
				placement: 'top',
				content: popoverContent.replace(/\n/g,'<br />'),
				html: true
			})
			.popover('toggle')
			.off('click')
			.on('click', function () {
				$(this).popover('show');
			});
			
			
		};
	};

})(Tc.$);
