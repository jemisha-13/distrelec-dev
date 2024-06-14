(function ($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Lightboxshoppinglist
	 * @extends Tc.Module
	 */
	Tc.Module.LightboxVideo = Tc.Module.extend({

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
			this.sandbox.subscribe('lightboxVideo', this);

            // set module variables
            this.$modal = this.$$('.modal');
			
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function (callback) {
			var mod = this;
			var $sbcLink = $('.sbc-link');
			var $btnClose = $('.btn-close');
			$sbcLink.on('click', $.proxy(this.sbcLinkClicked, this));
			$btnClose.on('click', $.proxy(this.btnCloseClicked, this));
			
			$('html').click(function(e) {
				if(e.target.className === 'modal-backdrop'){
					$('.youtubeIframe').remove();
				}
			});
			
            callback();
		},
		
		
		/**
		 * Special case for Simple Banner Component
		 * @method sbcLinkClicked
		 *
		 */
		sbcLinkClicked: function (ev) {
			var youtubeId = ev.currentTarget.attributes['data-youtubeid-sbp'].nodeValue;
			if (youtubeId !== ''){
				//blocks the href link
				ev.preventDefault();
				this.onShowLightboxVideo(youtubeId);
			}
		},		
		
		btnCloseClicked: function (ev) {
			$('.youtubeIframe').remove();
		},
		
		
		
		
		/**
		 * @method onShowLightboxVideo
		 * 
		 */
		onShowLightboxVideo: function(data){
			var mod = this;
			
			var youtubeId = data.youtubeId;
			if (youtubeId === undefined ) {
				youtubeId = data;
			}
			
			if (window.location.protocol == 'https:'){
				$('.videoContainer').html('<iframe class="youtubeIframe" width="889" height="500" src="https://www.youtube.com/embed/'+youtubeId+'?rel=0" frameborder="0" allowfullscreen></iframe>');
			}
			if (window.location.protocol == 'http:'){
				$('.videoContainer').html('<iframe class="youtubeIframe" width="889" height="500" src="http://www.youtube.com/embed/'+youtubeId+'?rel=0" frameborder="0" allowfullscreen></iframe>');
			}

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


