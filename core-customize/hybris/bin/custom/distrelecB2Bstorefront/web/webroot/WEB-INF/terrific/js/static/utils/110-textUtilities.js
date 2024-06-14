

/*
 * Add ... in the middle of a string
 */
Tc.Utils.midEllipsis = function (specimen, desiredLength) {										//e.g. "USB Mega Static 123 HD Expander", 20

	if (specimen === "" || specimen === null) return "";
	var defaultDesiredLength = 30;
	if (desiredLength === undefined) {
		desiredLength = defaultDesiredLength;
	}

	var result; 																				//e.g. "USB Mega S...D Expander"
	var specimenLength = specimen.length;														//e.g. 31
	var specimenHalfLength = Math.floor(specimenLength / 2);										//e.g. 15
	var shortenBy = specimenLength - desiredLength;												//e.g. 11
	if (shortenBy <= 0) return specimen;
	var shortenByTail = Math.floor(shortenBy / 2);												//e.g. 5
	var shortenByTop = shortenBy - shortenByTail;												//e.g. 6
	var specimenHead = specimen.substr(0, specimenHalfLength);									//e.g. "USB Mega Static"
	var specimenTail = specimen.substr(specimenHalfLength, specimenLength);						//e.g. " 113 HD Expander"
	var specimenHead = specimenHead.substr(0, specimenHalfLength - shortenByTail);					//e.g. "USB Mega S"
	var specimenTail = specimenTail.substr(shortenByTop, specimenLength);						//e.g. "D Expander"
	result = specimenHead + "..." + specimenTail;												//e.g. "USB Mega S...D Expander"

	return result;
}

/*
 * Add ... in the middle of a string
 */
Tc.Utils.truncateEllipsis = function (myString, desiredLength) {

	var strLength = myString.length;

	if((strLength + 3) <= desiredLength) {
		return myString;
	} else {
		// do not use .trim() because IE8 throws an error
		return $.trim(myString.substr(0, desiredLength)) + '...';
	}
}

/*
 * Escape special chars in html strings
 */
Tc.Utils.escapeHtml = function (text) {
	return text
		.replace(/&/g, "&amp;")
		.replace(/</g, "&lt;")
		.replace(/>/g, "&gt;")
		.replace(/"/g, "&quot;")
		.replace(/'/g, "&#039;");
}