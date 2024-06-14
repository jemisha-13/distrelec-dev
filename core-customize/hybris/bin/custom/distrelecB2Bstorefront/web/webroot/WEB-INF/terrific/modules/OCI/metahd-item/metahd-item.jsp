<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<spring:theme code="metahd.lists" text="Lists" var="sLists" />
<spring:theme code="metahd.lists.favorites-shopping" text="Favorites & Shopping" var="sFavoritesShopping" />
<spring:theme code="metahd.lists.favorite-products" text="My Favorite Products" var="sFavoriteProducts" />
<spring:theme code="metahd.lists.show-all-lists" text="Show all lists" var="sShowAllLists" />

<div>
    <h1>Default Meta HD Item Template</h1>
</div>