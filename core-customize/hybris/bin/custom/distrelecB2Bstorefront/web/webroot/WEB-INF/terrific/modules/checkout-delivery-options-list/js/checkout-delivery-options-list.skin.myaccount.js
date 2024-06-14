(function($) {

	/**
	 * Checkout Skin implementation for the module CheckoutDeliveryOptionsList.
	 *
	 * @author Remo Brunschwiler
	 * @namespace Tc.Module.CheckoutDeliveryOptionsList
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.CheckoutDeliveryOptionsList.Myaccount = function (parent) {
		this.on = function (callback) {
			parent.on(callback);

			var $ctx = this.$ctx;
			
			$('.mod-checkout-delivery-options-list .list__item').click(function(e) {
				e.preventDefault();

				$('.mod-checkout-delivery-options-list .list__item input[type=radio]').prop('checked', false);

				var $target = $(e.target);
				var $closestForm = $target.closest("form");

				if (!$target.hasClass('disabled')) {

                    $target.prop('checked', 'checked');

					var actUrl = $closestForm.attr('action');
					var val = $(this).find('.deliveryInfo').val();
                    var actUrlObj = actUrl + '?shippingOption=' + val;

					Tc.Utils.disableAndSubmitForm($closestForm, actUrlObj);
				}

			});

            var checkedItem = $('.list__item input[type=radio]:checked');
            checkedItem.parent().addClass('list__item--active');

			if(digitalData.page.pageInfo.shop === 'distrelec france' && $('#standardSAP_N2').length && ($('#standardSAP_N2').prop('checked') === true)) {
				$('.shipping-note-express').removeClass('hidden');
			} else {
				$('.shipping-note-express').addClass('hidden');
			}

		};

	};

})(Tc.$);
