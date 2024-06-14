(function($) {

	/**
	 * OrderHistory Skin implementation for the module AccountList.
	 *
	 * @author Céline Müller
	 * @namespace Tc.Module.AccountList
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.AccountList.OrderHistory = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */

		this.on = function (callback) {
            // calling parent method
            parent.on(callback);

			var self = this;

			// subscribe to connector channel/s
			this.sandbox.subscribe('myaccount', this);

		};

		this.onPaginationChange = function(data) {
			var $theForm = $('form#orderHistoryForm');
		    if ($theForm.length === 1) {
		    	$theForm.find('input#page').val(0);
		    	$theForm.find('input#pageSize').val(data.pageSize);
		    	$theForm.submit();
		    }
		};

	};

})(Tc.$);
