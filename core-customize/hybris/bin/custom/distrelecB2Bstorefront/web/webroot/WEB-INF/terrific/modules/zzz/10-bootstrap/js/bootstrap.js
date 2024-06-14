window.assetsUrl = '/_ui/all/';

/**
 * Terrific Bootstrapping
 */
(function (window, Tc, document) {
	$(document).ready(function () {
		var app
			, $ = Tc.$
			, $body = $(document)
			, config = {
				dependencyPath: {
					library: window.assetsUrl + '/scripts/libs-dyn/',
					plugin: window.assetsUrl + '/scripts/plugins-dyn/',
					util: window.assetsUrl + '/scripts/utils-dyn/'
				}
			}
			;
		
		// DISTRELEC-7386: DE-OCI Wacker Chemie kann nicht bestellen
		// http://stackoverflow.com/questions/16562264/enabling-browser-scroll-bars-after-window-open-with-scrollbars-no
		document.body.scroll = "yes";
		document.body.style.overflow = 'auto';

		// custom function for context selection
		Tc.Module.prototype.$$ = function $$(selector) {
			return this.$ctx.find(selector);
		};

		setTimeout(function () {
			// custom test to detect IE7 / IE in compatibility modes
			Modernizr.addTest('isIE7', function () {
				return $('html').hasClass('lt-ie8');
			});

			// custom test to detect eproc (oci / ariba) customer since some OCI Customer have IE10, but Doc Mode IE5 Quirks (not covered by isIE7 test)
			Modernizr.addTest('isEproc', function () {
				var usersettings = $('#backenddata .usersettings');
				var usersettingsData = usersettings.data();

				if (usersettings.length > '0' && usersettingsData.role == 'eproc') {
					return true;
				}
				else{
					return false;
				}
			});
		}, 200);

		// IE7 IndexOf Hack

		if(!Array.prototype.indexOf) {
			Array.prototype.indexOf = function(obj, start) {
				for (var i = (start || 0), j = this.length; i < j; i++) {
					if (this[i] === obj) { return i; }
				}
				return -1;
			};
		}

		// IE7 QuerySelectoAll Hack
		if(!Array.prototype.indexOf) {
			(function (d, s) {
				d = document, s = d.createStyleSheet();
				d.querySelectorAll = function (r, c, i, j, a) {
					a = d.all, c = [], r = r.replace(/\[for\b/gi, '[htmlFor').split(',');
					for (i = r.length; i--;) {
						s.addRule(r[i], 'k:v');

						for (j = a.length; j--;) a[j].currentStyle.k && c.push(a[j]);
						s.removeRule(0);
					}

					return c;
				};
			})();
		}

		// IE 7 & 8 Array.prototype.filter Hack
        if (!Array.prototype.filter){
            Array.prototype.filter = function(func, thisArg) {
                'use strict';
                if ( ! ((typeof func === 'Function' || typeof func === 'function') && this) )
                    throw new TypeError();

                var len = this.length >>> 0,
                    res = new Array(len), // preallocate array
                    t = this, c = 0, i = -1;

                var kValue;
                if (thisArg === undefined){
                    while (++i !== len){
                        // checks to see if the key was set
                        if (i in this){
                            kValue = t[i]; // in case t is changed in callback
                            if (func(t[i], i, t)){
                                res[c++] = kValue;
                            }
                        }
                    }
                }
                else{
                    while (++i !== len){
                        // checks to see if the key was set
                        if (i in this){
                            kValue = t[i];
                            if (func.call(thisArg, t[i], i, t)){
                                res[c++] = kValue;
                            }
                        }
                    }
                }

                res.length = c; // shrink down array to proper size
                return res;
            };
        }

		// csrf
		$(document).ajaxSend(function( event, request, settings ) {
			if (!settings.crossDomain) {
				var token = $("meta[name='_csrf']").attr("content");
				var header = $("meta[name='_csrf_header']").attr("content");
				request.setRequestHeader(header, token);
			}
		});

		app = window.application = new Tc.Application($body, config);

		// - Before Modules
		app.registerModule($body, 'i18n');
		app.registerModule($body, 'CartApi');
		app.registerModule($body, 'LightboxKeystrokeManager');
		app.registerModule($body, 'PrintCss');

		// - During Modules
		app.registerModules();

		// - After Modules
		app.registerModule($body, 'PageController');

		app.start();
	});
})(window, Tc, document);
