<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	trimDirectiveWhitespaces="false"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<mod:hero-teaser-pure
	template="ext-hero-teaser"
	heroTeaserData="${teaserItemData}"
	componentWidth="${component.componentWidth.code}" 
	componentHeight="${component.componentHeight.code}"
	title="${component.title}"
	autoplay="${autoplay}" 
	autoplayTimeout="${component.autoplayTimeout}" 
	wtTeaserTrackingId="${wtTeaserTrackingId}"
/>
