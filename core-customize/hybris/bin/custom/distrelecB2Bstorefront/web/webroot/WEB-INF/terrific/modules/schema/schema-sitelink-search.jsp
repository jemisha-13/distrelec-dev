<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- DISTRELEC-13257:  --%>
<script type="application/ld+json">
	{
	   "@context": "http://schema.org",
	   "@type": "WebSite",
	   "url": "${siteBaseURL}",
	   "potentialAction": {
	     "@type": "SearchAction",
	     "target": "${siteBaseURL}/search?q={search_term_string}",
	     "query-input": "required name=search_term_string"
	   }
	}
</script>