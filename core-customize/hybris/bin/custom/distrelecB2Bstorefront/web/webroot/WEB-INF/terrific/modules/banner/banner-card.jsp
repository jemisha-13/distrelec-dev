<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<article class="card__item">
    <div class="card__item__img">
        <img data-src=${media.URL} alt="${headline}" width="380" height="89"/>
    </div>
    <div class="card__item__info">
        <h2 class="card__item__title">${headline}</h2>
        <p class="card__item__description">${content}</p>
    </div>
</article>