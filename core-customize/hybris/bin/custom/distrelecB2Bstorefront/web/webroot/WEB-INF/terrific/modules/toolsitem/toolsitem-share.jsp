<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<a class="ico ico-share popover-special" title="<spring:message code="toolsitem.share" />" data-content-id="productlist_tools_share" data-original-title="<spring:message code="toolsitem.share.on" />">
	<i></i>
</a>
 
<div class="hidden" id="productlist_tools_share"> 
	<div class="social-bar">
		<a href="https://twitter.com/share" target="_blank" class="ico-share-twitter" data-size="large" data-count="none"><i></i></a>
		<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+'://platform.twitter.com/widgets.js';fjs.parentNode.insertBefore(js,fjs);}}(document, 'script', 'twitter-wjs');</script>
		<a class="ico-share-mail"><i></i></a>
		<a href="https://plus.google.com/share?url=${sharePageUrl}" target="_blank" class="ico-share-google" data-base-url="https://plus.google.com/share?url="><i></i></a>
	</div>
</div>