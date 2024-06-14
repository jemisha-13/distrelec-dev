(function ($) {

    /**
     * Module implementation.
     *
     * @namespace Tc.Module
     * @class Breadcrumb
     * @extends Tc.Module
     */

    Tc.Module.Breadcrumb = Tc.Module.extend({


		//
     	// Init
		//

        init: function ($ctx, sandbox, id) {

            // call base constructor
            this._super($ctx, sandbox, id);

	        // subscribe to connector channel/s
	        this.sandbox.subscribe('breadcrumb', this);

			// cache selectors
	        this.$bcList = $('.bc-list', $ctx);
            this.$bcItem = $('.bc-item', this.$bcList);
            this.$bcLink = $('.bc-link', this.$bcList);

            this.separator = '...';
            this.$originalMarkup = this.$bcList.html();

			// Test: DISTRELEC-1422: KR
            this.sluggishly = 0; // fast (200) sprightly timely sluggishly slow (600)
            this.painfully = 5000;
            this.lightningFast = 0;

			// build the breadcrumb data model
	        this.initModel();
        },
			
			//
			// Build the breadcrumb model from the html markup
			initModel: function() {

				var  self = this;

				// Push every breadcrumb item into an array (including home)
				this.bcItems = [];
				this.$bcItem.each(function (i) {

					var  $item = $(this)
						,html = self.$bcList.children().eq(i)
						,text = $.trim($('.breadcrumb-text', $item).text())
						,length = text.length
					;
					
					self.bcItems.push({ index: i, text: text, html: html, length: length });
				});

				// Helper function to get the current breadcrumb length
				this.getBcLength = function() {

					var totalLength = 0;

					$(this.bcItems).each(function (index, element) {
						totalLength += element.length;
					});

					return totalLength;
				};

				// The allowed max length of the breadcrumb
				this.bcMaxLength = (this.$bcList.data('max-length') !== undefined) ?  this.$bcList.data('max-length') : 200;
			},


		////////////////////////////////////////////////////////////////////////////////////////////////////////////////


		//
        // On
        //

        on: function (callback) {

			this.processBreadcrumb();

			if(sessionStorage.getItem('back-to-pfp') !== null && sessionStorage.getItem('back-to-pfp') === 'true' && $('body').hasClass('skin-layout-product')) {
				$('.skin-breadcrumb-product__return').addClass('hidden');
				$('.pfp-return-link').removeClass('hidden');
			}

			sessionStorage.removeItem('back-to-pfp');

            callback();
        },


        	//
        	// processBreadcrumb
        	processBreadcrumb: function() {

				var self = this;

				// Do we need to trim the breadcrumb?
				if (self.getBcLength() > self.bcMaxLength) {

					// How to we need to trim is?
					//  - normal = trim from the left until the breadcrumb is short enought
					// 	- special = we need to trim the active item too, so that the second highest level gets visible

					var normalTrimTypeLength = 0;	// This is the length of the breadcrumb if we trim it "normal"
					if(self.bcItems.length >= 3) {
						normalTrimTypeLength = self.bcItems[0].text.length + 3 + self.bcItems[self.bcItems.length-2].text.length + self.bcItems[self.bcItems.length-1].text.length;
					}

					// Insert the new Breadcrumb into the page and bind event listeners
					this.insertBreadcrumbHtml();
				}

				// Fade in the breadcrumb
				this.$bcItem.css('display', 'block');

        	},

        	
        	// checks if we are coming from a search- In this case we show the full breadcrumb
        	comingFromSearchListPage: function() {
        		var urlParams = location.search;
        		return urlParams.indexOf('?q=') !== -1;
        	},
        	
        	


			//
			// Insert the trimmed breadcrumb into the markup
			insertBreadcrumbHtml: function() {

				var self = this;

				// Remove existing Breadcrumb
				this.$bcList.empty();

				// Insert Breadcrumb Items from Model
				$(this.bcItems).each(function(index, element) {
					self.$bcList.append(element.html);
				});

				// Bind Click Handler
				this.$bcLink.on('click', function(e) {

					if($(this).text().trim() == self.separator) {
						self.$bcList.html(self.$originalMarkup);
						// Test: DISTRELEC-1422: KR
						self.$bcItem = $('.bc-item', self.$bcList); // To prevent old styling (display: listItem) from persisting clear $bcItem cached variable
						self.$bcItem.fadeIn(self.sluggishly); // Use self.lightningFast for immediate response
						e.preventDefault();
					}
				});
			},


		////////////////////////////////////////////////////////////////////////////////////////////////////////////////


		//
		// Helper Functions
		//

		// Return the items that should be trimmed
		 _getItemsToTrim: function () {

            var  self = this
				,len = self.getBcLength()
				,itemsArray = $.extend(true, {}, {obj:this.bcItems}).obj
				,itemsToTrim = []
            ;

				// Removes items from the items array as long as it is longer than maxLength
                trimList = function () {
                    if (len > self.bcMaxLength - 3) { // compensate for the three ...

						// We use 1 as index, as this is always the second item from the left (we dont want to trim Home)
                        len -= itemsArray[1].length;
                        itemsToTrim.push(itemsArray[1]);
                        itemsArray = Tc.Utils.removeArrayItem(itemsArray, 1);

                        trimList();
                    }
                };

            trimList();

            return itemsToTrim;
        },

        // Remove duplicate "..." items from array
		_cleanUpDuplicateDotItems: function() {

			var  self = this
				,countDots = 0
				,duplicateFreeItems = []
			;

			$(self.bcItems).each(function(index, element) {

				if(element.text !== self.separator || countDots === 0) {
					duplicateFreeItems.push(element);
				}

				if(element.text == self.separator) { countDots++; }
			});

			self.bcItems = duplicateFreeItems;
		},

		// Updates all values of an item depending on its text
		_updateBcItemValues: function(itemIndex, text) {
			this.bcItems[itemIndex].text = text;
			this.bcItems[itemIndex].length = text.length;
			$('.breadcrumb-text', this.bcItems[itemIndex].html).text(text);
		},

		// Trims a text from the middle, until the whole breadcrumb length equals the max length
		_centerTrimText: function(text) {
			var  self = this
				,countCharsToTrim = self.getBcLength() - self.bcMaxLength
			;

			
			
			if (text.trim().length <= countCharsToTrim) return text;

			var  sepLen = this.separator.length
				,charsToShow = text.length - countCharsToTrim - sepLen
				,frontChars = Math.ceil(charsToShow/2)
				,backChars = Math.floor(charsToShow/2)
			;

			return text.substr(0, frontChars) + this.separator + text.substr(text.length - backChars);
		},


		////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		//
		// Trigger Breadcrumb Update from another Module
		//

	    onBreadcrumbActiveItemUpdate: function(breadcrumbItemText){

	    	this._updateBcItemValues(this.bcItems.length-1, breadcrumbItemText);
	    	this.processBreadcrumb();
	    }

    });

})(Tc.$);