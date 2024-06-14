<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>


<article id="distcomp-14" class="container bannner bannner-component bannner-component--fourteen bannner-component--fourteen-col2">
	<div class=" row">
		<div class="col-md-6">
			<div class="bannner__content">
		        <c:if test="${not empty image1.URL}">
					<div class="bannner__content--image">
						<img alt="${headline}" src="${image1.URL}">
					</div>
		        </c:if>
		        <c:if test="${not empty video1.youtubeUrl}">
					<div class="bannner__content--video">
						<iframe width="560" height="315" src="${video1.youtubeUrl}"></iframe>
					</div>
		        </c:if>
				<c:if test="${not empty text1}">
					<div class="bannner__content--description">
						${text1}
					</div>
				</c:if>
		
		    </div>
		</div>
		<div class="col-md-6">
			<div class="bannner__content">
		        <c:if test="${not empty image2.URL}">
					<div class="bannner__content--image">
						<img alt="${headline}" src="${image2.URL}">
					</div>
		        </c:if>
		        <c:if test="${not empty video2.youtubeUrl}">
					<div class="bannner__content--video">
						<iframe width="560" height="315" src="${video2.youtubeUrl}"></iframe>
					</div>
		        </c:if>
				<c:if test="${not empty text2}">
					<div class="bannner__content--description">
						${text2}
					</div>
				</c:if>
		
		    </div>
		</div>
	</div>  

</article>