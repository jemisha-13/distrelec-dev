<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<ul class="tools-bar" data-product-id="${productId}">
    <mod:toolsitem template="toolsitem-favorite" skin="favorite" tag="li" productId="${productId}"/>
    <mod:toolsitem template="toolsitem-favorite-remove" skin="favorite-remove" tag="li" productId="${productId}"/>
</ul>
