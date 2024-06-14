(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class ImportToolMatching
	 * @extends Tc.Module
	 */
	Tc.Module.ImportToolMatching = Tc.Module.extend({

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
			this.ignoreFirstRow = false;
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {
			var self = this,
				$ctx = this.$ctx;
			
			self.$columnNameSelector = self.$$('.columnNameSelector');
			self.$ignoreFirstRowData = self.$$('#firstRowData');
			self.$reviewForm = self.$$('#review-file');
			
			self.$columnNameSelector.on('change', function(ev) {
				self.onChangeColumnNameSelector(ev);
			});		
			
			self.$ignoreFirstRowData.change(function() {
		        if($(this).is(":checked")) {
		        	$('#row-0').hide();
		        	$("#ignoreFirstRow").val(true);
			    }
		        else{
		        	$('#row-0').show();
		        	self.ignoreFirstRow = false;
		        	$("#ignoreFirstRow").val(false);
		        }
      
		    });
			
			
			$('#review-file').submit(function(event){
				event.preventDefault();	

				var $warningNoColumns = $('.warning-no-columns'),
                    self = this,
					$usersettings = $('.usersettings', self.$backendData);
				
				$warningNoColumns.addClass('hidden');
				$("select[id^='columnNameSelector']").css('border', '0px solid');
				
				if ($('.columnNameSelector.marked').length >= 2 ){

                    if ($usersettings.length > '0' && $usersettings.data('login').toString() === 'true') {
                    	this.submit();
                    } else {
                        $('.import-tool-modal').addClass('current');
                        $('body').append('<div class="modal-backdrop  in"></div>');
                    }

				}
				else{
					$warningNoColumns.removeClass('hidden');
					$("select[id^='columnNameSelector']").css('border', '1px solid red');
				}

			});

            digitalData.page.pageInfo.bomMethod = sessionStorage.getItem("digitalData.page.pageInfo.bomMethod");

            if ( ('.mod-global-messages .error').length > 0 ) {
                digitalData.page.pageInfo.error.error_page_type = $('.skin-layout-import-tool .mod-global-messages .error').text().trim();
            }

            callback();
		},
		
		
		/**
		 * Updates the mandatory field table according to the value selected on the dropdown.
		 */
		onChangeColumnNameSelector: function(ev){
			
			var columnChanged = ev.target.id;
			var optionSelected = $('#'+columnChanged+'').find(":selected").val();
			
			this.updateTableInstructions(optionSelected);
			this.colorSelectedColumn(optionSelected, columnChanged);
			this.updateFormParametersValues(columnChanged, optionSelected);
			
			if (optionSelected === ''){
				this.deselectPreviousTableInstruction(optionSelected);
				$('#'+columnChanged).removeClass('marked');
			}
			else{
				$('#'+columnChanged).addClass('marked');
			}
			
		},
		

		/**
		 * Set a green check in the mandatory fields table
		 */
		updateTableInstructions: function(optionSelected){

			if (optionSelected === 'quant'){
				$('.instruction-quantity').addClass('attribute-filled');
			}
			if (optionSelected === 'man'){
				$('.instruction-manufacturer').addClass('attribute-filled');
			}
			if (optionSelected === 'man-an'){
				$('.instruction-man').addClass('attribute-filled');
			}
			if (optionSelected === 'dis-an'){
				$('.instruction-dam').addClass('attribute-filled');
			}			
			if (optionSelected === 'ref'){
				$('.instruction-ref').addClass('attribute-filled');
			}				
		},
		
		
		/**
		 * Unset the green check in the mandatory fields table for the previous value
		 */		
		deselectPreviousTableInstruction: function(optionSelected){

			if (optionSelected === 'quant'){
				$('.instruction-quantity').removeClass('attribute-filled');
			}
			if (optionSelected === 'man'){
				$('.instruction-manufacturer').removeClass('attribute-filled');
			}
			if (optionSelected === 'man-an'){
				$('.instruction-man').removeClass('attribute-filled');
			}
			if (optionSelected === 'dis-an'){
				$('.instruction-dam').removeClass('attribute-filled');
			}	
			if (optionSelected === 'ref'){
				$('.instruction-ref').removeClass('attribute-filled');
			}				
		},	
		
		
		/**
		 *  Updates the form parameters values to set the number of the column for the following parameters
		 *	- articleNumberPosition
   		 *	- quantityPosition
    	 *	- referencePosition
		 */
		updateFormParametersValues: function(columnChanged, optionSelected){

			var columnNumber = columnChanged.split("-")[1];
			
			if (optionSelected === 'quant'){
				$("#quantityPosition").val(parseInt(columnNumber));
			}
			if (optionSelected === 'ref'){
				$("#referencePosition").val(parseInt(columnNumber));
			}			
			if (optionSelected === 'dis-an'){
				$("#articleNumberPosition").val(parseInt(columnNumber));
			}

			if(!optionSelected){
				if($("#quantityPosition").val() === columnNumber){
					$("#quantityPosition").val(null);
				}
				if($("#referencePosition").val() === columnNumber){
					$("#referencePosition").val(null);
				}
				if($("#articleNumberPosition").val() === columnNumber){
					$("#articleNumberPosition").val(null);
				}
			}
		},		
		
		
		/**
		 * Paints in green the selected column
		 */	
		colorSelectedColumn: function(optionSelected, columnChanged){
			if (optionSelected !== ''){
				$('colgroup .'+columnChanged+'').addClass('colorSelectedColumn');
			}
			else{
				$('colgroup .'+columnChanged+'').removeClass('colorSelectedColumn');
			}
		}
		
});

})(Tc.$);