/**
 *
 *
 * WIKI
 * https://wiki.namics.com/display/distrelint/Howto+-+Status+Modal+Box
 *
 * FIRE
 *
 * self.fire('error', {
                title: 'Fehler',
                boxTitle: 'Fehler',
                boxMessage: 'Es ist ein schwerer Fehler aufgetretten!',
                identifier: 'deleteItem_12'
            }, ['lightboxStatus']);
 *
 *
 *
 */

(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Lightbox-status
	 * @extends Tc.Module
	 */
	Tc.Module.LightboxStatus = Tc.Module.extend({

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
            this.sandbox.subscribe('lightboxStatus', this);
            this.sandbox.subscribe('cart', this);

            // set module variables
            this.$btnConfirm = ('.btn-confirm', $ctx);
            this.$modal = $('.modal', $ctx);

            this.identifier = '';
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
				self = this;

            this.$btnConfirm.on('click', function () {
                self.confirm();
            });

			callback();
		},

        onError: function (data) {
            this.setData('error', data);
        },
        onWarning: function (data) {
            this.setData('warning', data);
        },
        onSuccess: function (data) {
            this.setData('success', data);
        },
        onInfo: function (data) {
            this.setData('info', data);
        },

        setData: function (type, data) {
            var self = this,
                $ctx = this.$ctx,
                $box = $('.box', $ctx),
                boxTitle = '',
                boxMessage = '',
                boxButtonText = '';

            self.identifier = data.identifier;

            $ctx.find('.title').html(data.title);

            if (data.boxTitle !== undefined && data.boxTitle.trim().length > 0) {
                boxTitle = '<h4>' + data.boxTitle + '</h4>';
            }
            if (data.boxMessage !== undefined) {
                boxMessage = '<p>' + data.boxMessage + '</p>';
            }
            if (data.boxButtonText !== undefined) {
	            boxButtonText = data.boxMessage;
	            $box.find('.btn-confirm').val(boxButtonText);
            }

	        $box.html(boxTitle + boxMessage).removeAttr('class').attr('class', 'box ' + type + '-box');
	        
	        if (data.boxMessage !== undefined && data.boxMessage.length > 100){
	        	$('.mod-lightbox-status .'+type+'-box p').css("text-align", "justify");
	        	$('.mod-lightbox-status .'+type+'-box').css("padding", "10px");
            }

            self.$modal.modal();
        },

        confirm: function () {
            var self = this;
            self.$modal.modal('hide');
            self.fire('lightboxStatusConfirm', {
                identifier: self.identifier
            }, ['lightboxStatus']);
            self.fire('setFocus', {}, ['cart']);
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
