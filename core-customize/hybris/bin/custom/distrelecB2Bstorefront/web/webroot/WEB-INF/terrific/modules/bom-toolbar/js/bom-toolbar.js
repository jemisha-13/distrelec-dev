(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class BomToolbar
	 * @extends Tc.Module
	 */
	Tc.Module.BomToolbar = Tc.Module.extend({

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

			this.hasQuantityValidationError = false;
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {

			// subscribe to connector channel/s
			this.sandbox.subscribe('cartlistBulkAction', this);

			var self = this
				, $ctx = this.$ctx
				, $btnShopping = $('.btn-add-shopping', $ctx)
				, $btnCart = $('.btn-add-cart', $ctx)
                , $btnViewCart = $('.btn-view-cart', $ctx)
				, $btnBack = $('.btn-back', $ctx)
                , $btnSaveBomFile = $('.btn-bom-savefile', $ctx)
                , $iptReferenceSaveFile = $('.bom-product__reference')
                , $btnUpdateBomFile = $('.btn-bom-updatefile', $ctx)
                , $btnContinue = $('.btn-continue', $ctx)
				, $numeric = $('.numeric')
                , $savedBomFileName = ''
                , $noAlternativeSelectedModal = $('#noAlternativeSelectedModal', $ctx);


            $iptReferenceSaveFile.on('keyup', function() {
                $btnUpdateBomFile.removeClass('disabled').removeAttr('disabled','disabled');
            });

            $('.bom-product__numeric-stepper button').on('click', function() {
                $btnUpdateBomFile.removeClass('disabled').removeAttr('disabled','disabled');
            });

            $(".bom-product__numeric-stepper input").on('keyup', function() {
                $btnUpdateBomFile.removeClass('disabled').removeAttr('disabled','disabled');
            });

            $btnSaveBomFile.on('click', function() {
                self.saveUpdateAddBomFile('save', 'POST', '/bom-tool/save-file');
            });

            $btnUpdateBomFile.on('click', function() {
                self.saveUpdateAddBomFile('update', 'PUT', '/bom-tool/edit-file');
                $btnUpdateBomFile.addClass('disabled').attr('disabled','disabled');
            });

			$btnShopping.on('click', function() {
				if(!$btnShopping.hasClass('disabled')){
					if(!self.hasQuantityValidationError){
						$(this).addClass('active');
						self.fire('addAllProductsToShoppingList', {}, ['cartlistBulkAction']);
					}
					return false;
				}
			});

			$btnCart.on('click', function() {
                var $alternativeProduct = $('.bom-unavailable-product__alternatives-selection').length
					, $mpnProduct = $('.bom-unavailable-product__alternatives-selection').length
					, $alternativeLength = $alternativeProduct + $mpnProduct
					, $alternativeProductSelected = $('.bom-unavailable-product__alternatives-selection.selected .skin-product-bom.active').length
					, $mpnProductSelected = $('.bom-mpn-product__alternatives-selection.selected .skin-product-bom.active').length
					, $totalSelected = $alternativeProductSelected + $mpnProductSelected;

                if ( $alternativeLength > 0 && $totalSelected < 1) {
                    var modText = $('#noAlternativeSelectedModal .modal__description');
                    replacedText = modText.html().replace('{0}', modText.data('alternativecount') );
                    modText.html(replacedText);
                    $noAlternativeSelectedModal.modal('show');
				} else {
                    self.saveUpdateAddBomFile('add', 'PUT', '/bom-tool/cart-timestamp');
                    initForCart();
				}

				return false;
			});

            $btnContinue.on('click', function() {
				initForCart();
            });

            function initForCart () {

                if( $noAlternativeSelectedModal.hasClass('in') ) {
                	$noAlternativeSelectedModal.modal('hide');
                }

                self.checkProductValidation();

            }

            $btnViewCart.on('click', function() {
                window.location.href = window.location.protocol + '//' + window.location.host + '/cart';
            });

			$btnBack.on('click', function() {
				var filename = '';

				if(window.location.href.indexOf("bom-tool/review") > -1) {
					filename = window.location.href.toString().split('bom-tool/')[1];
				}

				switch (filename) {
					case 'review':
						window.history.back();
						break;
					case 'review-file':
						window.location.href = "/bom-tool/matching";
						break;
					default:
						window.location.href = "/bom-tool/matching";

				}

			});

			callback();
		},

        saveUpdateAddBomFile: function (_mode, callType, callUrl) {

            var jsonObj = []
                , productList = {}
            	, $mode = _mode
                , $callType = callType
                , $callUrl = callUrl;

            productList.fileName = $('#bomfileName').val();
            productList.customerId = $('#bomcustomerId').val();
            productList.entry = [];

            if ( $mode === 'update' || $mode === 'add') {

            	if (self.$savedBomFileName !== undefined) {
                    productList.fileName = self.$savedBomFileName;
				}
            }

            var $allMatchedBomProducts = $('.skin-productlist-bom .skin-product-bom'),
                $allUnMatchedBomProducts = $('.hiddenNotMathchedProducts'),
                $allUnavailableBomProducts = $('.hiddenUnavailableProducts'),
                $allDuplicateMpnBomProducts = $('.hiddenDuplicateMpnProducts');


            $.each( $allMatchedBomProducts, function (index, item) {

                var  $entryObj = {};

                $entryObj.code = $(item).find('.hidden-product-code').val() ;
                $entryObj.customerReference = $(item).find('.bom-product__reference').val() ;
                $entryObj.productCode = $(item).find('.hidden-product-code').val() ;
                $entryObj.quantity = $(item).find('.bom-product__numeric-stepper .ipt').val() ;

                productList.entry.push($entryObj);

            });

            $.each( $allUnMatchedBomProducts, function (index, item) {

                var $entryObj = {};

                $entryObj.code = $(item).data('code') ;
                $entryObj.customerReference = $(item).data('customerreference') ;
                $entryObj.productCode = '' ;
                $entryObj.quantity = $(item).data('quantity') ;

                productList.entry.push($entryObj);

            });

            $.each( $allUnavailableBomProducts, function (index, item) {

                var  $entryObj = {};

                $entryObj.code = $(item).data('code') ;
                $entryObj.customerReference = $(item).data('customerreference') ;
                $entryObj.productCode = '' ;
                $entryObj.quantity = $(item).data('quantity') ;

                productList.entry.push($entryObj);

            });

            $.each( $allDuplicateMpnBomProducts, function (index, item) {

                var  $entryObj = {};

                $entryObj.code = $(item).data('code') ;
                $entryObj.customerReference = $(item).data('customerreference') ;
                $entryObj.productCode = '' ;
                $entryObj.quantity = $(item).data('quantity') ;

                productList.entry.push($entryObj);

            });

            $.ajax({
                url: $callUrl,
                type: $callType,
                dataType: 'json',
                contentType: 'application/json',
                method: $callType,
                data: JSON.stringify(productList),
                success: function (data) {

                    if ( $mode === 'save') {
                    	var $fileSize = parseInt( $('.bom-file-list-size').html() );
                        $savedBomFileName = data;
                        $('.btn-bom-savefile').addClass('hidden');
                        $('.btn-bom-updatefile').removeClass('hidden').addClass('disabled').attr('disabled','disabled');
                        $('.skin-metahd-item-account .bom-file-list').removeClass('hidden');
                        $('.fileuplload-save').removeClass('hidden');

                        if ( $fileSize < 1 ) {
                            $('.skin-metahd-item-account .bom-file-list-size').html(1);
						}

                        setTimeout(function(){
                            $('.fileuplload-save').addClass('hidden');
                        }, 3000);

                    } else if ($mode === 'update') {
                        $('.fileuplload-update').removeClass('hidden');
                        setTimeout(function(){
                            $('.fileuplload-update').addClass('hidden');
                        }, 3000);
                    }
                },
                error: function (jqXHR, textStatus) {

                    if ( $mode == 'save') {
                        if ( jqXHR.status === 429) {
                            $(".mod-bom-toolbar .fileuplloadlimit-error").removeClass('hidden');
                        }
                    }

                }

            });

        },

        checkProductValidation: function () {
			var	self = this,
				$allBomProducts = $('.skin-product-bom.active'),
				isQuantiryError = false,
				$errorContainer= $('.skin-global-messages-component.bom'),
				$errorContent= $('.skin-global-messages-component.bom .messages-component__label'),
				$errorMessage = '';

			$.each($allBomProducts, function (index, item) {

				var $statuscode =  $(item).find('.bom-product').data('status-code');

				if ( $(item).find('.numeric').hasClass('numeric-error') ) {
					isQuantiryError = true;
					$(item).addClass('error');

					var message = $(item).find('.popover-content').html();
					$errorMessage = $errorMessage + message +'</br>';

				} else {
					$(item).removeClass('error');
				}

			});

			if (isQuantiryError) {
				$($errorContent).html( $errorMessage );
				$errorContainer.removeClass('hidden');
			} else {
				$errorContainer.addClass('hidden');

                $('.btn-add-cart').addClass('active');

                if(!self.hasQuantityValidationError) {
                    self.fire('addAllProductsToCart', {}, ['cartlistBulkAction']);
                }

			}

			return isQuantiryError;

		}

	});

})(Tc.$);
