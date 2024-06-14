(function($) {

	/**
	 * Print Skin implementation for the module Toolsitem.
	 *
	 * @author Remo Brunschwiler
	 * @namespace Tc.Module.Default
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.Toolsitem.PrintButton = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {

			// Context & Click Handler
			this.onClick = $.proxy(this, 'onClick');
			this.$ctx.on('click', this.onClick);

            // calling parent method
			parent.on(callback);
		};


		// Event Dispatcher

		this.onClick = function(e){

			e.preventDefault();

		  	// show print dialog
		  	window.print();
        };

		this.after = function () {
			parent.after();
		};

	};

})(Tc.$);
