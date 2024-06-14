(function($) {

	/**
	 * Skindefault Skin implementation for the module Tcdefault.
	 *
	 * @author Remo Brunschwiler
	 * @namespace Tc.Module.Default
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.Tcdefault.Skindefault = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			// calling parent method
			parent.on(callback);

			// Do stuff here
			//...
		};

		this.after = function () {
			// calling parent method
			parent.after();

			// Do stuff here
			//...
		};

	};

})(Tc.$);
