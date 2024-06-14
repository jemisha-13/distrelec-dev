(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class LightboxRejectOrder
	 * @extends Tc.Module
	 */
	Tc.Module.LightboxOpenOrder = Tc.Module.extend({

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
			this.sandbox.subscribe('lightboxOpenOrder', this);

			// set module variables
			this.$modal = this.$$('.modal');
			this.$body = this.$$('.bd');

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

			//
			// SelectBoxIt Dropdown
			if(!Modernizr.touch && !Modernizr.isie7) {

				// Lazy Load caroufredsel
				Modernizr.load([{
					load: '/_ui/all/cache/dyn-jquery-selectboxit.min.js',
					complete: function () {
						self.$$('.selectpicker').selectBoxIt({
							autoWidth : false
						});
					}
				}]);
			}

			self.$btnConfirm.on('click', function() {

				var selectedDate = self.$$('.select-open-order-date').find(":selected").val()
					,allUsersCanEditOption = self.$$('.all-users-can-edit').is(':checked')
				;

				// delete dates, which are later then the selected one and are not possible anymore
				var selectedCloseDateObject = new Date(selectedDate);
				var possibleDates = self.$$('.select-open-order-date option');
				$.each(possibleDates, function(index, possibleDate){
					var possibleDateObject = new Date($(possibleDate).val());

					if(possibleDateObject > selectedCloseDateObject){
						possibleDate.remove();
					}
				});

				if (jQuery.fn.selectBoxIt) {
					self.$$('.select-open-order-date').data("selectBox-selectBoxIt").refresh();
				}

				self.hideModal();
				self.fire('dialogConfirm', { selectedDate: selectedDate, allUsersCanEditOption: allUsersCanEditOption},['lightboxOpenOrder']);
			});

			self.$btnCancel.on('click', function() {
				self.hideModal();
			});

			callback();
		},

		openModal: function (data) {
			var self = this;

			// Display overlay/modal
			this.$modal.modal();
			Tc.Utils.calculateModalHeight(self.$modal);

		},

		hideModal: function() {
			// hide overlay/modal
			this.$modal.modal('hide');
		},

		/**
		 * Register Event Listeners
		 */

		onEditOpenOrderSettings: function(data){
			var self = this;
			var $select = this.$$('.select-open-order-date');

			// only use plugin method, if plugin was initialized
			if(!Modernizr.touch && !Modernizr.isie7) {
				$select.data("selectBox-selectBoxIt").selectOption(data.selectedClosingDate);
			}
			else{
				$select.val(data.selectedClosingDate);
			}
			this.$$('.all-users-can-edit').prop('checked', data.isEditableByAllOption);


			self.openModal();
		},

		onLightboxConfirm: function(){
        	Tc.Utils.lightboxConfirm(this);
        }
	});

})(Tc.$);
