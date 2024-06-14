(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class BomDataImport
	 * @extends Tc.Module
	 */
	Tc.Module.BomDataImport = Tc.Module.extend({

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
            this.sandbox.subscribe('lightboxLoginRequired', this);
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

			self.$textarea = self.$$('#data');
            this.$backendData = $('#backenddata');

			self.$textImportForm = self.$$('.form-textimport');
			self.$fileImportForm = self.$$('.form-fileimport');
			self.$selectBtn = self.$$('.upload-file');
			self.$uploadBtn = self.$$('#file');
			self.$fileName = self.$$('.filename');
			self.$continueBtnText = $('.btn-continue', self.$textImportForm);
			self.$continueBtnFile = $('.btn-continue', self.$fileImportForm);

			self.$errorFileArea = $('.errors', $ctx);

			self.$errorFileArea.hide();// why not hide via css?

			var sampleData =  self.$textarea.data('sample'),
				sampleDataConverted = sampleData.replace(/<br \/>/g, "\n");

			// populate textarea example text
			self.$textarea.val(sampleDataConverted);


			// note: IE8/9 do not support fileAPI, several workarounds implemented over the code (show standard input file, special checks and elements only for advanced browsers)
			// boolean for IE8/9 workaround (non-supported file api)
			self.enableIEWorkaround = $.browser.msie && ($.browser.version === '8.0' || $.browser.version === '9.0');


			// hide upload button
			if (!self.enableIEWorkaround) {
				self.$uploadBtn.hide();
			}
			else {// IE8/9 workaround
				var _txt = self.$selectBtn.text(),
					$alternative = $('<p class=\"ie-alt\">' + _txt + '</p>');

				self.$uploadBtn.before($alternative);
			}

			if ( ('.mod-global-messages .error').length > 0 ) {
                digitalData.page.pageInfo.error.error_page_type = $('.skin-layout-import-tool .mod-global-messages .error').text().trim();
			}

			// empty sample file name
			self.$fileName.empty();

			// make continue button inactive
			self.$continueBtnText.attr('disabled','disabled');
			self.$continueBtnFile.attr('disabled','disabled');

			// delegate module handler: click textarea clears default textarea value, enable continue btn
			self.$textarea.on('click', function(e) {
				if(self.$textarea.val() === sampleDataConverted) {
					self.$textarea.val('');
					self.$continueBtnText.removeAttr('disabled');
				}

                self.checkUserLoggedIn(e);
			});

            self.$continueBtnText.on('click', function(e) {
                digitalData.page.pageInfo.bomMethod = "copy paste";
                sessionStorage.setItem("digitalData.page.pageInfo.bomMethod", "copy paste");
            });

            self.$continueBtnFile.on('click', function(e) {
                sessionStorage.setItem("digitalData.page.pageInfo.bomMethod", "file import");
                digitalData.page.pageInfo.bomMethod = "file import";
            });

			
			self.$textarea.on('focus', function() {
				if(self.$textarea.val() === sampleDataConverted) {
					self.$continueBtnText.removeAttr('disabled');
				}
			});		

			// document mouseup handler: restore textarea default value if empty, disable continue btn
			$(document).mouseup(function() {
				if(self.$textarea.val() === '') {
					self.$textarea.val(sampleDataConverted);
					self.$continueBtnText.attr('disabled','disabled');
				}
			});

            var $usersettings = $('.usersettings', self.$backendData);

            if ($usersettings.length > '0' && $usersettings.data('login').toString() === 'true') {

                if (!self.enableIEWorkaround) {// IE8/9 workaround
                    self.$selectBtn.on('click', function() {
                        self.$uploadBtn.click();
                        self.isDropFile =  false;
                        return false;
                    });
                }
                else {
                    self.$selectBtn.hide();
                }

            } else {

                $('.upload-file.boxy').click(function (){
                    $('.import-tool-modal').addClass('current');
                    $('.skin-layout-import-tool .modal-backdrop').removeClass('hidden');
                    return false;
                });

            }

            self.$selectBtn.on('drag dragstart dragend dragover dragenter dragleave drop', function(e) {
                e.preventDefault();
                e.stopPropagation();
            })
			.on('dragover dragenter', function() {
				self.$selectBtn.addClass('upload-file--dragover');
			})
			.on('dragleave dragend drop', function() {
				self.$selectBtn.removeClass('upload-file--dragover');
			})
			.on('drop', function(e) {

				if ($usersettings.length > '0' && $usersettings.data('login').toString() === 'true') {
					var elem = document.getElementById('file');
					elem.files = e.originalEvent.dataTransfer.files;
					self.isDropFile =  true;
					self.$uploadBtn.trigger("change");
				} else {
					$('.import-tool-modal').addClass('current');
                    $('.skin-layout-import-tool .modal-backdrop').removeClass('hidden');
					return false;
				}

			});


            self.$uploadBtn.on('change', function(ev) {
				self.onFileChange(ev);
			});

            digitalData.page.pageInfo.bomStep = 1;

			callback();
		},

		/**
		 *  Refresh Uploaded Filename in Placeholder
		 */
		refreshFileName : function() {
			var $ctx = this.$ctx,
				self = this;

			var filename = self.$uploadBtn.val().split('\\').pop();
			self.$fileName.text(filename);
		},

        checkUserLoggedIn : function (e) {
			var $usersettings = $('.usersettings', self.$backendData);

			if ($usersettings.length > '0' && $usersettings.data('login').toString() === 'true') {
				return true;
			} else {
                e.preventDefault();
                $('.import-tool-modal').addClass('current');
                $('.skin-layout-import-tool .modal-backdrop').removeClass('hidden');
				return false;
			}
		},

		// trigger refresh file name after uploading a new file
		onFileChange : function(ev) {
			var $ctx = this.$ctx,
				self = this,
				$target = $(ev.target),
				allowedFileTypes = $target.data('file-types').split(','),
				maxFileSize = parseInt($target.data('max-file-size')),
				file = self.enableIEWorkaround ? $target.data('ie-file') : ev.target.files[0],// workaround IE8/9
				//fileSize = file.size !== 'undefined' ? file.size : file.fileSize,
				fileSize = self.enableIEWorkaround ? 50000 : file.size,// workaround IE8/9
				fileExt = $target.val().split('.').pop().toLowerCase(),
				fileSizeDeniedMessage = $target.data('file-size-denied-message'),
				fileSizeMessage = $target.data('file-size-message'),
				fileTypeDeniedMessage = $target.data('file-type-denied-message'),
				fileTypeMessage = $target.data('file-type-message'),
				$fileImportForm = self.$$('.form-fileimport'),
				$btnContinue = $('.btn-continue', $fileImportForm),
				$errorFileArea = $('.errors', $ctx),
				$errorFileMessage = $('p', $errorFileArea);

			$errorFileArea.hide();

            if ( file !== undefined ) {

                if (file === null) {
                    return;
                }
                fileSize = self.enableIEWorkaround ? 50000 : file.size;// workaround IE8/9

            } else {
                self.$fileName.empty();
                $btnContinue.attr('disabled','disabled');
                return;
            }


            if($.inArray(fileExt, allowedFileTypes) == -1) {
				$errorFileMessage.html(fileTypeDeniedMessage + ' .' + fileExt.toUpperCase() + '. ' + fileTypeMessage + ' ' + allowedFileTypes);
				$errorFileArea.show();
			} else {
				if(fileSize >= maxFileSize) {
					$errorFileMessage.html(fileSizeDeniedMessage + ' ' + Math.round(fileSize / (1024*1024) * 100) / 100 + ' MB. ' + fileSizeMessage);
					$errorFileArea.show();
				} else {
					self.refreshFileName();
					$btnContinue.removeAttr('disabled');
				}
			}
		}

	});

})(Tc.$);
