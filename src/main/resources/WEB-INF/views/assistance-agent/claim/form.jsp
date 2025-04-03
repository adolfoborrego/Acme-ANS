<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<jstl:if test="${_command != 'create'}">
		<acme:input-moment code="assistance-agent.claim.list.label.registration-moment" path="registrationMoment" readonly="${true}"/>
	</jstl:if>
	<acme:input-textbox code="assistance-agent.claim.list.label.passenger-email" path="passengerEmail" readonly="${published}"/>
	<acme:input-textarea code="assistance-agent.claim.list.label.description" path="description" readonly="${published}"/>
	<acme:input-select code="assistance-agent.claim.list.label.type" path="type" choices="${claimTypes}" readonly="${published}"/>
	<acme:input-select code="assistance-agent.claim.list.label.leg" path="leg" choices="${legs}" readonly="${published}"/>
	<!-- No se muestran los atributos derivados en el create -->
	<jstl:if test="${acme:anyOf(_command, 'show|update|delete|publish')}">
		<acme:input-textbox code="assistance-agent.claim.list.label.indicator" path="indicator" readonly="true"/>
		<acme:input-checkbox code="assistance-agent.claim.list.label.published" path="published" readonly="true"/>
	</jstl:if>
	<!-- Acciones -->
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && !published}">
			<acme:submit code="assistance-agent.claim.update.submit" action="/assistance-agent/claim/update"/>
			<acme:submit code="assistance-agent.claim.delete.submit" action="/assistance-agent/claim/delete"/>
			<jstl:if test="${!published}">
				<acme:submit code="assistance-agent.claim.publish.submit" action="/assistance-agent/claim/publish"/>
			</jstl:if>
		</jstl:when>		
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="assistance-agent.claim.create.submit" action="/assistance-agent/claim/create"/>
		</jstl:when>
	</jstl:choose>
	
</acme:form>
	