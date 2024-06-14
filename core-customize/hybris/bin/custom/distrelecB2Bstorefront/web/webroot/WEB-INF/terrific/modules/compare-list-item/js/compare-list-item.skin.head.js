(function($) {

	/**
	 * This module implements the accordion content skin download on the product detail page
	 *
	 * @namespace Tc.Module
	 * @class Head
	 * @skin Download
	 * @extends Tc.Module
	 */
	Tc.Module.CompareListItem.Head = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			// Head. calling parent method
			//parent.on(callback);

			var self = this,
			$ctx = self.$ctx;

			this.sandbox.subscribe('comparelist', this);

			// most actions are covered by submodules
			// column hover
			$ctx.on('mouseenter', '.b-item', function() {
				$(this).addClass('hover');
			});
			$ctx.on('mouseleave', '.b-item', function() {
				$(this).removeClass('hover');
			});
	
			callback();			

		};




	};

})(Tc.$);
