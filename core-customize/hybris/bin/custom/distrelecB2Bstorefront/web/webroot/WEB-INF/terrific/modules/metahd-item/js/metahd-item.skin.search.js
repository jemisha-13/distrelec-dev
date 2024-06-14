(function ($) {

	/**
	 * Product Skin Bom implementation for the module Productlist Order.
	 *
	 */
	Tc.Module.MetahdItem.Search = function (parent) {


		this.on = function (callback) {

			this.isEmptySearch = false;
			this.categoryRestrictions = '';

			this.onDocumentClick = $.proxy(this, 'onDocumentClick');
			this.onKeyPress = $.proxy(this, 'onKeyPress');
			this.onCategoryRestrictionsSelected = $.proxy(this, 'onCategoryRestrictionsSelected');

			this.sandbox.subscribe('metahdSearch', this);
			this.$searchInput = this.$$('#metahd-search');
			this.typeahead_uri = this.$searchInput.data('typeahead-uri');
			this.typeahead_channel = this.$searchInput.data('typeahead-channel');
			this.typeahead_minlength = this.$searchInput.data('typeahead-minlength');

			// store if fusion search is enabled
			this.searchExperience = $('#backenddata .shopsettings').data('search-experience');
			this.fusionSearchUrl = $('#backenddata .shopsettings').data('search-fusionurl');
			this.fusionSuffix = $('#backenddata .shopsettings').data('search-collection-suffix');
			this.fusionApiKey = $('#backenddata .shopsettings').data('search-fusionapikey');

			this.currentLanguage = $('#backenddata .shopsettings').data('language');
			this.$searchForm = this.$$('.searchForm');

            var $ctx = this.$ctx,
				self = this,
				$searchInput = this.$searchInput,
				$searchForm = this.$searchForm,
				$buttonSearch = this.$buttonSearch,
            	$currentSelectedRowIndex = 0;

			// array to store and cancel ongoing ajax calls
			this.jXhrPool = [];
			this.jXhrPoolId = 0;

			// Events
			$ctx.hover(
				function() {
					$ctx.addClass('on-hover');
				},
				function() {
					$ctx.removeClass('on-hover');
				}
			);
			$searchInput.on({
				'focus': function() {
					$ctx.addClass('active');
					$(document).off('click.search-blur').on('click.search-blur', self.onDocumentClick); // can't use .one because it fires immediately. :(
					$(document).off('keyup.search-esc').on('keyup.search-esc', self.onKeyPress); // can't use .one because it fires immediately. :(
				},
				'blur': function() {
					$ctx.removeClass('active');
				}
			});

            function removeSuggest(){
                $('.overlay-suggest').removeClass('active');
            }

            var defaultSelectedValue = sessionStorage.getItem('ls-metahd-select__value'); // Gets saved category value
            selectedValue  =  defaultSelectedValue; // Sets Selected value on page load

            $('#selected-cat-field').val(selectedValue);

            $('body').on('click','.overlay-suggest',function(){
                removeSuggest();
            });

            // Place selected category option into span
            $('select.metahd-select').on('change', function (e){
                selectedValue  =  $(this).find('option:selected').val();

                if((selectedValue === '')){
                    $('.metahd-select').attr( { name:"" } );
                } else {
                    $('.metahd-select').attr( { name:"filter_categoryCodePathROOT" } );
                }

                var selectedText =  $(this).find('option:selected').text();

                $('.select-holder__text').html(selectedText);
                $('#selected-cat-field').val(selectedValue);


                sessionStorage.setItem('ls-metahd-select__value', selectedValue);
                sessionStorage.setItem('ls-metahd-select__text', selectedText);
            });

            $('.mod-logo').on('click', function (e){
                $('.metahd-select').attr( { name:"" } );
                sessionStorage.removeItem('ls-metahd-select__value');
                sessionStorage.removeItem('ls-metahd-select__text');
            });

            if( (defaultSelectedValue === '') || ( defaultSelectedValue === null) ){
                $('.metahd-select').attr( { name:"" } );
                $('#selected-cat-field').val('');
            }

			// Check if the current page is the homepage and add autofocus to search input
			if(digitalData.page.pageInfo.pageName === "homepage") {
				$('.js-page-load-focus input').focus();
			}

			$('#ensCloseBanner').on('click', function () {
			    if(digitalData.page.pageInfo.pageName === "homepage") {
                    $('.js-page-load-focus input').focus();
                }
            });

            $('.btn-close-signup').on('click', function () {
            	if(digitalData.page.pageInfo.pageName === "homepage") {
                    $('.js-page-load-focus input').focus();
                }
            });

            // After page is redirected sets selected option for category selected
            var selectedCat = defaultSelectedValue;
            $('.metahd-select').val(selectedCat);

            if (typeof(Storage) !== "undefined") {
                var selectedCatName = sessionStorage.getItem('ls-metahd-select__text');
                var selectedCatValue = sessionStorage.getItem('ls-metahd-select__value');
                
                if ( selectedCatName !== null) {
                    $('.select-holder__text').html(selectedCatName);
				}

                if ( selectedCatValue === '') {
                    $('.metahd-select option:first').attr('selected','selected');
                } else {
                    $('.metahd-select option[value='+selectedCatValue+']').attr('selected','selected');
				}

            }  else {
                $('.metahd-select option:first').attr('selected','selected');
            }

            document.onkeydown = function(e) {
            	if ( $('.suggest-section .js-results').length > 0 	) {
                    switch (e.keyCode) {
                        case 37:
                            self.tabindexSearchResult('left');
                            break;
                        case 38:
                            self.tabindexSearchResult('up');
                            break;
                        case 39:
                            self.tabindexSearchResult('right');
                            break;
                        case 40:
                            self.tabindexSearchResult('down');
                            break;
                    }
				}
            };



            this.tabindexSearchResult = function (_dir) {
            	var $searchResultRow = $('#suggest-target .suggest-section .js-results .suggest-row');
                var $searchResultSection = $('#suggest-target .suggest-section');
            	var $searchLeftSection = $('#suggest-target .left .suggest-section');
                var $searchRightSection = $('#suggest-target .right .suggest-section');

                $searchResultRow.removeClass('selected');

                if ( _dir === 'up') {
                	if ( $currentSelectedRowIndex > 0) {
                        $currentSelectedRowIndex--;
					} else {
                        $currentSelectedRowIndex = $searchResultRow.length;
					}
				} else if ( _dir === 'down' ) {
                    if ( $currentSelectedRowIndex < $searchResultRow.length) {
                        $currentSelectedRowIndex++;
                    } else {
                        $currentSelectedRowIndex = 0;
					}
				} else if ( _dir === 'left' ) {
                    $currentSelectedRowIndex = $searchLeftSection.find('.suggest-row').eq(0).addClass('selected').attr('data-rowindex');

                } else if ( _dir === 'right' ) {
                    $currentSelectedRowIndex = $searchRightSection.find('.suggest-row').eq(0).addClass('selected').attr('data-rowindex');
                }

                $searchResultSection.find('.data-row-'+$currentSelectedRowIndex).addClass('selected');
                $searchResultSection.find('.data-row-'+$currentSelectedRowIndex).focus();
            };

			$searchInput.typeahead({
				source: $.proxy(this.getMatches, this),
				minLength: this.typeahead_minlength,
				onInvalidTerm: this.onInvalidTerm,
				delay: 300
			});
			
			$searchForm.on({
				'submit': function() {
					var mod = this;
                    sessionStorage.removeItem('PlpProductListRefreshfrom');

					if(mod.searchExperience !== 'factfinder') {
						if($searchInput[0].value === '') {
							$('.input-search').addClass('empty-search-text');
							$searchInput.attr("placeholder", $('#placeholder-value-empty')[0].value);
							return false;
						}
						else {	
							// where one result is present and mpn attribute is found, redirect to the pdp when search is submitted
							if(mod.responseData.response.numFound === 1 && mod.responseData.response.mpn) {
								mod.mpnRedirect(mod.responseData.response.mpn);
							}
							// logic for handling business rules here
							else if(mod.responseData.response.rulesTriggered === true) {
								var rules = mod.responseData.response.fusion;

								if(rules.jsonBlob) {
									sessionStorage.setItem('jsonBlob', rules.jsonBlob.empty[0]);
								}
								
								if(rules.banner) {
									sessionStorage.setItem('banner',rules.banner[0]);
								}
								
								if(rules.carousel) {
									sessionStorage.setItem('carousel',rules.carousel);
								}

								if(rules.redirect.url) {
									// take first item in array if multiple are present
									window.location.assign(window.location.hostname + rules.redirect.url[0]);
								}

								return true;
							}
							else {
								$('.input-search').removeClass('empty-search-text');
								$searchInput.attr("placeholder", $('#placeholder-value')[0].value);
								
								//avoid sending empty parameters
								$(this).children(':input[value=""]').attr("disabled", "disabled");
								return true;
							}
						}
					}
					else {
						if($searchInput[0].value === '') {
							$('.input-search').addClass('empty-search-text');
							$searchInput.attr("placeholder", $('#placeholder-value-empty')[0].value);
							return false;					
						}
						else {
							$('.input-search').removeClass('empty-search-text');
							$searchInput.attr("placeholder", $('#placeholder-value')[0].value);
							
							//avoid sending empty parameters
							$(this).children(':input[value=""]').attr("disabled", "disabled");
							return true;
						}
					}
				}
			});

			parent.on(callback);
		};

		this.onInvalidTerm = function(term) {
			$(document).trigger('search', {
				type: 'invalidTerm',
				term: term
			});
		};

		this.onDocumentClick = function(ev) {
			var mod = this;
			// once the focus is on the input we listen on the document for clicks outside
			// make sure the click was neither on the searchInput and the metahd-suggest
			// event also triggers, when form is submitted by enter (button type submit triggers document click), so we exclude the button also
			if ($(ev.target).closest('#metahd-search').length === 0 && $(ev.target).closest('#suggest-target').length === 0 && $(ev.target).closest('.btn-search').length === 0) {
				$(document)
					.off('click.search-blur')
					.trigger('search', { type: 'blur' })
				;
			}
		};

		this.onKeyPress = function(ev) {
			var mod = this;
			// We also listen for ESC Keypress no matter where the cursor is
			if (ev.keyCode === 27) {
				mod.$ctx.find('.input-search').val('');
				$(document).trigger('search', { type: 'blur' }); // keep event handler because focus stays within input field
			}
		};

		this.onSuggestClosed = function(){
			var $ctx = this.$ctx;
			$ctx.removeClass('activeSuggest');
            $("#js-products-dropdown").removeClass('search-active');
			$('.overlay-suggest').removeClass('active');
			$currentSelectedRowIndex = 0;
			$('body').css('overflow','auto');
        };

		this.getMatches = function(query, process) {
			var self = this;

			//DISTRELEC-10350 Remove - from search box when searching for SAP article number
			query = query.replace(/\-/g, '');

			var currentXhrId = ++self.jXhrPoolId;
			var ffid = $('#f_fid').val();
			var fusionUri = this.fusionSearchUrl + '/typeahead' + (this.fusionSuffix ? '_' + this.fusionSuffix : '');
			var country = $('#backenddata .shopsettings').data('country');
			var channel = $('#backenddata .shopsettings').data('channel-label');
			
			// where fusion search is active, modify request url
			if(this.searchExperience !== 'factfinder') {
				// create fusion url for use in the query
				var fusionUrl = fusionUri + '?country=' + country + '&language=' + this.currentLanguage.toLowerCase() + '&channel=' + channel + '&q=' + query;

				if($('#selected-cat-field').val() !== '') {
					fusionUrl += ('&fq=category1Code:' + selectedValue);
				}
				
				// add query and url to fusion store for use in productlist
				localStorage.setItem('fusion-query', query);
				localStorage.setItem('fusion-query-url', this.fusionSearchUrl + '/apps/webshop/query/search?country=' + country.toLowerCase() + '&language=' + this.currentLanguage.toLowerCase() + '&channel=' + channel.toLowerCase() + '&q=' + query + '&rows=50&start=0');

				$.ajax({
					url: fusionUrl,
					type: 'GET',
					headers: {
						'X-Api-Key': this.fusionApiKey
					},
					beforeSend: function(jqXHR) {
						$.each(self.jXhrPool, function(index, jqXHR) {
							if(jqXHR !== null && jqXHR.readyState !== 4) {
								jqXHR.abort();
							}
						});
						self.jXhrPool.push(jqXHR);
					},
					success: function (data, textStatus, jqXHR) {
						if(currentXhrId == self.jXhrPoolId) {
							self.responseData = data;
							self.jXhrPool.pop();
							self.onGotResult.call(self, data);						
						}
					},
					error: function (jqXHR, textStatus, errorThrown) {
						return errorThrown;
					}
				});

			}
			else {
				$.ajax({
					url: this.typeahead_uri,
					type: 'get',
					data: {
						query: query,
						filtercategoryCodePathROOT: selectedValue,
						channel: self.typeahead_channel,
						queryFromSuggest: 'true',
						userInput:query,
						format: 'json',
						sid: ffid,
					},
					beforeSend: function( jqXHR ) {
						// Stop all previous ongoing ajax calls
						$.each(self.jXhrPool, function(index, jqXHR) {
							if (jqXHR !== null && jqXHR.readyState !== 4) {
								jqXHR.abort();
							}
						});
						self.jXhrPool.push(jqXHR);
					},
					success: function(data) {
						if(currentXhrId == self.jXhrPoolId){
							self.jXhrPool.pop();
							
							self.onGotResult.call(self, data);
						}
					},
					error: function (jqXHR, textStatus, errorThrown) {
					}
				});
			}
		};

		this.onGotResult = function(data) {
			var self = this;
			self.$ctx.addClass('activeSuggest');
			$("#js-products-dropdown").addClass('search-active');
			$(document).trigger('search', {
				type: (self.searchExperience !== 'factfinder') ? 'fusionResult' : 'newResult',
				result: data
			});
            $( '#suggest-target .suggest-section .js-results .suggest-row' ).each(function (i) {

            	if (i > 0) {
                    $('body').css('overflow','hidden');
				}

                $(this).attr('data-rowindex', (i + 1) );
                $(this).addClass('data-row-'+ (i + 1) );
            });

            $currentSelectedRowIndex = 0;

		};

		this.onCategoryRestrictionsSelected = function (data) {
			var self = this
				, categories = [];
			$.each(data, function(key, value) {
				var category = value;
				categories.push(category);
			});

			self.categoryRestrictions = categories.join([separator = ',']);
			
			var arrayCategoryNames = self.categoryRestrictions.split(',');
			
			for (var i=0; i<arrayCategoryNames.length; i++){
				var categoryName = arrayCategoryNames[i];
				var position = i+1;
				self.$$('.filter_Category'+position).val(categoryName);
				
			}
		};

		this.mpnRedirect = function (data) {
			var response = data;

			if(response.isMPNSearch === true || response.isAlternativeMPNSearch === true) {
				window.location.assign(window.location.hostname + response.mpn.redirect.url);
			}
		};
	};

})(Tc.$);
