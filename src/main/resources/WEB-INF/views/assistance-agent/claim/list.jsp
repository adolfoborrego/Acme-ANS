<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="assistance-agent.claim.list.label.registration-moment" path="registrationMoment"/>
	<acme:list-column code="assistance-agent.claim.list.label.passenger-email" path="passengerEmail"/>
	<acme:list-column code="assistance-agent.claim.list.label.description" path="description"/>
	<acme:list-column code="assistance-agent.claim.list.label.type" path="type"/>
	<acme:list-column code="assistance-agent.claim.list.label.published" path="published"/>
	<acme:list-payload path="payload"/>	
</acme:list>