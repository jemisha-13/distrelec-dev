(function ($) {

    /**
     * Module implementation.
     *
     * @namespace Tc.Module
     * @class Product-image-gallery
     * @extends Tc.Module
     */
    Tc.Module.BomSavedEntries = Tc.Module.extend({

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
        },

        /**s
         * Hook function to do all of your module stuff.
         *
         * @method on
         * @param {Function} callback function
         * @return void
         */
        on: function (callback) {

            var self = this,
                $ctx = this.$ctx,
                $popoverMenu = $('.wrapper-dropdown__menu', $ctx),
                $bomEntryDelete = $('.saved-bom-entries__item-delete', $ctx),
                $bomEntryDeleteModal = $("#bomEntryDeleteModal", $ctx),
                $bomEntryDeleteBtn = $(".btn-bomentrydelete", $ctx),
                $bomEntryRename = $(".saved-bom-entries__item-rename", $ctx),
                $bomEntryDuplicate = $(".saved-bom-entries__item-duplicate", $ctx),
                $bomEntryWrappeDropdown = $(".saved-bom-entries .wrapper-dropdown"),
                $bomEntryDeleteFilename = '',
                $bomEntryDeleteItem = '',
                $bomEntryFilename = $('.saved-bom-entries__filename', $ctx),
                $bomEntryRenameSaveItem = $('.saved-bom-entries__save-item'),
                $bomEntryRenameSave = $('.saved-bom-entries__save-item-save'),
                $bomEntryRenameSaveClose = $('.saved-bom-entries__save-item-close'),
                isOCI = $('.mod-layout').hasClass('isOCI-true');

            if( $bomEntryDeleteModal.hasClass('in') ) {
                $bomEntryDeleteModal.modal('hide');
            }

            $bomEntryDeleteBtn.on('click', function(e){
                e.preventDefault();
                savedBomFileDataVm.fileDelete();
            });

            $(document).click(function(e) {
                e.stopPropagation();
                if ( ! ( $(e.target).hasClass('close-icon') || $(e.target).hasClass('wrapper-dropdown__menu') ) ) {
                    $(".saved-bom-entries .wrapper-dropdown").removeClass('active');
                }
            });

            var savedBomFileDataVm = new Vue({
                el: '.saved-bom-entries',
                data: function () {
                    return {
                        savedBomFileData: []
                    };
                },
                created: function () {
                    var vueSelf = this;

                    axios.get('/bom-tool/list-files' )
                        .then(function (response){
                            vueSelf.savedBomFileData = response.data;
                            $('.bom-file-list-size').html(vueSelf.savedBomFileData.length);
                        });

                },
                methods: {
                    getSavedFileList: function () {
                        var vueSelf = this;

                        axios.get('/bom-tool/list-files' )
                            .then(function (response){
                                vueSelf.savedBomFileData = response.data;
                            });
                        return vueSelf.savedBomFileData;
                    },
                    openFileMenu: function(_file) {
                        var _elem = $(event.target);
                        $(".saved-bom-entries .wrapper-dropdown").removeClass('active');
                        _elem.parents('.wrapper-dropdown').toggleClass('active');
                    },

                    fileRename: function(_file) {
                        var _elem = $(event.target);
                        $('.wrapper-dropdown').removeClass('active');

                        var $bomEntryRenameSaveItemCurrent = _elem.parents('.saved-bom-entries__item'),
                            $bomEntryRenameSaveItem = $bomEntryRenameSaveItemCurrent.find('.saved-bom-entries__save-item'),
                            $bomEntryFilename = $bomEntryRenameSaveItemCurrent.find('.saved-bom-entries__filename');

                        $bomEntryFilename.addClass('hidden');
                        $bomEntryRenameSaveItem.removeClass('hidden');

                    },
                    fileRenameClose: function(_file) {
                        var _elem = $(event.target),
                            $bomEntryRenameSaveItemCurrent = _elem.parents('.saved-bom-entries__item'),
                            $bomEntryRenameSaveItem = $bomEntryRenameSaveItemCurrent.find('.saved-bom-entries__save-item'),
                            $bomEntryFilename = $bomEntryRenameSaveItemCurrent.find('.saved-bom-entries__filename');

                        $bomEntryRenameSaveItem.addClass('hidden');
                        $bomEntryFilename.removeClass('hidden');

                    },
                    fileRenameSave: function(_file) {
                        var vueSelf = this,
                            _elem = $(event.target),
                            $bomEntryItem = _elem.parents('.saved-bom-entries__item'),
                            $bomEntrySaveItem = $bomEntryItem.find('.saved-bom-entries__save-item'),
                            $bomEntryFilename = $bomEntryItem.find('.saved-bom-entries__filename'),
                            $bomEntryFilenameIpt = $bomEntryItem.find('.saved-bom-entries__item-filename-ipt'),
                            $bomEntryFilenameOld = $bomEntryItem.find('.saved-bom-entries__item-filename').val().toString(),
                            $bomEntryFilenameNew = $bomEntryItem.find('.saved-bom-entries__item-filename-ipt').val().toString();

                        $.ajax({
                            url: '/bom-tool/rename-file/'+$bomEntryFilenameOld,
                            type: 'POST',
                            method: 'post',
                            contentType: "text/html; charset=UTF-8",
                            data: $bomEntryFilenameNew,
                            success: function (data, textStatus, jqXHR) {
                                $bomEntrySaveItem.addClass('hidden');
                                $bomEntryFilename.removeClass('hidden');
                                vueSelf.savedBomFileData = vueSelf.getSavedFileList();
                            },
                            error: function (jqXHR, textStatus, errorThrown) {

                            }
                        });

                    },
                    fileDuplicate: function(_file) {
                        var vueSelf = this;

                        $.ajax({
                            url: '/bom-tool/copyFile?filename='+_file,
                            type: 'POST',
                            method: 'post',
                            contentType: "text/html; charset=UTF-8",
                            success: function (data, textStatus, jqXHR) {
                                $(".saved-bom-entries .wrapper-dropdown").removeClass('active');
                                vueSelf.savedBomFileData = data;
                                $('.bom-file-list-size').html(vueSelf.savedBomFileData.length);
                            },
                            error: function (jqXHR, textStatus) {

                                if ( jqXHR.status === 429) {
                                    $(".saved-bom-entries .fileuplloadlimit-error").removeClass('hidden');
                                    $('html, body').animate({
                                        scrollTop: 0
                                    },500);
                                }

                            }
                        });

                    },
                    showFileDeleteModal: function(_file) {
                        var vueSelf = this,
                            $elem = $(event.target);

                        $bomEntryWrappeDropdown.removeClass('active');
                        $bomEntryDeleteItem = $elem.parents('.saved-bom-entries__item');
                        $bomEntryDeleteFilename = $bomEntryDeleteItem.find('.saved-bom-entries__item-filename').val();
                        $bomEntryDeleteModal.removeClass('hidden');
                        $bomEntryDeleteModal.modal('show');
                    },
                    fileDelete: function() {
                        var vueSelf = this;

                        $.ajax({
                            url: '/bom-tool/delete-file?filename='+$bomEntryDeleteFilename,
                            type: 'POST',
                            method: 'post',
                            dataType: "json",
                            contentType: "text/html; charset=UTF-8",
                            success: function (data, textStatus, jqXHR) {
                                $bomEntryDeleteModal.modal('hide');
                                vueSelf.savedBomFileData = data;
                                $(".saved-bom-entries .wrapper-dropdown").removeClass('active');

                                var _size = vueSelf.savedBomFileData;
                                if ( _size > 0) {
                                    $('.bom-file-list-size').html(_size);
                                    $('.skin-metahd-item-account .bom-file-list').removeClass('hidden');
                                } else {
                                    $('.skin-metahd-item-account .bom-file-list').addClass('hidden');
                                }
                                $(".saved-bom-entries .fileuplloadlimit-error").addClass('hidden');
                                if(vueSelf.savedBomFileData.length === 0) {
                                    $('.no-files-saved').removeClass('hidden');
                                }
                            },
                            error: function (jqXHR, textStatus, errorThrown) {

                            }
                        });
                    }
                }

            });

            callback();
        }

    });

})(Tc.$);