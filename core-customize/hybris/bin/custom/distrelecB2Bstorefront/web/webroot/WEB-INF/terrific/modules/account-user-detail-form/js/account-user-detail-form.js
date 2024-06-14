(function($) {

    /**
     * Module implementation.
     *
     * @namespace Tc.Module
     * @class AccountUserDetailForm
     * @extends Tc.Module
     */
    Tc.Module.AccountUserDetailForm = Tc.Module.extend({

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

            // subscribe to channel(s)
			this.sandbox.subscribe('lightboxYesNo', this);
            
            this.$formBox = this.$$('.form-box');
			this.$chkLimit = this.$$('.b-limit');
			this.$fields = this.$$('.b-toggle');
			this.$btnDelete = this.$$('.btn-delete-user');
			this.action = this.$btnDelete.data('action');
			this.actionIdentifier = 'deleteSubUser';
			this.customerId = this.$btnDelete.data('customer-id');
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

			// Lazy Load SelectBoxIt Dropdown
			if(!Modernizr.touch && !Modernizr.isie7) {
				Modernizr.load([{
					load: '/_ui/all/cache/dyn-jquery-selectboxit.min.js',
					complete: function () {
						self.$$('#select-title,#select-department,#select-function').selectBoxIt({
							autoWidth : false
						});
					}
				}]);
			}


			// delegate module handler: click checkbox toggles budget fields (in)active
			self.$chkLimit.on('click', function() {
				self.toggleFields();
			});

			
			this.$btnDelete.click( function(ev) {
				var $link = $(ev.target).closest('a')
					, lightboxTitle = $link.data('lightbox-title')
					, lightboxMessage = $link.data('lightbox-message')
					, lightboxBtnCancel = $link.data('lightbox-btn-cancel')
					, lightboxShowBtnConfirm = $link.data('lightbox-show-confirm-button')
					, lightboxBtnConf = $link.data('lightbox-btn-conf')
				;
				self.fire(
					'yesNoAction',
					{
						actionIdentifier: self.actionIdentifier,
						attribute: self.customerId,
						lightboxTitle: lightboxTitle,
						lightboxMessage: lightboxMessage,
						lightboxBtnDeny: lightboxBtnCancel,
						lightboxShowBtnConfirm: lightboxShowBtnConfirm,
						lightboxBtnConf: lightboxBtnConf
					},
					['lightboxYesNo']
				);
				return false;
			});

			// init budget fields state
			self.toggleFields();


            callback();
        },
        
        onDialogConfirm: function (data) {
			if(data.actionIdentifier === this.actionIdentifier){
				window.location.href = this.action;
			}
		},

		onDialogCancel: function (data) {
		},

		// helper: toggle budget fields (enabled/disabled) if checkbox is set accordingly
		toggleFields: function() {
			var self = this;

			if (self.$chkLimit.is(':checked')) {
				self.$fields.addClass('disabled');
				self.$fields.prop('disabled', true);
			}
			else {
				self.$fields.removeClass('disabled');
				self.$fields.prop('disabled', false);
			}
		}
    });

})(Tc.$);
