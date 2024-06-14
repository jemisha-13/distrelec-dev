
/* Formats a price according to country-specific rules regarding thousand grouping and decimal separator.
 * Parameters:
 * 		String price (formatted as 1.234,00 or 1,234.00 or 1234,00 or 1234.00)
 * 		String countryCode (Two-letter ISO code, upper or lower case)
 * Returns:
 * 		Formatted numeric string
 */

Tc.Utils.formatPrice = function (price, countryCode) {
	var price = price.replace(/&#\d+;/g,'').replace(/[^0-9.,]/g,''),
		decDefault = ',',
		groupDefault = '&nbsp;',
		decCharMap = {
			'ch': '.',
			'ex': '.',
			'gb': '.',
			'ie': '.',
			'li': '.',
			'mt': '.'
		},
		groupCharMap = {
			'ch': '\'',
			'de': '.',
			'ex': ',',
			'gb': ',',
			'ie': ',',
			'it': '.',
			'li': '\'',
			'mt': ',',
			'nl': '.'
		},
		dotPos = price.indexOf('.'),
		commaPos = price.indexOf(','),
		decCurrent = dotPos > commaPos ? '.' : ',',
		groupCurrent= dotPos > commaPos ? ',' : '.',
		intPart = price.substring(0,price.indexOf(decCurrent));
	
	if (intPart.length > 3 && intPart.indexOf(groupCurrent)===-1) {
		price = price.substring(0,price.indexOf(decCurrent) - 3) + groupCurrent + price.substr(price.indexOf(decCurrent) - 3);
	}
	
	countryCode = countryCode.toLowerCase();
	price = price.replace(groupCurrent,'^').replace(decCurrent,'*');
	if (groupCharMap[countryCode]===undefined) {
		price = price.replace('^',groupDefault);
	} else {
		price = price.replace('^',groupCharMap[countryCode]);
	}
	if (decCharMap[countryCode]===undefined) {
		price = price.replace('*',decDefault);
	} else {
		price = price.replace('*',decCharMap[countryCode]);
	}
	return price;
}