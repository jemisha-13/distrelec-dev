(function($) {

	/**
	 * InvoiceHistory Skin implementation for the module AccountList.
	 *
	 * @author Céline Müller
	 * @namespace Tc.Module.AccountList
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.AccountList.InvoiceHistory = function (parent) {

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
			this.sandbox.subscribe('lightboxShareInvoice', this);
				this.sandbox.subscribe('lightboxShareInvoiceQuery', this);
			this.sandbox.subscribe('facetActions', this); // for updating url

			// Context & Click Handler
			this.onClick = $.proxy(this, 'onClick');
			this.onClick = $.proxy(this, 'onClick');
			this.$ctx.on('click', this.onClick);

		};

		this.onPaginationChange = function(data) {
			var $theForm = $('form#invoiceHistoryForm');
		    if ($theForm.length === 1) {
		    	$theForm.find('input#page').val(0);
		    	$theForm.find('input#pageSize').val(data.pageSize);
		    	$theForm.submit();
		    }
		};

		// Event Dispatcher

		this.onClick = function (ev) {
			var $targetIco = $(ev.target).closest('a');

			if ($targetIco.hasClass('ico-share-mail')) {
				ev.preventDefault();
				this.fireShareEmail();
			}
			else if ($targetIco.hasClass('ico-share-query')) {
				ev.preventDefault();
				this.fireShareQuery();
			}

		};


		this.fireShareEmail = function () {
			this.fire('showLightbox',['lightboxShareInvoice']);
		};

		this.fireShareQuery = function () {
			this.fire('showLightbox',['lightboxShareInvoiceQuery']);
		};

	};

})(Tc.$);
