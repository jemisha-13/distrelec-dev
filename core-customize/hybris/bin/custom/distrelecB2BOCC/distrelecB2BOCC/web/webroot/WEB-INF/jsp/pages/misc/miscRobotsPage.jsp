<%@ page contentType="text/plain" language="java"
	trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="baseUrlMatch"
	value="${fn:contains(siteBaseURL, 'storefront.')}" />

<c:choose>
	<c:when test="${isProd && !baseUrlMatch}">
User-agent: *
<%@ include file="disallow.tag"%>

# Block Bad-Bots or Useless-Bot
User-agent: CazoodleBot
User-agent: Gigabot
User-agent: admantx
User-agent: Wget
User-agent: semvisuBot
User-agent: Baiduspider
User-agent: Baiduspider-image
User-agent: BLEXBot
User-agent: YandexBot
User-agent: Yandex
User-agent: Sogou
User-agent: Exabot
User-agent: spbot
User-agent: Fetchbot
User-agent: betaBot
User-agent: LinkpadBot
User-agent: Mail.RU_Bot
User-agent: SeznamBot
User-agent: DotBot
User-agent: Yeti
User-agent: SMTBot
User-agent: Findxbot
User-agent: Genio
User-agent: BubING
User-agent: proximic
User-agent: coccoc
User-agent: GrapeshotCrawler
User-agent: savetheworldheritage.org
User-agent: ChatGPT-User
User-agent: GPTBot
User-agent: ClaudeBot
User-agent: BrightBot

Disallow: /

Sitemap: <c:url value="${siteBaseURL}/sitemap_index.xml" />

	</c:when>
	<c:when test="${isProd && baseUrlMatch}">
# For all robots
User-agent: *

# Block access to specific groups of pages
Disallow: <c:url value="/" />

	</c:when>
	<c:otherwise>
#Allow only Semrush bot
User-agent: SiteAuditBot

<%@ include file="disallow.tag"%>

# For all robots Block access to specific groups of pages
User-agent: *
Disallow: /
	</c:otherwise>
</c:choose>
