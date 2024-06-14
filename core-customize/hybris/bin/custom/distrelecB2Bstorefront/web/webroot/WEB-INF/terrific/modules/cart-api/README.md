# Distrelec Cart API

There are 5 HTTP actions available:

	GET     /cart/json
	GET     /cart/recalulate
	POST    /cart/add?productCodePost=3010006&qty=4
	POST    /cart/add/bulk?productCodePost=3010006&qty=4
	POST    /cart/remove?entryNumber=0

`entryNumber` is the index of a product in the cart.

Example responses can be found in [...]webroot/_ui/all/data