<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>

<div class="content-list-box base">

	<a href="${url}"><i class="content-list-link-icon"></i></a>
	<a href="${url}"><h3 class="content-list-title">${title}</h3></a>

	<small class="content-list-info">${subTitle}</small>
	<p>${text}</p>

</div>