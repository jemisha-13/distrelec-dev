<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<%-- List of Attributes --%>
<c:set var="headline" value="${headline}" />
<c:set var="body" value="${body}"/>
<c:set var="type" value="${type}" />
<c:set var="widthPercent" value="${widthPercent}" />
<c:set var="displayIcon" value="${displayIcon}" />
<c:set var="accArgSeparator" value="${not empty accArgumentSeparator ? accArgumentSeparator : ','}" />


<%--
	 This component is also used via CMS. CMS provides type & body attributes. Direct use does not.
	 Therefore we need to set the used Variables, if they are empty i.e. don't come from the CMS.
--%>

<%--
	1) Direct use (Backend Validation error / confirmation / warning messages)
--%>

<c:if test="${not ignoreGlobalMessages}">
		<c:if test = "${not empty accConfMsgs}">
			<c:set var="messages" value="${accConfMsgs}" />
			<c:set var="type" value="success" />
			<c:set var="body" value="${confMsgText}" />
			<%-- Error messages (includes spring validation messages)--%>
			<c:if test="${not empty messages}">
				<c:forEach items="${messages}" var="msg">
					<c:set var="msgText"><spring:theme code="${msg}" arguments="${accArguments[msg]}" argumentSeparator="${accArgSeparator}"/></c:set>
			
					<%-- allowed types: information, promotion, error, warning, success --%>
					<%-- tested widths: .width-oneThird (33%), .width-twoThird (66%), .width-fullWidth (100%) --%>
					<div class="bd ${type}<c:if test="${not empty widthPercent}"> width-${widthPercent}</c:if>">
						<div class="ct">
							<div class="c-center">
								<div class="c-center-content">
									<c:if test="${displayIcon}">
										<div class="col-i">
											<i></i>
										</div>
									</c:if>
									<div class="col-c">
										<c:if test="${not empty headline}">
											<h3>${headline}</h3>
										</c:if>

										<c:if test="${not empty msgText}">
											<p>${msgText}</p>
										</c:if>
									</div>
									<c:if test="${displayIcon}">
										<div class="col-i">
											<i></i>
										</div>
									</c:if>
								</div>
							</div>
						</div>
					</div>
			
				</c:forEach>
			</c:if>
		</c:if>
		<c:if test = "${not empty accInfoMsgs}">
			<c:set var="messages" value="${accInfoMsgs}" />
			<c:set var="type" value="information" />
			<c:set var="body" value="${infoMsgText}" />
						<c:if test="${not empty messages}">
				<c:forEach items="${messages}" var="msg">
					<c:set var="msgText"><spring:theme code="${msg}" arguments="${accArguments[msg]}" argumentSeparator="${accArgSeparator}"/></c:set>
			
					<%-- allowed types: information, promotion, error, warning, success --%>
					<%-- tested widths: .width-oneThird (33%), .width-twoThird (66%), .width-fullWidth (100%) --%>
					<div class="bd ${type}<c:if test="${not empty widthPercent}"> width-${widthPercent}</c:if>">
						<div class="ct">
							<div class="c-center">
								<div class="c-center-content">
									<c:if test="${displayIcon}">
										<div class="col-i">
											<i></i>
										</div>
									</c:if>
									<div class="col-c">
										<c:if test="${not empty headline}">
											<h3>${headline}</h3>
										</c:if>

										<c:if test="${not empty msgText}">
											<p>${msgText}</p>
										</c:if>
									</div>
									<c:if test="${displayIcon}">
										<div class="col-i">
											<i></i>
										</div>
									</c:if>
								</div>
							</div>
						</div>
					</div>
			
				</c:forEach>
			</c:if>
		</c:if>
		<c:if test = "${not empty accErrorMsgs}">
			<c:set var="messages" value="${accErrorMsgs}" />
			<c:set var="type" value="error" />
			<c:set var="body" value="${errorMsgText}" />
						<c:if test="${not empty messages}">
				<c:forEach items="${messages}" var="msg">
					<c:set var="msgText"><spring:theme code="${msg}" arguments="${accArguments[msg]}" argumentSeparator="${accArgSeparator}"/></c:set>
					<%-- allowed types: information, promotion, error, warning, success --%>
					<%-- tested widths: .width-oneThird (33%), .width-twoThird (66%), .width-fullWidth (100%) --%>
					<div class="bd ${type}<c:if test="${not empty widthPercent}"> width-${widthPercent}</c:if>">
						<div class="ct">
							<div class="c-center">
								<div class="c-center-content">
									<c:if test="${displayIcon}">
										<div class="col-i">
											<i></i>
										</div>
									</c:if>
									<div class="col-c">
										<c:if test="${not empty headline}">
											<h3>${headline}</h3>
										</c:if>

										<c:if test="${not empty msgText}">
											<p>${msgText}</p>
										</c:if>
									</div>
									<c:if test="${displayIcon}">
										<div class="col-i">
											<i></i>
										</div>
									</c:if>
								</div>
							</div>
						</div>
					</div>
			
				</c:forEach>
			</c:if>
		</c:if>
		<c:if test = "${not empty accWarnMsgs}">
			<c:set var="messages" value="${accWarnMsgs}" />
			<c:set var="type" value="warning" />
			<c:set var="body" value="${warnMsgText}" />
						<c:if test="${not empty messages}">
				<c:forEach items="${messages}" var="msg">
					<c:set var="msgText"><spring:theme code="${msg}" arguments="${accArguments[msg]}" argumentSeparator="${accArgSeparator}"/></c:set>
			
					<%-- allowed types: information, promotion, error, warning, success --%>
					<%-- tested widths: .width-oneThird (33%), .width-twoThird (66%), .width-fullWidth (100%) --%>
					<div class="bd ${type}<c:if test="${not empty widthPercent}"> width-${widthPercent}</c:if>">
						<div class="ct">
							<div class="c-center">
								<div class="c-center-content">
									<c:if test="${displayIcon}">
										<div class="col-i">
											<i></i>
										</div>
									</c:if>
									<div class="col-c">
										<c:if test="${not empty headline}">
											<h3>${headline}</h3>
										</c:if>

										<c:if test="${not empty msgText}">
											<p>${msgText}</p>
										</c:if>
									</div>
									<c:if test="${displayIcon}">
										<div class="col-i">
											<i></i>
										</div>
									</c:if>
								</div>
							</div>
						</div>
					</div>
			
				</c:forEach>
			</c:if>
		</c:if>
</c:if>


<%--
	2) CMS use (distWarningComponent), can be added throughout the cms cockpit
--%>

<c:if test="${empty messages}">
	<c:if test="${not empty type}">
		<%-- allowed types: information, promotion, error, warning, success --%>
		<%-- tested widths: .width-oneThird (33%), .width-twoThird (66%), .width-fullWidth (100%) --%>
		<div class="bd ${type}<c:if test="${not empty widthPercent}"> width-${widthPercent}</c:if>">
			<div class="ct">
				<div class="c-center">
					<div class="c-center-content">
						<c:if test="${displayIcon}">
							<div class="col-i">
								<i></i>
							</div>
						</c:if>
						<div class="col-c">
							<c:if test="${not empty headline}">
								<h3>${headline}</h3>
							</c:if>

							<c:if test="${not empty body}">
								<p>${body}</p>
							</c:if>
						</div>
						<c:if test="${displayIcon}">
							<div class="col-i">
								<i></i>
							</div>
						</c:if>
					</div>
				</div>
			</div>
		</div>
	</c:if>
</c:if>
