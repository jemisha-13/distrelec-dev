<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<views:page-default-md-full pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-category-index skin-layout-wide">
	<div class="container">
		<div id="breadcrumb" class="breadcrumb">
			<mod:breadcrumb />
		</div>
		<mod:global-messages />
		<div class="category-index-holder row">
			<c:if test="${not empty categories}">
				<c:forEach items="${categories}" var="l1_cat">
					<div class="col-12 col-md-6">
						<div class="category-index-holder__item">
							<div class="main-holder">
								<a href="${l1_cat.url}">
									<h1 class="base page-title l1_category">${l1_cat.name}</h1>
								</a>
							</div>
							<c:forEach items="${l1_cat.children}" var="l2_cat">
								<div class="category-index-holder__sub">
									<div class="sub-holder">
										<a href="${l2_cat.url}">
											<h3 class="base page-title l2_category">${l2_cat.name}</h3>
										</a>
									</div>
								</div>
							</c:forEach>
						</div>
					</div>

				</c:forEach>
			</c:if>
		</div>
	</div>
</views:page-default-md-full>
