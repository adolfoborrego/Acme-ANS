<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<jstl:if test="${_command != 'create'}">
		<acme:input-moment code="assistance-agent.tracking-log.list.label.last-update-moment" path="lastUpdateMoment" readonly="true"/>
	</jstl:if>
	<acme:input-textbox code="assistance-agent.tracking-log.list.label.step" path="step" readonly="${published}"/>
	<acme:input-double code="assistance-agent.tracking-log.list.label.resolution-percentage" path="resolutionPercentage" readonly="${published}"/>
	<acme:input-select code="assistance-agent.tracking-log.list.label.indicator" path="indicator" choices="${trackingLogIndicators}" readonly="${published}"/>
	<acme:input-textarea code="assistance-agent.tracking-log.list.label.resolution" path="resolution" readonly="${published}"/>
	<jstl:if test="${_command != 'create'}">
		<acme:input-checkbox code="assistance-agent.tracking-log.list.label.published" path="published" readonly="true"/>
	</jstl:if>
	<!-- Acciones -->
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && !published}">
			<acme:submit code="assistance-agent.tracking-log.update.submit" action="/assistance-agent/tracking-log/update"/>
			<acme:submit code="assistance-agent.tracking-log.delete.submit" action="/assistance-agent/tracking-log/delete"/>
			<jstl:if test="${!published}">
				<acme:submit code="assistance-agent.tracking-log.publish.submit" action="/assistance-agent/tracking-log/publish"/>
			</jstl:if>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="assistance-agent.tracking-log.create.submit" action="/assistance-agent/tracking-log/create?claimId=${claimId}"/>
		</jstl:when>
	</jstl:choose>
</acme:form>