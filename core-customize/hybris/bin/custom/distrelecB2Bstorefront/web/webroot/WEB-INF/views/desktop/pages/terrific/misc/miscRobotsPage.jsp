<%@ page contentType="text/plain" language="java"
	trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="baseUrlMatch"
	value="${fn:contains(siteBaseURL, 'storefront.')}" />

<c:choose>
	<c:when test="${isProd && !baseUrlMatch}">
User-agent: *
Disallow: <c:url value="*?q=*&filter" />
Disallow: <c:url value="*&filterURL=*" />
Disallow: <c:url value="*/search" />
Disallow: <c:url value="*/checkToggles" />
Disallow: <c:url value="*/cart" />
Disallow: <c:url value="*/checkout" />
Disallow: <c:url value="*/my-account" />
Disallow: <c:url value="*/login" />
Disallow: <c:url value="*//app/etc/local.xml" />
Disallow: <c:url value="*/medias/sys_master/*.gz" />
Disallow: <c:url value="*/gsitemap*.xml.gz" />
Disallow: <c:url value="*serviceplus*" />
Disallow: <c:url value="*/ServicePlusShop/*" />
Disallow: <c:url value="*/alsoBought*" />
Disallow: <c:url value="*/MobileStore*" />
Disallow: <c:url value="*/sendToFriend*" />
Disallow: <c:url value="*/availability*" />
Disallow: <c:url value="*/facet*" />
Disallow: <c:url value="*/bom-tool-upload*" />
Disallow: <c:url value="*/import-tool-upload*" />
Disallow: <c:url value="*/c/TODO*" />
Disallow: <c:url value="*it.queryUrl*" />
Disallow: <c:url value="*undefined*" />
Disallow: <c:url value="*/feedback-campaigns" />
Disallow: <c:url value="*/special-shops/*" />
Disallow: <c:url value="*/shop-in-shop/*" />
Disallow: <c:url value="*/_s/allsitesettings" />
Disallow: <c:url value="*/energyefficencydata$" />
Disallow: <c:url value="*/energyefficencydata/*" />
Disallow: <c:url value="/compliance-document/*" />
Disallow: <c:url value="*/p/getAlterntaives/*" />

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
	<c:otherwise>
# For all robots
User-agent: *

# Block access to specific groups of pages
Disallow: <c:url value="/" />

	</c:otherwise>
</c:choose>
