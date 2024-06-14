(function($) {

	/**
	 * This module implements the accordion content skin download on the product detail page
	 *
	 * @namespace Tc.Module
	 * @class DetailAccordionContent
	 * @skin Download
	 * @extends Tc.Module
	 */
	Tc.Module.EprocSuccess.Cxml = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			if ($('input#sendForm').val() === 'true'){
				var orderForm = $('form[name="CxmlOrderForm"]')[0];
				orderForm.submit();
			}
		};
	};

})(Tc.$);
