(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Category-thumbs
	 * @extends Tc.Module
	 */
	Tc.Module.CategoryThumbs = Tc.Module.extend({

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

            //this.fusionEnabled = true;

			// Do stuff here
			//...
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

            var $itemTitles = $('.title-name', this.$ctx),
                $checkCatLinkCounter = 0;

			$itemTitles.each(function(index, el) {
				var $el = $(el);

                var elInnerHeight = $el.innerHeight(),
                    computedLineheight = Number(getComputedStyle($el[0]).lineHeight.replace('px', ''));

                if(computedLineheight < elInnerHeight) {    //text is wrapped
                    $el.addClass('wrapped-text');
                }
			});

            $('.accordion__trigger a').on('click', function(e) {
                e.preventDefault();

                var triggerDOM = $(this),
                    subCatListLen = $('.sub-category__content .sub-category__content-tile').length;

                $checkCatLinkCounter++;

                if(triggerDOM.hasClass('closed')) {
                    triggerDOM.closest('.accordion__trigger').next('.accordion__content').addClass('content-open');
                    triggerDOM.removeClass('closed').addClass('open');
                    triggerDOM.find('.accordion__trigger-label').html(triggerDOM.data('hide-message'));
                    triggerDOM.attr('data-link-text','hide list view');

                    if ( $(window).width() < 992) {
                        $("html, body").animate({ scrollTop: $("#sub-category").offset().top - 200 }, "fast");
                    } else {
                        $("html, body").animate({ scrollTop: $("#sub-category").offset().top - 120 }, "fast");
					}

                } else if(triggerDOM.hasClass('open')) {
                    triggerDOM.removeClass('open').addClass('closed');
                    triggerDOM.closest('.accordion__trigger').next('.accordion__content').removeClass('content-open');
                    triggerDOM.find('.accordion__trigger-label').html(triggerDOM.data('show-message'));
                    triggerDOM.attr('data-link-text', 'show list view');
                }

				if ( subCatListLen < 2) {
                    self.checkSubcategories();
				}





			});

            $('.page__title--back-naviagtion').on('click', function(e) {
                e.preventDefault();
                window.history.back();
            });

			callback();
		},

		checkSubcategories: function () {
			var isOCI = $('.sub-category').data('isoci'),
            	catUrl = digitalData.page.pageInfo.pageUrl.toString().split('?')[0],
            	ajaxUrl=  catUrl +'/subCategory',
                $subCategoryContent = $('.sub-category__content'),
                $catJsTemplate = $('.category-js-template');

			if (isOCI ) {

                $.ajax({
                    url: ajaxUrl,
                    type: 'post',
                    success: function (data, textStatus, jqXHR) {
                        var categoriesData = data;
                        $('.sub-category .ajax-product-loader').hide();

                        categoriesData.sort(function(a, b) {
                            return a.name.localeCompare(b.name);
                        });

                        $.each(categoriesData, function(order, category) {
                            var $category = $catJsTemplate.clone();
                            $category.attr('id',category.code);
                            $category.find('.title-name').html(category.name);
                            $category.find('.sub-category__content-tile__title').attr('href',category.url);
                            $category.find('.sub-category__content-tile__title').attr('title',category.name);
                            $category.find('.title-image').attr('src',category.image.portrait_small.url);

                            var $subCategoryLinks =  $category.find('.sub-category__content-tile__links'),
                                $subCatJsTemplate =  $category.find('.subcategory-js-template');

                            if ( category.subcategoryDisplayDataList.length > 0) {

                                $.each(category.subcategoryDisplayDataList, function(order, subcategory) {

                                        var $subCategory = $subCatJsTemplate.clone();
                                        $subCategory.attr('id',subcategory.code);
                                        $subCategory.attr('title',subcategory.name);
                                        $subCategory.attr('href',subcategory.url);
                                        $subCategory.find('.title-name').html(subcategory.name);
                                        $subCategory.find('.title-count').html(subcategory.count);

                                        $subCategory.removeClass('subcategory-js-template');
                                        $subCategoryLinks.append($subCategory);

                                });


                            } else {

                                if (category.count > 0) {

                                    var $subCategory = $subCatJsTemplate.clone();
                                    $subCategory.attr('id',category.code);
                                    $subCategory.attr('title',category.name);
                                    $subCategory.attr('href',category.url);
                                    $subCategory.find('.title-name').html(category.name);
                                    $subCategory.find('.title-count').html(category.count);

                                    $subCategory.removeClass('subcategory-js-template');
                                    $subCategoryLinks.append($subCategory);

                                } else {
                                    $category.remove();
                                }

                            }

                            $category.removeClass('category-js-template');
                            $category.removeClass('hidden');

                            $subCategoryContent.append($category);
                            $subCatJsTemplate.remove();
                            
                        });

                    },
                    error: function (jqXHR, textStatus, errorThrown) {

                    }
                });

			} else {
                var categoryDataVm = new Vue({
                    el: '.sub-category__content-tile-vm',
                    data: {
                        categoriesData: []
                    },
                    created: function () {
                        var self = this;

                        axios
                            .get(ajaxUrl)
                            .then(function (response) {
                                // when fusion enabled, response format is slightly different
                                self.categoriesData = response.data;
                                self.categoriesData.sort(function(a, b) {
                                    return a.name.localeCompare(b.name);
                                });

                                $('.sub-category .ajax-product-loader').hide();
                            });

                    },
                    methods: {
                        mapFusion: function (data) {
                            // fusion field mappings
                            var dict = {
                                name: 'name',
                                url: 'url',
                                subcategoryDisplayDataList: 'subcategoryDisplayDataList',
                                image: 'image',
                                count: 'count'
                            };

                            var output = [];

                            // loop over categories and convert them to match the structure of the current data
                            for(var category in data) {
                                var cat = {};

                                cat.name = category[dict.name];
                                cat.image = category[dict.image];
                                cat.url = category[dict.url];
                                cat.subcategoryDisplayDataList = category[dict.subcategoryDisplayDataList];

                                // where sub categories are present loop over them to format data structure
                                if(cat.subcategoryDisplayDataList.length > 0) {
                                    var formattedSubCats = [];

                                    for(var subcategory in cat[dict.subcategoryDisplayDataList]) {
                                        var subcat = {};

                                        subcat.name = subcategory[dict.name];
                                        subcat.count = subcategory[dict.count];
                                        subcat.url = subcategory[dict.url];

                                        formattedSubCats.push(subcat);
                                    }

                                    if(formattedSubCats.length > 0) {
                                        cat.subcategoryDisplayDataList = formattedSubCats;
                                    }
                                }

                                output.push(cat);
                            }

                            return output;
                        }
                    }

                });
			}

        },


		/**
		 * Hook function to trigger your events.
		 *
		 * @method after
		 * @return void
		 */
		after: function() {
			// Do stuff here or remove after method
			//...
		}

	});

})(Tc.$);
