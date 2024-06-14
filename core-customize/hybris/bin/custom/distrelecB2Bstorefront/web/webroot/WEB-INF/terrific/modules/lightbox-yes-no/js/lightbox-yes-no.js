(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class LightboxYesNo
	 * @extends Tc.Module
	 */
	Tc.Module.LightboxYesNo = Tc.Module.extend({

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
			this.sandbox.subscribe('lightboxYesNo', this);

            // set module variables
            this.$modal = this.$$('.modal');
			this.$title = this.$$('.title', $ctx);
			this.$message = this.$$('.bd > p', $ctx);
            this.$btnConfirm = this.$$('.btn-confirm', $ctx);
			this.$btnCancel = this.$$('.btn-cancel', $ctx);
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {
            var mod = this;

            mod.$btnConfirm.off('click').on('click', function (ev) {
	            ev.preventDefault();
	            var attribute = mod.$modal.find('.bd').data('attribute');
	            var actionIdentifier = mod.$modal.find('.bd').data('action-identifier');

	            mod.hideModal();
	            mod.fire('dialogConfirm', { attribute : attribute, actionIdentifier: actionIdentifier }, ['lightboxYesNo']);
            });

			mod.$btnCancel.off('click').on('click', function (ev) {
				ev.preventDefault();
				var attribute = mod.$modal.find('.bd').data('attribute');
				var actionIdentifier = mod.$modal.find('.bd').data('action-identifier');

				mod.fire('dialogCancel', { attribute : attribute, actionIdentifier: actionIdentifier }, ['lightboxYesNo']);
			});

            callback();
		},

		onYesNoAction: function(data){
			var mod = this;
			mod.$modal.find('.bd').data('attribute', data.attribute);
			mod.$modal.find('.bd').data('action-identifier', data.actionIdentifier);
			mod.$title.html(data.lightboxTitle);
			mod.$message.html(data.lightboxMessage);
			mod.$btnCancel.val();
			mod.$btnCancel.val(data.lightboxBtnDeny);
			mod.$btnConfirm.val(data.lightboxBtnConf);
			mod.openModal();
		},

        openModal: function () {
            var mod = this;
	        mod.$modal.modal();
        },

        hideModal: function () {
	        var mod = this;
	        mod.$modal.modal('hide');
        },
        /**
		 *
		 * @method onLightboxConfirm - keystroke extension
		 *
		 */
		onLightboxConfirm: function(){
        	Tc.Utils.lightboxConfirm(this);
        }
	});

})(Tc.$);
