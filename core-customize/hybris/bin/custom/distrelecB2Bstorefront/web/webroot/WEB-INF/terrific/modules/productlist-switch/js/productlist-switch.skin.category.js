(function($) {

	/**
	 * Dev Skin implementation for the module ProductlistSwitch-Category.
	 *
	 * @author Ruben Diaz
	 * @namespace Tc.Module.Default
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.ProductlistSwitch.Category = function (parent) {

		
		/**
		 * override the appropriate methods from the decorated module4 (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			
			var $ctx = this.$ctx,
			self = this;

			self.$btnSwitch.on('click', self.onSwitchListViewTypeButtonClick);
	
			parent.on(callback);

		};
		
		
		
		this.onSwitchListViewTypeButtonClick = function(ev){
			
			var clickedButton = $(ev.target).closest('button')
				,useTechnicalView = false
				,useStandardView = false
				,useIconView = false
				,self = this
				,viewType = ''
				;

	
			
			// standard & technical view: Rendered by ajax 2
			if(!clickedButton.hasClass('active') ){
				
				clickedButton.addClass('active').siblings('.btn').removeClass('active');
	
				if (clickedButton.hasClass('btn-switch-technical')) {
					useTechnicalView = true;
					viewType = 'useTechnicalView';
				}
				if (clickedButton.hasClass('btn-switch-standard')) {
					useStandardView = true;
					viewType = 'useListView';
				}
				if (clickedButton.hasClass('btn-switch-icon')) {
					useIconView = true;
					viewType = 'useIconView';
				}				
	
	
				var  $backenddata = $('#backenddata');
				$('.shopsettings', $backenddata).data('use-technical-view', useTechnicalView);
				$('.shopsettings', $backenddata).data('use-standard-view', useStandardView);
				$('.shopsettings', $backenddata).data('use-icon-view', useIconView);
	
				
				clickedButton.addClass('active').siblings('.btn').removeClass('active');
				
				
				var url; 
				var url2; 
				
				url = window.location.href.split('?')[0];
				
				url2 = window.location.href.split('?')[1];
				
				if (url2 !== undefined){
					url2 = url2.replace('useListView=true', '');
					url2 = url2.replace('useTechnicalView=true', '');
					url2 = url2.replace('useIconView=true', '');
				}
				
				
				if (url.indexOf('?') > -1){
				   url += '&'+viewType+'=true';
				}else{
				   url += '?'+viewType+'=true';
				}
				
				
				if (url2 !== undefined && url2 !== ''){
					var newUrl = url + '&' + url2;
					newUrl = newUrl.replace('&&', '&');
					window.location.href = newUrl;
				}
				else{
					window.location.href = url;
				}
				
				
				
				
			}
			
		};

	};

})(Tc.$);
