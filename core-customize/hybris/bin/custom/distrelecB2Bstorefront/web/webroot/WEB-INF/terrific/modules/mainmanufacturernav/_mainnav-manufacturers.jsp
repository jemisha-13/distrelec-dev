<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>

<li class="holder mainnav-manufacturers-list">

    <mod:loading-state skin="loading-state" />

    <div class="holder__item">
        <p class="l2__border"></p>

        <ul class="man-select">

            <li v-for="(manufacturer, index) in manufacturerMenuListData" v-cloak>
                <a class="man-nav" :href="manufacturer.key.toLowerCase()" v-on:click="scollToKey">{{manufacturer.key}}</a>
            </li>

        </ul>

        <div class="man-wrap">

            <div v-for="(manufacturer, index) in manufacturerMenuListData" class="man-items" v-cloak>
                <h3 class="man-starts-with"> {{manufacturer.key}}</h3>

                <div v-for="(manuCat, index) in manufacturer.manuCat" class="man-item">
                    <a :href=" '${currentLanguage.isocode}/manufacturer/' + manuCat.nameSeo + '/' + manuCat.code" :title="manuCat.name" data-aainteraction="navigation"
                    data-location="manufacturer nav" data-parent-link="manufacturers" :data-link-value="manuCat.name">{{manuCat.name}}</a>
                </div>

            </div>

        </div>

        <ul id="man-feat" class="man-feat">
        	<c:forEach var="component1" items="${DistBannerComponentsList}">
            	<li>
		       		<cms:component component="${component1}"/>
       			</li>
			</c:forEach>
        </ul>
    </div>
</li>
