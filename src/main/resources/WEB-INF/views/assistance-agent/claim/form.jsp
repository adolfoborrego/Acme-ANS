<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-moment code="assistance-agent.claim.list.label.registration-moment" path="registrationMoment"/>
	<acme:input-textbox code="assistance-agent.claim.list.label.passenger-email" path="passengerEmail"/>
	<acme:input-textarea code="assistance-agent.claim.list.label.description" path="description"/>
	<acme:input-select code="assistance-agent.claim.list.label.type" path="type" choices="${claimTypes}"/>
	<acme:input-select code="assistance-agent.claim.list.label.leg" path="leg" choices="${legs}"/>
	<!-- No se muestran los atributos derivados en el create -->
	<jstl:if test="${acme:anyOf(_command, 'show|update|delete|publish')}">
		<acme:input-textbox code="assistance-agent.claim.list.label.indicator" path="indicator" readonly="true"/>
		<acme:input-checkbox code="assistance-agent.claim.list.label.published" path="published" readonly="true"/>
	</jstl:if>
	<!-- Acciones -->
	<jstl:choose>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="assistance-agent.claim.create.submit" action="/assistance-agent/claim/create"/>
		</jstl:when>
	</jstl:choose>
	
</acme:form>
	