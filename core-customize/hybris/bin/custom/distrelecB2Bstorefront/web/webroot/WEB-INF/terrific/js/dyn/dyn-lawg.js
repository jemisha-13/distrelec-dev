/**
 * Global development and debugging helper
 * Author: Marc Diethelm
 */

;(function() {

	var  w = window
		,c
		,isElement
		,isjQuery
		,isArray
		,fn = 'function'
		,cMethods = ['log', 'info', 'debug', 'warn', 'error', 'dir', 'table']
		,method
		,i
		,len
	;

	function concatArgs(args, nestedCall) {
		var aStr = []
			,i
			,arg
			,delim = nestedCall ? ', ' : ',\n'
		;

		for (i = 0; i<args.length; i++) {
			arg = args[i];
			if (arg !== undefined && arg !== null) {

				if (isArray(arg))
					aStr.push('[' + arguments.callee(Array.prototype.slice.call(arg), true) + ']');
				// if this is an DOM element node, show an appropriate representation.
				else if (isElement(arg))
					aStr.push( elementToString(arg) );
				// if this is a jQuery object, use the log method plugin defined below
				else if (isjQuery(arg))
					aStr.push(jQueryToString(arg));
				// if it's a NodeList 'convert' it to an array and then... magic
				else if (arg.toString() == '[object NodeList]')
					aStr.push('NodeList[' + arguments.callee(Array.prototype.slice.call(arg), true) + ']');
				// for everything else use native JS toString methods. Or fail miserably! :((
				else
					aStr.push(arg.toString());
			 // arg is either undefined or null. Let the JS engine convert it to string.
			} else
				aStr.push(arg + '');

		}
		return aStr.join(delim);
	}

	function elementToString(elem) {
		var htmlId = ''
		    ,htmlClass = ''
		;
		elem.id && (htmlId = '#' + elem.id);
		elem.className && (htmlClass = '.' + elem.className.replace(/\s/g, '.')); // replace any white space with a '.'
		return elem.tagName + htmlId + htmlClass;
	}

	function jQueryToString($obj) {
		var i
			,stringArray = []
		;
		for (i=0; i<$obj.length; i++) {
			stringArray.push( elementToString($obj[i]) );
		}

		return '$('+ stringArray.join(', ') +')';
	}

	//Resulting function returns true if param is a DOM element
	if (typeof HTMLElement == 'function') {
		isElement = function(obj) { return obj instanceof HTMLElement }
	}
	else {
		isElement = function(obj) { return typeof obj == 'object' && obj.nodeType === 1 && typeof obj.nodeName==='string' }
	}

	// Resulting function returns true if param is a jQuery object
	try {
		$() instanceof jQuery; // abusing try/catch for flow control. Old crappy browsers (I'm lookin at you IE) will throw.
		isjQuery = function(obj) { return obj instanceof jQuery }
	} catch (e) {
		isjQuery = function(obj) { return typeof obj == 'object' && 'jquery' in obj && typeof obj.jquery == 'string' }
	}

	isArray = Array.isArray || function(obj) {
		return Object.prototype.toString.call(obj) == '[object Array]';
	};
	
	if (w.console) {
		c = console;
		if ('dir' in c && 'apply' in c.dir) { // create global shortcuts
			for (i=0, len = cMethods.length; i<len && (method = cMethods[i]); i++) {
				typeof c[method] === fn && ( function(method) { // create a new scope to preserve the value of method
					w[method] = function() { c[method].apply(c, arguments) }
				} )(method);
			}
		}
		else { // IE: we have console.log but it just accepts one param. let's fix that! :)
			w.log = function() {
				c.log(concatArgs(arguments));
			}

			w.info = function() {
				c.log(concatArgs(arguments));
			}

			w.dir = function() {
				c.log(concatArgs(arguments));
			}

			w.error = function() {
				c.log(concatArgs(arguments));
			}
		}

		clear = c.clear; // supported in IE and Fx
	}
	else {
		w.log = function() { };
		w.info = function() { };
		w.dir = function() { };
		w.error = function() { };
	}
	
	w._alert = function() { alert(concatArgs(arguments)) };

	/**
	 * A tiny jQuery plugin adding logging to any jQuery object
	 * @param clear Boolean Clear the console before logging?
	 */
	
	w.jQuery && (jQuery.fn.log = function(clear) {
		clear && c.clear.call(c);
		c.log.call(c, this);
		return this;
	});
	
	w.jQuery && (jQuery.fn.alert = function() {
		w._alert(concatArgs(this));
	});
	
})();
