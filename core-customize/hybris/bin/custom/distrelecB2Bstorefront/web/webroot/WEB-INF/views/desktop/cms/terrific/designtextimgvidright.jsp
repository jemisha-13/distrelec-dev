<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<div class="container bannner bannner-component bannner-component--sixteen">
    <article id="distcomp-16" class="row ">
        <div class="col-sm-7 col-md-9 bannner__content bannner__content--left">
            <div class="bannner__content--heading">
                ${headline}
            </div>
            <div class="bannner__content--description">
                ${longtext}
            </div>
        </div>
        <div class="col-sm-5 col-md-3 bannner__content bannner__content--right">
            <c:if test="${not empty image.URL}">
                <div class="bannner__content--image">
                    <img alt="${headline}" src="${image.URL}">
                </div>
            </c:if>
            <c:if test="${not empty video.youtubeUrl}">
                <div class="bannner__content--video">
                    <iframe width="560" height="315" src="${video.youtubeUrl}"></iframe>
                </div>
            </c:if>
        </div>
    </article>
    <hr class="border-bottom">
</div>