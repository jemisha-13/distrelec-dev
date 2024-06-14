(function($) {

	Tc.Module.LightboxShareEmail = Tc.Module.extend({

		//
		// Init

		init: function($ctx, sandbox, id) {
			// call base constructor
			this._super($ctx, sandbox, id);

            // set module variables
            this.$modal = $('.modal', $ctx);

            // subscribe to connector channel/s
			this.sandbox.subscribe('lightboxShareEmail', this);
			this.sandbox.subscribe('captcha', this);

            // Validation Errors
			this.validationErrorEmpty = this.$$('#tmpl-lightbox-share-email-validation-error-empty').html();
            this.validationErrorEmail = this.$$('#tmpl-lightbox-share-email-validation-error-email').html();
            this.validationErrorCaptcha = this.$$('#tmpl-lightbox-share-email-validation-error-captcha').html();

		},

		//
		// On

		on: function(callback) {

            var self = this
				,$ctx = this.$ctx
            ;

            self.$modalFormView = $('.modal-form-view', $ctx);
            self.$btnConfirm = $('.btn-save', $ctx);

            self.$modalSent = $('.modal-sent-view', $ctx);
            self.$modalSentError = $('.modal-sent-view-error', $ctx);

            shareProductEmailCaptchaCallback = function () {
        		self.sendForm();
        	};


            callback();
		},

		//
		// Show Lightbox

        onShowLightbox: function (data) {

            var self = this,
                count = 0;

			// Show the Lightbox
            self.$modalFormView.modal();

            Tc.Utils.calculateModalHeight(self.$modalFormView);

			// Bind Click Event to Btn Send
            self.$btnConfirm.off('click.toolsitem.share').on('click.toolsitem.share', function (e) {

                e.preventDefault();

				// Validation
				var isValid = true;

				Tc.Utils.validate($('.validate-empty',self.$ctx), self.validationErrorEmpty, 'triangle', function(error) {
					if(error) { isValid = false; }
				});

				Tc.Utils.validate($('.validate-email',self.$ctx), self.validationErrorEmail, 'triangle', function(error) {
					if(error) { isValid = false; }
				});

				Tc.Utils.validate($('.captcha', self.$ctx), self.validationErrorCaptcha, 'triangle', function(error) {
					if(error) { isValid = false; }
				});

	            // check if scrollbar should be added when content is getting too high
	            if(!isValid){
		            Tc.Utils.calculateModalHeight(self.$modal);
	            }
	            else{
					var $thisCaptcha = $('.g-recaptcha',self.$ctx).last(),
						gid = $thisCaptcha.data('gid');
					if (typeof gid !== 'number') {
						gid = grecaptcha.render($thisCaptcha[0],{callback:'shareProductEmailCaptchaCallback'},true);
						$thisCaptcha.data({'gid':gid});
					}else{
                        grecaptcha.reset(gid);
					}

					grecaptcha.execute(gid);
//            		self.sendForm(data.currentQueryUrl);
	            }

            });
        },


        //
        // Send Form

        sendForm: function (currentQueryUrl) {

            var self = this,
                $ctx = this.$ctx,
                postData = Tc.Utils.getFormData($ctx),
	            searchParams = window.location.search
	        ;
            postData['g-recaptcha-response'] = $('.g-recaptcha',self.$ctx).last().find('#g-recaptcha-response').val();

	        // currentQueryUrl is not empty when we are on a facet page and the url in the browser bar might not be actual in IE8/IE9
	        if(currentQueryUrl !== undefined && currentQueryUrl !== ""){
		        searchParams = "?" + currentQueryUrl.split('?')[1];
	        }

            self.$btnConfirm.attr('disabled', 'disabled');

            $.ajax({
                url: window.location.protocol + '//' + window.location.host + window.location.pathname + '/sendToFriend' + searchParams,
                type: 'post',
                data: postData,
                success: function (data, textStatus, jqXHR) {
	                if(data.errorCode === ''){
		                self.sentSuccess(postData);
	                }
	                else{
						if (data.errorCode === 'captcha') {

							// Reload Captcha Image
							self.fire('reloadCaptcha', {}, ['form.captcha.error']);

							$('#captchaAnswer', self.$ctx).val('');

							Tc.Utils.validate($('.captcha', self.$ctx), self.validationErrorCaptcha, 'triangle', function(error) {
								if(error) { isValid = false; }
							});
						} else {
		                	self.sentError(postData);
						}

	                }
	                self.$btnConfirm.removeAttr('disabled');
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    self.$btnConfirm.removeAttr('disabled');
                    self.sentError(postData);
                }
            });
        },

        //
        // Send success

        sentSuccess: function (postData) {
            var self = this;

            $(window).off('keydown.lightboxShareEmail');
            self.$modalFormView.modal('hide');

            self.$modalSent.find('.edit-email').html(Tc.Utils.escapeHtml(postData.email2));
            self.$modalSent.find('.message-text').html(Tc.Utils.escapeHtml(postData.message).replace(/\n/g, '<br />'));

	        self.$modalSent.removeClass('height-adjusted');
            self.$modalSent.modal();

	        Tc.Utils.calculateModalHeight(self.$modalSent);

	        $(window).on('keydown.lightboxShareEmailSent', function (e) {
                if (e.keyCode === 13) {
                    self.$modalSent.find('.btn-ok').trigger('click');
                }
            });

            self.$modalSent.find('.btn-ok').off('click').on('click', function () {
                $(window).off('keydown.lightboxShareEmailSent');
                self.$modalSent.modal('hide');
            });
        },


        //
        // Send error

        sentError: function (postData) {
            var self = this;

            $(window).off('keydown.lightboxShareEmail');
            self.$modalFormView.modal('hide');

	        self.$modalSentError.find('.message-text').html(postData.message);

	        self.$modalSentError.removeClass('height-adjusted');
	        self.$modalSentError.modal();

	        Tc.Utils.calculateModalHeight(self.$modalSentError);

	        $(window).on('keydown.lightboxShareEmailSentError', function (e) {
                if (e.keyCode === 13) {
                    self.$modalSentError.find('.btn-cancel').trigger('click');
                }
            });


            self.$modalSentError.find('.btn-cancel').off('click').on('click', function () {
                $(window).off('keydown.lightboxShareEmailSentError');
                self.$modalSentError.modal('hide');
            });
        },


		// Clear Form
		clearForm: function() {
			var self = this,
				$modalBd = self.$modalFormView.find('.bd');

			// Reload Captcha Image
			self.fire('reloadCaptcha', {}, ['captcha']);

			self.$modalFormView.find('input[type=text], input[type=email], textarea').val('');

			// reset modal scroll/height handling
			$modalBd
				.removeAttr('style')
				.removeClass('modal-height-scroll');
		}
	});

})(Tc.$);