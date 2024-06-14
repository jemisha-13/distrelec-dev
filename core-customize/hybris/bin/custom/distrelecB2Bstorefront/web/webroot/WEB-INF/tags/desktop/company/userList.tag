<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ attribute name="users" required="true" type="java.util.List" %>
<%@ attribute name="action" required="true" type="java.lang.String" %>
<%@ attribute name="createUrl" required="true" type="java.lang.String" %>
<%@ attribute name="editUrl" required="true" type="java.lang.String" %>
<%@ attribute name="role" required="true" type="java.lang.String" %>
<%@ attribute name="subtitleKey" required="true" type="java.lang.String" %>

<div class="item_container_holder">
	<div class="title_holder">
		<div class="title">
			<div class="title-top">
				<span></span>
			</div>
		</div>
		<h2>
			<spring:theme code="text.company.manage.units.header.${action}" text="${action}"/>
		</h2>
	</div>
	<div class="last">
		<a href="${editUrl}">
			<button class="positive right pad_right place-order edit">
				<spring:theme code="text.company.manage.units.users.button.edit" text="Edit"/>
			</button>
		</a>
		<a href="${createUrl}&role=${role}">
			<button class="positive right pad_right place-order create-new">
				<spring:theme code="text.company.manage.units.users.button.createnew" text="Create New"/>
			</button>
		</a>
	</div>
	<div class="item_container">
		<spring:theme code="${subtitleKey}"/>
		<div class="span-19">
			<div class="item_container_holder">
				<div class="item_container">
					<table class="border">
						<thead>
						<tr>
							<th id="header1">
								<spring:theme code="text.company.manage.units.user.name" text="Name"/>
							</th>
							<th id="header2">
								<spring:theme code="text.company.manage.units.user.email" text="Email"/>
							</th>
							<th id="header3">
								<spring:theme code="text.company.manage.units.user.roles" text="Roles"/>
							</th>
							<th id="header4">
								<spring:theme code="text.company.manage.units.user.actions" text="Actions"/>
							</th>
						</tr>
						</thead>
						<tbody>
						<c:forEach items="${users}" var="user">
							<spring:url value="/my-company/organization-management/manage-units/edituser/"
										var="editUserUrl">
								<spring:param name="unit" value="${unit.uid}"/>
								<spring:param name="user" value="${user.uid}"/>
							</spring:url>
							<spring:url value="/my-company/organization-management/manage-units/viewuser/"
										var="viewUserUrl">
								<spring:param name="unit" value="${unit.uid}"/>
								<spring:param name="user" value="${user.uid}"/>
							</spring:url>
							<spring:url value="/my-company/organization-management/manage-units/members/confirm/remove"
										var="removeUserUrl">
								<spring:param name="unit" value="${unit.uid}"/>
								<spring:param name="user" value="${user.uid}"/>
								<spring:param name="role" value="${role}"/>
							</spring:url>
							<tr>
								<td headers="1"><a href="${viewUserUrl}">${fn:escapeXml(user.firstName)}&nbsp;${fn:escapeXml(user.lastName)}</a></td>
								<td headers="2">${fn:escapeXml(user.uid)}</td>
								<td headers="3">
									<c:forEach items="${user.roles}" var="group">
										<p><spring:theme code="b2busergroup.${group}.name"/></p>
									</c:forEach>
								</td>
								<td headers="4">
									<a href="${editUserUrl}">
											<spring:theme code="text.company.manage.unit.user.edit"
													  text="Edit"/></a>
									|
									<a href="${removeUserUrl}">
										<spring:theme code="text.company.manage.unit.user.remove"
													  text="Remove"/></a>
								</td>
							</tr>
						</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
</div>
