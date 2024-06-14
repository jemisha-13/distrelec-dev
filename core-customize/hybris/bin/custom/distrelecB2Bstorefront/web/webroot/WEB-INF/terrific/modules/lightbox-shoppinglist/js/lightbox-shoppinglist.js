(function ($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Lightboxshoppinglist
	 * @extends Tc.Module
	 */
	Tc.Module.LightboxShoppinglist = Tc.Module.extend({

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
			this.sandbox.subscribe('lightboxShoppinglist', this);
			this.sandbox.subscribe('lightboxLoginRequired', this);

			// set module variables
			this.$modal = $('.modal', $ctx);
			this.$btnConfirm = $('.btn-send', $ctx);
			this.$checkboxGroup = $('.checkbox-group', $ctx);
			this.$inputfield = $('.new-shopping-list-input', $ctx);

			// Checkbox Group Template and Target
			this.tmplListItem = doT.template($('#tmpl-lightbox-shoppinglist-list-item', $ctx).html());
			this.$targetTmplItems = $('.target-items', $ctx);

			// Validation Errors
			this.validationErrorCheckboxGroup = this.$$('#tmpl-lightbox-shoppinglist-validation-error-checkboxgroup').html();

			// box
			this.$box = {
				error: this.$modal.find('.error-box'),
				receive: this.$modal.find('.receive-box'),
				start: this.$modal.find('.start-box')
			};

            this.addedProducts = [];
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function (callback) {

			var self = this;

			// Bind click Save Button
			this.$btnConfirm.off('click.lightboxshoppinglist').on('click.lightboxshoppinglist', function () {
				self.validateAndSave();
			});

			callback();
		},

		/**
		 *
		 * @method showPopupOrChkNewSave
		 *
		 */
		validateAndSave: function () {
			var self = this;

			Tc.Utils.validate($('.validate-checkbox-group', self.$ctx), self.validationErrorCheckboxGroup, 'triangle', function(error) {

				// Don't show errors before we know that the new list field is also empty
				$('.field-msgs', this.$ctx).hide();

				if(error === false) {
					self.sendForm('existingList');
				} else {
					Tc.Utils.validate($('.validate-empty', self.$ctx), self.validationErrorEmpty, 'triangle', function(error) {
						if(error === false) {
							self.sendForm('newList');
						} else {
							$('.field-msgs', this.$ctx).show();
						}
					});
					// Recalculate modal height
					Tc.Utils.calculateModalHeight(self.$modal);
				}
			});
		},

		/**
		 *
		 * @method cleanUpForm
		 *
		 */
		cleanUpForm: function () {
			this.$checkboxGroupBoxes.prop('checked', false);
			this.$inputfield.val('');
			$('.field-msgs', this.$ctx).remove();
		},


		/**
		 *
		 * @method getShoppingLists
		 *
		 * get json data from servlet:
		 * hybris/bin/custom/distrelecB2Bstorefront/web/webroot/WEB-INF/views/desktop/fragments/terrific/wishlist/shoppingPopup.jsp
		 *
		 * @param callback
		 */
		getShoppingLists: function (callback) {
			var self = this;

			$.ajax({
				url: '/shopping/popup',
				type: 'get',
				dataType: 'json',
				success: function (data) {
					//
					// add to dot template
					//
					self.$targetTmplItems.html(self.tmplListItem(data));

					callback();
				},
				error: function () { }
			});
		},

		/**
		 *
		 * @method onCheckUserLoggedIn
		 *
		 * @param data
		 */
		onCheckUserLoggedIn: function (data) {

			var self = this,
				$usersettings = $('.usersettings', self.$backendData);

			self.productCodesArray = data.productCodesArray;
			self.productQuantityArray = data.productQuantityArray;
			self.productReferenceArray = data.productReferenceArray;

			if ($usersettings.length > '0' && $usersettings.data('login').toString() === 'true') {
				self.openModal();
			} else {
				self.fire('openModal', ['lightboxLoginRequired']);
			}
		},

		/**
		 *
		 * @method onLoginSuccessful
		 *
		 */
		onLoginSuccessful: function () {
			this.openModal();
		},

		/**
		 *
		 * @method openModal
		 *
		 */
		openModal: function () {
			var self = this;

			this.getShoppingLists(function () {
				self.$modal.modal();
				Tc.Utils.calculateModalHeight(self.$modal);

				self.$checkboxGroupBoxes = self.$checkboxGroup.find('input[type=checkbox]');

				// clean up previous errors and selections
				self.cleanUpForm();
			});
		},

		/**
		 *
		 * @method hideModal
		 *
		 */
		hideModal: function () {
			this.$modal.modal('hide');
		},

		/**
		 *
		 * @method gatherListIds
		 *
		 * @returns {{add: Array, remove: Array}}
		 */
		gatherListIds: function () {
			var self = this,
				listIdArray = [],
				$chk = self.$modal.find('.checkbox-group').find('input[type=checkbox]');

			$chk.each(function () {
				var $chkItem = $(this),
					listId = $chkItem.attr('name');

				if ($chkItem.is(':checked')) {
					if (listId !== '' && listId !== undefined && listId !== 'undefined') {
						listIdArray.push(listId);
					}
				}
			});

			return listIdArray;
		},

		/**
		 *
		 * @method sendForm
		 *
		 */
		sendForm: function (addTo) {
			var self = this
				, listIdArray = self.gatherListIds()
				,productCodeQuantityArrayJson = "{"
			;

			var productCodesArray = []
				,productQuantityArray = []
				,productReferenceArray = []
				,hasValidationError = false
			;
			
			
			// Create Json with product codes and quantity
			$.each(self.productCodesArray, function(index, product) {
				
				productCodesArray[index] = product;
				productQuantityArray[index] = self.productQuantityArray[index];
				productReferenceArray[index] = self.productReferenceArray[index];
				
				if( productReferenceArray[index] === undefined){
					productReferenceArray[index] = "";
				}
								
				if(index > 0){
					productCodeQuantityArrayJson += ',';
				}

				// build shoppinglistAPI productCode, quantity and references json
				productCodeQuantityArrayJson += '"' + self.productCodesArray[index] + '":{"qty":' + 
					self.productQuantityArray[index] + ',"ref":"' + 
					productReferenceArray[index]+ '"}';
			});
			productCodeQuantityArrayJson += "}";

			self.addedProducts = self.productCodesArray;

			self.$btnConfirm.attr('disabled', 'disabled');

			// Add to new list
			if (addTo === 'newList') {
				self.saveNew(productCodeQuantityArrayJson, self.productCodesArray);
				$('.shopping-lists-size').text(parseInt($('.shopping-lists-size').text()) + 1);
				
			}

			// Add to existing list
			if (addTo === 'existingList') {

				self.saveExisting(listIdArray, productCodeQuantityArrayJson, function (data) {

					if (data != "undefined") {
						self.savedListSuccessful(data, self.productCodesArray);
					}
				});
			}

		},

		/**
		 *
		 * @method saveExisting
		 *
		 * @param listIdArray
		 * @param productCodes
		 */
		saveExisting: function (listIdArray, productCodeQuantityArrayJson, callback) {
			var self = this;

			if (listIdArray !== undefined && listIdArray.length > 0 && productCodeQuantityArrayJson !== undefined) {
				$.ajax({
					url: '/shopping/add',
					type: 'post',
					data: {
						listIds: listIdArray,
						productsJson: productCodeQuantityArrayJson
					},
					success: function (data, textStatus, jqXHR) {
                        self.onShoppingListAddedSuccess('saveExisting',productCodeQuantityArrayJson);
						callback(data);

						for (var firstKey in JSON.parse(productCodeQuantityArrayJson)) break;

						window.dataLayer.push({
							event: 'addToShoppingList',
							shoppingListProduct: firstKey
						});
					},
					error: function () {
						self.savedListError();
					}
				});
			}
			else {
				callback("undefined");
			}
		},

		/**
		 *
		 * @method saveNew
		 *
		 */
		saveNew: function (productCodeQuantityArrayJson, productCodesArray) {
			var self = this
				,$ctx = this.$ctx
				,listName = self.$inputfield.val()
			;

			if (productCodeQuantityArrayJson !== undefined) {
				$.ajax({
					url: '/shopping/new/add',
					type: 'post',
					data: {
						listName: listName,
						productsJson: productCodeQuantityArrayJson
					},
					success: function (data, textStatus, jqXHR) {
						self.addedListSuccessful(data, listName, productCodesArray);
                        self.onShoppingListAddedSuccess('saveNew',productCodeQuantityArrayJson);
					},
					error: function () {
						self.savedListError();
					}
				});
			}
		},

		/**
		 *
		 * @method savedListSuccessful
		 *
		 */
		savedListSuccessful: function (data, productCodesArray) {
			var self = this;

			self.$btnConfirm.removeAttr('disabled');
			self.hideModal();

			// Trigger MetaHD to show popover
			$(document).trigger('listsChange', {
				type: 'list',
				quantity: productCodesArray.length
			});

			// Event is needed for shopping and favorite list page
			this.fire('addProductsToExistingShoppingListSuccess', { listData: data.listData, productCodes: productCodesArray, listCount: productCodesArray.length }, ['lightboxShoppinglist']);
		},

		/**
		 *
		 * @method addedListSuccessful
		 *
		 */
		addedListSuccessful: function (data, listName, productCodesArray) {
			var self = this;

			self.$btnConfirm.removeAttr('disabled');
			self.hideModal();

			// Trigger MetaHD to show popover
			$(document).trigger('listsChange', {
				type: 'list',
				quantity: productCodesArray.length
			});

			// update metaHD and Shopping List Page
			this.fire('addedNewListAndProductsSuccess', { listId: data.newListId, listName: Tc.Utils.escapeHtml(listName), productCodes: productCodesArray, listCount: productCodesArray.length }, ['lightboxShoppinglist']);
		},

		/**
		 *
		 * @method savedListError
		 *
		 */
		savedListError: function () {
			var self = this;

			self.$btnConfirm.removeAttr('disabled');
			self.$box.error.css('display', 'block');
			self.$box.receive.css('display', 'none');
			self.$box.start.css('display', 'none');
			self.$btnConfirm.on('click', function () {
				self.hideModal();
			});
		},

		/**
		 *
		 * @method onLightboxConfirm - keystroke extension
		 *
		 */
		onLightboxConfirm: function () {
			var namespace = ".lightboxshoppinglist";
			Tc.Utils.lightboxConfirm(this, namespace);
		},

		onShoppingListAddedSuccess: function (_label,_data) {
            var self = this
				, $bomAddSelectedToShopping = $('.mod-bom-toolbar .btn-add-shopping')
                , $isBomtool = ( $('.mod-bom-toolbar .btn-add-shopping').length > 0 ) ? true : false
                , $btnBomAddCart = $('.mod-bom-toolbar .btn-add-cart')
                , $btnBomViewCart = $('.mod-bom-toolbar .btn-view-cart')
				, $addedProducts = self.addedProducts
                , $btnSaveAllProductToShoppingList = $('.mod-bom-toolbar .btn-add-shopping')
                , $btnSaveAllProductToCartList = $('.mod-bom-toolbar .btn-add-cart')
                , $allProductSelection = $('.bom-product-controllbar__select-all input');

            if ($isBomtool) {

				$.each($addedProducts, function (index,item) {
					var productId = item.code,
						$bomProduct = $('.bom-product--'+$addedProducts[index]);

                    $bomProduct.find('.ico-shopping').addClass('active');
					$bomProduct.parents('.skin-product-bom').removeClass('active');
                    $bomAddSelectedToShopping.removeClass('active');
				});

                $btnSaveAllProductToShoppingList.attr('disabled', 'disabled');
                $btnSaveAllProductToCartList.attr('disabled', 'disabled');
                $allProductSelection.prop( "checked", false );
                $allProductSelection.parent().removeClass('active');

            }

        }


	});

})(Tc.$);


