(function ($) {

	
	
	/**
	 * List Skin implementation for the module Toolsitem.
	 *
	 * @author Laurent Zamofing
	 * @namespace Tc.Module.Default
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.Toolsitem.Availability = function (parent) {

		
		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			// calling parent method
			parent.on(callback);

			var self = this
				,$ico = this.$$('.btn-check-availability');

			// subscribe to connector channel/s
			self.sandbox.subscribe('lightboxStatus', self);
			
			$ico.on('click', function () {
				var warehouseCode = $ico.data('warehouse-code');
				
				// Call the controller to receive the availability
				$.ajax({
			        type: "get",
			        url: "/availability/pickup",
			        data:"warehouseCode=" + warehouseCode ,
			        success: function(data){
			        	if(data.availabilityState){
			        		$('.fa-check').removeClass('hidden');
			        	} else {
			        		$('.fa-exclamation-triangle').removeClass('hidden');
			        		
			        		// Search the messages
							var sMessageTitle = $('#sMessageTitle').text();
							var sMessageBoxTitle = $('#sMessageBoxTitle').text();
							var sMessageBoxMessage = $('#sMessageBoxMessage', this.$ctx).html().replace("{0}",data.availabilityDate);

			        		 // $('#modalStatus').css("margin-left","-350px");
			        		 // $('#modalStatus').css("width","700px");
			        		  
			        		self.fire('info', {
				        	    title: sMessageTitle,
				        	    boxTitle: sMessageBoxTitle,
				        	    boxMessage: sMessageBoxMessage
				        	}, ['lightboxStatus']); 
			        	}
			        	   	
			        },
			        error: function(jqXHR, exception) {
			    		self.fire('error', {
			        	    title: 'error',
			        	    boxTitle: 'An error occur, please try again in few minutes'
			        	}, ['lightboxStatus']); 
			        }
				});
				
			});
		};

	

	};

})(Tc.$);
