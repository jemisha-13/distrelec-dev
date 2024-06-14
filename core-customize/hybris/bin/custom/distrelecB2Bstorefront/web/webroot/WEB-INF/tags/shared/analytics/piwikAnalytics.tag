<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>

<c:if test="${ymktTrackingEnabled and isMarketingCookieEnabled == '1'}" >
<script type="text/javascript">
    var piwikTrackerUrl = "${request.contextPath}/events"; // Use Piwik HTTPS Endpoint by default
    var piwikSiteId = "${ycommerce:encodeJavaScript(piwikSiteId)}";
    var sessionId = "${ycommerce:encodeJavaScript(SESSION_ID)}";

    var tracker = Piwik.getAsyncTracker(piwikTrackerUrl, piwikSiteId);

    tracker.setTrackerUrl(piwikTrackerUrl);
    tracker.setSiteId(piwikSiteId);
    tracker.setRequestMethod('POST');
    tracker.setRequestContentType('application/json; charset=UTF-8');
    tracker.setVisitorCookieTimeout(31536000);

    var processPiwikRequest = (function (request) {
        try {
            var pairs = request.split('&');

            var requestParametersArray = {};
            for (index = 0; index < pairs.length; ++index) {
                var pair = pairs[index].split('=');
                requestParametersArray[pair[0]] = decodeURIComponent(pair[1] || '');
            }
            requestParametersArray['sessionId'] = sessionId;

            return JSON.stringify(requestParametersArray);
        } catch (err) {
            return request;
        }
    });

    tracker.setCustomRequestProcessing(processPiwikRequest);

    var hybrisAnalyticsPiwikPlugin = (function() {
        function _getEventtypeParam(suffix) { return '&eventtype=' + suffix; }
        function ecommerce() { return _getEventtypeParam("ecommerce"); }
        function event() { return _getEventtypeParam("event"); }
        function goal() { return _getEventtypeParam("goal"); }
        function link() { return _getEventtypeParam("link"); }
        function load() { return _getEventtypeParam("load"); }
        function log() { return _getEventtypeParam("log"); }
        function ping() { return _getEventtypeParam("ping"); }
        function run() { return _getEventtypeParam("run"); }
        function sitesearch() { return _getEventtypeParam("sitesearch"); }
        function unload() { return _getEventtypeParam("unload"); }

        return {
            ecommerce: ecommerce,
            event : event,
            goal : goal,
            link : link,
            load : load,
            log : log,
            ping: ping,
            sitesearch : sitesearch,
            unload : unload
        };
    })();
    tracker.addPlugin('hybrisAnalyticsPiwikPlugin', hybrisAnalyticsPiwikPlugin);
    // Hybris Analytics specifics - END


    <c:choose>
    <c:when test="${pageType == 'Product'}">
    //View Product event
    <c:set var="categoriesJavaScript" value="" />
    <c:forEach items="${product.categories}" var="category">
    <c:set var="categoriesJavaScript">${categoriesJavaScript},'${ycommerce:encodeJavaScript(category.code)}'</c:set>
    </c:forEach>
    tracker.setEcommerceView('${ycommerce:encodeJavaScript(product.code)}',  // (required) SKU: Product unique identifier
        '${ycommerce:encodeJavaScript(product.name)}',  // (optional) Product name
        [${fn:substringAfter(categoriesJavaScript, ',')}],  // (optional) Product category, or array of up to 5 categories
        '${ycommerce:encodeJavaScript(product.price.value)}');

    tracker.trackPageView('ViewProduct');  //Do we really need this ??
    </c:when>

    <c:when test="${pageType == 'ContentPage' && not empty cmsPage}">
        var labelData = {label: '${cmsPage.label}'};
        tracker.trackPageView("ContentPage", labelData);
    </c:when>



    <c:when test="${pageType == 'Category'}">
    //View category - Start
    <c:choose>
    <c:when test="${searchPageData.pagination.totalNumberOfResults > 0}">
    <c:if test="${not empty breadcrumbs}">
    tracker.trackSiteSearch('',  // Search keyword searched for
        '${ycommerce:encodeJavaScript(categoryCode)}:${ycommerce:encodeJavaScript(categoryName)}',  // Search category selected in your search engine. If you do not need this, set to false
        '${ycommerce:encodeJavaScript(searchPageData.pagination.totalNumberOfResults)}',// Number of results on the Search results page. Zero indicates a 'No Result Search Keyword'. Set to false if you don't know
        tracker.setCustomData('categoryName', '${ycommerce:encodeJavaScript(categoryName)}'));
    </c:if>
    </c:when>
    <c:otherwise>
    tracker.trackSiteSearch('${ycommerce:encodeJavaScript(searchPageData.freeTextSearch)}',  // Search keyword searched for
        false,  // Search category selected in your search engine. If you do not need this, set to false
        '0'  // Number of results on the Search results page. Zero indicates a 'No Result Search Keyword'. Set to false if you don't know
    );
    </c:otherwise>
    </c:choose>
    </c:when>



    <c:when test="${pageType == 'ProductSearch'}">
    //View Product search - START
    <c:choose>
    <c:when test="${searchPageData.pagination.totalNumberOfResults > 0}">
    <c:if test="${not empty breadcrumbs}">
    <c:set var="categoriesJavaScript" value="" />
    <c:forEach items="${breadcrumbs}" var="breadcrumb">
    <c:set var="categoriesJavaScript">${categoriesJavaScript},'${ycommerce:encodeJavaScript(breadcrumb.name)}'</c:set>
    </c:forEach>
    tracker.trackSiteSearch('${ycommerce:encodeJavaScript(searchPageData.freeTextSearch)}',  // Search keyword searched for
        [${fn:substringAfter(categoriesJavaScript, ',')}],  // Search category selected in your search engine. If you do not need this, set to false
        '${ycommerce:encodeJavaScript(searchPageData.pagination.totalNumberOfResults)}',  // Number of results on the Search results page. Zero indicates a 'No Result Search Keyword'. Set to false if you don't know
         getSearchResultsProductCodes());
    </c:if>
    </c:when>
    <c:otherwise>
    tracker.trackSiteSearch('${ycommerce:encodeJavaScript(searchPageData.freeTextSearch)}',  // Search keyword searched for
        false,  // Search category selected in your search engine. If you do not need this, set to false
        '0'  // Number of results on the Search results page. Zero indicates a 'No Result Search Keyword'. Set to false if you don't know
    );
    </c:otherwise>
    </c:choose>
    </c:when>


    <c:when test="${pageType == 'OrderConfirmation'}">
    <c:forEach items="${order.entries}" var="entry">
    tracker.setCustomVariable(1,"ec_id","${ycommerce:encodeJavaScript(order.code)}","page");
    <c:forEach items="${entry.product.categories}" var="category">
    <c:set var="categoriesJavaScript">${categoriesJavaScript},'${ycommerce:encodeJavaScript(category.code)}'</c:set>
    </c:forEach>
    tracker.setEcommerceView('${ycommerce:encodeJavaScript(entry.product.code)}',  // (required) SKU: Product unique identifier
        '${ycommerce:encodeJavaScript(entry.product.name)}',  // (optional) Product name
        [${fn:substringAfter(categoriesJavaScript, ',')}],  // (optional) Product category. You can also specify an array of up to 5 categories eg. ["Books", "New releases", "Biography"]
        '${ycommerce:encodeJavaScript(entry.product.price.value)}'  // (recommended) Product price
    );
    tracker.addEcommerceItem('${ycommerce:encodeJavaScript(entry.product.code)}',  // (required) SKU: Product unique identifier
        '${ycommerce:encodeJavaScript(entry.product.name)}', // (optional) Product name
        [${fn:substringAfter(categoriesJavaScript, ',')}],  // (optional) Product category. You can also specify an array of up to 5 categories eg. ["Books", "New releases", "Biography"]
        '${ycommerce:encodeJavaScript(entry.product.price.value)}',  // (recommended) Product price
        '${ycommerce:encodeJavaScript(entry.quantity)}'.toString()  // (optional, default to 1) Product quantity
    );

    </c:forEach>
    tracker.trackEcommerceOrder('${ycommerce:encodeJavaScript(order.code)}',  // (required) Unique Order ID
        '${ycommerce:encodeJavaScript(order.totalPrice.value)}',  // (required) Order Revenue grand total (includes tax, shipping, and subtracted discount)
        '${ycommerce:encodeJavaScript(order.totalPrice.value - order.deliveryCost.value)}',  // (optional) Order sub total (excludes shipping)
        '${ycommerce:encodeJavaScript(order.totalTax.value)}',  // (optional) Tax amount
        '${ycommerce:encodeJavaScript(order.deliveryCost.value)}',  // (optional) Shipping amount
        false  // (optional) Discount offered (set to false for unspecified parameter)
    );
    tracker.clearEcommerceCart();
    </c:when>

    </c:choose>

    tracker.enableLinkTracking();
    // handlers and their subscription for cart events


    function trackAddToCart_piwik(productCode, quantityAdded, cartData) {
        if (quantityAdded == -1) {
            quantityAdded = 0
        } else if (quantityAdded == 0) {
            quantityAdded = 1
        }
        tracker.setEcommerceView(String(cartData.productCode),  // (required) SKU: Product unique identifier
            cartData.productName,  // (optional) Product name
            [ ], // (optional) Product category, string or array of up to 5 categories
            quantityAdded+""  // (optional, default to 1) Product quantity
        );
        tracker.addEcommerceItem(String(cartData.productCode),  // (required) SKU: Product unique identifier
            cartData.productName,  // (optional) Product name
            [ ], // (optional) Product category, string or array of up to 5 categories
            cartData.productPrice+"", // (recommended) Product price
            quantityAdded+""  // (optional, default to 1) Product quantity
        );

        if (!cartData.cartCode)
        {
            cartData.cartCode="${ycommerce:encodeJavaScript(cartData.code)}";
        }
        tracker.setCustomVariable(1,"ec_id",cartData.cartCode,"page");
        tracker.trackEcommerceCartUpdate(
            '0'  // (required) Cart amount
        );
        tracker.clearEcommerceCart();
    }

    function trackAddToCartBulk_piwik(products) {
        products.forEach(function(product) {
            tracker.setEcommerceView(product.productCode, '', [], product.quantity);
            tracker.addEcommerceItem(product.productCode, '', [],"0", product.quantity + "");
        });
        if (digitalData && digitalData.cart && digitalData.cart.cartID) {
            tracker.setCustomVariable(1,"ec_id",digitalData.cart.cartID,"page");
        }
        tracker.trackEcommerceCartUpdate('0');
        tracker.clearEcommerceCart();
    }

    function trackBannerClick_piwik(url) {
        tracker.setCustomVariable(1,"bannerid",url,"page");
        tracker.trackLink(url, 'banner');
    }

    function trackContinueCheckoutClick_piwik() {
        tracker.setCustomVariable(1,"ec_id","${ycommerce:encodeJavaScript(cartData.code)}","page");
        tracker.trackEvent('checkout', 'proceed', '','');
    }

    function trackShowReview_piwik() {
        tracker.trackEvent('review', 'view', '', '');
    }

    function trackUpdateCart_piwik(productCode,newQuantity,cartData) {
        trackAddToCart_piwik(productCode, newQuantity,cartData);
    }

    function trackRemoveFromCart_piwik(productCode, cartData) {
        trackAddToCart_piwik(productCode, -1, cartData);
    }

    function trackRemoveFromCartBulk_piwik(products) {
        products.forEach(function(product) {
            tracker.setEcommerceView(product.productCode, '', [], "0");
            tracker.addEcommerceItem(product.productCode, '', [],"0", "0");
        });
        if (digitalData && digitalData.cart && digitalData.cart.cartID) {
            tracker.setCustomVariable(1,"ec_id",digitalData.cart.cartID,"page");
        }
        tracker.trackEcommerceCartUpdate('0');
        tracker.clearEcommerceCart();
    }

    function getSearchResultsProductCodes() {
        var i;
        var products = [];
        for (i = 0 ; i < digitalData.product.length; i++) {
            products.push(digitalData.product[i].productInfo.productID);
        }
        return products;
    }

    function findProduct(productCode) {
        var products = digitalData.product;
        var filtered = products.filter(function (product) {
            return product.productInfo.productID == productCode;
        });
        if (!filtered[0] && digitalData.cart && digitalData.cart.item) {
            filtered = digitalData.cart.item.filter(function(product) {
                return product.productInfo.productID == productCode;
            });
        }
        return filtered[0];
    }

    function findProductForCartEntry(entryNo) {
        var items = digitalData.cart.item;
        return items[entryNo];
    }

    function formatPrice(price) {
        return price.split(":")[0];
    }

    function onAdd(data){
        var productCode = data.productCodePost;
        var quantity = data.qty;
        var product = findProduct(productCode);
        var productName, productPrice;
        if (!product) {
            productName = "";
            productPrice = "0"
        } else {
            productName = product.productInfo.productName;
            productPrice = formatPrice(product.productInfo.unitPrice[0]);
        }
        var cartData = {productCode:productCode, productName: productName, productPrice: productPrice};
        trackAddToCart_piwik(productCode,quantity,cartData);
    }

    function onAddBulk(data) {
        if (data && data.productsJson) {
            var products = null;
            try {
                products = JSON.parse(data.productsJson);
            } catch (e) {
                console.log(e)
            }
            if (products) {
                trackAddToCartBulk_piwik(products);
            }
        }
    }

    function onRemove(data) {
        var product = findProductForCartEntry(data.entryNumber);
        var productCode = product.productInfo.productID;
        var productName, productPrice;
        if (!product) {
            productName = "";
            productPrice = "0"
        } else {
            productName = product.productInfo.productName;
            productPrice = formatPrice(product.productInfo.unitPrice[0]);
        }
        var cartData = {productCode:productCode, productName: productName, productPrice: productPrice};
        trackRemoveFromCart_piwik(data.productCodePost,cartData);
    }

    function onUpdate(data) {
        var product = findProductForCartEntry(data.entryNumber);
        var quantity = data.quantity;
        if (!quantity) quantity = data.qty;
        var productCode = product.productInfo.productID;
        var productName, productPrice;
        if (!product) {
            productName = "";
            productPrice = "0"
        } else {
            productName = product.productInfo.productName;
            productPrice = formatPrice(product.productInfo.unitPrice[0]);
        }
        var cartData = {productCode:productCode, productName: productName, productPrice: productPrice};
        trackUpdateCart_piwik(productCode,quantity,cartData);
    }

    $(document).on('cart', $.proxy(function (ev, data) {

        switch (data.type) {
            case 'add':
                onAdd(data);
                break;
            case 'addBulk':
                onAddBulk(data);
                break;
            case 'remove':
                onRemove(data);
                break;
            case 'update':
                onUpdate(data);
                break;

        }
    }, this));

     var emptyCartAnchor = $('.skin-toolsitem-empty-cart').find('a');
     if (emptyCartAnchor) {
         emptyCartAnchor.on('click', function () {
             if (digitalData && digitalData.cart && digitalData.cart.item) {
                 var items = digitalData.cart.item;
                 var products = [];
                 var i;
                 for (i = 0; i < items.length; i++) {
                     var item = items[i];
                     if (item.productInfo && item.productInfo.productID) {
                         var product = {productCode : item.productInfo.productID};
                         products.push(product);
                     }
                 }
                 if (products && products.length > 0) {
                     trackRemoveFromCartBulk_piwik(products)
                 }
             }
         });
     }

    $(document).on('downloadsReady', function () {
        var downloads = $('.download-container');
        if (downloads.length) {
            var downloadLinks = downloads.find('a[data-aainteraction="file download"]');
            if (downloadLinks) {
                downloadLinks.each(function () {
                    $(this).on('click', function () {
                        var dMime = $(this).data('file-type');
                        var dName = $(this).data('file-name');
                        var dUrl = $(this).attr('href');
                        var downloadData = {name: dName, mime: dMime, url: dUrl};
                        tracker.trackPageView("Download", downloadData);
                    })
                });
            }
        }
    });

    // Get unsubscribe feedback form element in DOM
    var $unsubscribeFeedbackForm = $('.js-unsubscribeFeedback-form');
    // If this element exists in DOM
    if ($unsubscribeFeedbackForm.length) {
        // Get button which submits form
        var $unsubscribeFeedbackSubmitButton = $('.js-unsubscribeFeedback-submit');
        // When we receive event which means that piwik logic can be executed
        $unsubscribeFeedbackForm.on('piwikAnalytics-start', function () {
            // Get all reason scopes on page
            var $reasons = $('.js-unsubscribeFeedback-reason', $unsubscribeFeedbackForm);
            // Get category data from BE
            var category = $unsubscribeFeedbackForm.data('category');
            // Get email data from BE
            var email = $unsubscribeFeedbackForm.data('email');
            // Set temporary var which we will use to store users choice
            var tempReasonData = [];

            // Iterate through reasons scope and prepare data for BE
            $reasons.each(function () {
                var $currentReason = $(this);
                // Get checkbox
                var $checkbox = $('.js-reason-input', $currentReason);
                // Get additional field if exists inside reason scope
                var $currentAdditionalField = $('.js-unsubscribeFeedback-reason-additional', $currentReason);
                // Get value from checkbox
                var checkboxValue = $checkbox.val();

                // If checkbox is selected
                if ($checkbox.is(':checked')) {
                    var additionalString = '';
                    // If additional field exists in reason scope
                    if ($currentAdditionalField.length) {
                        // Get additional field form element
                        var $additionalFormElement = $('.js-unsubscribeFeedback-reason-additional-formElement', $currentAdditionalField);
                        // Replace all pipes in value if exists, pipe is used by BE to separate data
                        additionalString = '|' + $additionalFormElement.attr('name') + '=' + $additionalFormElement.val().replace(/\|/g, ' ')
                    }

                    // Push value into temporary array
                    tempReasonData.push([
                        'email=' + email + '|reason=' + checkboxValue + additionalString
                    ]);
                }
            });

            // Send data with /events call
            tracker.setCustomData("reason", tempReasonData);

            if (category) {
                // Send data with /events call
                 tracker.setCustomVariable(1, "category", category, "page");
            }

            if (email) {
                // Send data with /events call
                 tracker.setCustomData("email", email);
                 tracker.setCustomData("category", category);
            }

            // Send data with /events call
            tracker.trackPageView('WebsiteView');

            // When user clicks, we want to first /events call should be made, so thats why we are postponing form submit little bit
            setTimeout(function () {
                // Trigger event which means that piwik logic has finished
                $unsubscribeFeedbackForm.trigger('piwikAnalytics-end');
            }, 400);
        });
    }
</script>
<noscript><p><img src="${fn:escapeXml(piwikTrackerUrl)}?idsite=${fn:escapeXml(piwikSiteId)}" style="border:0;" alt="" /></p></noscript>
</c:if>
