(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class CheckoutOrderSummaryCostCenterBox
	 * @extends Tc.Module
	 */
	Tc.Module.CheckoutOrderSummaryCostCenterBox = Tc.Module.extend({

		/**
		 * Initialize.
		 *
		 * @method init
		 * @return {void}
		 * @constructor
		 * @param {jQuery} $ctx the jquery context
		 * @param {Sandbox} sandbox the sandbox to get the resources from
		 * @param {Number} id the unique module id
		 */
		init: function($ctx, sandbox, id) {
			// call base constructor
			this._super($ctx, sandbox, id);

		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {
			var $ctx = this.$ctx,
				self = this,
				$inputCostCenter = self.$$('input#costcenter'),
				$inputProjectNumber = self.$$('input#projectnumber'),
				$btnEdit = self.$$('.js-btn-edit-order-ref'),
				$refInputContainer = self.$$('div.js-refInputContainer'),
				$formOrderRef = self.$$('.js-refInputForm'),
				$inputOrderRef = $formOrderRef.find('input.js-refInput'),
				$textOrderRef = $refInputContainer.next('span.js-reference-text'),
				$errorOrderRef = $textOrderRef.next('div.js-reference-error'),
				$form = self.$$('form');

			$inputCostCenter.on('blur', function() {
				var valueCostCenter = $inputCostCenter.val();
				$.ajax({
					url: '/checkout/review/setCostCenter',
					type: 'post',
					data: {
						costCenter: valueCostCenter
					},
					success: function (data) {
					}
				});
			});

			$inputProjectNumber.on('blur', function() {
				var valueProjectNumber = $inputProjectNumber.val();
				$.ajax({
					url: '/checkout/review/setProjectNumber',
					type: 'post',
					data: {
						projectNumber: valueProjectNumber
					},
					success: function (data) {
					}
				});
			});

			$btnEdit.on('click', function() {
				$refInputContainer.toggleClass('hidden');
				if(!$refInputContainer.hasClass('hidden')) {
					$inputOrderRef.val($textOrderRef.html()).focus();
				}
				$textOrderRef.toggle();
				$errorOrderRef.addClass('hidden');
			});

			$refInputContainer.find('button').on('click', function() {
				$.ajax({
					url: '/my-account/order-detail/update/order-reference',
					type: 'POST',
					data: $('.js-refInputForm').serialize(),
					success: function() {
						$textOrderRef.html($inputOrderRef.val());
						$btnEdit.trigger('click');
					},
					error: function() {
						$btnEdit.trigger('click');
						$errorOrderRef.removeClass('hidden');
					}
				});
			});

			callback();
		}

	});

})(Tc.$);
