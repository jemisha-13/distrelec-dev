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
	Tc.Module.Toolsitem.Information = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			// calling parent method
			parent.on(callback);

			var mod = this;
			
			var popoverTitle = this.$$('.ico-information').data('title');
			var popoverContent = this.$$('.ico-information').data('message');

			this.$$('.ico-information')
			.popover({
				title: popoverTitle,
				placement: 'top',
				content: popoverContent,
				html: true
			})
			.popover('toggle')
			.off('click mouseover')
			.on('click mouseover', function (e) {
				e.preventDefault();
				$(this).popover('show');
			});
		};
	};

})(Tc.$);
