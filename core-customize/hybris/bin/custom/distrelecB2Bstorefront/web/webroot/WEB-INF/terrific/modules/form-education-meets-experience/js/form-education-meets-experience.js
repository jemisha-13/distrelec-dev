(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Register
	 * @extends Tc.Module
	 */
	Tc.Module.FormEducationMeetsExperience = Tc.Module.extend({

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

			//
			// Validation Error Messages
			this.validationErrorEmpty = this.$$('#tmpl-form-education-meets-experience-validation-error-empty').html();
			this.validationErrorDropdown = this.$$('#tmpl-form-education-meets-experience-validation-error-dropdown').html();
			this.validationErrorEmail = this.$$('#tmpl-form-education-meets-experience-validation-error-email').html();
			this.validationErrorEmailRepeat = this.$$('#tmpl-form-education-meets-experience-validation-error-email-repeat').html();
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
			self.$selectBtn   = self.$$('.btn-select');
			self.$selectBtn1 = self.$$('.eTalent2014File-select');
			self.$selectBtn2 = self.$$('.studyExchangeFile-select');
			self.$selectBtn3 = self.$$('.motivationFile-select');
			self.$uploadBtn   = self.$$('#eTalent2014File, #studyExchangeFile, #motivationFile');
			self.$uploadBtn1 = self.$$('#eTalent2014File');
			self.$uploadBtn2 = self.$$('#studyExchangeFile');
			self.$uploadBtn3 = self.$$('#motivationFile');
			self.$fileName = self.$$('.filename');
			self.$errorFileArea = $('.errors', $ctx);
			

			// note: IE8/9 do not support fileAPI, several workarounds implemented over the code (show standard input file, special checks and elements only for advanced browsers)
			// boolean for IE8/9 workaround (non-supported file api)
			self.enableIEWorkaround = $.browser.msie && ($.browser.version === '8.0' || $.browser.version === '9.0');

			// hide upload button
			if (!self.enableIEWorkaround) {
				self.$uploadBtn1.hide();
				self.$uploadBtn2.hide();
				self.$uploadBtn3.hide();
			} else {	// IE8/9 workaround
				var _txt = self.$selectBtn1.text(),
					$alternative = $('<p class=\"ie-alt\">' + _txt + '</p>');
				self.$uploadBtn1.before($alternative);
				 _txt = self.$selectBtn2.text(),
					$alternative = $('<p class=\"ie-alt\">' + _txt + '</p>');
				self.$uploadBtn2.before($alternative);
				 _txt = self.$selectBtn3.text(),
					$alternative = $('<p class=\"ie-alt\">' + _txt + '</p>');
				self.$uploadBtn3.before($alternative);
			}

			if (!self.enableIEWorkaround) {	// IE8/9 workaround
				self.$selectBtn1.on('click', function() {
					self.$uploadBtn1.click();
					return false;
				});
				self.$selectBtn2.on('click', function() {
					self.$uploadBtn2.click();
					return false;
				});
				self.$selectBtn3.on('click', function() {
					self.$uploadBtn3.click();
					return false;
				});
			}
			else {
				self.$selectBtn.hide();
			}

			self.$uploadBtn.on('change', function(ev) {
				self.onFileChange(ev);
			});


			// Lazy Load SelectBoxIt Dropdown
			if(!Modernizr.touch && !Modernizr.isie7) {
				Modernizr.load([{
					load: '/_ui/all/cache/dyn-jquery-selectboxit.min.js',
					complete: function () {
						self.$$('.selectpicker').selectBoxIt({
							autoWidth : false
						});
					}
				}]);
			}

			//
			// Validate Elements
			this.$ctx.on('click', '.btn-primary', function(e) {

				e.preventDefault();
				var isValid = true;

				Tc.Utils.validate($('.validate-empty',self.$ctx), self.validationErrorEmpty, 'triangle', function(error) {
					if (error) {isValid = false;}
				});

				Tc.Utils.validate($('.validate-dropdown',self.$ctx), self.validationErrorDropdown, 'triangle', function(error) {
					if (error) {isValid = false;}
				});

				Tc.Utils.validate($('.validate-email',self.$ctx), self.validationErrorEmail, 'triangle', function(error) {
					if (error) {isValid = false;}
				});
				
				Tc.Utils.validate($('.validate-email-repeat',self.$ctx), self.validationErrorEmailRepeat, 'triangle', function(error) {
					if (error) {isValid = false;}
				});

				if (isValid === true) {
					var gid = $('.g-recaptcha',self.$ctx).eq(0).data('gid');
					if (typeof gid !== 'number') {
						gid = grecaptcha.render($('.g-recaptcha',self.$ctx)[0],{},true);
						$('.g-recaptcha',self.$ctx).eq(0).data({'gid':gid});
					}
					grecaptcha.execute(gid);
				} else {
					// Scroll to the first error
					Tc.Utils.scrollToFirstError(self.$ctx);
				}

			});

			callback();
		},

		/**
		 *  Refresh Uploaded Filename in Placeholder
		 */
		refreshFileName : function(n) {
			var $ctx = this.$ctx,
				self = this;

			var filename = self.$uploadBtn[n].value.split('\\').pop();
			self.$fileName.text(filename).removeClass('hidden');
		},

		// trigger refresh file name after uploading a new file
		onFileChange : function(ev) {
			var $ctx = this.$ctx,
				self = this,
				$target = $(ev.target),
				n=$target.attr('id')=='eTalent2014File'?0:$target.attr('id')=='studyExchangeFile'?1:2,
				allowedFileTypes = $target.data('file-types').split(','),
				maxFileSize = parseInt($target.data('max-file-size')),
				file = self.enableIEWorkaround ? $target.data('ie-file') : ev.target.files[0],// workaround IE8/9
				//fileSize = file.size !== 'undefined' ? file.size : file.fileSize,
				fileSize = self.enableIEWorkaround ? 50000 : file.size,// workaround IE8/9
				fileExt = $target.val().split('.').pop().toLowerCase(),
				fileTypeDeniedMessage = $target.data('file-type-denied-message'),
				fileTypeMessage = $target.data('file-type-message'),
				fileSizeDeniedMessage = $target.data('file-size-denied-message'),
				fileSizeMessage = $target.data('file-size-message'),
				$errorFileMessage = $('.errors', $ctx);

			$errorFileMessage.addClass('hidden');

			if ($.inArray(fileExt, allowedFileTypes) == -1) {
				$errorFileMessage.html(fileTypeDeniedMessage + ' .' + fileExt.toUpperCase() + '. ' + fileTypeMessage + ' ' + allowedFileTypes).removeClass('hidden');
			} else {
				if (fileSize >= maxFileSize) {
					$errorFileMessage.html(fileSizeDeniedMessage + ' ' + Math.round(fileSize / (1024*1024) * 100) / 100 + ' MB. ' + fileSizeMessage).removeClass('hidden');
				} else {
					self.refreshFileName(n);
				}
			}
		}

	});

})(Tc.$);
