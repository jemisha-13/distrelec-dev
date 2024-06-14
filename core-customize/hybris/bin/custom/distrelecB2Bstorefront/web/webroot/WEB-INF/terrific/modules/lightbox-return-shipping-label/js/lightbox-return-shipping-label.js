(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class LightboxYesNo
	 * @extends Tc.Module
	 */
	Tc.Module.LightboxReturnShippingLabel = Tc.Module.extend({

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
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {

			function removeModal(e){

                e.preventDefault();
                $('.modal-backdrop').remove();
                $('.modal').removeClass('current');

            }

            var str = $('.rmaAddress__text').text();
            str = str.replace(/([;])+/g, '<br>');
            $('.rmaAddress__text').html(str);

            $('.modal .hd .btn-close').click(function (e){

                removeModal(e);

            });

			$('.modal .ft .cancel-btn').click(function (e){

                removeModal(e);

            });

            $('body').on('click','.modal-backdrop',function(e){

                removeModal(e);

            });

            callback();
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
