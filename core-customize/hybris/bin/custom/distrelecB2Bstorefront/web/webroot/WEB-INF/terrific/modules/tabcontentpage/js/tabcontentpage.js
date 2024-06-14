(function($) {

	Tc.Module.Tabcontentpage = Tc.Module.extend({

		init: function($ctx, sandbox, id) {
			this._super($ctx, sandbox, id);
			this.sandbox.subscribe('anchorLinks', this);
			this.$downloadTab = $('.info-tab_download', $ctx);
			this.onAnchorClickEvent = $.proxy(this.onAnchorClickEvent, this);
		},

		on: function(callback) {
			var self = this,
				hash = document.URL.split('#')[1],
				urlDocument = document.URL;
			
			var productCode = self.$downloadTab.data('product-code');

			self.$downloadTab.off('click').on('click', function (data) {
				self.fire('downloadTabClicked', { productCode: productCode }, ['anchorLinks']);
			});				
			
			self.initTabs();
			var paramTabId = self.getUrlParameter(urlDocument, 'tab_id');
			
			if (paramTabId !== undefined ){
				self.selectTabAsync(paramTabId);
			}
			
			callback();
		},

		initTabs: function() {
			var self = this
				,$tabs = self.$$('.info-tabs a').not('.product-family')
				,isATabSelected = false
				;
			self.$tabs = $tabs;
			
			$('.nav-tabs li:first-child').addClass('active');
			$('.nav-tabs li:first-child').addClass('visited');

			// start tabs and handle clicks
			$tabs.tab().on('click', function(ev) {
				var $this = $(this)
					,tabIndex = $this.parent().index()
					;
				
				if (!$this.hasClass('viewAllProducts')){
					ev.preventDefault();
				}

				else{
					window.open($this.attr('href'), '_self');
				}
			
				$('.tab-content').addClass('hidden');
				
				if (!$this.parent().hasClass('visited')){
					$this.parent().addClass('visited');
					self.selectTabAsync(this.dataset.uid);
				}

				else{
					$('.tab-content').addClass('hidden');
					$('.uid-'+this.dataset.uid).removeClass('hidden');
				}

			});
		},
		
		
		selectTabAsync: function(uid){
			var self = this;
			self.uid = uid;
			
			$('.tab-content').addClass('hidden');
			$('.nav-tabs li').removeClass('active');
			
			$.ajax({
				url: '/async-slot-load?suid='+self.uid,
				contentType: 'application/json',
				success: function (data) {
					//var uid = self.getUrlParameter(document.URL, 'suid');
					$('.content-container').append('<div class="tab-content uid-'+self.uid+'"></div>'); 
					$('#'+uid).addClass('active visited');
					$('.uid-'+self.uid).append(data);
				},

				error: function (jqXHR, textStatus, errorThrown) {
				}

			});

		},

		getUrlParameter: function(url, sParam) {
		    var sPageURL = url,
		        sURLVariables = sPageURL.split('?'),
		        sParameterName,
		        i;

		    for (i = 0; i < sURLVariables.length; i++) {
		        sParameterName = sURLVariables[i].split('=');

		        if (sParameterName[0] === sParam) {
                    return sParameterName[1] === undefined ? true : sParameterName[1];
                }

		    }

		}

	});

})(Tc.$);
