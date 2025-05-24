<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<jstl:if test="${_command != 'create'}">
		<acme:input-moment code="assistance-agent.claim.list.label.registration-moment" path="registrationMoment" readonly="true"/>
	</jstl:if>
	<acme:input-textbox code="assistance-agent.claim.list.label.passenger-email" path="passengerEmail" readonly="${published}"/>
	<acme:input-textarea code="assistance-agent.claim.list.label.description" path="description" readonly="${published}"/>
	<acme:input-select code="assistance-agent.claim.list.label.type" path="type" choices="${claimTypes}" readonly="${published}"/>
	<acme:input-select code="assistance-agent.claim.list.label.leg" path="leg" choices="${legs}" readonly="${published}"/>
	<jstl:if test="${_command != 'create'}">
		<acme:input-textbox code="assistance-agent.claim.list.label.indicator" path="indicator" readonly="true"/>
		<acme:input-checkbox code="assistance-agent.claim.list.label.published" path="published" readonly="true"/>
	</jstl:if>
	<!-- Acciones -->
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && !published}">
			<acme:submit code="assistance-agent.claim.update.submit" action="/assistance-agent/claim/update?id=${id}"/>
			<acme:submit code="assistance-agent.claim.delete.submit" action="/assistance-agent/claim/delete?id=${id}"/>
			<jstl:if test="${!published}">
				<acme:submit code="assistance-agent.claim.publish.submit" action="/assistance-agent/claim/publish?id=${id}"/>
			</jstl:if>
		</jstl:when>		
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="assistance-agent.claim.create.submit" action="/assistance-agent/claim/create"/>
		</jstl:when>
	</jstl:choose>
	<!-- TrackingLogs -->
	<jstl:if test="${_command != 'create'}">
		<jstl:choose>
			<jstl:when test="${published}">
				<acme:button code="assistance-agent.tracking-log.list" action="/assistance-agent/tracking-log/list?claimId=${id}"/>
			</jstl:when>
		</jstl:choose>
	</jstl:if>
	
</acme:form>
	