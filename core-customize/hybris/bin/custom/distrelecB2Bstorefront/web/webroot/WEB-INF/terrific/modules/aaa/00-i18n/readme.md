# i18n Module

This Module stores and provides string bundles for the client-side application. The module is logic-only. i18n strings
may contain doT.js syntax and uses doT's rendering mechanism if data is provided to the i18n's getString methods.

Code Example and Usage can be Found in Module Facets

i18n is registered as a module in the Tc application bootstrap and a reference is added to very module in the
application:

    Tc.Module.prototype.i18n = app.registerModule($body, 'i18n');

i18n has several methods to add string bundles to its store.

	.addBundle {Function}
	.addBundleLiteral {Function}
	.addBundleLiteralById {Function}

All the methods above require a name for the new string bundle. The methods for retrieving string bundles and single
strings are:

	.getBundle(name) {Function}
	.getString(bundle, stringKey, data) {Function}

Before creating a new string bundle it is often a good idea to check if it was already created, e.g. by another module
instance of the same type:

	if (!this.i18n.getBundle('bundleName')) {
		this.i18n.addBundleLiteralById('htmlId', 'bundleName', [jQ context])
	}

Any module may uses `.getBundle()` for access to a certain string bundle.

The most convenient of these function is `.addBundleLiteralById()`. It will accept the HTML id of a tag containing a string
bundle.

A string bundle is formatted like this:

	<script id="unique-id" type="text/x-i18n-stringbundle">
		{
			'aStringKey': 'A localized string'
			,'anotherStringKey': 'Another localized string'
		}
	</script>

The content of the script tag is safely 'eval-ed' using `new Function`, a new bundle is created and then returned.

A string bundle is an object with the following properties:

	.name {String}
	.strings {Object}
	.getString(key, data) {Function}

**Finally the intended way to retrieve a string is the `getString()` method on a *string bundle***:

	stringbundle.getString('stringKey', [data]])

If data is provided the string will be sent through doT.js for rendering.

*For a complete usage example see the modules:* metahd-suggest *and* cart-logic*