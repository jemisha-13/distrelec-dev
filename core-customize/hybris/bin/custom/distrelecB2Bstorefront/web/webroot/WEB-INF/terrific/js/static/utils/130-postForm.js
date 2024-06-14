/*
Fill a form with data and send it to the given url.
Parameters:
	url: String, the receiving url
	data: Object, payload of the request. Multiple values for same parameter as array: {name1: value1, name2: [value2, value3]}
	method: String, GET or POST
	decode: Boolean, true if data is URIencoded
*/

Tc.Utils.postForm = function(url, data, method, decode) {
	if (method === null) method = 'POST';
	if (data === null) data = {};
	var token = $("meta[name='_csrf']").attr("content");
	var $form = $('<form>').attr({
		method: method,
		action: url
	 }).css({
		display: 'none'
	 });

	var addData = function(name, data) {
		if ($.isArray(data)) {
			for (var i = 0; i < data.length; i++) {
				var value = data[i];
				addData(name, value);
			}
		} else if (typeof data === 'object') {
			for (var key in data) {
				if (data.hasOwnProperty(key)) {
					addData(name, data[key]);
				}
			}
		} else if (data !== null) {
			var param = decode ? decodeURIComponent(String(name).replace(/\+/g,' ')) : String(name);
			var value = decode ? decodeURIComponent(String(data).replace(/\+/g,' ')) : String(data);
			$form.append($('<input>').attr({
			  type: 'hidden',
			  name: param,
			  value: value
			}));
		}
	};

	for (var key in data) {
		if (data.hasOwnProperty(key)) {
			addData(key, data[key]);
		}
	}
	
	if(method === 'POST'){
		$form.append($('<input>').attr({
			  type: 'hidden',
			  name: '_csrf',
			  value: token
			}));
		}
	
	$form.appendTo('body').submit();
};

Tc.Utils.postFormForCount = function(url, data, method, decode) {
    if (method === null) method = 'POST';
    if (data === null) data = {};
    var token = $("meta[name='_csrf']").attr("content");
    var $form = $('<form>').attr({
        method: method,
        action: url
    }).css({
        display: 'none'
    });

    var addData = function(name, data) {
        if ($.isArray(data)) {
            for (var i = 0; i < data.length; i++) {
                var value = data[i];
                addData(name, value);
            }
        } else if (typeof data === 'object') {
            for (var key in data) {
                if (data.hasOwnProperty(key)) {
                    addData(name, data[key]);
                }
            }
        } else if (data !== null) {
            var param = decode ? decodeURIComponent(String(name).replace(/\+/g,' ')) : String(name);
            var value = decode ? decodeURIComponent(String(data).replace(/\+/g,' ')) : String(data);
            $form.append($('<input>').attr({
                type: 'hidden',
                name: param,
                value: value
            }));
        }
    };

    for (var key in data) {
        if (data.hasOwnProperty(key)) {
            addData(key, data[key]);
        }
    }
    
    if(method === 'POST'){
		$form.append($('<input>').attr({
			  type: 'hidden',
			  name: '_csrf',
			  value: token
			}));
		}
    
    $form.appendTo('body');
    var currentUrl = url;
    
    if ( $('.mod-layout').hasClass('skin-layout-category') ) {
    	currentUrl=url.replace("/c/", "/c/cnt/");
	}
    
    if ( $('.mod-layout').hasClass('skin-layout-store') ) {
        currentUrl=url.replace("/new", "/new/cnt?");
		currentUrl=currentUrl.replace("/clearance", "/clearance/cnt");
	}

    if ( $('.mod-layout').hasClass('skin-layout-manufacturer') ) {
        currentUrl=url.replace("/manufacturer/", "/manufacturercount/");
    }

    if ( $('.mod-layout').hasClass('skin-layout-search') ) {
        currentUrl=url.replace("/search", "/search/cnt?");
    }
    
    if($('body').hasClass('skin-layout-product-family')) {
    	currentUrl=currentUrl.concat("/cnt");
    }
    
    $.ajax({
        url: currentUrl,
        type: method,
        data: $form.serialize(),
        success: function(data) {
        	
            var facetResultCount = Number( data );

            if ( facetResultCount > 0 ) {
            	$('.facet-result-count').html(data);
                $('.plp-filter-controllbar__apply-filter').removeClass('disabled').removeAttr('disabled', 'disabled');
                $('.xmod-filter__matched-products').addClass('success');
                $('.xmod-filter__matched-products').removeClass('error');
            } else {
            	
            	if(isNaN(facetResultCount)){
            		$('.plp-filter-controllbar__apply-filter').removeClass('disabled').removeAttr('disabled', 'disabled');
            	}else{
            		$('.facet-result-count').html(data);
            		$('.plp-filter-controllbar__apply-filter').addClass('disabled').attr('disabled', 'disabled');
                    $('.xmod-filter__matched-products').addClass('error');
            	}
            }

        }
    });

};
