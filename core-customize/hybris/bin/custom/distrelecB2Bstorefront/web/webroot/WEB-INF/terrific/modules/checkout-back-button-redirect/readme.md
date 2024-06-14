This Module is used to check if there is an active cart when the user clicks the browser backbutton on checkout pages.
Since the browser back button uses the cached version of the page, we need to do an ajax post call to check if
the cart is not active anymore. In that case we redirect to the empty cart page
