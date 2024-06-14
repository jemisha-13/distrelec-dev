(function ($) {

	Tc.Module.MetahdItem.Categories = function (parent) {


		this.on = function (callback) {
			var self = this
				,$ctx = this.$ctx;

			this.$checkboxes = this.$$('input[type=checkbox]');
			this.$searchAll = this.$$('.js-all-categories');
			this.$categoryCheckboxes = this.$$('.js-category');

			this.sandbox.subscribe('metahdSearch', this);

			this.getSearchValues = $.proxy(this, 'getSearchValues');
			this.collectSelectedCategories = $.proxy(this, 'collectSelectedCategories');
			this.initCategoryDropdown = $.proxy(this, 'initCategoryDropdown');

			this.initCategoryDropdown();

			// remove selected categories
			self.$searchAll.on('click', function() {
				$.each(self.$categoryCheckboxes, function() {
					if ($(this).is(':checked')) {
						$(this).prop('checked', false);
					}
				});
			});

			// checkboxes click handler
			self.$checkboxes.on('click', function() {

				var checkedCount = 0;

				// loop checkbox group, count :checked
				$.each(self.$categoryCheckboxes, function() {
					if ($(this).is(':checked')) {
						checkedCount += 1;
					}
					else{
						$( 'input[value="'+$(this)[0].id+'"]').val("");
					}
				});

				// do search in categories
				if (checkedCount >= 1) {
					self.getSearchValues();
				} else {
					$('.label').removeClass('selected').addClass('all');
					$('.js-all-categories').prop('checked', true);
					$('.all').removeClass('hidden');
					$('.selected-categories').addClass('hidden');
				}

			});

			parent.on(callback);
		};

		this.initCategoryDropdown = function() {
			var self = this
				,$ctx = this.$ctx
				,checkedCount = 0
				,selectedCategories = []
				,categoryNames = [];

			// loop checkbox group, count :checked
			$.each(self.$checkboxes, function() {
				if ($(this).is(':checked')) {
					checkedCount += 1;
				}
			});

			if (checkedCount === 0) {
				self.$searchAll.prop('checked', true);
				self.$$('.all').removeClass('hidden');
			} else {
				self.collectSelectedCategories(selectedCategories, categoryNames);
			}

		};

		this.getSearchValues = function () {
			var $ctx = this.$ctx
				,self = this
				,selectedCategories = []
				,categoryNames = [];

			self.collectSelectedCategories(selectedCategories, categoryNames);

			if(selectedCategories.length > 0) {
				// fire it to metahdsearch
				self.fire('categoryRestrictionsSelected', { categories: categoryNames }, ['metahdSearch']);
			}
		};

		this.collectSelectedCategories = function(selectedCategories, categoryNames) {
			var $ctx = this.$ctx,
				self = this;

			self.$categoryCheckboxes.each(function () {
				var $checkbox = $(this);

				if ($checkbox.prop('checked')) {
					var filterstring = $checkbox.val();
					var categoryName = $checkbox.attr('id');

					selectedCategories.push(filterstring);
					categoryNames.push(categoryName);
				}
			});

			this.$$('.all').addClass('hidden');
			this.$$('.label').removeClass('all').addClass('selected');
			this.$$('.selected-categories').removeClass('hidden');
			this.$$('.js-all-categories').prop('checked', false);

			this.$$('.count').empty().append(selectedCategories.length);
			this.$$('.category-names').empty().append(categoryNames.join([separator = ', ']));
		};
	};

})(Tc.$);
