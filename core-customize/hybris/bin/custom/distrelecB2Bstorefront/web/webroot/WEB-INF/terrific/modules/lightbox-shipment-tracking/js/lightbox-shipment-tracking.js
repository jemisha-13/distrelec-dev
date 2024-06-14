(function ($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Lightboxshoppinglist
	 * @extends Tc.Module
	 */
	Tc.Module.LightboxShipmentTracking = Tc.Module.extend({

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
		init: function ($ctx, sandbox, id) {

			// call base constructor
			this._super($ctx, sandbox, id);

			// subscribe to connector channel/s
			this.sandbox.subscribe('lightboxShipmentTracking', this);

			// set module variables
			this.$modal = $('.modal', $ctx);
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function (callback) {


            $('.cell-header-collapse').click(function() {

                $(this).next(".cell-body-content").toggleClass('current');
                $(this).toggleClass('current');

            });

            function removeModal(e){
                e.preventDefault();
                $('.modal').removeClass('current');
                $('.modal-backdrop.in').remove();
			}

            $('.modal .hd .btn-close').click(function (e){

                removeModal(e);

			});

            $('body').on('click','.modal-backdrop',function(e){

                removeModal(e);

            });

			callback();
		},

		/**
		 *
		 * @method gatherListIds
		 *
		 * @returns {{add: Array, remove: Array}}
		 */
		gatherListIds: function () {
			var self = this,
				listIdArray = [],
				$chk = self.$modal.find('.checkbox-group').find('input[type=checkbox]');

			$chk.each(function () {
				var $chkItem = $(this),
					listId = $chkItem.attr('name');

				if ($chkItem.is(':checked')) {
					if (listId !== '' && listId !== undefined && listId !== 'undefined') {
						listIdArray.push(listId);
					}
				}
			});

			return listIdArray;
		}
	});

})(Tc.$);


