(function($) {

	// private stuff

		// collection of all bundles
	var _bundles = {},

		// bundle constructor
		_Bundle = function Bundle(stringBundleObj, name, i18n) {

			this.strings = stringBundleObj;
			this.name = name;

			this.getString = $.proxy(function(key, data) {
				return i18n.getString(this, key, data);
			}, this);
		};

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class i18n
	 * @extends Tc.Module
	 */
	Tc.Module.i18n = Tc.Module.extend({

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
			// And add a reference in every module.
			Tc.Module.prototype.i18n = this;
		}


		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		,on: function(callback) {
			callback();
		}

		/**
		 * Add a Stringbundle (plain object with string keys and string values) to the collection.
		 *
		 * @method addBundle
		 * @param {Object} bundle a plain object with string keys and string values to add to the collection
		 * @param {String} name of the bundle
		 * @return {Object} string bundle
		 */
		,addBundle: function(bundle, name) {
			if (!$.isPlainObject(bundle)) {
				throw new TypeError('Bundle not created from an object literal');
			}
			if (typeof name != 'string') {
				throw new TypeError('bundle name: string expected');
			}

			_bundles[name] = new _Bundle(bundle, name, this);

			return _bundles[name];
		}

		,addBundleLiteral: function(objLiteral, bundleName) {
			var bundle;

			objLiteral = objLiteral.replace(/\s+/g, ' ');

			try {
				bundle = (new Function('return '+ objLiteral))(); // 'eval' the strings source
			} catch (e) {
				throw new EvalError('Invalid literal. Reason: ' + e.message);
			}
			return this.addBundle(bundle, bundleName);
		}

		/**
		 *
		 * @param elemId
		 * @param bundleName
		 * @param context
		 * @returns {*}
		 */
		,addBundleLiteralById: function(elemId, bundleName, context) {
			var $elem = $('#' + elemId, context);

			if (!$elem.length) {
				throw new Error('No element with id: ' + elemId);
			}
			return this.addBundleLiteral( $elem.html(), bundleName ); // cannot use .text() because IE returns empty string
		}

		/**
		 * Get a bundle or check if it exists
		 * @param name
		 * @returns {*} A string bundle object or null, if there's no bundle with the name.
		 */
		,getBundle: function(name) {
			if (_bundles[name]) {
				return _bundles[name];
			}
			else {
				return null;
			}
		}

		/**
		 *
		 * @param bundle {Object}
		 * @param stringKey {String}
		 * @param data {Object}
		 * @returns {*} A string or undefined, if the stringKey does not exist.
		 */
		,getString: function(bundle, stringKey, data) {
			if (stringKey in bundle.strings)
				if (data) {
					return doT.template(bundle.strings[stringKey])(data);
				} else {
					return bundle.strings[stringKey];
				}
			else {
				throw new ReferenceError('Key not defined: ' + stringKey + ' in bundle ' + bundle.name);
			}
		}/*

		,getString: function(bundleName, stringKey, data) {
			if (bundleName in _bundles) {
				if (stringKey in _bundles[bundleName])
					if (data) {
						return doT.template(_bundles[bundleName][stringKey])(data);
					} else {
						return this._bundles[bundleName][stringKey];
					}
				else {
					throw new ReferenceError('string not defined: ' + stringKey + ' in ' + bundleName);
				}
			} else {
				throw new ReferenceError('bundle not defined: ' + bundleName);
			}
		}*/
	});

})(Tc.$);
