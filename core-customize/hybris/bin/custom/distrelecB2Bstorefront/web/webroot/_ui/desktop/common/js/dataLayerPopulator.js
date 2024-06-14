(function () {
    function isUserLoggedIn() {
        return window.digitalData.user[0].userID !== 'anonymous';
    }

    function getUserLanguage() {
        return window.digitalData.page.pageInfo.language;
    }

    function getUserID() {
        if (isUserLoggedIn()) {
            return window.digitalData.user[0].userID;
        }
        return undefined;
    }

    function getUserType() {
        if (isUserLoggedIn()) {
            return window.digitalData.user[0].segment.type;
        }
        return window.digitalData.page.pageInfo.channel;
    }

    function getPageUrl() {
        return window.digitalData.page.pageInfo.pageUrl;
    }

    function getCountryCode() {
        return window.digitalData.page.pageInfo.countryCode;

    }

    function getCurrencyCode() {
        return window.digitalData.page.pageInfo.currency;
    }

    function getCheckoutStep(event) {

        if(event === 'checkoutStart') {
            return 1;
        } else if (event === 'checkoutDelivery') {
            return 3;
        } else if (event === 'checkoutBilling') {
            return 2;
        } else if (event === 'checkoutPayment') {
            return 4;
        }
    }

    function getTransactionData() {
        return {
            order_id: window.digitalData.transaction.transactionID,
            subtotal: window.digitalData.transaction.total.basePrice,
            voucher: window.digitalData.transaction.total.voucherCode,
            voucher_discount: window.digitalData.transaction.total.voucherDiscount,
            tax: (window.digitalData.transaction.total.priceWithTax - window.digitalData.transaction.total.basePrice).toFixed(2),
            shipping_cost: window.digitalData.transaction.total.shippingCost,
            total: window.digitalData.transaction.total.cartTotal,
            payment_method: window.digitalData.transaction.total.paymentMethod
        };
    }

    function getPDProductData() {
        return {
            brand: window.digitalData.product[0].productInfo.manufacturer,
            id: window.digitalData.product[0].productInfo.productID,
            name: window.digitalData.product[0].productInfo.productName,
            price: window.digitalData.product[0].productInfo.unitPrice[0].substring(0, window.digitalData.product[0].productInfo.unitPrice[0].indexOf(':')),
            category: window.digitalData.product[0].category.primaryCategory
        };
    }

    function getProducts() {
        var products = window.digitalData.cart.item.map(function (product) {
            return {
                quantity: product.quantity,
                brand: product.productInfo.manufacturer,
                id: product.productInfo.productID,
                name: product.productInfo.productName,
                price: product.price.basePrice,
                category: product.category.primaryCategory
            };
        });
        return products;
    }

    function getSearchedTerm() {
        return window.digitalData.page.pageInfo.searchTerm;
    }

    function getPrimaryCategory() {
        return window.digitalData.page.pageCategory.pageType;
    }

    var checkoutFunnelEvents = ['checkoutPayment', 'checkoutBilling', 'checkoutDelivery'];
    var events = window.digitalData.eventName ? window.digitalData.eventName.split('|') : [];
    var params = new URLSearchParams(window.location.search);

    window.dataLayer = window.dataLayer || [];
    window.dataLayer.push({ 'user': null }, { 'page': null }, { 'ecommerce': null });  // Clear the previous user, page and ecommerce objects, if present
    window.dataLayer.push(
      {
          event: 'pageview',
          user: {
              logged_in: isUserLoggedIn(),
              user_id: getUserID(),
              language: getUserLanguage(),
              first_purchase: '',
              customer_type: getUserType()
          },
          page: {
              url: getPageUrl(),
              category: getPrimaryCategory(),
              market: getCountryCode()
          }
      });

    events.forEach(function(eventName) {
        populateDatalayer(eventName);
    });

    function populateDatalayer(eventName) {

        if (eventName === 'login') {
            window.dataLayer.push({ 'user': null }); // Clear the previous user object, if present
            window.dataLayer.push({
                event: 'login',
                user: {
                    logged_in: isUserLoggedIn(),
                    user_id: getUserID(),
                    language: getUserLanguage(),
                    customer_type: getUserType()
                }
            });
        } else if (eventName === 'register') {
            window.dataLayer.push({ 'user': null }); // Clear the previous user object, if present
            window.dataLayer.push({
                event: 'register',
                user: {
                    logged_in: isUserLoggedIn(),
                    user_id: getUserID(),
                    language: getUserLanguage(),
                    customer_type: getUserType()
                }
            });
        } else if (eventName === 'checkoutStart') {
            window.dataLayer.push({ 'user': null }); // Clear the previous user object, if present
            window.dataLayer.push({
                event: 'checkoutStart',
                user: {
                    logged_in: isUserLoggedIn(),
                    user_id: getUserID(),
                    language: getUserLanguage(),
                    customer_type: getUserType()
                },
                ecommerce: {
                    currencyCode: getCurrencyCode(),
                    checkout: {
                        actionField: { step: getCheckoutStep(eventName) },
                        products: getProducts()
                    }
                }
            });
        } else if (eventName === 'productDetail') {
            window.dataLayer.push({ 'user': null }, { 'ecommerce': null });  // Clear the previous user and ecommerce objects, if present
            window.dataLayer.push({
                event: eventName,
                ecommerce: {
                    currencyCode: getCurrencyCode(),
                    detail: {
                      products: getPDProductData()
                    }
                }
            });
        } else if (checkoutFunnelEvents.includes(eventName)) {
            window.dataLayer.push({ 'user': null }, { 'ecommerce': null });  // Clear the previous user and ecommerce objects, if present
            window.dataLayer.push(
              {
                  event: eventName,
                  user: {
                      logged_in: isUserLoggedIn(),
                      user_id: getUserID(),
                      language: getUserLanguage(),
                      first_purchase: '',
                      customer_type: getUserType(),
                      registration_type: '',
                      lookup_used: '',
                      guest_checkout: ''
                  },
                  ecommerce: {
                      currencyCode: getCurrencyCode(),
                      checkout: {
                          actionField: { step: getCheckoutStep(eventName) },
                          products: getProducts()
                      }
                  }
              });
        } else if (eventName === 'transaction') {
            window.dataLayer.push({ 'user': null }, { 'ecommerce': null });  // Clear the previous user and ecommerce objects, if present
            window.dataLayer.push(
              {
                  event: 'transaction',
                  user: {
                      logged_in: isUserLoggedIn(),
                      user_id: getUserID(),
                      language: getUserLanguage(),
                      customer_type: getUserType(),
                      registration_type: '',
                      lookup_used: true,
                      guest_checkout: false
                  },
                  ecommerce: {
                      currencyCode: getCurrencyCode(),
                      purchase: {
                          actionField: getTransactionData(),
                          products: getProducts()
                      }
                  }
              });
        } else if (eventName === 'nilResultSearch') {
            window.dataLayer.push({
                event: 'nilResultSearch',
                searchTerm: params.get('q')
            });
        }

        if (params.get('ikw') && params.get('int_cid')) {
            window.dataLayer.push({'ecommerce': null});  // Clear the previous ecommerce objects, if present
            window.dataLayer.push({
                event: 'promotionClick',
                ecommerce: {
                    promoClick: {
                        promotions: [{
                            id: params.get('ikw'),
                            name: params.get('int_cid')
                        }]
                    }
                }
            });
        }
    }
})();
