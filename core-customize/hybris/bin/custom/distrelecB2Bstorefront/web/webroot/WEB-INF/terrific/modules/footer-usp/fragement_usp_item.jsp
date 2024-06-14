<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nam" uri="/WEB-INF/tld/namicscommercetags.tld" %>

<div class="gu-1">
	<div class="usp-wrapper">
		<div class="image-wrapper"><img class="usp-image" src="${item.icon.url}" alt="${item.icon.altText}" /></div>
		<div class="value-wrapper"><span class="usp-value">${item.text}</span></div>
	</div>
</div>