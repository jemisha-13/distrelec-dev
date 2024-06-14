(function ($) {

	Tc.Module.ShoppinglistMetaActions = Tc.Module.extend({

		init: function ($ctx, sandbox, id) {

			// call base constructor
			this._super($ctx, sandbox, id);

			// subscribe to connector channel/s
			this.sandbox.subscribe('lightboxYesNo', this);
			this.sandbox.subscribe('lightboxShoppinglist', this);
			this.sandbox.subscribe('shoppinglist', this);
			this.sandbox.subscribe('favoritelist', this);
			this.sandbox.subscribe('breadcrumb', this);

			this.onListNameClick = $.proxy(this.onListNameClick, this);

			this.uiTplNewListItem = doT.template(this.$$('#tmpl-shoppinglist-new-list-item').html());
			this.validationErrorEmpty = this.$$('#tmpl-shoppinglist-validation-error-empty').html();
		},

		on: function (callback) {

			//
			// Variables

			this.$editableListItems = this.$$('.editable-list-item');
			this.$listsContainer = this.$$('.shopping-lists');
			this.$newListInputfield = this.$$('.new-list-name');

			//
			// Add new List
			
			this.$$('.add-new-list').on('click', '.add-new-list-button', $.proxy(this.onListAddNewButtonClick, this));
			this.$newListInputfield.on('click', $.proxy(this.onNewListInputfieldClick, this));

			//
			// Event Dispatcher
			
			this.$listsContainer.on('click', '.editable-list-item', $.proxy(this.onListEvent, this));

			callback();
		},



		//////////////////////////////////////////////////////////////////////
		//
		// Add new List

		onListAddNewButtonClick: function (e) {

			Tc.Utils.validate($('.validate-empty', this.$ctx), this.validationErrorEmpty, 'triangle', function(error) {

				if(error) { e.preventDefault(); }

			});
		},

		onNewListInputfieldClick: function() {
			this.hideAllPopovers();
		},


		//////////////////////////////////////////////////////////////////////
		//
		// Event Dispatcher & Events


		onListEvent: function (ev) {

			var self = this;

			if($(ev.target).closest('a').hasClass('btn-list-edit')){
				$.proxy(self.onListEditButtonClick(ev), self);
			}
			else if($(ev.target).closest('a').hasClass('btn-list-delete')){
				$.proxy(self.onListDeleteButtonClick(ev), self);
			}
			else if($(ev.target).hasClass('list-name')){
				$.proxy(self.onListNameClick(ev), self);
			}
		},


			//
			// Edit List

			onListEditButtonClick: function (ev) {

				var  self = this
					,listItem = $(ev.target).closest('li')
					,listName = listItem.find('.list-name')
					,saveButtonText = listName.data('save-button-text')
				;

				ev.preventDefault();

				$.proxy(self.hideAllPopovers(), self);
				$.proxy(self.activateEditable(listItem, listName, saveButtonText), self);
			},


			//
			// Delete List

			onListDeleteButtonClick: function (ev) {
				ev.preventDefault();
				var mod = this
					, $link = $(ev.target).closest('a')
					, listId = $link.data('list-id')
					, lightboxTitle = $link.data('lightbox-title')
					, lightboxMessage = $link.data('lightbox-message')
					, lightboxBtnDeny = $link.data('lightbox-btn-deny')
					, lightboxShowBtnConfirm = $link.data('lightbox-show-confirm-button')
					, lightboxBtnConf = $link.data('lightbox-btn-conf')
					;
				mod.fire(
					'yesNoAction',
					{
						attribute: listId,
						lightboxTitle: lightboxTitle,
						lightboxMessage: lightboxMessage,
						lightboxBtnDeny: lightboxBtnDeny,
						lightboxShowBtnConfirm: lightboxShowBtnConfirm,
						lightboxBtnConf: lightboxBtnConf
					},
					['lightboxYesNo']
				);
			},


			onDialogConfirm: function (data) {
				var mod = this
					, listId = data.attribute
					;
				mod.onListDeleteConfirmClick(listId);
			},

			onListDeleteConfirmClick: function (listId) {
				this.$$('.editable-list-item', this.$ctx).find('.ctrls #form-' + listId).submit();
			},


			//
			// List Name Klick (Disable Link)

			onListNameClick: function (ev) {
				var listItem = $(ev.target).closest('li');
				if (listItem.hasClass('edit-in-progress')) {
					ev.preventDefault();
				}
			},


		//////////////////////////////////////////////////////////////////////
		//
		// Editable Events


		//
		// Activate Editable

		activateEditable: function(listItem, listName, saveButtonText) {

			var self = this;

			listItem.addClass('edit-in-progress');
			listName
				.editable({
					submit: saveButtonText,
					submitBy: 'click',
					onSubmit: $.proxy(self.onListSaveButtonClick, self),
					onCancel: $.proxy(self.onListEditCancel, self)
				})
				// trigger click to open inline edit
				.trigger('click');

			self.toggleLinkEnabled(listItem.find('.shopping-list'));
		},


		//
		// Save Button Click

		onListSaveButtonClick: function (content) {

			var  self = this
				,listNameTag = content.listNameTag
				,listItem = listNameTag.closest('li')
				,listName = listItem.find('.list-name')
				,saveButtonText = listName.data('save-button-text')
			;

			// Restore default state without editing
			listItem.removeClass('edit-in-progress');
			listNameTag.editable('destroy');
			self.toggleLinkEnabled(listItem.find('.shopping-list'));

			// Validate Input
			if(content.listNameTag.text() === '') {

				// Invalid!

				// Restore previous listname
				content.listNameTag.text(content.previous);

				// Show popover
				Tc.Utils.showPopover(content.listNameTag, self.validationErrorEmpty);

				// Restore editable state
				self.activateEditable(listItem, listName, saveButtonText);

			} else {

				// Valid!

				// Hide popover
				Tc.Utils.hidePopover(content.listNameTag);

				// Valid, save it
				self.saveListRename(content);
			}
		},

			//
			// Save List Rename

			saveListRename: function(content) {

				var  mod = this
					,listNameTag = content.listNameTag
					,listId = listNameTag.data('list-id')
					,listName = listNameTag.html()
				;

				// Notify MetaHD of Name Change
				mod.fire('listNameUpdate', { listId: listId, listName: listName }, ['shoppinglist']);

				// Notify Shoppinglist Title and Breadcrumb of Name Change if current list's name was changed
				if (listNameTag.closest('.shopping-list').hasClass('active')) {
					mod.fire('shoppingListNameUpdate', { listId: listId, listName: listName }, ['shoppinglist']);
					mod.fire('breadcrumbActiveItemUpdate', listName, ['breadcrumb']);
				}

				$.ajax({
					url: '/shopping/update',
					dataType: 'json',
					method: 'post',
					data: {
						"listId": listId,
						"listName": listName
					},
					success: function (data, textStatus, jqXHR) {
						mod.onUpdateListDone(data, textStatus, jqXHR);
					}, error: function (jqXHR, textStatus, errorThrown) {
						mod.onUpdateListFail(jqXHR, textStatus, errorThrown);
					}
				});
			},

				// List rename success callback
				onUpdateListDone: function (data, textStatus, jqXHR) {
					if (data.action.status === false) {
						// Error Handling Backend Action failed
					}
				},

				// List rename fail callback
				onUpdateListFail: function (jqXHR, textStatus, errorThrown) {
					// Error Handling Ajax Call
				},



		//
		// Chancel List Renaming

		onListEditCancel: function (content) {

			var listNameTag = content.listNameTag;
			var listItem = listNameTag.closest('li');

			listItem.removeClass('edit-in-progress');
			listNameTag.editable('destroy');

			this.toggleLinkEnabled(listItem.find('.shopping-list'));
			this.hideAllPopovers();
		},


		////////////////////////////////////////////////////////////////////////////
		//
		// Helper


		//
		// Enable / Disable Menu Link

		toggleLinkEnabled: function ($tag) {

			if ($tag.attr('href')) {
				$tag.attr('data-href', $tag.attr('href'));
				$tag.removeAttr('href');
			}
			else {
				$tag.attr('href', $tag.data('href'));
			}
		},


		//
		// Hide all Popovers from Shoppinglists and New List Inputfield

		hideAllPopovers: function() {

			var  $allItems = $('.list-name', this.$editableListItems).add(this.$newListInputfield);

			$.each($allItems, function() {
				Tc.Utils.hidePopover($(this));
			});
		},



		////////////////////////////////////////////////////////////////////////////
		//
		// Listener


		//
		// Favorite List

		// Update Product Count
		onUpdateFavoriteProductCount: function (data) {

			var  $listItemCountTag = this.$$('.favorite-list', this.$ctx).find('.list-item-count')
				,oldListItemCount = parseInt($listItemCountTag.text(), 10)
				,quantityDifference = parseInt(data.quantity, 10)
				,listItemCount = parseInt(data.listItemCount, 10)
			;

			if (isNaN(listItemCount)) {
				$listItemCountTag.text(oldListItemCount + quantityDifference);
			}
			else {
				$listItemCountTag.text(listItemCount);
			}
		},


		//
		// Shoppinglist

		// Add Products
		onAddProductsToExistingShoppingListSuccess: function (data) {

			var mod = this;

			$.each(data.listData, function (index, listItem) {

				var  $listItem = mod.$editableListItems.find('#list-item-' + listItem.listId)
					,$listItemCountTag = $listItem.find('.list-item-count')
				;

				$listItemCountTag.text(listItem.count);
			});
		},

		// Added new List with Products
		onAddedNewListAndProductsSuccess: function (data) {
			// Parse Template
			this.$$('.shopping-lists .add-new-list').before(this.uiTplNewListItem(data));
		},

		// Remove Products
		onRemoveProductsFromShoppingList: function (data) {

			var  $listItem = this.$editableListItems.find('#list-item-' + data.listId)
				,$listItemCountTag = $listItem.find('.list-item-count')
				,oldListItemCount = parseInt($listItemCountTag.text(), 10)
				,quantityDifference = parseInt(data.quantity, 10)
			;

			$listItemCountTag.text(oldListItemCount + quantityDifference);
		}
	});

})(Tc.$);
