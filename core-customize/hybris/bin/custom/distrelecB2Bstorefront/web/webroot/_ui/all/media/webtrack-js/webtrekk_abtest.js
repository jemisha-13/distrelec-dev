//// START Webtrekk A/B Test
var wt_vt;(function () {function wt_getCookie(cookiename) {var cookiestring = "" + document.cookie;var index1 = cookiestring.indexOf(cookiename);if (index1==-1 || cookiename=="") {return false;}var index2=cookiestring.indexOf(';',index1);if (index2==-1) {index2=cookiestring.length; }return unescape(cookiestring.substring(index1+cookiename.length+1,index2));};function wt_setCookie(name, value, duration) {var d = location.hostname;var dReg = "^[0-9]{1,3" + String.fromCharCode(125) + "\.[0-9]{1,3" + String.fromCharCode(125) + "\.[0-9]{1,3" + String.fromCharCode(125) + "\.[0-9]{1,3" + String.fromCharCode(125) + "$";if (d.search(dReg) == -1) {d = location.hostname.split(".");d = d[d.length-2] + "." + d[d.length-1];}var c;if(d.split('.')[0].length < 3) {c = name + "=" + escape(value) + ";path=/";}else {c = name + "=" + escape(value) + ";path=/;domain=" + d;}document.cookie = c;};	if(document.referrer.length > 0 && !wt_getCookie("wt_ref")) {wt_setCookie("wt_ref", document.referrer);}var wt_vt_c = wt_getCookie('wt_vt');var wt_vt_turl = escape(location.protocol + "//" + location.host.replace(/^www\./,'') + location.pathname);document.write('<sc' + 'ript src="' + location.protocol + '//vs.webtrekk.net/variantServer/?wt_vt=' + (wt_vt_c ? wt_vt_c : '') + '&wt_turl=' + wt_vt_turl + '&t=' + new Date().valueOf() + '" type="text/javascript" charset="utf-8"></sc' + 'ript>');})();
//// END OF Webtrekk A/B Test