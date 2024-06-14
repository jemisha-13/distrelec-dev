(function ($) {

    /**
     * Module implementation.
     *
     * @namespace Tc.Module
     * @class Product
     * @extends Tc.Module
     */
    Tc.Module.QualityPage = Tc.Module.extend({

        /**
         * Hook function to do all of your module stuff.
         *
         * @method on
         * @param {Function} callback function
         * @return void
         */

        on: function (callback) {
            var $ctx = this.$ctx,
                self = this;
            // based on the BOM tool upload
            this.$backendData = $('#backenddata');

            self.$fileImportForm = self.$$('.form-fileimport');
            self.$selectBtn = self.$$('.upload-file');
            self.$uploadBtn = self.$$('#file');
            self.$fileName = self.$$('.filename');
            self.$loader = self.$$('.ajax-product-loader');
            self.$bulkDownload = self.$$('.download-bulk');
            self.downloadTemplates = self.$$('.download');
            self.$errorFileArea = $('.errors', $ctx);
            self.$warningFileArea = $('.warnings', $ctx);
            self.$downloadLinks = $('.download__excel', $ctx);
            self.$errorFileArea.hide();// why not hide via css?
            self.$warningFileArea.hide();
            self.downloadTemplates.addClass('disable');
            // note: IE8/9 do not support fileAPI, several workarounds implemented over the code (show standard input file, special checks and elements only for advanced browsers)
            // boolean for IE8/9 workaround (non-supported file api)
            self.enableIEWorkaround = $.browser.msie && ($.browser.version === '8.0' || $.browser.version === '9.0');

            // empty sample file name
            self.$fileName.empty();

            // hide upload button
            if (!self.enableIEWorkaround) {
                self.$uploadBtn.hide();
            } else {// IE8/9 workaround
                var _txt = self.$selectBtn.text(),
                    $alternative = $('<p class=\"ie-alt\">' + _txt + '</p>');

                self.$uploadBtn.before($alternative);
            }

            if (!self.enableIEWorkaround) {// IE8/9 workaround
                self.$selectBtn.on('click', function () {
                    self.$uploadBtn.click();
                    self.isDropFile = false;
                    return false;
                });
            } else {
                self.$selectBtn.hide();
            }

            // drag events
            self.$selectBtn.on('drag dragstart dragend dragover dragenter dragleave drop', function (e) {
                e.preventDefault();
                e.stopPropagation();
            })
                .on('dragover dragenter', function () {
                    self.$selectBtn.addClass('upload-file--dragover');

                })
                .on('dragleave dragend drop', function () {
                    self.$selectBtn.removeClass('upload-file--dragover');

                })
                .on('drop', function (e) {
                    var elem = document.getElementById('file');
                    elem.files = e.originalEvent.dataTransfer.files;
                    self.isDropFile = true;
                    self.$uploadBtn.trigger("change");
                });

            self.$uploadBtn.on('change', function (ev) {
                self.onFileChange(ev);
            });

            //uploading the same filename
            self.$uploadBtn.on('click', function () {
                if(!$(this).val()){
                    //Initial Case when no document has been uploaded
                    $("input[type='file']").change(function(ev){
                        self.onFileChange(ev);
                    });
                }else{
                    //Subsequent cases when the exact same document will be uploaded several times
                    $(this).val('');
                    $("input[type='file']").unbind('change');
                    $("input[type='file']").change(function(ev){
                        self.onFileChange(ev);
                    });
                }
            });

            callback();
        },
        /**
         *  Refresh Uploaded Filename in Placeholder
         */
        refreshFileName: function () {
            var $ctx = this.$ctx,
                self = this;

            var filename = self.$uploadBtn.val().split('\\').pop();
            self.$fileName.text(filename);
        },
        disableDownload: function () {
            var $ctx = this.$ctx,
                self = this;
            self.$loader.fadeOut();
            self.downloadTemplates.addClass('disable');
            self.$fileName.empty();
            self.$bulkDownload.attr('disabled', 'disabled');
            self.$warningFileArea.fadeOut();
            self.$downloadLinks.css('pointer-events', 'none');
        },
        disableDownloadWhileDownloading: function () {
            var $ctx = this.$ctx,
                self = this;
            self.downloadTemplates.addClass('disable');
            self.$bulkDownload.attr('disabled', 'disabled');
            self.$warningFileArea.fadeOut();
            self.$downloadLinks.css('pointer-events', 'none');
        },
        enableDownload: function () {
            var $ctx = this.$ctx,
                self = this;
            self.$loader.fadeOut();
            self.downloadTemplates.removeClass('disable');
            self.$bulkDownload.removeAttr('disabled');
            self.$warningFileArea.fadeOut();
            self.$downloadLinks.css('pointer-events', 'auto');
        },
        onFileChange: function (ev) {
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
                fileTypeDeniedMessage = $target.data('file-type-denied-message'),
                fileArticleWarning = $target.data('file-article-warning'),
                fileNoValidProductCodes = $target.data('file-invalid-product-codes'),
                $warningFileArea = $('.warnings', $ctx),
                $errorFileArea = $('.errors', $ctx),
                $errorFileMessage = $('p', $errorFileArea);
                $warningFileMessage = $('p', $warningFileArea);
                $initialMargin = $('#qualityPageDownloadExcel', $ctx);

            $errorFileArea.fadeOut();

            if (file !== undefined) {

                if (file === null) {
                    return;
                }
                fileSize = self.enableIEWorkaround ? 50000 : file.size;// workaround IE8/9

            } else {
                self.$fileName.empty();
                return;
            }

            if ($.inArray(fileExt, allowedFileTypes) == -1) {
                $initialMargin.removeClass('initialMarginNoErrors');
                $errorFileMessage.html(fileTypeDeniedMessage);
                $errorFileArea.fadeIn();
                $errorFileArea.css('display', 'flex');
                self.disableDownload();
            } else {
                if (fileSize >= maxFileSize) {
                    $initialMargin.removeClass('initialMarginNoErrors');
                    $errorFileMessage.html(fileSizeDeniedMessage);
                    $errorFileArea.fadeIn();
                    $errorFileArea.css('display', 'flex');
                    self.disableDownload();
                } else {
                    self.refreshFileName();
                    self.$bulkDownload.removeAttr('disabled');
                    self.downloadTemplates.removeClass('disable');
                    self.$loader.fadeOut();

                    /* AJAX calls /file-upload */
                    var uploadFile = document.getElementById('file').files[0];
                    var form = document.getElementById('upload-form-text');
                    var formData = new window.FormData(form);
                    formData.append("file", uploadFile);

                    $.ajax({
                        url: form.action,
                        method: "POST",
                        data: formData,
                        contentType: false,
                        processData: false,
                        beforeSend : function (){
                            $initialMargin.addClass('initialMarginNoErrors');
                            self.$loader.fadeIn();
                        },
                        success: function (data) {
                            self.$loader.fadeOut();
                            self.$downloadLinks.css('pointer-events', 'all');
                            var productCodes = data.productCodes;
                            var invalidProductsCodes = data.invalidProductsCodes;

                            if(!productCodes || productCodes.length === 0) {
                                $initialMargin.removeClass('initialMarginNoErrors');
                                $errorFileMessage.html(fileNoValidProductCodes);
                                $errorFileArea.fadeIn();
                                $errorFileArea.css('display', 'flex');
                                self.disableDownload();
                            } else {
                                if(invalidProductsCodes) {
                                    $initialMargin.removeClass('initialMarginNoErrors');
                                    $warningFileMessage.html(fileArticleWarning + '<br>' + invalidProductsCodes);
                                    $warningFileArea.fadeIn();
                                    $warningFileArea.css('display', 'flex');
                                } else {
                                    $warningFileArea.fadeOut();
                                    $initialMargin.addClass('initialMarginNoErrors');
                                }
                            }

                            $('#qualityPageExcelProductCodes').val(productCodes);
                            $('#qualityPagePDFProductCodes').val(productCodes);
                            $('#qualityPageBulkDownloadProductCodes').val(productCodes);

                            /* click download Excel */
                            $('#qualityPageDownloadExcel').on('click', function (e) {

                                self.$loader.fadeIn();
                                self.disableDownloadWhileDownloading();

                                $("#download-excel-report").submit(function(){
                                    var $form = $(this);

                                    $.ajax({
                                        type     : "POST",
                                        cache    : false,
                                        url      : $form.attr('action'),
                                        data     : $form.serializeArray(),
                                        beforeSend : function (){
                                            $initialMargin.addClass('initialMarginNoErrors');
                                        },
                                        success  : function() {
                                            self.enableDownload();
                                            self.$loader.fadeOut();
                                        },
                                        error: function (error) {
                                            $initialMargin.removeClass('initialMarginNoErrors');
                                            if (error.readyState === 0) {
                                                self.enableDownload();
                                                self.$loader.fadeOut();
                                            }
                                            $errorFileMessage.html(error.responseJSON.errorMessage);
                                            $errorFileArea.fadeIn();
                                            $errorFileArea.css('display', 'flex');
                                            self.disableDownload();
                                        }
                                    });
                                });

                            });

                            /* click download PDF */
                            $('#qualityPageDownloadPDF').on('click', function (e) {

                                self.$loader.fadeIn();
                                self.disableDownloadWhileDownloading();

                                $("#download-pdf-report").submit(function(){

                                    var $form = $(this);
                                    $.ajax({
                                        type     : "POST",
                                        cache    : false,
                                        url      : $form.attr('action'),
                                        data     : $form.serializeArray(),
                                        beforeSend : function (){
                                            $initialMargin.addClass('initialMarginNoErrors');
                                        },
                                        success  : function() {
                                            self.enableDownload();
                                            self.$loader.fadeOut();
                                        },
                                        error: function (error) {
                                            $initialMargin.removeClass('initialMarginNoErrors');
                                            if (error.readyState === 0) {
                                                self.enableDownload();
                                                self.$loader.fadeOut();
                                            }
                                            $errorFileMessage.html(error.responseJSON.errorMessage);
                                            $errorFileArea.fadeIn();
                                            $errorFileArea.css('display', 'flex');
                                            self.disableDownload();
                                        }
                                    });
                                });
                            });

                            /* click bulk download */
                            $('#qualityPageDownloadBulk').on('click', function (e) {

                                self.$loader.fadeIn();
                                self.disableDownloadWhileDownloading();

                                $("#download-bulk-download").submit(function(){

                                    var $form = $(this);
                                    $.ajax({
                                        type     : "POST",
                                        cache    : false,
                                        url      : $form.attr('action'),
                                        data     : $form.serializeArray(),
                                        beforeSend : function (){
                                            $initialMargin.addClass('initialMarginNoErrors');
                                        },
                                        success  : function() {
                                            self.enableDownload();
                                            self.$loader.fadeOut();
                                        },
                                        error: function (error) {
                                            $initialMargin.removeClass('initialMarginNoErrors');
                                            if (error.readyState === 0) {
                                                self.enableDownload();
                                                self.$loader.fadeOut();
                                            }
                                            $errorFileMessage.html(error.responseJSON.errorMessage);
                                            $errorFileArea.fadeIn();
                                            $errorFileArea.css('display', 'flex');
                                            self.disableDownload();
                                        }
                                    });
                                });
                            });
                        },
                        error: function (error) {
                            $initialMargin.removeClass('initialMarginNoErrors');
                            $errorFileMessage.html(error.responseJSON.errorMessage);
                            $errorFileArea.fadeIn();
                            $errorFileArea.css('display', 'flex');
                            self.disableDownload();
                        }
                    });
                }
            }
        },
        after: function () {

        }

    });

})(Tc.$);
