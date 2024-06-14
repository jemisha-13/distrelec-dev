(function($) {

	/**
	 * Admin Skin implementation for the module Checkout Order Budget Approval Bar.
	 *
	 * @author Céline Müller
	 * @namespace Tc.Module
	 * @class CheckoutOrderBudgetApprovalBar
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.CheckoutOrderBudgetApprovalBar.Admin = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			// calling parent method
			parent.on(callback);

			var $ctx = this.$ctx,
				self = this;

			this.sandbox.subscribe('lightboxRejectOrder', this);

			// Click on a Button
			$ctx.on('click', '.btn-reject', function(e) {
				var $target = $(e.target),
					action = $target.data('action'),
					modelAttribute = $target.data('model-attribute'),
					workflowCode = $target.data('workflow-code'),
					decision = $target.data('decision');

				// Reject: Show Lightbox
				if ($target.hasClass('btn-reject')) {
					self.fire(
						'rejectOrderAction',
						{
							action: action,
							modelAttribute: modelAttribute,
							workFlowActionCode: workflowCode,
							approverSelectedDecision: decision
						},
						['lightboxRejectOrder']
					);
				}

				return false;
			});
		};
	};

})(Tc.$);
