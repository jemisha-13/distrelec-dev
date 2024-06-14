(function ($) {

	/**
	 * OpenOrder Skin implementation for the module CheckoutOrderSummaryInfoBox.
	 *
	 * @author Remo Brunschwiler
	 * @namespace Tc.Module.CheckoutOrderSummaryInfoBox
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.CheckoutOrderSummaryCostCenterBox.OpenOrder = function (parent) {

		this.on = function (callback) {

			this.onBtnChangeClick = $.proxy(this, 'onBtnChangeClick');
			this.onDialogConfirm = $.proxy(this, 'onDialogConfirm');

			// subscribe to connector channel/s
			this.sandbox.subscribe('lightboxOrderReference', this);

			this.$$('.btn-change').on('click', this.onBtnChangeClick);

			parent.on(callback);
		};

		this.onBtnChangeClick = function (ev) {
			ev.preventDefault();

			var orderReference = this.$$('.value').html();

			this.fire('editOrderReference', { orderReference: orderReference }, ['lightboxOrderReference']);
		};

		// modal was confirmed
		this.onDialogConfirm = function (data) {

			var orderCode = this.$$('.btn-change').data('order-code');

			// Set new Values in Dom
			this.$$('.cost-center').html(data.orderReference);

			// Send new Values to backend
			$.ajax({
				url: '/checkout/openorder/changeOpenOrderInERP?code=' + orderCode +'&orderReference='+data.orderReference,
				type: 'POST',
				dataType: 'json',
				method: 'post',
				success: function (data, textStatus, jqXHR) {
				},
				error: function (jqXHR, textStatus, errorThrown) {
				}
			});
		};

	};

})(Tc.$);
