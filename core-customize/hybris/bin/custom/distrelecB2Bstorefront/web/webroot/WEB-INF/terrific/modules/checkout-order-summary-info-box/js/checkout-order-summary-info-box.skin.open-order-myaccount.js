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
	Tc.Module.CheckoutOrderSummaryInfoBox.OpenOrderMyaccount = function (parent) {

		this.on = function (callback) {

			this.onBtnChangeClick = $.proxy(this, 'onBtnChangeClick');
			this.onDialogConfirm = $.proxy(this, 'onDialogConfirm');

			// subscribe to connector channel/s
			this.sandbox.subscribe('lightboxOpenOrder', this);

			this.$$('.btn-change').on('click', this.onBtnChangeClick);

			parent.on(callback);
		};

		this.onBtnChangeClick = function (ev) {
			ev.preventDefault();

			var selectedClosingDate = this.$$('.open-order-closing-date-value').data('selected-closing-date');
			var isEditableByAllOption = this.$$('.is-editable-by-all-value').data('boolean');

			this.fire('editOpenOrderSettings', { selectedClosingDate: selectedClosingDate, isEditableByAllOption: isEditableByAllOption}, ['lightboxOpenOrder']);
		};

		// modal was confirmed
		this.onDialogConfirm = function (data) {
			// Set new Values in Dom
			this.$$('.open-order-closing-date-value').html(data.selectedDate).data('selected-closing-date', data.selectedDate);

			var orderCode = this.$$('.btn-change').data('order-code');
			var isEditableByAllValue = this.$$('.is-editable-by-all-value');
			isEditableByAllValue.data('boolean', data.allUsersCanEditOption);
			if (data.allUsersCanEditOption) {
				isEditableByAllValue.html(isEditableByAllValue.data('boolean-true'));
			}
			else {
				isEditableByAllValue.html(isEditableByAllValue.data('boolean-false'));
			}

			var formattedDate = data.selectedDate.replace(/\-/g,'');

			// Send new Values to backend
			$.ajax({
				url: '/checkout/openorder/changeOpenOrderInERP?code=' + orderCode + '&allUsersCanEdit='+data.allUsersCanEditOption+'&closeDate='+formattedDate,
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
