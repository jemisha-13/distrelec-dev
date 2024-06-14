(function ($) {

	/**
	 * Product Skin Bom implementation for the module Productlist Order.
	 *
	 */
	Tc.Module.MetahdItem.Searchemptypage = function (parent) {


		this.on = function (callback) {
			
			this.onDocumentClick = $.proxy(this, 'onDocumentClick');
			this.onKeyPress = $.proxy(this, 'onKeyPress');

			this.sandbox.subscribe('metahdSearch', this);
			this.$searchInput = this.$$('#metahd-searchemptypage');
			this.typeahead_uri = this.$searchInput.data('typeahead-uri');
			this.typeahead_uri = this.$searchInput.data('typeahead-uri');
			this.typeahead_minlength = this.$searchInput.data('typeahead-minlength');

			var $ctx = this.$ctx,
				self = this,
				$searchInput = this.$searchInput;

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

			$searchInput.typeahead({
				source: $.proxy(this.getMatches, this),
				minLength: this.typeahead_minlength,
				onInvalidTerm: this.onInvalidTerm,
				delay: 300
			});

			// DEV: immediate mock query
			//this.getMatches('plas');

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
			if ($(ev.target).closest('#metahd-searchemptypage').length === 0 && $(ev.target).closest('#suggest-target').length === 0 && $(ev.target).closest('.btn-search').length === 0) {
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
				mod.$ctx.find('.input-searchemptypage').val('');
				$(document).trigger('search', { type: 'blur' }); // keep event handler because focus stays within input field
			}
		};

		this.onSuggestClosed = function(){
			var $ctx = this.$ctx;
			$ctx.removeClass('activeSuggest');
		};

		this.getMatches = function(query, process) {
			var self = this;
			
			//DISTRELEC-10350 Remove - from search box when searching for SAP article number
			query = query.replace(/\-/g, '');			
			
			//Searchemptypage
			var currentXhrId = ++self.jXhrPoolId;
			$.ajax({
				url: this.typeahead_uri,
				type: 'get',
				data: {
					query: query
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
				success: function (data) {
					// double check if it is really the latest xhrCall which was successful
					if(currentXhrId == self.jXhrPoolId){
						self.jXhrPool.pop();
						self.onGotResult.call(self, data);
					}
				},
				error: function (jqXHR, textStatus, errorThrown) {
				}
			});

		};

		this.onGotResult = function(data) {
			var self = this;
			self.$ctx.addClass('activeSuggest');
			$(document).trigger('search', {
				type: 'newResult',
				result: data
			});
		};
	};

})(Tc.$);
