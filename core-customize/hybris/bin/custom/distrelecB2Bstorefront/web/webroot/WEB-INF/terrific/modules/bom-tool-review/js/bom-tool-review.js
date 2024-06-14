(function ($) {

    /**
     * Module implementation.
     *
     * @namespace Tc.Module
     * @class Product-image-gallery
     * @extends Tc.Module
     */
    Tc.Module.BomToolReview = Tc.Module.extend({

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
                $showhidetoggle = $('.showhidetoggle__header-link'),
                $loadfileEdit = $('.loadfile__edit-icon'),
                $loadfileEditItem = $('.loadfile__edit-item'),
                $loadfileSaveItem = $('.loadfile__save-item'),
                $loadfileSave = $('.loadfile__save-item-save'),
                $loadfileSaveClose = $('.loadfile__save-item-close');

            $showhidetoggle.on('click', function(e){
                e.preventDefault();
                $(this).toggleClass('active');
                $(this).parents('.showhidetoggle').find('.showhidetoggle__content').toggleClass('active');
            });

            $loadfileEdit.on('click', function(e){
                e.preventDefault();
                $loadfileEditItem.addClass('hidden');
                $loadfileSaveItem.removeClass('hidden');
            });

            $loadfileSave.on('click', function(e){
                e.preventDefault();
                var $oldFileItem = $('.loadfile__old-filename'),
                    $oldFilename = $oldFileItem.html(),
                    $newFilename = $('.loadfile__new-filename').val().toString();

                $.ajax({
                    url: '/bom-tool/rename-file/'+$oldFilename,
                    type: 'POST',
                    method: 'post',
                    contentType: "text/html; charset=UTF-8",
                    data: $newFilename,
                    success: function (data, textStatus, jqXHR) {
                        $oldFileItem.html(data);
                        $loadfileSaveItem.addClass('hidden');
                        $loadfileEditItem.removeClass('hidden');
                    },
                    error: function (jqXHR, textStatus, errorThrown) {

                    }
                });

            });

            $loadfileSaveClose.on('click', function(e){
                e.preventDefault();
                $loadfileSaveItem.addClass('hidden');
                $loadfileEditItem.removeClass('hidden');
            });

            if ( ('.mod-global-messages .error').length > 0 ) {
                digitalData.page.pageInfo.error.error_page_type = $('.skin-layout-import-tool .mod-global-messages .error').text().trim();
            }

            digitalData.page.pageInfo.bomStep = 3;

            digitalData.page.pageInfo.bomMethod = sessionStorage.getItem("digitalData.page.pageInfo.bomMethod");

            digitalData.page.pageInfo.bomResults = {
                requested: $('#bomResults').data('requested'),
                matched: $('#bomResults').data('matched'),
                alternatives: $('#bomResults').data('alternatives'),
                notFound: $('#bomResults').data('notfound')
            };

            callback();
        }

    });

})(Tc.$);