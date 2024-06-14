(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class LightboxRejectOrder
	 * @extends Tc.Module
	 */
	Tc.Module.LightboxRejectOrder = Tc.Module.extend({

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

			// subscribe to connector channel/s
			this.sandbox.subscribe('lightboxRejectOrder', this);

			// set module variables
			this.$modal = this.$$('.modal');
			this.$body = this.$$('.bd');
			this.$form = this.$$('form');
			this.$hiddenActionCode = this.$$('#rejectWorkFlowActionCode');
			this.$hiddenDecision= this.$$('#rejectApproverSelectedDecision');
			this.$comments = this.$$('#comments');

			this.$btnConfirm = this.$$('.btn-submit');
			this.$btnCancel = this.$$('.btn-cancel');
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {
			var self = this;

			// subscribe to channel
			self.sandbox.subscribe('lightboxRejectOrder', this);

			self.$comments.bind('keyup', function() {
				self.$comments.val(self.$comments.val().slice(0, 255));
			});

			self.$btnConfirm.on('click', function(e) {

				if(e.which !== 1){
					e.preventDefault();
					return false;
				}
				
				var attribute = self.$modal.find('.bd').data('attribute');
				$.ajax({
					type: 'POST',
					url: location.host + self.$body.data('action'),
					data: {
						workFlowActionCode: self.$body.data('workflow-code'),
						approverSelectedDecision:  self.$body.data('decision'),
						comments: self.$comments.val()
					},
					success: function() {
					}
				});
			});

			self.$btnCancel.on('click', function() {
				self.hideModal();
			});

			callback();
		},

		openModal: function(data) {
			// Display overlay/modal
			this.$modal.modal();

			Tc.Utils.calculateModalHeight(this.$modal);
		},

		hideModal: function() {
			// hide overlay/modal
			this.$modal.modal('hide');
		},

		/**
		 * Register Event Listeners
		 */

		onRejectOrderAction: function(data){
			var self = this;
			self.openModal();

			// set attributes to form
			self.$form
				.attr('action', data.action)
				.attr('id', data.modelAttribute);

			self.$hiddenActionCode.val(data.workFlowActionCode);
			self.$hiddenDecision.val(data.approverSelectedDecision);
		},

		onLightboxConfirm: function(){
        	Tc.Utils.lightboxConfirm(this);
        }
	});

})(Tc.$);
