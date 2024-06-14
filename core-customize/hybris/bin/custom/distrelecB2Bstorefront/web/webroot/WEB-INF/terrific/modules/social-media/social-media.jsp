<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="nam" uri="/WEB-INF/tld/namicscommercetags.tld" %>

<c:set var="linkedinlink" value="https://www.linkedin.com/company/distrelec-distrelec-schuricht-and-elfa-distrelec_/" />
<c:set var="facebooklink" value="https://www.facebook.com/Distrelec.Group/" />
<c:set var="twitterlink" value="https://twitter.com/Distrelec" />
<c:set var="youtubelink" value="https://www.youtube.com/user/DistrelecChannel" />

<spring:message code="social.media.facebook" var="sFacebookAriaText" />
<spring:message code="social.media.twitter" var="sTwitterAriaText" />
<spring:message code="social.media.linkedin" var="sLinkedinAriaText" />
<spring:message code="social.media.youtube" var="sYouTubeAriaText" />

<c:choose>
    <c:when test="${currentCountry.isocode eq 'CH'}">
        <c:set var="facebooklink" value="https://www.facebook.com/Distrelec.Schweiz/" />
    </c:when>
    <c:when test="${currentCountry.isocode eq 'DE'}">
        <c:set var="facebooklink" value="https://www.facebook.com/Distrelec.Deutschland/" />
    </c:when>
    <c:when test="${currentCountry.isocode eq 'SE'}">
        <c:set var="facebooklink" value="https://www.facebook.com/ElfaDistrelec.Sverige/" />
    </c:when>
    <c:when test="${siteUid eq 'distrelec_FR'}">
        <c:set var="linkedinlink" value="https://www.linkedin.com/company/distrelec-distrelec-schuricht-and-elfa-distrelec_/" />
        <c:set var="facebooklink" value="https://www.facebook.com/Distrelec.Schweiz/" />
        <c:set var="twitterlink" value="https://twitter.com/Distrelec" />
        <c:set var="youtubelink" value="https://www.youtube.com/user/DistrelecChannel" />
    </c:when>
</c:choose>

<ul class="social-media">
    <li class="social-media__item">
        <a class="icon icon--linkedin" href="${linkedinlink}" aria-label="${sLinkedinAriaText}" title="LinkedIn">
            <i class="fab fa-linkedin-in"></i>
        </a>
    </li>
    <li class="social-media__item">
        <a class="icon icon--facebook" href="${facebooklink}" aria-label="${sFacebookAriaText}" title="Facebook">
            <i class="fab fa-facebook-f"></i>
        </a>
    </li>
    <li class="social-media__item">
        <a class="icon icon--twitter" href="${twitterlink}" aria-label="${sTwitterAriaText}" title="Twitter">
            <i class="fab fa-twitter"></i>
        </a>
    </li>
    <li class="social-media__item">
        <a class="icon icon--youtube" href="${youtubelink}" aria-label="${sYouTubeAriaText}" title="YouTube">
            <i class="fab fa-youtube"></i>
        </a>
    </li>
</ul>